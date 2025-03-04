package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.services.VoucherService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Rest
@RequestMapping(VoucherResource.VOUCHERS)
public class VoucherResource {
    public static final String VOUCHERS = "/vouchers";
    public static final String SEARCH = "/search";
    public static final String REFERENCE_ID = "/{reference}";

    private final VoucherService voucherService;

    @Autowired
    public VoucherResource(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(produces = {"application/json"})
    public Mono<Voucher> create(@Valid @RequestBody Voucher voucher) {
        voucher.doDefault();
        return this.voucherService.create(voucher);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(REFERENCE_ID)
    public Mono<Voucher> read(@PathVariable String reference) {
        return this.voucherService.read(reference);
    }

    @GetMapping(SEARCH)
    public Flux<Voucher> findByReferenceAndValueNullSafe(
            @RequestParam(required = false) String reference,
            @RequestParam(required = false)BigDecimal value) {
        return this.voucherService.findByReferenceAndValueNullSafe(reference, value);
    }

}
