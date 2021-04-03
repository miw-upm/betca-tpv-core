package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import es.upm.miw.betca_tpv_core.domain.persistence.OrderLinePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderLineService {

    private OrderLinePersistence orderLinePersistence;

    @Autowired
    public OrderLineService(OrderLinePersistence orderLinePersistence) {
        this.orderLinePersistence = orderLinePersistence;
    }

    public Mono<OrderLine> readById(String id) {
        return this.orderLinePersistence.findById(id);
    }

    public Mono<OrderLine> create(OrderLine orderLine) {
        return this.orderLinePersistence.create(orderLine);
    }

}
