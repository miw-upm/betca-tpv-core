package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.services.OrderService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Rest
@RequestMapping(OrderResource.ORDERS)
public class OrderResource {

    public static final String ORDERS = "/orders";
    public static final String SEARCH = "/search";
    public static final String REFERENCE_ID = "/{reference}";

    private final OrderService orderService;

    @Autowired
    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Order> create(@Valid @RequestBody Order order) {
        order.doDefault();
        return this.orderService.create(order);
    }

    @GetMapping(SEARCH)
    public Flux<Order> findByReferenceAndDescriptionAndCompanyAndOpeningDateAndClosingDateNullSafe(
            @RequestParam(required = false) String reference, @RequestParam(required = false) String description, @
                    RequestParam(required = false) String company, @RequestParam(required = false) LocalDateTime openingDate,
            @RequestParam(required = false) LocalDateTime closingDate) {
        return this.orderService.findByReferenceAndDescriptionAndCompanyAndOpeningDateAndClosingDateNullSafe(
                        reference, description, company, openingDate, closingDate)
                .map(Order::ofReferenceDescriptionCompanyOpeningDateClosingDate);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(REFERENCE_ID)
    public Mono<Order> read(@PathVariable String reference) {
        return this.orderService.read(reference);
    }

    @PutMapping(REFERENCE_ID)
    public Mono<Order> update(@PathVariable String reference, @Valid @RequestBody Order order) {
        return this.orderService.update(reference, order);
    }

    @DeleteMapping(REFERENCE_ID)
    public Mono<Void> delete(@PathVariable String reference) {
        return this.orderService.delete(reference);
    }
}
