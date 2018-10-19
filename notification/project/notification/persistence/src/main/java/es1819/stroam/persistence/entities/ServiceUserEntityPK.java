package es1819.stroam.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ServiceUserEntityPK implements Serializable {
    private String serviceId;
    private String userId;

    @Column(name = "ServiceId")
    @Id
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "UserId")
    @Id
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceUserEntityPK that = (ServiceUserEntityPK) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serviceId, userId);
    }
}
