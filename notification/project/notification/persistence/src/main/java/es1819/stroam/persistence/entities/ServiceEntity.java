package es1819.stroam.persistence.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Services", schema = "nottheservicedb", catalog = "")
public class ServiceEntity {
    private int id;
    private String name;
    private int channelPrefixId;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "ChannelPrefixId")
    public int getChannelPrefixId() {
        return channelPrefixId;
    }

    public void setChannelPrefixId(int channelPrefixId) {
        this.channelPrefixId = channelPrefixId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceEntity that = (ServiceEntity) o;
        return id == that.id &&
                channelPrefixId == that.channelPrefixId &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, channelPrefixId);
    }
}
