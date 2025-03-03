package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.*;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.*;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.math.BigDecimal.ZERO;

@Log4j2
@Service // @Profile("dev")
public class DatabaseSeederDev {
    private final ArticleDao articleDao;
    private final ProviderDao providerDao;
    private final ArticlesTreeDao articlesTreeDao;
    private final TicketDao ticketDao;
    private final CashierDao cashierDao;
    private final RgpdDao rgpdDao;
    private final OfferDao offerDao;
    private final CustomerPointsDao customerPointsDao;
    private final InvoiceDao invoiceDao;
    private final BudgetDao budgetDao;
    private final VoucherDao voucherDao;

    private final DatabaseStarting databaseStarting;

    @Autowired
    public DatabaseSeederDev(
            ArticleDao articleDao,
            ProviderDao providerDao,
            ArticlesTreeDao articlesTreeDao,
            TicketDao ticketDao,
            CashierDao cashierDao,
            DatabaseStarting databaseStarting,
            RgpdDao rgpdDao,
            OfferDao offerDao,
            CustomerPointsDao customerPointsDao,
            InvoiceDao invoiceDao,
            BudgetDao budgetDao,
            VoucherDao voucherDao
    ) {
        this.articleDao = articleDao;
        this.providerDao = providerDao;
        this.articlesTreeDao = articlesTreeDao;
        this.ticketDao = ticketDao;
        this.cashierDao = cashierDao;
        this.databaseStarting = databaseStarting;
        this.rgpdDao = rgpdDao;
        this.offerDao = offerDao;
        this.customerPointsDao = customerPointsDao;
        this.invoiceDao = invoiceDao;
        this.budgetDao = budgetDao;
        this.voucherDao = voucherDao;

        this.deleteAllAndInitializeAndSeedDataBase();
    }

    public void deleteAllAndInitializeAndSeedDataBase() {
        this.deleteAllAndInitialize();
        this.seedDataBaseJava();
    }

    private void deleteAllAndInitialize() {
        this.ticketDao.deleteAll();
        this.articleDao.deleteAll();
        this.providerDao.deleteAll();
        this.cashierDao.deleteAll();
        this.offerDao.deleteAll();
        this.customerPointsDao.deleteAll();
        this.invoiceDao.deleteAll();
        this.budgetDao.deleteAll();
        this.voucherDao.deleteAll();

        log.warn("------- Delete All -----------");
        this.databaseStarting.initialize();
    }

    private void seedDataBaseJava() {
        log.warn("------- Initial Load from JAVA -----------");
        ProviderEntity[] providers = {
                ProviderEntity.builder().company("pro1").nif("12345678b").phone("9166666601")
                        .address("C/TPV-pro, 1").email("p1@gmail.com").note("p1").active(true).build(),
                ProviderEntity.builder().company("pro2").nif("12345678z").phone("9166666602")
                        .address("C/TPV-pro, 2").email("p2@gmail.com").active(false).build(),
                ProviderEntity.builder().company("pro3").nif("12345678e").phone("9166666603")
                        .address("C/TPV-pro, 3").email("p2@gmail.com").note("p3").active(true).build(),
                ProviderEntity.builder().company("pro4").nif("12345678h").phone("9166666604")
                        .address("C/TPV-pro, 4").email("p3@gmail.com").note("p4").active(true).build(),
        };
        this.providerDao.saveAll(List.of(providers));
        log.warn("        ------- providers");
        ArticleEntity[] articles = {
                ArticleEntity.builder().barcode("8400000000017").reference("zz-falda-T2").description("Zarzuela - Falda T2")
                        .retailPrice(new BigDecimal("20")).stock(10).providerEntity(providers[0])
                        .registrationDate(LocalDateTime.now()).discontinued(false).build(),
                ArticleEntity.builder().barcode("8400000000024").reference("zz-falda-T4").description("Zarzuela - Falda T4")
                        .retailPrice(new BigDecimal("27.8")).stock(5).providerEntity(providers[0])
                        .registrationDate(LocalDateTime.now()).discontinued(false).build(),
                ArticleEntity.builder().barcode("8400000000031").reference("ref-a3").description("descrip-a3")
                        .retailPrice(new BigDecimal("10.12")).stock(8).tax(Tax.FREE).providerEntity(providers[0])
                        .registrationDate(LocalDateTime.now()).discontinued(false).build(),
                ArticleEntity.builder().barcode("8400000000048").reference("ref-a4").description("descrip-a4")
                        .retailPrice(new BigDecimal("0.23")).stock(1).tax(Tax.REDUCED).providerEntity(providers[0])
                        .registrationDate(LocalDateTime.now()).discontinued(false).build(),
                ArticleEntity.builder().barcode("8400000000055").reference("ref-a5").description("descrip-a5")
                        .retailPrice(new BigDecimal("0.23")).stock(0).tax(Tax.SUPER_REDUCED).providerEntity(providers[0])
                        .registrationDate(LocalDateTime.now()).discontinued(false).build(),
                ArticleEntity.builder().barcode("8400000000062").reference("ref-a6").description("descrip-a6")
                        .retailPrice(new BigDecimal("0.01")).stock(0).providerEntity(providers[1])
                        .registrationDate(LocalDateTime.now()).discontinued(true).build(),
                ArticleEntity.builder().barcode("8400000000079").reference("zz-polo-T2").description("Zarzuela - Polo T2")
                        .retailPrice(new BigDecimal("16")).stock(10).providerEntity(providers[0])
                        .registrationDate(LocalDateTime.now()).discontinued(false).build(),
                ArticleEntity.builder().barcode("8400000000086").reference("zz-polo-T4").description("Zarzuela - Polo T4")
                        .retailPrice(new BigDecimal("17.8")).stock(5).providerEntity(providers[0])
                        .registrationDate(LocalDateTime.now()).discontinued(false).build(),
                ArticleEntity.builder().barcode("8400000000093").description("WARNING!!!. This article can be modificated")
                        .retailPrice(new BigDecimal("17.7")).stock(10).registrationDate(LocalDateTime.now())
                        .discontinued(false).build(),
                ArticleEntity.builder().barcode("8400000000100").reference("without provider").description("without provider")
                        .retailPrice(new BigDecimal("0.12")).stock(5).registrationDate(LocalDateTime.now())
                        .discontinued(false).build(),
        };
        this.articleDao.saveAll(List.of(articles));
        log.warn("        ------- articles");
        ArticlesTreeEntity[] singleList = {
                new SingleArticleEntity(articles[0]),
                new SingleArticleEntity(articles[1]),
                new SingleArticleEntity(articles[2]),
                new SingleArticleEntity(articles[3]),
                new SingleArticleEntity(articles[4]),
                new SingleArticleEntity(articles[5]),
                new SingleArticleEntity(articles[6]),
                new SingleArticleEntity(articles[7]),
        };
        this.articlesTreeDao.saveAll(Arrays.asList(singleList));
        ArticlesTreeEntity[] sizeCompositeList = {
                new CompositeArticleEntity("Zz Falda", TreeType.SIZES, "Zarzuela - Falda"),
                new CompositeArticleEntity("Zz Polo", TreeType.SIZES, "Zarzuela - Polo")
        };
        sizeCompositeList[0].add(singleList[0]);
        sizeCompositeList[0].add(singleList[1]);
        sizeCompositeList[1].add(singleList[6]);
        sizeCompositeList[1].add(singleList[7]);
        this.articlesTreeDao.saveAll(Arrays.asList(sizeCompositeList));
        ArticlesTreeEntity[] compositeList = {
                new CompositeArticleEntity("root", TreeType.ARTICLES, "root"),
                new CompositeArticleEntity("Zz", TreeType.ARTICLES, "Zarzuela"),
                new CompositeArticleEntity("varios", TreeType.ARTICLES, "varios"),
        };
        this.articlesTreeDao.saveAll(Arrays.asList(compositeList));
        compositeList[0].add(compositeList[1]);
        compositeList[0].add(compositeList[2]);
        compositeList[0].add(singleList[2]);
        compositeList[1].add(sizeCompositeList[0]);
        compositeList[1].add(sizeCompositeList[1]);
        compositeList[1].add(singleList[3]);
        compositeList[2].add(singleList[4]);
        compositeList[2].add(singleList[5]);
        this.articlesTreeDao.saveAll(Arrays.asList(compositeList));
        log.warn("        ------- articles tree");
        ShoppingEntity[] shoppingList = {
                new ShoppingEntity(articles[0], articles[0].getDescription(), articles[0].getRetailPrice(),
                        1, ZERO, ShoppingState.COMMITTED),
                new ShoppingEntity(articles[1], articles[1].getDescription(), articles[1].getRetailPrice(),
                        3, new BigDecimal("50"), ShoppingState.NOT_COMMITTED),
                new ShoppingEntity(articles[0], articles[0].getDescription(), articles[0].getRetailPrice(),
                        1, BigDecimal.TEN, ShoppingState.COMMITTED),
                new ShoppingEntity(articles[2], articles[2].getDescription(), articles[2].getRetailPrice(),
                        3, new BigDecimal("50"), ShoppingState.COMMITTED),
                new ShoppingEntity(articles[4], articles[4].getDescription(), articles[4].getRetailPrice(),
                        3, ZERO, ShoppingState.COMMITTED),
                new ShoppingEntity(articles[5], articles[5].getDescription(), articles[5].getRetailPrice(),
                        2, ZERO, ShoppingState.COMMITTED),
        };
        LocalDateTime date = LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10);
        TicketEntity[] tickets = {
                new TicketEntity("5fa45e863d6e834d642689ac", "nUs81zZ4R_iuoq0_zCRm6A",
                        List.of(shoppingList[0], shoppingList[1]), date, new BigDecimal("20.0"),
                        ZERO, ZERO, "note", "666666000", ZERO),
                new TicketEntity("5fa45f6f3a61083cb241289c", "lpiHOlsoS_WkkEyWeFNJtg",
                        List.of(shoppingList[2]), date, new BigDecimal("25.0"),
                        ZERO, ZERO, "note", "666666004", ZERO),
                new TicketEntity("5fa4603b7513a164c99677ac", "FGhfvfMORj6iKmzp5aERAA",
                        List.of(shoppingList[3], shoppingList[4]), date, new BigDecimal("18.0"),
                        ZERO, ZERO, "note", "666666004", ZERO),
                new TicketEntity("5fa4608f4928694ef5980e4c", "WB9-e8xQT4ejb74r1vLrCw",
                        List.of(shoppingList[5]), date, new BigDecimal("20"),
                        new BigDecimal("5"), ZERO, "note", "666666005", ZERO),
                new TicketEntity("5fa4608f4928694ef5980e4d", "WB9-e8xQT4ejb74r1vLrCw",
                        List.of(shoppingList[5]), date, new BigDecimal("20"),
                        new BigDecimal("5"), ZERO, "note", "666666005", ZERO),
        };
        this.ticketDao.saveAll(Arrays.asList(tickets));
        log.warn("        ------- tickets");

        byte[] SampleAgreementEncoded = "SampleAgreement".getBytes();
        byte[] newAgreementEncoded = "NewAgreement".getBytes();
        RgpdEntity[] rgpdList = {
                RgpdEntity.builder().agreement(SampleAgreementEncoded).rgpdType(RgpdType.BASIC).userMobile("600000001").userName("Alex").build(),
                RgpdEntity.builder().agreement(newAgreementEncoded).rgpdType(RgpdType.ADVANCED).userMobile("600000002").userName("John").build(),
                RgpdEntity.builder().agreement(SampleAgreementEncoded).rgpdType(RgpdType.ADVANCED).userMobile("600000003").userName("Stephen").build(),
                RgpdEntity.builder().agreement(newAgreementEncoded).rgpdType(RgpdType.MEDIUM).userMobile("600000004").userName("Darius").build()
        };
        this.rgpdDao.saveAll(Arrays.asList(rgpdList));
        log.warn("        ------- data-protection-rgpd");
        
        LocalDateTime dateOfferCreation = LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10);
        LocalDateTime dateOfferExpiry = LocalDateTime.of(2020, Month.JANUARY, 12, 10, 10);
        OfferEntity[] offers = {
                OfferEntity.builder().reference("SrIZGD09QayXFbeplhQi9A").description("Offer code 15% discount").discount(15)
                        .articleEntities(List.of(articles[0], articles[1])).creationDate(dateOfferCreation)
                        .expiryDate(dateOfferExpiry).build(),
                OfferEntity.builder().reference("R_hRxaoeQuyEDdZpmpboVg").description("Offer code 20% discount").discount(20)
                        .articleEntities(Collections.emptyList()).creationDate(dateOfferCreation)
                        .expiryDate(dateOfferExpiry).build(),
                OfferEntity.builder().reference("zbtBZtcRQJGCR4ULwslweg").description("Offer code 10% discount").discount(10)
                        .articleEntities(List.of(articles[4])).creationDate(dateOfferCreation)
                        .expiryDate(dateOfferExpiry).build(),
                OfferEntity.builder().reference("cjmJNO_2R8CVRq031FRKTQ").description("Offer code 5% discount").discount(5)
                        .articleEntities(List.of(articles[5], articles[7])).creationDate(dateOfferCreation)
                        .expiryDate(dateOfferExpiry).build(),
        };
        this.offerDao.saveAll(Arrays.asList(offers));
        log.warn("        ------- offers");

        this.customerPointsDao.saveAll(List.of(
                CustomerPointsEntity.builder().value(0).lastDate(LocalDateTime.now()).userMobileNumber("6").build(),
                CustomerPointsEntity.builder().value(100).lastDate(LocalDateTime.now()).userMobileNumber("66").build(),
                CustomerPointsEntity.builder().value(20).lastDate(LocalDateTime.now()).userMobileNumber("666666003").build(),
                CustomerPointsEntity.builder().value(50).lastDate(LocalDateTime.of(2024, Month.SEPTEMBER, 1, 0, 0)).userMobileNumber("666666004").build(),
                CustomerPointsEntity.builder().value(10).lastDate(LocalDateTime.of(2023, Month.JANUARY, 1, 0, 0)).userMobileNumber("666666005").build()
        ));

        log.warn("------- seeded customer points for users");

        InvoiceEntity[] invoice = {
                InvoiceEntity.builder().id("1").identity(20251).baseTax(new BigDecimal("14.5")).taxValue(new BigDecimal("34"))
                        .ticketId("5fa45e863d6e834d642689ac").userMobile("666666000").creationDate(LocalDateTime.now()).build(),
                InvoiceEntity.builder().id("2").identity(20252).baseTax(new BigDecimal("5.9")).taxValue(new BigDecimal("85"))
                        .ticketId("5fa4603b7513a164c99677ac").userMobile("666666004").creationDate(LocalDateTime.now()).build(),
                InvoiceEntity.builder().id("3").identity(20253).baseTax(new BigDecimal("27.1")).taxValue(new BigDecimal("20"))
                        .ticketId("5fa4608f4928694ef5980e4c").userMobile("666666003").creationDate(LocalDateTime.now()).build(),
                InvoiceEntity.builder().id("4").identity(20254).baseTax(new BigDecimal("16.3")).taxValue(new BigDecimal("15"))
                        .ticketId("5fa45f6f3a61083cb241289c").userMobile("666666002").creationDate(LocalDateTime.now()).build(),
        };
                this.invoiceDao.saveAll(Arrays.asList(invoice));
        LogManager.getLogger(this.getClass()).warn("        ------- invoices");

        LocalDateTime budgetCreationDate = LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10);
        BudgetEntity[] budgets = {
            BudgetEntity.builder().id("1").reference("1").creationDate(budgetCreationDate).shoppingEntityList(List.of(shoppingList[0], shoppingList[1])).build(),
            BudgetEntity.builder().id("2").reference("2").creationDate(budgetCreationDate).shoppingEntityList(List.of(shoppingList[0], shoppingList[1])).build()
        };


        this.budgetDao.saveAll(Arrays.asList(budgets));
        log.warn("        ------- budgets");

        LocalDateTime creationDate = LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10);
        VoucherEntity[] vouchers = {
                VoucherEntity.builder().reference("VOUCHER001").value(BigDecimal.valueOf(50)).creationDate(creationDate).dateOfUse(null).user(User.builder().mobile("635635635").build()).build(),
                VoucherEntity.builder().reference("VOUCHER002").value(BigDecimal.valueOf(30)).creationDate(creationDate).dateOfUse(creationDate.plusDays(10)).user(User.builder().mobile("635635635").build()).build(),
                VoucherEntity.builder().reference("VOUCHER003").value(BigDecimal.valueOf(100)).creationDate(creationDate).dateOfUse(null).user(User.builder().mobile("635635635").build()).build()
        };

        this.voucherDao.saveAll(Arrays.asList(vouchers));
        log.warn("        ------- vouchers");

        CashierEntity[] cashiers = {
                CashierEntity.builder().id("00010b300000000000000000").cardSales(BigDecimal.valueOf(100)).cashSales(BigDecimal.valueOf(1)).withdrawal(BigDecimal.valueOf(20)).deposit(BigDecimal.valueOf(20))
                        .initialCash(BigDecimal.valueOf(0)).finalCash(BigDecimal.valueOf(1)).comment("Demo cashier closure Jan 1970 (1)")
                        .openingDate(LocalDateTime.of(1970,1,1,8,0)).closureDate(LocalDateTime.of(1970,1,1,20,0)).build(),
                CashierEntity.builder().id("00025cb00000000000000000").cardSales(BigDecimal.valueOf(200)).cashSales(BigDecimal.valueOf(10)).withdrawal(BigDecimal.valueOf(9)).deposit(BigDecimal.valueOf(0))
                        .initialCash(BigDecimal.valueOf(1)).finalCash(BigDecimal.valueOf(2)).comment("Demo cashier closure Jan 1970 (2)")
                        .openingDate(LocalDateTime.of(1970,1,2,8,0)).closureDate(LocalDateTime.of(1970,1,2,20,0)).build(),
                CashierEntity.builder().id("002898300000000000000000").cardSales(BigDecimal.valueOf(20)).cashSales(BigDecimal.valueOf(100)).withdrawal(BigDecimal.valueOf(100)).deposit(BigDecimal.valueOf(3))
                        .initialCash(BigDecimal.valueOf(2)).finalCash(BigDecimal.valueOf(5)).comment("Demo cashier closure Jan 1970 (3)")
                        .openingDate(LocalDateTime.of(1970,1,31,8,0)).closureDate(LocalDateTime.of(1970,1,31,20,0)).build(),
                CashierEntity.builder().id("0029e9b00000000000000000").cardSales(BigDecimal.valueOf(25)).cashSales(BigDecimal.valueOf(25)).withdrawal(BigDecimal.valueOf(20)).deposit(BigDecimal.valueOf(3))
                        .initialCash(BigDecimal.valueOf(5)).finalCash(BigDecimal.valueOf(13)).comment("Demo cashier closure Feb 1970 (1)")
                        .openingDate(LocalDateTime.of(1970,2,1,8,0)).closureDate(LocalDateTime.of(1970,2,1,20,0)).build(),
                CashierEntity.builder().id("003c5eb00000000000000000").cardSales(BigDecimal.valueOf(100)).cashSales(BigDecimal.valueOf(27)).withdrawal(BigDecimal.valueOf(30)).deposit(BigDecimal.valueOf(0))
                        .initialCash(BigDecimal.valueOf(13)).finalCash(BigDecimal.valueOf(10)).comment("Demo cashier closure Feb 1970 (2)")
                        .openingDate(LocalDateTime.of(1970,2,15,8,0)).closureDate(LocalDateTime.of(1970,2,15,20,0)).build(),
                CashierEntity.builder().id("00cea7200000000000000000").cardSales(BigDecimal.valueOf(0)).cashSales(BigDecimal.valueOf(40)).withdrawal(BigDecimal.valueOf(25)).deposit(BigDecimal.valueOf(50))
                        .initialCash(BigDecimal.valueOf(10)).finalCash(BigDecimal.valueOf(75)).comment("Demo cashier closure Jun 1970 (1)")
                        .openingDate(LocalDateTime.of(1970,6,6,8,0)).closureDate(LocalDateTime.of(1970,6,6,20,0)).build(),
                CashierEntity.builder().id("01e0ed300000000000000000").cardSales(BigDecimal.valueOf(75)).cashSales(BigDecimal.valueOf(25)).withdrawal(BigDecimal.valueOf(50)).deposit(BigDecimal.valueOf(0))
                        .initialCash(BigDecimal.valueOf(75)).finalCash(BigDecimal.valueOf(50)).comment("Demo cashier closure Dec 1970 (1)")
                        .openingDate(LocalDateTime.of(1970,12,31,8,0)).closureDate(LocalDateTime.of(1970,12,31,20,0)).build(),
                CashierEntity.builder().id("01e23eb00000000000000000").cardSales(BigDecimal.valueOf(15)).cashSales(BigDecimal.valueOf(100)).withdrawal(BigDecimal.valueOf(0)).deposit(BigDecimal.valueOf(50))
                        .initialCash(BigDecimal.valueOf(50)).finalCash(BigDecimal.valueOf(200)).comment("Demo cashier closure Jan 1971 (1)")
                        .openingDate(LocalDateTime.of(1971,1,1,8,0)).closureDate(LocalDateTime.of(1971,1,1,20,0)).build()
        };

        this.cashierDao.saveAll(Arrays.asList(cashiers));
        log.warn("        ------- cashierClosure");
    }
}