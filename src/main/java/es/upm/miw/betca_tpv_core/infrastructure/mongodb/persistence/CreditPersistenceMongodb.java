package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.domain.persistence.CreditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.TicketUnpaidDto;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CreditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CreditSaleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.List;

@Repository
public class CreditPersistenceMongodb implements CreditPersistence {
    private CreditReactive creditReactive;
    private CreditSaleReactive creditSaleReactive;
    private TicketReactive ticketReactive;

    @Autowired
    public CreditPersistenceMongodb(CreditReactive creditReactive, CreditSaleReactive creditSaleReactive, TicketReactive ticketReactive) {
        this.creditReactive = creditReactive;
        this.creditSaleReactive = creditSaleReactive;
        this.ticketReactive = ticketReactive;
    }

    @Override
    public Mono<Credit> create(Credit credit) {
        return this.creditReactive.save(new CreditEntity(credit))
                .map(CreditEntity::toCredit);
    }

    @Override
    public Mono<Credit> findByUserReference(String userReference) {
        return this.creditReactive.findByUserReference(userReference)
                .map(CreditEntity::toCredit);
    }

    @Override
    public Mono<Credit> addCreditSale(String userRef, CreditSale creditSale) {
        Mono<CreditEntity> creditEntityMono = this.creditReactive.findByUserReference(userRef);

        return creditEntityMono
                .switchIfEmpty(Mono
                        .error(new NotFoundException("Non existent credit-line")))
                .flatMap(creditEntity -> {
                    BeanUtils.copyProperties(creditEntity, creditEntity);
                    return this.creditSaleReactive.findByReference(creditSale.getReference())
                            .switchIfEmpty(Mono.error(
                                    new NotFoundException("Non existent credit-sale"))
                            )
                            .map(creditSaleEntity -> {
                                creditEntity.getCreditSaleEntities().add(creditSaleEntity);
                                return creditEntity;
                            });
                })
                .flatMap(this.creditReactive::save)
                .map(CreditEntity::toCredit);
    }

    @Override
    public Mono<List<TicketUnpaidDto>> findUnpaidTicketsFromCreditLine(String userRef) {
        Mono<CreditEntity> creditEntityMono = this.creditReactive.findByUserReference(userRef);
        return creditEntityMono
                .map(creditEntity -> {
                    creditEntity.setCreditSaleEntities(creditEntity.getCreditSaleEntities().stream().filter(CreditSaleEntity -> !CreditSaleEntity.getPayed()).collect(Collectors.toList()));
                    return creditEntity;
                })
                .map(CreditEntity::getCreditSaleEntities)
                .map(creditSaleEntity -> {
                    List<TicketUnpaidDto> ticketList = new ArrayList<>();
                    creditSaleEntity.forEach(creditSaleEntity1 -> ticketList.add(creditSaleEntity1.getTicketEntity().toTicketUnpaidDto()));
                    return ticketList;
                });
    }

    @Override
    public Mono<Credit> payUnpaidTicketsFromCreditLine(String userRef, String cashOrCard) {
        Mono<CreditEntity> creditEntityMono = this.creditReactive.findByUserReference(userRef);
        return creditEntityMono
                .map(creditEntity -> {
                    List<CreditSaleEntity> creditSaleListWithoutPay = creditEntity.getCreditSaleEntities().stream().filter(CreditSaleEntity -> !CreditSaleEntity.getPayed()).collect(Collectors.toList());
                    creditSaleListWithoutPay.forEach(creditSaleEntity1 -> {
                        if (cashOrCard.equalsIgnoreCase("card")) {
                            creditSaleEntity1.getTicketEntity().setCard(creditSaleEntity1.getTicketEntity().toTicket().total());
                            creditSaleEntity1.getTicketEntity().setCash(new BigDecimal(0));
                        } else {
                            creditSaleEntity1.getTicketEntity().setCash(creditSaleEntity1.getTicketEntity().toTicket().total());
                            creditSaleEntity1.getTicketEntity().setCard(new BigDecimal(0));
                        }
                        Date date = new Date();
                        creditSaleEntity1.getTicketEntity().setNote("Payed by credit-line (" + date.toString() + ")");
                        this.ticketReactive.save(creditSaleEntity1.getTicketEntity());
                        creditSaleEntity1.setPayed(true);
                        this.creditSaleReactive.save(creditSaleEntity1);
                    });
                    return creditEntity;
                })
                .flatMap(this.creditReactive::save)
                .map(CreditEntity::toCredit);
    }

}
