package es.upm.miw.betca_tpv_core.domain.persistence;


import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import reactor.core.publisher.Mono;

public interface OrderLinePersistence {

    Mono<OrderLine> findById(String id);

    Mono<OrderLine> create(OrderLine OrderLine);

    Mono<OrderLine> update(String barcode, OrderLine orderLine);
}
