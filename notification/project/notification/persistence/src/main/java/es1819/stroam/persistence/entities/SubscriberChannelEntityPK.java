package es1819.stroam.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class SubscriberChannelEntityPK implements Serializable {
    private int subscriberId;
    private int channelId;

    @Column(name = "SubscriberId")
    @Id
    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
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
        SubscriberChannelEntityPK that = (SubscriberChannelEntityPK) o;
        return subscriberId == that.subscriberId &&
                channelId == that.channelId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(subscriberId, channelId);
    }
}
