package es1819.stroam.persistence.entities;


import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Channels", schema = "nottheservicedb", catalog = "")
public class ChannelEntity {
    private int id;
    private String path;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelEntity that = (ChannelEntity) o;
        return id == that.id &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, path);
    }
}
