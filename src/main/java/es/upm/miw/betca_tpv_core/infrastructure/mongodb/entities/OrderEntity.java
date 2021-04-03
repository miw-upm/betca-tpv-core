package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
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
import java.util.ArrayList;
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
    private String description;
    @DBRef(lazy = true)
    private ProviderEntity providerEntity;
    private LocalDateTime openingDate;
    private LocalDateTime closingDate;
    private List<OrderLineEntity> orderLineEntities;


    public OrderEntity(Order order, ProviderEntity providerEntity) {
        BeanUtils.copyProperties(order, this);
        this.providerEntity = providerEntity;
        this.orderLineEntities = new ArrayList<>();
    }

    public void add(OrderLineEntity orderLineEntity) {
        this.orderLineEntities.add(orderLineEntity);
    }

    public Order toOrder() {
        Order order = new Order();
        BeanUtils.copyProperties(this, order);
        if (Objects.nonNull(this.getProviderEntity())) {
            order.setProviderCompany(this.getProviderEntity().getCompany());
        }
        if (Objects.nonNull(this.getOrderLineEntities())) {
            List<OrderLine> orderLines = new ArrayList<>();
            for (int i = 0; i < this.getOrderLineEntities().size(); i++) {
                orderLines.add(this.getOrderLineEntities().get(i).toOrderLine());
            }
            order.setOrderLines(orderLines);
        }
        return order;
    }
}
