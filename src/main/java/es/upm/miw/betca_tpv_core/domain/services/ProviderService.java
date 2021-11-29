package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Provider;
import es.upm.miw.betca_tpv_core.domain.persistence.ProviderPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProviderService {

    private final ProviderPersistence providerPersistence;

    @Autowired
    public ProviderService(ProviderPersistence providerPersistence) {
        this.providerPersistence = providerPersistence;
    }

    public Mono< Provider > create(Provider provider) {
        return this.providerPersistence.create(provider);
    }

    public Mono< Provider > read(String company) {
        return this.providerPersistence.readByCompany(company);
    }

    public Mono< Provider > update(String company, Provider provider) {
        return this.providerPersistence.update(company, provider);
    }

    public Flux< String > findByCompanyAndActiveIsTrueNullSave(String company) {
        return this.providerPersistence.findByCompanyAndActiveIsTrueNullSave(company);
    }

    public Flux< Provider > findByCompanyAndPhoneAndNoteNullSafe(String company, String phone, String note) {
        return this.providerPersistence.findByCompanyAndPhoneAndNoteNullSafe(company, phone, note);
    }
}
