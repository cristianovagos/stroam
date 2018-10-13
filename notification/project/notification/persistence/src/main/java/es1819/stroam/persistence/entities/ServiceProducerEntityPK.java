package es1819.stroam.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ServiceProducerEntityPK implements Serializable {
    private int serviceId;
    private int producerId;

    @Column(name = "ServiceId")
    @Id
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "ProducerId")
    @Id
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
        ServiceProducerEntityPK that = (ServiceProducerEntityPK) o;
        return serviceId == that.serviceId &&
                producerId == that.producerId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(serviceId, producerId);
    }
}
