package es1819.stroam.notification.commons.communication.serial;

import es1819.stroam.contributions.classes.SerialPortList;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.IOException;

public class SerialCommunication {

    private String serialPortName;
    private SerialPort serialPort;
    private SerialCommunicationCallback callback;

    public SerialCommunication(String serialPortName) {
        if(serialPortName == null || serialPortName.isEmpty())
            throw new IllegalArgumentException("serialPortName is null or empty");

        this.serialPortName = serialPortName;
        this.serialPort = new SerialPort(serialPortName);
    }

    public SerialCommunication setCallback(SerialCommunicationCallback callback) {
        this.callback = callback;
        return this;
    }

    public String getSerialPortName() {
        return serialPortName;
    }

    public String[] getAvailablePortsNames() {
        return SerialPortList.getPortNames();
    }

    public boolean isOpened() {
        return serialPort.isOpened();
    }

    public void open() throws IOException {
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_1200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE, false, false);

            serialPort.addEventListener(serialPortEvent -> {
                if(callback != null) {
                    try {
                        byte[] receivedBytes = serialPort.readBytes();

                        if(receivedBytes != null && receivedBytes.length > 0)
                            callback.serialDataReceived(serialPortName, receivedBytes);
                    } catch (SerialPortException serialReadBytesException) {
                        callback.serialPortDataReceiveException(serialPortName, new IOException(serialReadBytesException));
                    }
                }
            });

        } catch (SerialPortException serialOpenPortProcedureException) {
            throw new IOException(serialOpenPortProcedureException);
        }
    }

    public void close() throws IOException {
        try {
            serialPort.closePort();
        } catch (SerialPortException serialClosePortException) {
            throw new IOException(serialClosePortException);
        }
    }

    public void reset() throws IOException {
        try { //reset the connection
            serialPort.removeEventListener();
            close();
            open();
        } catch (SerialPortException serialResetException) {
            throw new IOException(serialResetException);
        }
    }

    public void send(byte[] data) throws IOException {
        try {
            serialPort.writeBytes(data);
        } catch (SerialPortException serialWriteBytesException) {
            throw new IOException(serialWriteBytesException);
        }
    }
}
