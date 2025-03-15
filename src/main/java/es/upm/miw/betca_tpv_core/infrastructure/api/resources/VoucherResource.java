package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.services.VoucherService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Rest
@RequestMapping(VoucherResource.VOUCHERS)
public class VoucherResource {
    public static final String VOUCHERS = "/vouchers";
    public static final String SEARCH = "/search";
    public static final String REFERENCE_ID = "/{reference}";
    public static final String PDF = "/pdf";

    private final VoucherService voucherService;

    @Autowired
    public VoucherResource(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

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

    @PutMapping(REFERENCE_ID)
    public Mono<Voucher> update(@PathVariable String reference, @Valid @RequestBody Voucher voucher) {
        return this.voucherService.update(reference, voucher);
    }

    /*@GetMapping(SEARCH)
    public Flux<Voucher> findByReferenceAndValueNullSafe(
            @RequestParam(required = false) String reference,
            @RequestParam(required = false)BigDecimal value) {
        return this.voucherService.findByReferenceAndValueNullSafe(reference, value);
    }*/

    @GetMapping(SEARCH)
    public Flux<Voucher> findByDateRangeAndConsumedNullSafe(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam Boolean consumed){
        return this.voucherService.findByDateRangeAndConsumedNullSafe(startDate, endDate, consumed);
    }

    @GetMapping(value = REFERENCE_ID + PDF, produces = {"application/pdf", "application/json"})
    public Mono<byte[]> readPdf(@PathVariable String reference) {
        return this.voucherService.readPdf(reference);
    }

}
