package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.CustomerPoints;
import es.upm.miw.betca_tpv_core.domain.services.CustomerPointsService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(CustomerPointsResource.CUSTOMER_POINTS)
public class CustomerPointsResource {

    public static final String CUSTOMER_POINTS = "/customer-points";
    public static final String MOBILE = "/{mobile}";
    public static final String ADD_POINTS = "/add-points";
    public static final String USE_POINTS = "/use-points";

    private final CustomerPointsService customerPointsService;

    @Autowired
    public CustomerPointsResource(CustomerPointsService customerPointsService) {
        this.customerPointsService = customerPointsService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<CustomerPoints> createCustomerPoints(@Valid @RequestBody CustomerPoints customerPoints) {
        return this.customerPointsService.createCustomerPoints(customerPoints);
    }

    @GetMapping(MOBILE)
    public Mono<CustomerPoints> readCustomerPointsByMobile(@PathVariable String mobile) {
        return this.customerPointsService.readCustomerPointsByMobile(mobile);
    }

    @PutMapping(MOBILE)
    public Mono<CustomerPoints> updateCustomerPoints(@PathVariable String mobile, @Valid @RequestBody CustomerPoints customerPoints) {
        return this.customerPointsService.updateCustomerPoints(mobile, customerPoints);
    }

    @PostMapping(value = MOBILE + ADD_POINTS, produces = {"application/json"})
    public Mono<CustomerPoints> addCustomerPoints(@PathVariable String mobile, @RequestParam int points) {
        return this.customerPointsService.addCustomerPoints(mobile, points);
    }

    @PostMapping(value = MOBILE + USE_POINTS, produces = {"application/json"})
    public Mono<CustomerPoints> useCustomerPoints(@PathVariable String mobile, @RequestParam int points) {
        return this.customerPointsService.useCustomerPoints(mobile, points);
    }
}
