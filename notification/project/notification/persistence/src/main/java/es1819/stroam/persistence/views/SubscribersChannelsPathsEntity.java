package es1819.stroam.persistence.views;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "SubscribersChannelsPaths_View", schema = "nottheservicedb", catalog = "")
public class SubscribersChannelsPathsEntity implements Serializable {
    private int subscriberId;
    private int channelId;
    private String path;

    @Column(name = "SubscriberId")
    @Id
    public int getSubscriberId() {
        return subscriberId;
    }

    protected void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Column(name = "ChannelId")
    @Id
    public int getChannelId() {
        return channelId;
    }

    protected void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    @Basic
    @Column(name = "Path")
    public String getPath() {
        return path;
    }

    protected void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscribersChannelsPathsEntity that = (SubscribersChannelsPathsEntity) o;
        return subscriberId == that.subscriberId &&
                channelId == that.channelId &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(subscriberId, channelId, path);
    }
}
