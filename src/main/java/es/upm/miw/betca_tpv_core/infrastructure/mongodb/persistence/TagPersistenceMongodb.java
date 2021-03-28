package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Tag;
import es.upm.miw.betca_tpv_core.domain.persistence.TagPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TagReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TagEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Repository
public class TagPersistenceMongodb implements TagPersistence {

    private final ArticleReactive articleReactive;
    private final TagReactive tagReactive;

    @Autowired
    public TagPersistenceMongodb(TagReactive tagReactive, ArticleReactive articleReactive) {
        this.tagReactive = tagReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Mono<Tag> create(Tag tag) {
        TagEntity tagEntity = new TagEntity(tag);
        return Flux.fromStream(tag.getArticleList().stream())
                .flatMap(article -> this.articleReactive.findByBarcode(article.getBarcode())
                        .switchIfEmpty(Mono.error(new NotFoundException("Non existent article: " + article.getBarcode())))
                ).doOnNext(tagEntity::add)
                .then(this.assertNameNotExist(tag.getName()))
                .then(this.tagReactive.save(tagEntity))
                .map(TagEntity::toTag);
    }

    private Mono<Void> assertNameNotExist(String name) {
        return this.tagReactive.findByName(name)
                .flatMap(tagEntity -> Mono.error(
                        new ConflictException("Tag Name already exists : " + name)
                ));
    }

    @Override
    public Mono<Tag> readByName(String name) {
        return this.tagReactive.findByName(name)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent tag name: " + name)))
                .map(TagEntity::toTag);
    }

    @Override
    public Mono<Tag> update(String name, Tag tag) {
        Mono<TagEntity> tagEntityMono;
        if (!name.equals(tag.getName())) {
            tagEntityMono = this.assertNameNotExist(tag.getName())
                    .then(this.tagReactive.findByName(name));
        } else {
            tagEntityMono = this.tagReactive.findByName(name);
        }
        return tagEntityMono
                .switchIfEmpty(Mono
                        .error(new NotFoundException("Non existent tag name: " + name)))
                .flatMap(tagEntity ->
                {
                    BeanUtils.copyProperties(tag, tagEntity);
                    tagEntity.setArticleEntityList(new ArrayList<ArticleEntity>());
                    return Flux.fromStream(tag.getArticleList().stream())
                            .flatMap(article -> this.articleReactive.findByBarcode(article.getBarcode())
                                    .switchIfEmpty(Mono.error(new NotFoundException("Non existent article: " + article.getBarcode()))))
                            .doOnNext(tagEntity::add)
                            .then(Mono.just(tagEntity));
                })
                .flatMap(this.tagReactive::save)
                .map(TagEntity::toTag);
    }

    @Override
    public Flux<Tag> findBNameAndGroupAndDescriptionNullSafe(String name, String group, String description) {
        return this.tagReactive.findBNameAndGroupAndDescriptionNullSafe(
                name, group, description)
                .map(TagEntity::toTag);
    }
}
