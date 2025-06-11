    package es.upm.miw.betca_tpv_core.infrastructure.api.resources;


    import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
    import es.upm.miw.betca_tpv_core.domain.services.CustomerDiscountService;
    import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
    import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.CustomerDiscountDto;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    @Rest
    @RequestMapping(CustomerDiscountResource.CUSTOMER_DISCOUNT)
    public class CustomerDiscountResource {
        public static final String CUSTOMER_DISCOUNT = "/customer-discount";
        public static final String MOBILE = "/{userMobile}";

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

        @GetMapping(MOBILE)
        public Mono<CustomerDiscount> readByUserMobile(@PathVariable String userMobile){
            return this.customerDiscountService.readByUserMobile(userMobile);
        }

        @PutMapping(MOBILE)
        public Mono<CustomerDiscount> updateCustomerDiscount(@PathVariable String userMobile, @Valid @RequestBody CustomerDiscount customerDiscount){
            return this.customerDiscountService.updateCustomerDiscount(userMobile, customerDiscount);
        }

    }
