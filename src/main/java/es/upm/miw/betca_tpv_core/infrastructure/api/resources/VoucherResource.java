package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.services.VoucherService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(VoucherResource.VOUCHERS)
public class VoucherResource {
    public static final String VOUCHERS = "/vouchers";
    public static final String SEARCH_ID = "/{reference}";

    private final VoucherService voucherService;

    public VoucherResource(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public Flux< Voucher > readAll() {
        return this.voucherService.readAll();
    }

    @GetMapping(SEARCH_ID)
    public Mono<Voucher> readByReference(@PathVariable String reference) {
        return this.voucherService.readByReference(reference);
    }

    @PostMapping
    public Mono<Voucher> create(@RequestBody Voucher voucher) {
        return this.voucherService.create(voucher);
    }
}
