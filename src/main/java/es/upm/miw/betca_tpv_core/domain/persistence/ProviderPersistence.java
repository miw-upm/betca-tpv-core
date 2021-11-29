package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Provider;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProviderPersistence {

    Mono< Provider > create(Provider provider);

    Mono< Provider > readByCompany(String company);

    Mono< Provider > update(String company, Provider provider);

    Flux< String > findByCompanyAndActiveIsTrueNullSave(String company);

    Flux< Provider > findByCompanyAndPhoneAndNoteNullSafe(String company, String phone, String note);
}
