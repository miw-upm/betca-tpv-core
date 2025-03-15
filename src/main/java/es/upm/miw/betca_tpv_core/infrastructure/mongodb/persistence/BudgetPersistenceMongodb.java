package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.persistence.BudgetPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.BudgetReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.BudgetEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ShoppingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Repository
public class BudgetPersistenceMongodb implements BudgetPersistence {

    private final BudgetReactive budgetReactive;
    private final ArticleReactive articleReactive;

    @Autowired
    public BudgetPersistenceMongodb(BudgetReactive budgetReactive, ArticleReactive articleReactive) {
        this.budgetReactive = budgetReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Mono<Budget> create(Budget budget) {
        BudgetEntity budgetEntity = new BudgetEntity(budget);
        return Flux.fromStream(budget.getShoppingList().stream())
                .flatMap(shopping -> {
                    ShoppingEntity shoppingEntity = new ShoppingEntity(shopping);
                    return this.articleReactive.findByBarcode(shopping.getBarcode())
                            .switchIfEmpty(Mono.error(new NotFoundException("Article: " + shopping.getBarcode())))
                            .map(articleEntity -> {
                                shoppingEntity.setArticleEntity(articleEntity);
                                shoppingEntity.setDescription(articleEntity.getDescription());
                                return shoppingEntity;
                            });
                }).doOnNext(budgetEntity::add)
                .then(this.budgetReactive.save(budgetEntity))
                .map(BudgetEntity::toBudget);
    }

    @Override
    public Mono<Budget> readById(String id) {
        return this.budgetReactive.findById(id)
                .map(BudgetEntity::toBudget);
    }

    @Override
    public Mono<Budget> update(String id, Budget budget) {
        Mono<BudgetEntity> budgetEntityMono;
        if (!id.equals(budget.getId())) {
            budgetEntityMono = this.assertIdNotExist(budget.getId())
                    .then(this.budgetReactive.findById(id));
        } else {
            budgetEntityMono = this.budgetReactive.findById(id);
        }

        return budgetEntityMono
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent budget id: " + id)))
                .flatMap(budgetEntity -> {
                    budgetEntity.setShoppingEntityList(new ArrayList<>());
                    return Flux.fromStream(budget.getShoppingList().stream())
                            .flatMap(shopping -> {
                                ShoppingEntity shoppingEntity = new ShoppingEntity(shopping);
                                return this.articleReactive.findByBarcode(shopping.getBarcode())
                                        .switchIfEmpty(Mono.error(new NotFoundException("Article: " + shopping.getBarcode())))
                                        .map(articleEntity -> {
                                            shoppingEntity.setArticleEntity(articleEntity);
                                            shoppingEntity.setDescription(articleEntity.getDescription());
                                            return shoppingEntity;
                                        });
                            })
                            .doOnNext(budgetEntity::add) // Ensure add() modifies the list
                            .then(this.budgetReactive.save(budgetEntity))
                            .map(BudgetEntity::toBudget);
                });
    }


    private Mono<Void> assertIdNotExist(String id) {
        return this.budgetReactive.findById(id)
                .flatMap(budgetEntity -> Mono.error(new ConflictException("Budget already exists: " + id)));
    }


    @Override
    public Mono<Void> deleteById(String id) {
        return this.budgetReactive.deleteById(id);
    }

    @Override
    public Flux<Budget> findByReferenceLike(String reference) {
        return this.budgetReactive.findByReferenceLike(reference)
                .map(BudgetEntity::toBudget);
    }
}
