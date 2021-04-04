package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.services.OrderService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Rest
@RequestMapping(OrderResource.ORDERS)
public class OrderResource {

    public static final String ORDERS = "/orders";
    public static final String SEARCH = "/search";
    public static final String REFERENCE = "/{reference}";

    private OrderService orderService;

    @Autowired
    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(SEARCH)
    public Flux<OrderDto> readByDescriptionAndOpeningDateBetween(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate

    ) {
        return this.orderService.readByDescriptionAndOpeningDateBetween(description, LocalDateTime.parse(fromDate), LocalDateTime.parse(toDate))
                .map(OrderDto::new);
    }

    @GetMapping(REFERENCE)
    public Mono<Order> readByReference(@PathVariable String reference) {
        return this.orderService.readByReference(reference);
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Order> create(@Valid @RequestBody Order order) {
        order.doDefault();
        return this.orderService.create(order);
    }

    @PutMapping(REFERENCE)
    public Mono<Order> update(@PathVariable String reference, @Valid @RequestBody Order order) {
        return this.orderService.update(reference, order);
    }

    @DeleteMapping(REFERENCE)
    public Mono<Void> delete(@PathVariable String reference) {
        return this.orderService.delete(reference);
    }
}
