package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Provider;
import es.upm.miw.betca_tpv_core.domain.persistence.ProviderPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ProviderEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProviderPersistenceMongodb implements ProviderPersistence {

    private final ProviderReactive providerReactive;

    @Autowired
    public ProviderPersistenceMongodb(ProviderReactive providerReactive) {
        this.providerReactive = providerReactive;
    }

    @Override
    public Mono< Provider > create(Provider provider) {
        return this.assertCompanyAndNifNotExist(provider.getCompany(), provider.getNif())
                .then(this.providerReactive.save(new ProviderEntity(provider)))
                .map(ProviderEntity::toProvider);
    }

    @Override
    public Mono< Provider > readByCompany(String company) {
        return this.providerReactive.findByCompany(company)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent company: " + company)))
                .map(ProviderEntity::toProvider);
    }

    @Override
    public Mono< Provider > update(String company, Provider provider) {
        return this.providerReactive.findByCompany(company)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent company: " + company)))
                .flatMap(providerEntity -> {
                    BeanUtils.copyProperties(provider, providerEntity);
                    return this.providerReactive.save(providerEntity);
                })
                .map(ProviderEntity::toProvider);
    }

    @Override
    public Flux< String > findByCompanyAndActiveIsTrueNullSave(String company) {
        return this.providerReactive.findByCompanyAndActiveIsTrueNullSave(company)
                .map(ProviderEntity::getCompany);
    }

    @Override
    public Flux< Provider > findByCompanyAndPhoneAndNoteNullSafe(String company, String phone, String note) {
        return this.providerReactive.findByCompanyAndPhoneAndNoteNullSafe(company, phone, note)
                .map(ProviderEntity::toProvider);
    }

    private Mono< Void > assertCompanyAndNifNotExist(String company, String nif) {
        return this.providerReactive.findByCompany(company)
                .mergeWith(this.providerReactive.findByNif(nif))
                .flatMap(provider -> Mono.error(new ConflictException
                        ("Existing already company or nif : " + company + "," + nif)
                ))
                .then();

    }
}
