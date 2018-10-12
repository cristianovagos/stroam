package es1819.stroam.server;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class SharedMessageQueue {

    private Queue<JSONObject> jsonObjectsQueue = new LinkedList<JSONObject>();
    private Semaphore queueAccessControllerMutex = new Semaphore(1);

    public void put(JSONObject jsonObject) {
        try { queueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) {}
        jsonObjectsQueue.offer(jsonObject);
        queueAccessControllerMutex.release();
    }

    public JSONObject get() {
        try { queueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) {}
        JSONObject jsonObject = jsonObjectsQueue.poll();
        queueAccessControllerMutex.release();
        return jsonObject;
    }
}
