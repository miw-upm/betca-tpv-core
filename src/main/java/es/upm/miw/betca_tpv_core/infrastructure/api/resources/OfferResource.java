package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.services.OfferService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(OfferResource.OFFERS)
public class OfferResource {

    public static final String OFFERS = "/offers";
    public static final String SEARCH = "/search";
    public static final String REFERENCE_ID = "/{reference}";
    public static final String PDF = "/pdf";

    private final OfferService offerService;

    @Autowired
    public OfferResource(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Offer> create(@Valid @RequestBody Offer offer) {
        offer.doDefault();
        return this.offerService.create(offer);
    }

    @GetMapping(SEARCH)
    public Flux<Offer> findByReferenceAndDescriptionNullSafe(
            @RequestParam(required = false) String reference, @RequestParam(required = false) String description) {
        return this.offerService.findByReferenceAndDescriptionNullSafe(reference, description);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(REFERENCE_ID)
    public Mono<Offer> read(@PathVariable String reference) {
        return this.offerService.read(reference);
    }

    @PutMapping(REFERENCE_ID)
    public Mono<Offer> update(@PathVariable String reference, @Valid @RequestBody Offer offer) {
        offer.doDefault();
        return this.offerService.update(reference, offer);
    }

    @GetMapping(value = REFERENCE_ID + PDF, produces = {"application/pdf", "application/json"})
    public Mono<byte[]> readPdf(@PathVariable String reference) {
        return this.offerService.readPdf(reference);
    }
}
