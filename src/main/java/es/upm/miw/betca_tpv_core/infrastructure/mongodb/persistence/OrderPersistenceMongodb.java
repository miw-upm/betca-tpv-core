package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.persistence.OrderPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.OrderLineReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.OrderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderLineEntity;
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
    private OrderLineReactive orderLineReactive;
    private ProviderReactive providerReactive;
    private ArticleReactive articleReactive;

    @Autowired
    public OrderPersistenceMongodb(OrderReactive orderReactive, OrderLineReactive orderLineReactive, ProviderReactive providerReactive, ArticleReactive articleReactive) {
        this.orderReactive = orderReactive;
        this.orderLineReactive = orderLineReactive;
        this.providerReactive = providerReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Flux<Order> findByDescriptionAndCompanyAndOpeningDateBetweenAndNullSafe(String company, String description, LocalDateTime openingDate) {
        return this.orderReactive.findByDescriptionAndCompanyAndOpeningDateBetweenAndNullSafe(company, description, openingDate)
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

        return this.assertReferenceNotExist(order.getReference())
                .flatMap(providerCompany -> this.providerReactive.findByCompany(order.getProviderCompany())
                        .switchIfEmpty(Mono.error(
                                new NotFoundException("Non existent company: " + order.getProviderCompany())
                        ))
                )
                .map(providerEntity -> new OrderEntity(order, providerEntity))
                .flatMap(orderEntity -> {
                    return Flux.fromStream(orderEntity.getOrderLineEntities() == null ?
                            Stream.empty() :
                            orderEntity.getOrderLineEntities().stream())
                            .flatMap(orderLine -> {
                                return this.articleReactive.findByBarcode(orderLine.getArticleEntity().getBarcode())
                                        .switchIfEmpty(Mono.error(new NotFoundException("Non existent barcode: " + orderLine.getArticleEntity().getBarcode())))
                                        .map(articleEntity -> {
                                            OrderLineEntity orderLineEntity = new OrderLineEntity(orderLine.toOrderLine(), articleEntity);
                                            return orderLineEntity;
                                        });

                            }).then(this.orderReactive.save(orderEntity));
                }).map(OrderEntity::toOrder);

    }

    public Mono<Void> assertReferenceNotExist(String reference) {
        return this.orderReactive.findByReference(reference)
                .flatMap(offerEntity -> Mono.error(
                        new ConflictException("Offer Reference already exists : " + reference)
                ));
    }


    @Override
    public Mono<Order> update(String reference, Order order) {
        return this.orderReactive.findByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException(ORDER_NOT_EXISTS + reference)))
                .flatMap(providerCompany -> this.providerReactive.findByCompany(order.getProviderCompany())
                        .switchIfEmpty(Mono.error(
                                new NotFoundException("Non existent company: " + order.getProviderCompany())
                        ))
                )
                .map(providerEntity -> new OrderEntity(order, providerEntity))
                .flatMap(orderEntity -> {
                    return Flux.fromStream(orderEntity.getOrderLineEntities() == null ?
                            Stream.empty() :
                            orderEntity.getOrderLineEntities().stream())
                            .flatMap(orderLine -> {

                                return this.articleReactive.findByBarcode(orderLine.getArticleEntity().getBarcode())
                                        .switchIfEmpty(Mono.error(new NotFoundException("Non existent barcode: " + orderLine.getArticleEntity().getBarcode())))
                                        .map(articleEntity -> {
                                            OrderLineEntity orderLineEntity = new OrderLineEntity(orderLine.toOrderLine(), articleEntity);
                                            return orderLineEntity;
                                        });

                            }).then(this.orderReactive.save(orderEntity));
                }).map(OrderEntity::toOrder);
    }

    @Override
    public Mono<Void> delete(String reference) {
        Mono<String> idOrder = this.orderReactive.findByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException(ORDER_NOT_EXISTS + reference)))
                .map(OrderEntity::getId);
        return this.orderReactive.deleteById(idOrder);
    }

    private Mono<Void> assertBarcodeNotExist(String barcode) {
        return this.articleReactive.findByBarcode(barcode)
                .flatMap(articleEntity -> Mono.error(
                        new ConflictException("Article Barcode already exists : " + barcode)
                ));
    }

    private Mono<Void> assertCompanyNotExist(String providerCompany) {
        return this.providerReactive.findByCompany(providerCompany)
                .flatMap(provider -> Mono.error(new ConflictException
                        ("Existing already providerCompany : " + providerCompany)
                ))
                .then();

    }
}
