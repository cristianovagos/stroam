package es1819.stroam.persistence.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Subscribers_Channels", schema = "nottheservicedb", catalog = "")
@IdClass(SubscriberChannelEntityPK.class)
public class SubscriberChannelEntity {
    private int subscriberId;
    private int channelId;

    @Id
    @Column(name = "SubscriberId")
    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
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
        SubscriberChannelEntity that = (SubscriberChannelEntity) o;
        return subscriberId == that.subscriberId &&
                channelId == that.channelId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(subscriberId, channelId);
    }
}
