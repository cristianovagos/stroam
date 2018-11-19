package es1819.stroam.notification.server.core.handler;

import es1819.stroam.notification.commons.communication.message.Message;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

//this class contains the basic logic of the handlers (if necessary handlers must override this methods
public abstract class Handler implements Runnable {

    protected boolean keepRunning;
    protected Queue<Message> processingQueue = new LinkedList<>();
    protected Semaphore processingQueueAccessControllerMutex = new Semaphore(1);
    protected Semaphore threadRunController = new Semaphore(0);

    public Handler() {
        //Empty constructor
    }

    public void start() {
        if(keepRunning)
            return;

        keepRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        if(!keepRunning)
            return;

        keepRunning = false;
        threadRunController.release();
    }

    public void handle(Message message) {
        try { processingQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) { return; }
        //Mutual exclusion access
        processingQueue.offer(message);
        processingQueueAccessControllerMutex.release();

        threadRunController.release();
    }

    @Override
    public abstract void run();
}
