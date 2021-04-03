package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import es.upm.miw.betca_tpv_core.domain.services.OrderLineService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
@Rest
@RequestMapping(OrderLineResource.ORDER_LINES)
public class OrderLineResource {

    public static final String ORDER_LINES = "/orderLines";
    public static final String ID = "/{id}";

    private OrderLineService orderLineService;

    @Autowired
    public OrderLineResource(OrderLineService orderLineService) {
        this.orderLineService = orderLineService;
    }

    @GetMapping(ID)
    public Mono<OrderLine> readById(@PathVariable String id) {
        return this.orderLineService.readById(id);
    }
}