package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.services.CustomerDiscountService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

@Rest
@RequestMapping(CustomerDiscountResource.CUSTOMERS_DISCOUNTS)
public class CustomerDiscountResource {
    public static final String CUSTOMERS_DISCOUNTS = "/customer-discount";
    public static final String SEARCH = "/search";

    private CustomerDiscountService customerDiscountService;

    @Autowired
    public CustomerDiscountResource(CustomerDiscountService customerDiscountService) {
        this.customerDiscountService = customerDiscountService;
    }

/*    @GetMapping(SEARCH)
    public Flux< CustomerDiscount > findByUser(
            @RequestParam(required = false) String user
    ) {
        return this.customerDiscountService.findByUser(user);
    }*/

    @GetMapping(SEARCH)
    public Flux< CustomerDiscount > findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(
            @RequestParam(required = false) String note, @RequestParam(required = false) Double discount,
            @RequestParam(required = false) Double minimumPurchase, @RequestParam(required = false) String user
    ) {
        return this.customerDiscountService.findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(note, discount, minimumPurchase, user);
    }
}
