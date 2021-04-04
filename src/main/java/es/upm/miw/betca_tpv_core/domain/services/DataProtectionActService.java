package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.persistence.RgpdPersistence;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfAgreementBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DataProtectionActService {

    private RgpdPersistence rgpdPersistence;

    @Autowired
    public DataProtectionActService(RgpdPersistence rgpdPersistence) {
        this.rgpdPersistence = rgpdPersistence;
    }

    public Mono<Rgpd> read(String mobile) {
        return this.rgpdPersistence.readByMobile(mobile);
    }

    public Mono<Rgpd> create(Rgpd rgpd) {
        return this.rgpdPersistence.create(rgpd);
    }

    public Mono<Rgpd> update(String mobile, Rgpd rgpd) {
        return this.rgpdPersistence.update(mobile, rgpd);
    }

    public Mono<byte[]> createAgreement(Rgpd rgpd) {
        return Mono.just(new PdfAgreementBuilder().generateAgreement(rgpd));
    }
}
