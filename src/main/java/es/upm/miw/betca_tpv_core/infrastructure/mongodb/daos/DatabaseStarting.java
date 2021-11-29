package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.domain.model.Tax;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.ArticleDao;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.CashierDao;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.ProviderDao;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CashierEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ProviderEntity;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.math.BigDecimal.ZERO;

@Repository
public class DatabaseStarting {
    private static final String VARIOUS_CODE = "1";
    private static final String VARIOUS_NAME = "Various";
    private static final String VARIOUS_PHONE = "000000000";

    private final ArticleDao articleDao;
    private final ProviderDao providerDao;
    private final CashierDao cashierDao;

    @Autowired
    public DatabaseStarting(ArticleDao articleDao, ProviderDao providerDao, CashierDao cashierDao) {
        this.articleDao = articleDao;
        this.providerDao = providerDao;
        this.cashierDao = cashierDao;
        this.initialize();
    }

    void initialize() {
        if (this.articleDao.findByBarcode(VARIOUS_CODE).isEmpty()) {
            ProviderEntity provider = this.providerDao.save(ProviderEntity.builder().company(VARIOUS_NAME)
                    .nif(VARIOUS_NAME).phone(VARIOUS_PHONE).active(true).build());
            this.articleDao.save(ArticleEntity.builder().barcode(VARIOUS_CODE).reference(VARIOUS_NAME)
                    .description(VARIOUS_NAME).retailPrice(new BigDecimal("100.00")).stock(1000)
                    .providerEntity(provider).registrationDate(LocalDateTime.now()).tax(Tax.GENERAL)
                    .discontinued(false).build());
            LogManager.getLogger(this.getClass()).warn("------- Create Article Various -----------");
        }

        if (this.cashierDao.findFirstByOrderByOpeningDateDesc().isEmpty()) {
            this.cashierDao.save(CashierEntity.builder().initialCash(ZERO).cashSales(ZERO)
                    .cardSales(ZERO).usedVouchers(ZERO).deposit(ZERO).withdrawal(ZERO).lostCash(ZERO).lostCard(ZERO)
                    .finalCash(ZERO).comment("Initial").openingDate(LocalDateTime.now()).closureDate(LocalDateTime.now())
                    .build());
            LogManager.getLogger(this.getClass()).warn("------- Create cashierClosure -----------");
        }
    }

}
