package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Provider;
import es.upm.miw.betca_tpv_core.domain.services.ProviderService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ProviderCompanyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(ProviderResource.PROVIDERS)
public class ProviderResource {
    public static final String PROVIDERS = "/providers";

    public static final String COMPANY_ID = "/{company}";
    public static final String COMPANY = "/company";
    public static final String SEARCH = "/search";

    private final ProviderService providerService;

    @Autowired
    public ProviderResource(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono< Provider > create(@Valid @RequestBody Provider provider) {
        provider.doDefault();
        return this.providerService.create(provider);
    }

    @GetMapping(COMPANY_ID)
    public Mono< Provider > read(@PathVariable String company) {
        return this.providerService.read(company);
    }

    @PutMapping(COMPANY_ID)
    public Mono< Provider > update(@PathVariable String company, @Valid @RequestBody Provider provider) {
        provider.doDefault();
        return this.providerService.update(company, provider);
    }

    @GetMapping(COMPANY)
    public Mono< ProviderCompanyDto > findByCompanyAndActiveIsTrueNullSave(@RequestParam(required = false) String company) {
        return this.providerService.findByCompanyAndActiveIsTrueNullSave(company)
                .collectList()
                .map(ProviderCompanyDto::new);
    }

    @GetMapping(SEARCH)
    public Flux< Provider > findByCompanyAndPhoneAndNoteNullSafe(
            @RequestParam(required = false) String company, @RequestParam(required = false) String phone,
            @RequestParam(required = false) String note) {
        return this.providerService.findByCompanyAndPhoneAndNoteNullSafe(company, phone, note)
                .map(Provider::ofCompanyPhoneNote);
    }

}
