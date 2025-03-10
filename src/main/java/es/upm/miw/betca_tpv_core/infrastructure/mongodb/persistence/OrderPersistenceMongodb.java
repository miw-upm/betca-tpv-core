package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.persistence.OrderPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.OrderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.OrderDao;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderLineEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderPersistenceMongodb implements OrderPersistence {

    private final OrderReactive orderReactive;

    @Autowired
    public OrderPersistenceMongodb(OrderReactive orderReactive, OrderDao OrderDao) {
        this.orderReactive = orderReactive;
    }

    @Override
    public Mono<Order> create(Order order) {
        return this.assertReferenceNotExist(order.getReference())
                .then(Mono.just(order))
                .flatMap(o -> {
                    List<OrderLineEntity> orderLineEntities = o.getOrderLinesList().stream()
                            .map(orderLine -> {
                                OrderLineEntity orderLineEntity = new OrderLineEntity();
                                BeanUtils.copyProperties(orderLine, orderLineEntity);
                                return orderLineEntity;
                            })
                            .collect(Collectors.toList());
                    OrderEntity orderEntity = new OrderEntity(o, orderLineEntities);
                    return this.orderReactive.save(orderEntity);
                })
                .map(OrderEntity::toOrder);
    }

    @Override
    public Mono<Order> readByReference(String reference) {
        return this.orderReactive.findByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent Order reference: " + reference)))
                .map(OrderEntity::toOrder);
    }

    @Override
    public Mono<Order> update(String reference, Order order) {
        return this.orderReactive.findByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent Order reference: " + reference)))
                .flatMap(existingOrderEntity -> {
                    existingOrderEntity.setClosingDate(LocalDateTime.now());
                    List<OrderLineEntity> orderLineEntities = order.getOrderLinesList().stream()
                            .map(orderLine -> {
                                OrderLineEntity orderLineEntity = new OrderLineEntity();
                                BeanUtils.copyProperties(orderLine, orderLineEntity);
                                return orderLineEntity;
                            })
                            .toList();
                    existingOrderEntity.setOrderLineEntities(orderLineEntities);
                    return this.orderReactive.save(existingOrderEntity);
                })
                .map(OrderEntity::toOrder);
    }

    private Mono<Void> assertReferenceNotExist(String reference) {
        return this.orderReactive.findByReference(reference)
                .flatMap(orderEntity -> Mono.error(
                        new ConflictException("Order reference already exists : " + reference)
                ));
    }
}
