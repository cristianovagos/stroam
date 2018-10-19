package es1819.stroam.persistence.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "LogSeverities", schema = "nottheservicedb", catalog = "")
public class LogSeverityEntity {
    private int id;
    private int level;
    private Integer description;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Level")
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Basic
    @Column(name = "Description")
    public Integer getDescription() {
        return description;
    }

    public void setDescription(Integer description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogSeverityEntity that = (LogSeverityEntity) o;
        return id == that.id &&
                level == that.level &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, level, description);
    }
}
