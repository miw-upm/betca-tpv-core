package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.StockManager;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
public class StockManagerService {
    private ArticlePersistence articlePersistence;
    private TicketPersistence ticketPersistence;

    @Autowired
    public StockManagerService(ArticlePersistence articlePersistence, TicketPersistence ticketPersistence) {
        this.articlePersistence = articlePersistence;
        this.ticketPersistence = ticketPersistence;
    }

    public Flux<StockManager> searchProductsByStock(Integer stock) {
        return this.articlePersistence.findByStockLessThan(stock)
                .map(StockManager::ofProductsByStock);

    }

    public Flux<StockManager> searchSoldProducts(LocalDateTime initial, LocalDateTime end) {
        Flux<Ticket> tickets = this.ticketPersistence.findByRangeRegistrationDate(initial, end);
        return Flux.concat(tickets.map(ticket -> articleOfShopping(ticket.getShoppingList().stream(), ticket.getCreationDate())));
    }

    private Flux<StockManager> articleOfShopping(Stream<Shopping> shoppingStream, LocalDateTime dateCreation) {
        return Flux.fromStream(shoppingStream
                .map(shopping -> StockManager.ofShopping(shopping, dateCreation)));
    }

    public Mono<StockManager> searchFutureStock(String barcode) {
        return this.articlePersistence.readByBarcode(barcode)
                .switchIfEmpty(Mono.error(new NotFoundException("Article : " + barcode)))
                .map(this::futureStock)
                .flatMap(stockManagerMono -> stockManagerMono);
    }

    private Mono<StockManager> futureStock(Article article) {
        // last week prevision
        LocalDateTime ini = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        return this.ticketPersistence.findByRangeRegistrationDate(ini, end)
                .map(ticket -> amountArticleSold(ticket.getShoppingList().stream(), article.getBarcode()))
                .reduce(0, Integer::sum)
                .map(amount -> StockManager.ofSoldStock(article, amount));
    }

    public int amountArticleSold(Stream<Shopping> shoppingStream, String barcode) {
        return shoppingStream
                .filter(shopping -> shopping.getBarcode().equals(barcode))
                .map(Shopping::getAmount)
                .reduce(0, Integer::sum);
    }
}
