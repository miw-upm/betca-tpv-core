package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import es.upm.miw.betca_tpv_core.domain.persistence.OrderLinePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.OrderLineReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderLineEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public class OrderLinePersistenceMongodb implements OrderLinePersistence {

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
        return this.orderLineReactive.findByBarcode(barcode)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent article with the barcode: " + barcode)))
                .flatMap(orderLineEntity -> {
                    BeanUtils.copyProperties(orderLine, orderLineEntity);
                    return this.orderLineReactive.save(orderLineEntity);
                }).map(OrderLineEntity::toOrderLine);
    }

    @Override
    public Mono<OrderLine> findByBarcode(String barcode) {
        return this.orderLineReactive.findByBarcode(barcode)
                .map(OrderLineEntity::toOrderLine);
    }
}
