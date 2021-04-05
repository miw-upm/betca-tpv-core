package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.persistence.OrderPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.OrderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderLineEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Repository
public class OrderPersistenceMongodb implements OrderPersistence {

    private static final String ORDER_NOT_EXISTS = "Order does not exist: ";
    private OrderReactive orderReactive;
    private ProviderReactive providerReactive;
    private ArticleReactive articleReactive;

    @Autowired
    public OrderPersistenceMongodb(OrderReactive orderReactive, ProviderReactive providerReactive, ArticleReactive articleReactive) {
        this.orderReactive = orderReactive;
        this.providerReactive = providerReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Flux<Order> findByAll() {
        return this.orderReactive.findAll()
                .map(OrderEntity::toOrder);
    }

    @Override
    public Flux<Order> findByDescriptionAndOpeningDateBetween(String description, LocalDateTime fromDate, LocalDateTime toDate) {
        return this.orderReactive.findByDescriptionAndOpeningDateBetween(description, fromDate, toDate)
                .map(OrderEntity::toOrder);
    }

    @Override
    public Mono<Order> findByReference(String reference) {
        return this.orderReactive.findByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException(ORDER_NOT_EXISTS + reference)))
                .map(OrderEntity::toOrder);
    }

    @Override
    public Mono<Order> create(Order order) {
        OrderEntity orderEntity = new OrderEntity(order, null);
        return Flux.fromStream(order.getOrderLines() == null ?
                Stream.empty() :
                order.getOrderLines().stream())
                .flatMap(orderLine -> {
                    OrderLineEntity orderLineEntity = new OrderLineEntity(orderLine, null);
                    return this.articleReactive.findByBarcode(orderLine.getArticleBarcode())
                            .switchIfEmpty(Mono.error(new NotFoundException("Non existent barcode: " + orderLine.getArticleBarcode()))).
                                    map(articleEntity -> {
                                        orderLineEntity.setArticleEntity(articleEntity);
                                        orderEntity.add(orderLineEntity);
                                        return orderLineEntity;
                                    });

                }).then(
                        this.assertReferenceNotExist(order.getReference()))
                .then(Mono.justOrEmpty(order.getProviderCompany()))
                .flatMap(providerCompany -> this.providerReactive.findByCompany(order.getProviderCompany()))
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Non existent company: " + order.getProviderCompany())
                )).map(providerEntity -> {
                    orderEntity.setProviderEntity(providerEntity);
                    return orderEntity;
                }).then(this.orderReactive.save(orderEntity))
                .map(OrderEntity::toOrder);

    }

    public Mono<Void> assertReferenceNotExist(String reference) {
        return this.orderReactive.findByReference(reference)
                .flatMap(offerEntity -> Mono.error(
                        new ConflictException("Offer Reference already exists : " + reference)
                ));
    }


    @Override
    public Mono<Order> update(String reference, Order order) {
        OrderEntity orderEntity = new OrderEntity(order, null);
        BeanUtils.copyProperties(order, orderEntity);
        orderEntity.getOrderLineEntities().clear();
        return Flux.fromStream(order.getOrderLines() == null ?
                Stream.empty() :
                order.getOrderLines().stream())
                .flatMap(orderLine -> {
                    OrderLineEntity orderLineEntity = new OrderLineEntity(orderLine, null);
                    return this.articleReactive.findByBarcode(orderLine.getArticleBarcode())
                            .switchIfEmpty(Mono.error(new NotFoundException("Non existent barcode: " + orderLine.getArticleBarcode()))).
                                    map(articleEntity -> {
                                        orderLineEntity.setArticleEntity(articleEntity);
                                        orderEntity.add(orderLineEntity);
                                        return orderLineEntity;
                                    });

                }).then(this.orderReactive.findByReference(reference)
                        .switchIfEmpty(Mono.error(new NotFoundException(ORDER_NOT_EXISTS + reference)))).then(Mono.justOrEmpty(order.getProviderCompany()))
                .flatMap(providerCompany -> this.providerReactive.findByCompany(order.getProviderCompany()))
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Non existent company: " + order.getProviderCompany())
                )).map(providerEntity -> {
                    orderEntity.setProviderEntity(providerEntity);
                    return orderEntity;
                }).then(this.orderReactive.save(orderEntity))
                .map(OrderEntity::toOrder);
    }

    @Override
    public Mono<Void> delete(String reference) {
        Mono<String> idOrder = this.orderReactive.findByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException(ORDER_NOT_EXISTS + reference)))
                .map(OrderEntity::getId);
        return this.orderReactive.deleteById(idOrder);
    }
}
