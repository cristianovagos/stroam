package es1819.stroam.persistence.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Producers_Channels", schema = "nottheservicedb", catalog = "")
@IdClass(ProducerChannelEntityPK.class)
public class ProducerChannelEntity {
    private int producerId;
    private int channelId;

    @Id
    @Column(name = "ProducerId")
    public int getProducerId() {
        return producerId;
    }

    public void setProducerId(int producerId) {
        this.producerId = producerId;
    }

    @Id
    @Column(name = "ChannelId")
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
        ProducerChannelEntity that = (ProducerChannelEntity) o;
        return producerId == that.producerId &&
                channelId == that.channelId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(producerId, channelId);
    }
}
