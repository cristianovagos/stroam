package es1819.stroam.server;

import es1819.stroam.server.constants.Constants;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

//TODO: ver funcionalidade
/*
Criar thread para que um processo possa simplesmente depositar o seu item e regressar
O objectivo da thread é:
-quando iniciada, verificar na base de dados se existem mensagens nao processadas
    devem ser retidas as mensagens com um timestamp mais antigo primeiro!
-antes de finalizar colocar todas as mensagens existentes na fila na base de dados
    se ao terminar a thread houverem mensagens com um id (da base de dados) atribuido que se encontrem na fila
    (significa que foram persistidas, retiradas novamente para a final mas por algum motivo nao foram processadas,
    por exemplo, finalização da thread antes da mensagem sair, deve ser actualizado o seu estado para não processada.
-durante o seu tempo de vida, se a fila ficar cheia deve persistir as novas mensagens recebidas e
 à medida que as mensagens vão saindo vai colocando novamente as que foram persistidas na fila
NOTA: as mensagens que vao para a fila devem permanecer na base de dados. Apenas deve ser mudado
o valor de "Processed" para 1.
 */

public class ManagedMessageQueue {

    private int queueSizeLimit;
    private int queuedObjectsCount;
    private Queue<MessageChannelPayload> messageChannelPayloadQueue = new LinkedList<>();
    private Semaphore queueAccessControllerMutex = new Semaphore(1);

    public ManagedMessageQueue(int queueSizeLimit) {
        if(queueSizeLimit == 0) //if 0 set the to the default value
            queueSizeLimit = Constants.MANAGE_QUEUE_DEFAULT_SIZE_LIMIT;

        this.queueSizeLimit = queueSizeLimit;
    }

    public void put(MessageChannelPayload messageChannelPayload) {
        try { queueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) {}
        messageChannelPayloadQueue.offer(messageChannelPayload);
        queuedObjectsCount++;
        queueAccessControllerMutex.release();
    }

    public MessageChannelPayload get() {
        try { queueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) {}
        MessageChannelPayload result = messageChannelPayloadQueue.poll();
        queuedObjectsCount--;
        queueAccessControllerMutex.release();
        return result;
    }
}

/*
if(queuedObjectsCount == queueSizeLimit) {
    EntityManager entityManager = PersistenceUtilities.getEntityManagerInstance();
    if(entityManager == null)
        throw new NullPointerException("entityManager cannot be null or empty"); //TODO: tentar colocar objecto em memoria para nao ser perdido

    QueuedMessageEntity queuedMessageEntity = new QueuedMessageEntity();
    queuedMessageEntity.setChannel(messageChannelPayload.getChannel());
    queuedMessageEntity.setPayload(messageChannelPayload.getBytes());
    queuedMessageEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));

    entityManager.getTransaction().begin();
    entityManager.persist(queuedMessageEntity);
    entityManager.getTransaction().commit();
    entityManager.close();
    return;
}
 */
