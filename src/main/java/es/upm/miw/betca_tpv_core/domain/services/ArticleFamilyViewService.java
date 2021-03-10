package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyView;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleFamilyViewPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ArticleFamilyViewService {

    private ArticleFamilyViewPersistence articleFamilyViewPersistence;

    @Autowired
    public ArticleFamilyViewService(ArticleFamilyViewPersistence articleFamilyViewPersistence) {
        this.articleFamilyViewPersistence = articleFamilyViewPersistence;
    }

    public Flux<ArticleFamilyView> read(String reference) {
        return this.articleFamilyViewPersistence.readByReference(reference);
    }

}
