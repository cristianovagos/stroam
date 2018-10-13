package es1819.stroam.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ProducerChannelEntityPK implements Serializable {
    private int producerId;
    private int channelId;

    @Column(name = "ProducerId")
    @Id
    public int getProducerId() {
        return producerId;
    }

    public void setProducerId(int producerId) {
        this.producerId = producerId;
    }

    @Column(name = "ChannelId")
    @Id
    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProducerChannelEntityPK that = (ProducerChannelEntityPK) o;
        return producerId == that.producerId &&
                channelId == that.channelId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(producerId, channelId);
    }
}
