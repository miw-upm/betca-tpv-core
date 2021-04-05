package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleSizeFamily;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleSizeFamilyPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleSizeFamilyReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleSizeFamilyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ArticleSizeFamilyPersistenceMongodb implements ArticleSizeFamilyPersistence {

    private ArticleSizeFamilyReactive articleSizeFamilyReactive;
    private ProviderReactive providerReactive;

    @Autowired
    public void setArticleSizeReactive(ProviderReactive providerReactive, ArticleSizeFamilyReactive articleSizeFamilyReactive) {
        this.providerReactive = providerReactive;
        this.articleSizeFamilyReactive = articleSizeFamilyReactive;
    }

    @Override
    public Mono<ArticleSizeFamily> create(ArticleSizeFamily articleSizeFamily) {
//       return articleSizeFamilyReactive.save(new ArticleSizeFamilyEntity(articleSizeFamily))
//                .map(ArticleSizeFamilyEntity::toArticleSizeFamily);
        return Mono.justOrEmpty(articleSizeFamily.getProviderCompany())
                .flatMap(providerCompany -> this.providerReactive.findByCompany(articleSizeFamily.getProviderCompany())
                        .switchIfEmpty(Mono.error(
                                new NotFoundException("Non existent company: " + articleSizeFamily.getProviderCompany())
                        ))
                )
                .map(providerEntity -> new ArticleSizeFamilyEntity(articleSizeFamily, providerEntity))
                .switchIfEmpty(Mono.just(new ArticleSizeFamilyEntity(articleSizeFamily, null)))
                .flatMap(this.articleSizeFamilyReactive::save)
                .map(ArticleSizeFamilyEntity::toArticleSizeFamily);
    }
}
