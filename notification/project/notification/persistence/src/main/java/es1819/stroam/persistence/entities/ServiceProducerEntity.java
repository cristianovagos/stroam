package es1819.stroam.persistence.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Services_Producers", schema = "nottheservicedb", catalog = "")
@IdClass(ServiceProducerEntityPK.class)
public class ServiceProducerEntity {
    private int serviceId;
    private int producerId;

    @Id
    @Column(name = "ServiceId")
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "ProducerId")
    public int getProducerId() {
        return producerId;
    }

    public void setProducerId(int producerId) {
        this.producerId = producerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceProducerEntity that = (ServiceProducerEntity) o;
        return serviceId == that.serviceId &&
                producerId == that.producerId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(serviceId, producerId);
    }
}
