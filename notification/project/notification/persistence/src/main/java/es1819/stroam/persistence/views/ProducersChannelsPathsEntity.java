package es1819.stroam.persistence.views;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ProducersChannelsPaths_View", schema = "nottheservicedb", catalog = "")
public class ProducersChannelsPathsEntity implements Serializable {
    private int producerId;
    private int channelId;
    private String path;
    private byte isProducerPrefix;

    @Column(name = "ProducerId")
    @Id
    public int getProducerId() {
        return producerId;
    }

    protected void setProducerId(int producerId) {
        this.producerId = producerId;
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

    @Basic
    @Column(name = "IsProducerPrefix")
    public byte getIsProducerPrefix() {
        return isProducerPrefix;
    }

    protected void setIsProducerPrefix(byte isProducerPrefix) {
        this.isProducerPrefix = isProducerPrefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProducersChannelsPathsEntity that = (ProducersChannelsPathsEntity) o;
        return producerId == that.producerId &&
                channelId == that.channelId &&
                isProducerPrefix == that.isProducerPrefix &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(producerId, channelId, path, isProducerPrefix);
    }
}
