package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.services.CustomerDiscountService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(CustomerDiscountResource.CUSTOMERS_DISCOUNTS)
public class CustomerDiscountResource {
    public static final String CUSTOMERS_DISCOUNTS = "/customer-discount";
    public static final String SEARCH = "/search";
    public static final String CUSTOMER_DISCOUNT_ID = "/{id}";

    private CustomerDiscountService customerDiscountService;

    @Autowired
    public CustomerDiscountResource(CustomerDiscountService customerDiscountService) {
        this.customerDiscountService = customerDiscountService;
    }

    @GetMapping(SEARCH)
    public Flux< CustomerDiscount > findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(
            @RequestParam(required = false) String note, @RequestParam(required = false) Double discount,
            @RequestParam(required = false) Double minimumPurchase, @RequestParam(required = false) String user
    ) {
        return this.customerDiscountService.findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(note, discount, minimumPurchase, user);
    }

    @PostMapping(produces = {"application/json"})
    public Mono<CustomerDiscount> create(@Valid @RequestBody CustomerDiscount customerDiscount){
        return this.customerDiscountService.create(customerDiscount);
    }

    @PutMapping(CUSTOMER_DISCOUNT_ID)
    public Mono<CustomerDiscount> update(@PathVariable String id, @Valid @RequestBody CustomerDiscount customerDiscount) {
        return this.customerDiscountService.update(id, customerDiscount);
    }
}
