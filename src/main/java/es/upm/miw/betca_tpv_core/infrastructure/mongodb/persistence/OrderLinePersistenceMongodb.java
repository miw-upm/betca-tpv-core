package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import es.upm.miw.betca_tpv_core.domain.persistence.OrderLinePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.OrderLineReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderLineEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class OrderLinePersistenceMongodb implements OrderLinePersistence {

    private static final String ORDERLINE_NOT_EXISTS = "OrderLine does not exist: ";
    private OrderLineReactive orderLineReactive;
    private ArticleReactive articleReactive;

    @Autowired
    public OrderLinePersistenceMongodb(OrderLineReactive orderLineReactive, ArticleReactive articleReactive) {
        this.orderLineReactive = orderLineReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Mono<OrderLine> create(OrderLine orderLine) {
        return Mono.justOrEmpty(orderLine.getArticleBarcode())
                .flatMap(article -> this.articleReactive.findByBarcode(orderLine.getArticleBarcode())
                        .switchIfEmpty(Mono.error(
                                new NotFoundException("Non existent article with the barcode " + orderLine.getArticleBarcode())
                        ))
                )
                .map(articleEntity -> new OrderLineEntity(orderLine, articleEntity))
                .flatMap(this.orderLineReactive::save)
                .map(OrderLineEntity::toOrderLine);
    }

    @Override
    public Mono<OrderLine> update(String barcode, OrderLine orderLine) {
        return this.articleReactive.findByBarcode(barcode)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent article with the barcode: " + barcode)))
                .map(articleEntity -> new OrderLineEntity(orderLine, articleEntity))
                .flatMap(orderLineEntity -> {
                    BeanUtils.copyProperties(orderLine, orderLineEntity);
                    return this.orderLineReactive.save(orderLineEntity);
                }).map(OrderLineEntity::toOrderLine);
    }

    @Override
    public Mono<OrderLine> findById(String id) {
        return this.orderLineReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(ORDERLINE_NOT_EXISTS + id)))
                .map(OrderLineEntity::toOrderLine);
    }
}
