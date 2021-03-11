package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.StockManager;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;

@Service
public class StockManagerService {
    private ArticlePersistence articlePersistence;
    @Autowired
    public StockManagerService(ArticlePersistence articlePersistence) {
        this.articlePersistence = articlePersistence;
    }

    public Flux<StockManager> searchProductsByStock(Integer stock) {
        return this.articlePersistence.findByStockLessThan(stock)
                .map(StockManager::ofProductsByStock);

    }
}
