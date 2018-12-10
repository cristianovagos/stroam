package es1819.stroam.notification.commons.communication.serial;

import java.io.IOException;

public interface SerialCommunicationCallback {
    void serialDataReceived(String portName, byte[] data);
    void serialPortDataReceiveException(String portName, IOException serialDataReceiveException);
}
