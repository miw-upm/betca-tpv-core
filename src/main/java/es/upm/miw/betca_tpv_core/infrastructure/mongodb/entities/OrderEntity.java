package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class OrderEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reference;
    @Indexed(unique = true)
    private String providerCompany;
    private String description;
    private LocalDateTime openingDate;
    private LocalDateTime closingDate;
    private List<OrderLineEntity> orderLineEntities;

    public OrderEntity(Order order, List<OrderLineEntity> orderLineEntities) {
        BeanUtils.copyProperties(order, this);
        this.orderLineEntities = orderLineEntities;
    }

    public Order toOrder() {
        Order order = new Order();
        BeanUtils.copyProperties(this, order);
        if (Objects.nonNull(this.orderLineEntities) && !this.orderLineEntities.isEmpty()) {
            order.setOrderLinesList(this.orderLineEntities.stream()
                    .map(OrderLineEntity::toOrderLine)
                    .toList());
        }
        return order;
    }

}
