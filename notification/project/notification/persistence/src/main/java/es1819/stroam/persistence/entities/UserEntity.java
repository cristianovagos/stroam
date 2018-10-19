package es1819.stroam.persistence.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Users", schema = "nottheservicedb", catalog = "")
public class UserEntity {
    private String id;
    private String externalId;
    private String name;
    private String emailAddress;
    private String phoneNumber;
    private byte pushNotification;
    private byte emailNotification;
    private byte phoneNotification;
    private byte active;

    @Id
    @Column(name = "Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @Basic
    @Column(name = "PushNotification")
    public byte getPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(byte pushNotification) {
        this.pushNotification = pushNotification;
    }

    @Basic
    @Column(name = "EmailNotification")
    public byte getEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(byte emailNotification) {
        this.emailNotification = emailNotification;
    }

    @Basic
    @Column(name = "PhoneNotification")
    public byte getPhoneNotification() {
        return phoneNotification;
    }

    public void setPhoneNotification(byte phoneNotification) {
        this.phoneNotification = phoneNotification;
    }

    @Basic
    @Column(name = "Active")
    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return pushNotification == that.pushNotification &&
                emailNotification == that.emailNotification &&
                phoneNotification == that.phoneNotification &&
                active == that.active &&
                Objects.equals(id, that.id) &&
                Objects.equals(externalId, that.externalId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(emailAddress, that.emailAddress) &&
                Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, externalId, name, emailAddress, phoneNumber, pushNotification, emailNotification, phoneNotification, active);
    }
}
