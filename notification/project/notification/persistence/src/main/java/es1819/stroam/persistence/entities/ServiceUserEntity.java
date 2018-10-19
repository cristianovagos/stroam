package es1819.stroam.persistence.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Services_Users", schema = "nottheservicedb", catalog = "")
@IdClass(ServiceUserEntityPK.class)
public class ServiceUserEntity {
    private String serviceId;
    private String userId;

    @Id
    @Column(name = "ServiceId")
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "UserId")
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
        ServiceUserEntity that = (ServiceUserEntity) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serviceId, userId);
    }
}
