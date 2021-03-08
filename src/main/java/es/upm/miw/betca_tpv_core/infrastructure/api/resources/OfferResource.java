package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.services.OfferService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.OfferCreationEditionDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.OfferListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(OfferResource.OFFERS)
public class OfferResource {

    public static final String OFFERS = "/offers";
    public static final String SEARCH = "/search";

    private OfferService offerService;

    @Autowired
    public OfferResource(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping(SEARCH)
    public Flux<OfferListDto> findByReferenceAndDescriptionNullSafe(
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String description) {
        return this.offerService.findByReferenceAndDescriptionNullSafe(reference, description)
                .map(OfferListDto::new);
    }

    @PostMapping(produces = {"application/json"})
    public Mono<OfferCreationEditionDto> create(@Valid @RequestBody Offer offer) {
        offer.doDefault();
        return this.offerService.create(offer)
                .map(OfferCreationEditionDto::new);
    }
}