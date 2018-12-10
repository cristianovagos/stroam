package es1819.stroam.notification.server.core.handler;

import es1819.stroam.notification.commons.Constants;
import es1819.stroam.notification.commons.communication.message.Message;
import es1819.stroam.notification.commons.communication.message.request.RequestMessage;
import es1819.stroam.notification.commons.communication.message.response.ResponseMessage;
import es1819.stroam.notification.commons.communication.serial.SerialCommunication;
import es1819.stroam.notification.commons.communication.serial.SerialCommunicationCallback;

import java.io.IOException;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class PhoneHandler extends Handler implements SerialCommunicationCallback {

    private String temporaryDeviceResponse = ""; //used because, sometimes, device response come fragmented
    private ResponseSenderHandler responseSenderHandler;
    private SerialCommunication serialCommunication;
    private Timer deviceResponseTimeoutTimer;
    private Semaphore deviceResponseThreadRunController = new Semaphore(0);
    private SmsSendStatus smsSendStatusFlag;

    public PhoneHandler(ResponseSenderHandler responseSenderHandler) {
        super();

        if(responseSenderHandler == null)
            throw new IllegalArgumentException("responseSenderHandler cannot be null or empty");

        this.responseSenderHandler = responseSenderHandler;
    }

    @Override
    public void start() {
        super.start();

        String serialPortName = Constants.runtimeProperties.getProperty(Constants.PROPERTY_SERIAL_PORT_NAME);
        if(serialPortName == null || serialPortName.isEmpty()) {
            System.err.println("Serial port not specified. Phone notifications will not be sent by the device!");
            return;
        }

        serialCommunication = new SerialCommunication(serialPortName);
        serialCommunication.setCallback(this);

        if(!checkSerialConnection())
            System.err.println("Can't open serial port " + serialPortName + ". Please check if the port name is " +
                    "correct and if the device is connected and operational");
    }

    @Override
    public void stop() {
        super.stop();

        if(serialCommunication != null && serialCommunication.isOpened()) {
            try {
                serialCommunication.close();
            } catch (IOException serialCloseException) {
                serialCloseException.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try { threadRunController.acquire(); } catch (InterruptedException ignored) { continue; }

            if(!keepRunning)
                break;

            try { processingQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) { continue; }
            Message message = processingQueue.poll();
            processingQueueAccessControllerMutex.release();

            if(message == null)
                continue;

            if(message instanceof ResponseMessage) {
                //TODO: colocar excepção abaixo no log
                new IllegalArgumentException("received a message to process of unexpected type of ResponseMessage").printStackTrace(); //TODO: debug
                continue;
            }
            RequestMessage requestMessage = (RequestMessage)message;

            String phoneNumber = requestMessage.getPhoneNumber();
            if(phoneNumber == null || phoneNumber.isEmpty()) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_NUMBER_NULL_OR_EMPTY.getResultCode(),
                                requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to process " +
                                        "with a null or empty phone number"));
                continue;
            }

            String phoneMessageBody = requestMessage.getPhoneBody();
            if(phoneMessageBody == null || phoneMessageBody.isEmpty()) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_BODY_NULL_OR_EMPTY.getResultCode(),
                                requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to process with a null or empty body"));
                continue;
            }


            String decodedPhoneMessageBody;
            try {
                decodedPhoneMessageBody = new String(Base64.getDecoder().decode(phoneMessageBody));
            } catch (IllegalArgumentException messageDecodeException) {
                messageDecodeException.printStackTrace(); //TODO: logar excepçao

                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_BODY_DECODE_ERROR.getResultCode(),
                                requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to process with an invalid encoded body"));
                continue;
            }

            if(decodedPhoneMessageBody.isEmpty()) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_DECODED_BODY_NULL_OR_EMPTY.getResultCode(),
                                requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to process " +
                                        "with a null or empty decoded body"));
                continue;
            }

            if(decodedPhoneMessageBody.length() > Constants.PHONE_MESSAGE_MAX_CHARACTERS) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_BODY_TOO_LONG.getResultCode(),
                                requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to " +
                                        "process with more than " + Constants.PHONE_MESSAGE_MAX_CHARACTERS +
                                        "characters long"));
                continue;
            }

            //TODO: melhorar codigo e colocar a registar no log tambem
            if(Constants.runtimeProperties.getProperty(Constants.PROPERTY_DEBUG_MODE_NAME)
                    .equalsIgnoreCase("true")) {
                System.out.println("Sending phone message to: " + requestMessage.getPhoneNumber());
                System.out.println("Body: " + decodedPhoneMessageBody);
            }

            if(!checkSerialConnection()) { //can't connect to the device
                responseSenderHandler.handle(new ResponseMessage(
                        HandleResultType.UNKNOWN_ERROR.getResultCode(), requestMessage.getRequestId())
                        .setReason("an unknown error occurred while sending phone SMS. Try again later"));
                continue;
            }

            byte[] serialDataMessageBytes = (requestMessage.getPhoneNumber() + "," +
                    decodedPhoneMessageBody + "\n") //without the "\n" at the end the message is not sended
                    .getBytes();

            try {
                serialCommunication.send(serialDataMessageBytes);
                startDeviceResponseTimeoutTimer();

                smsSendStatusFlag = SmsSendStatus.SENDING;

            } catch (IOException serialSendException) {
                serialSendException.printStackTrace();

                responseSenderHandler.handle(new ResponseMessage(
                        HandleResultType.PHONE_SENDING_ERROR.getResultCode(), requestMessage.getRequestId())
                        .setReason("an error occurred while sending phone SMS. Try again later"));
                continue;
            }

            //waits for device response;
            try { deviceResponseThreadRunController.acquire(); } catch (InterruptedException ignored) {}

            deviceResponseTimeoutTimer.cancel();
            if(smsSendStatusFlag == SmsSendStatus.SEND_OK) {
                responseSenderHandler.handle(new ResponseMessage(
                        HandleResultType.PHONE_SENDING_SUCCESS.getResultCode(), requestMessage.getRequestId())
                        .setReason("phone message successfully sended to " + phoneNumber));
            } else {
                responseSenderHandler.handle(new ResponseMessage(
                        HandleResultType.UNKNOWN_ERROR.getResultCode(), requestMessage.getRequestId())
                        .setReason("an unknown error occurred while sending phone SMS. Try again later"));
            }

            temporaryDeviceResponse = ""; //reset the variable value to the next request
        }
    }

    @Override
    public void serialDataReceived(String portName, byte[] data) {
        temporaryDeviceResponse += new String(data);

        if(temporaryDeviceResponse.compareTo(Constants.DEVICE_SMS_SEND_SUCCESS_RESPONSE) == 0)
            smsSendStatusFlag = SmsSendStatus.SEND_OK;
        else if(temporaryDeviceResponse.compareTo(Constants.DEVICE_SMS_SEND_FAIL_RESPONSE) == 0)
            smsSendStatusFlag = SmsSendStatus.SEND_ERROR;

        if(smsSendStatusFlag != SmsSendStatus.SENDING) //solve the problem of the response coming fragmented
            deviceResponseThreadRunController.release();
    }

    @Override
    public void serialPortDataReceiveException(String portName, IOException serialDataReceiveException) {
        System.err.println("Data receive exception in port " + portName);
        serialDataReceiveException.printStackTrace();

        smsSendStatusFlag = SmsSendStatus.SEND_ERROR;
    }

    private void startDeviceResponseTimeoutTimer() {
        deviceResponseTimeoutTimer = new Timer(); //if timer is canceled
        deviceResponseTimeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                smsSendStatusFlag = SmsSendStatus.SEND_ERROR; //the SMS may not be sended
                System.err.println("Device response timed out. Resetting connection...");

                try {
                    serialCommunication.reset(); //force the Arduino reset to avoid an inconsistent state
                    Thread.sleep(Constants.DEVICE_BOOT_DELAY);
                } catch (IOException serialResetException) {
                    serialResetException.printStackTrace();
                } catch (InterruptedException ignored) {}
                deviceResponseThreadRunController.release();
            }
        }, Constants.DEVICE_WAIT_RESPONSE_DELAY);
    }

    private boolean checkSerialConnection() {
        if(serialCommunication == null)
            return false;

        if(!serialCommunication.isOpened()) { //if serial port is already open don't do anything
            try {
                System.out.println("Opening the serial port to device: " + serialCommunication.getSerialPortName());

                serialCommunication.open();
                Thread.sleep(Constants.DEVICE_BOOT_DELAY); //wait for Arduino to be ready
            } catch (IOException serialOpenException) {
                serialOpenException.printStackTrace();
                return false;
            } catch (InterruptedException ignored) {}
        }
        return true;
    }

    private enum SmsSendStatus {
        SEND_OK,
        SEND_ERROR,
        SENDING
    }
}
