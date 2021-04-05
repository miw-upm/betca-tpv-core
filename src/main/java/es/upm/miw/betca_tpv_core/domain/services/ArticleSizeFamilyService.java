package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.ArticleSizeFamily;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleSizeFamilyPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ArticleSizeFamilyService {

    private ArticleSizeFamilyPersistence articleSizeFamilyPersistence;

    @Autowired
    public void setArticleSizePersistence(ArticleSizeFamilyPersistence articleSizeFamilyPersistence) {
        this.articleSizeFamilyPersistence = articleSizeFamilyPersistence;
    }

    public Mono<ArticleSizeFamily> create(ArticleSizeFamily articleSizeFamily) {
        articleSizeFamily.setRegistrationDate(LocalDateTime.now());
        return this.articleSizeFamilyPersistence.create(articleSizeFamily);
    }
}
