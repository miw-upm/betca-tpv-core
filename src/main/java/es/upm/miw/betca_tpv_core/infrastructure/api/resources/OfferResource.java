package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.services.OfferService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.OfferListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

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
}