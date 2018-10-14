package es1819.stroam.persistence.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Subscribers", schema = "nottheservicedb", catalog = "")
public class SubscriberEntity {
    private int id;
    private String externalId;
    private String name;
    private String emailAddress;
    private String phoneNumber;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "ExternalId")
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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
    @Column(name = "EmailAddress")
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Basic
    @Column(name = "PhoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriberEntity that = (SubscriberEntity) o;
        return id == that.id &&
                Objects.equals(externalId, that.externalId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(emailAddress, that.emailAddress) &&
                Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, externalId, name, emailAddress, phoneNumber);
    }
}
