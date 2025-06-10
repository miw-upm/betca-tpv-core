package es.upm.miw.betca_tpv_core.infrastructure.api.resources;


import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.services.CustomerDiscountService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.CustomerDiscountDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(CustomerDiscountResource.CUSTOMER_DISCOUNT)
public class CustomerDiscountResource {
    public static final String CUSTOMER_DISCOUNT = "/customer-discount";

    private final CustomerDiscountService customerDiscountService;

    @Autowired
    public CustomerDiscountResource(CustomerDiscountService customerDiscountService) {
        this.customerDiscountService = customerDiscountService;
    }

    @PostMapping
    public Mono<CustomerDiscount> createCustomerDiscount(@Valid @RequestBody CustomerDiscount customerDiscount) {
        return this.customerDiscountService.createCustomerDiscount(customerDiscount);
    }

    @GetMapping
    public Flux<CustomerDiscountDto> findAll() {
        return this.customerDiscountService.findAll()
                .map(CustomerDiscount::toDto);
    }

}
