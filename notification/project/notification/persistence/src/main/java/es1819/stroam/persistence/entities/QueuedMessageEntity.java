package es1819.stroam.persistence.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "QueuedMessages", schema = "nottheservicedb", catalog = "")
@IdClass(QueuedMessageEntityPK.class)
public class QueuedMessageEntity {
    private int id;
    private String userId;
    private byte[] payload;
    private Timestamp timestamp;
    private byte processed;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Id
    @Column(name = "UserId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "Payload")
    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Basic
    @Column(name = "Timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name = "Processed")
    public byte getProcessed() {
        return processed;
    }

    public void setProcessed(byte processed) {
        this.processed = processed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueuedMessageEntity that = (QueuedMessageEntity) o;
        return id == that.id &&
                processed == that.processed &&
                Objects.equals(userId, that.userId) &&
                Arrays.equals(payload, that.payload) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(id, userId, timestamp, processed);
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }
}
