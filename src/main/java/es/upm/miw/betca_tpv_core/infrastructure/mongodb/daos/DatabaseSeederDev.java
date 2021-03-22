package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.domain.model.ShoppingState;
import es.upm.miw.betca_tpv_core.domain.model.Tax;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.*;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static java.math.BigDecimal.ZERO;

@Service // @Profile("dev")
public class DatabaseSeederDev {
    private ArticleDao articleDao;
    private ProviderDao providerDao;
    private ArticlesTreeDao articlesTreeDao;
    private TicketDao ticketDao;
    private CashierDao cashierDao;
    private OfferDao offerDao;
    private StockAlarmDao stockAlarmDao;
    private CreditSaleDao creditSaleDao;
    private CreditDao creditDao;
    private RgpdDao rgpdDao;
    private CustomerDiscountDao customerDiscountDao;
    private BudgetDao budgetDao;
    private MessengerDao messengerDao;
    private InvoiceDao invoiceDao;
    private SalespeopleDao salespeopleDao;
    private ProviderInvoiceDao providerInvoiceDao;
    private DatabaseStarting databaseStarting;

    @Autowired
    public DatabaseSeederDev(ArticleDao articleDao, ProviderDao providerDao, ArticlesTreeDao articlesTreeDao,
                             TicketDao ticketDao, CashierDao cashierDao, OfferDao offerDao, StockAlarmDao stockAlarmDao,
                             CreditSaleDao creditSaleDao, CreditDao creditDao, RgpdDao rgpdDao,
                             CustomerDiscountDao customerDiscountDao, BudgetDao budgetDao, MessengerDao messengerDao,
                             SalespeopleDao salespeopleDao, InvoiceDao invoiceDao, ProviderInvoiceDao providerInvoiceDao,
                             DatabaseStarting databaseStarting) {

        this.articleDao = articleDao;
        this.providerDao = providerDao;
        this.articlesTreeDao = articlesTreeDao;
        this.ticketDao = ticketDao;
        this.cashierDao = cashierDao;
        this.offerDao = offerDao;
        this.creditSaleDao = creditSaleDao;
        this.creditDao = creditDao;
        this.databaseStarting = databaseStarting;
        this.stockAlarmDao = stockAlarmDao;
        this.rgpdDao = rgpdDao;
        this.customerDiscountDao = customerDiscountDao;
        this.budgetDao=budgetDao;
        this.messengerDao=messengerDao;
        this.invoiceDao = invoiceDao;
        this.salespeopleDao=salespeopleDao;
        this.providerInvoiceDao = providerInvoiceDao;

        this.deleteAllAndInitializeAndSeedDataBase();
    }

    public void deleteAllAndInitializeAndSeedDataBase() {
        this.deleteAllAndInitialize();
        this.seedDataBaseJava();
    }

    private void deleteAllAndInitialize() {
        this.ticketDao.deleteAll();
        this.articleDao.deleteAll();
        this.providerInvoiceDao.deleteAll();
        this.providerDao.deleteAll();
        this.cashierDao.deleteAll();
        this.offerDao.deleteAll();
        this.stockAlarmDao.deleteAll();
        this.creditSaleDao.deleteAll();
        this.creditDao.deleteAll();
        this.rgpdDao.deleteAll();
        this.customerDiscountDao.deleteAll();
        this.budgetDao.deleteAll();
        this.messengerDao.deleteAll();
        this.salespeopleDao.deleteAll();

        LogManager.getLogger(this.getClass()).warn("------- Delete All -----------");
        this.databaseStarting.initialize();
    }

    private void seedDataBaseJava() {
        LogManager.getLogger(this.getClass()).warn("------- Initial Load from JAVA -----------");
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
        LogManager.getLogger(this.getClass()).warn("        ------- providers");
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
        LogManager.getLogger(this.getClass()).warn("        ------- articles");
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
        LogManager.getLogger(this.getClass()).warn("        ------- articles tree");
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
                        ZERO, ZERO, "note", "666666000"),
                new TicketEntity("5fa45f6f3a61083cb241289c", "lpiHOlsoS_WkkEyWeFNJtg",
                        List.of(shoppingList[2]), date, new BigDecimal("25.0"),
                        ZERO, ZERO, "note", "666666004"),
                new TicketEntity("5fa4603b7513a164c99677ac", "FGhfvfMORj6iKmzp5aERAA",
                        List.of(shoppingList[3], shoppingList[4]), date, new BigDecimal("18.0"),
                        ZERO, ZERO, "note", "666666004"),
                new TicketEntity("5fa4608f4928694ef5980e4c", "WB9-e8xQT4ejb74r1vLrCw",
                        List.of(shoppingList[5]), date, new BigDecimal("20"),
                        new BigDecimal("5"), ZERO, "note", "666666005"),
                new TicketEntity("5fa4603b7513a164chop77ac", "FGhfv521Rj6iKmzp5aERAA",
                        List.of(shoppingList[1], shoppingList[5]), date, new BigDecimal("21.0"),
                        ZERO, ZERO, "note", "66"),
                new TicketEntity("5gfaw03b7513a164chop77ac", "AAhfv521Rj6iKmzp5aERAA",
                        List.of(shoppingList[2], shoppingList[3], shoppingList[4]), date, new BigDecimal("25.0"),
                        ZERO, ZERO, "note", "66"),
        };
        this.ticketDao.saveAll(Arrays.asList(tickets));
        LogManager.getLogger(this.getClass()).warn("        ------- tickets");
        LocalDate offerCreationDate = LocalDate.of(2021, Month.MARCH, 12);
        LocalDate offerExpiryDate = LocalDate.of(2021, Month.JULY, 12);
        OfferEntity[] offers = {
                OfferEntity.builder().id("1lh67i968h3d7809l982376mn").reference("cmVmZXJlbmNlb2ZmZXIx")
                        .description("this is offer 1").creationDate(offerCreationDate).expiryDate(offerExpiryDate)
                        .discount(new BigDecimal("10")).articleEntityList(List.of(articles[0], articles[1], articles[2]))
                        .build(),
                OfferEntity.builder().id("2lh67i968h3d7809l982376mn").reference("cmVmZXJlbmNlb2ZmZXIy")
                        .description("this is offer 2").creationDate(offerCreationDate).expiryDate(offerExpiryDate)
                        .discount(new BigDecimal("20")).articleEntityList(List.of(articles[3]))
                        .build(),
                OfferEntity.builder().id("3lh67i968h3d7809l982376mn").reference("cmVmZXJlbmNlb2ZmZXIz")
                        .description("this is offer 3").creationDate(offerCreationDate).expiryDate(offerExpiryDate)
                        .discount(new BigDecimal("30")).articleEntityList(List.of(articles[0], articles[4]))
                        .build(),
                OfferEntity.builder().id("4lh67i968h3d7809l982376mn").reference("cmVmZXJlbmNlb2ZmZXI0")
                        .description("this is offer 4").creationDate(offerCreationDate)
                        .expiryDate(LocalDate.of(2021, Month.JANUARY, 12))
                        .discount(new BigDecimal("40")).articleEntityList(List.of(articles[5], articles[6]))
                        .build(),
        };
        this.offerDao.saveAll(List.of(offers));
        LogManager.getLogger(this.getClass()).warn("        ------- offers");

        ArticleEntity article1 = ArticleEntity.builder().barcode("stockAlamt00001").reference("zz-falda-T2").description("Zarzuela - Falda T2")
                .retailPrice(new BigDecimal("20")).stock(10).providerEntity(providers[0])
                .registrationDate(LocalDateTime.now()).discontinued(false).build();

        ArticleEntity article2= ArticleEntity.builder().barcode("stockAlamt00002").reference("zz-falda-T2").description("Zarzuela - Falda T2")
                .retailPrice(new BigDecimal("20")).stock(4).providerEntity(providers[0])
                .registrationDate(LocalDateTime.now()).discontinued(false).build();

        this.articleDao.save(article1);
        this.articleDao.save(article2);

        StockAlarmLineEntity stockAlarmLineEntity1 = StockAlarmLineEntity.builder().articleEntity(article1).build();
        StockAlarmLineEntity stockAlarmLineEntity2 = StockAlarmLineEntity.builder().articleEntity(article2).warning(1).critical(2).build();
        StockAlarmLineEntity stockAlarmLineEntity3 = StockAlarmLineEntity.builder().articleEntity(articles[3]).warning(2).build();
        StockAlarmLineEntity stockAlarmLineEntity4 = StockAlarmLineEntity.builder().articleEntity(articles[4]).critical(3).build();
        StockAlarmLineEntity stockAlarmLineEntity5 = StockAlarmLineEntity.builder().articleEntity(articles[5]).warning(5).critical(3).build();

        StockAlarmEntity[] stocksAlarms = {
                StockAlarmEntity.builder().name("alarm-pack-1").warning(11).critical(5).alarmLine(stockAlarmLineEntity1).alarmLine(stockAlarmLineEntity2).build(),
                StockAlarmEntity.builder().name("alarm-pack-2").warning(20).critical(11).alarmLine(stockAlarmLineEntity1).alarmLine(stockAlarmLineEntity4).build(),
                StockAlarmEntity.builder().name("alarm-pac-2").warning(55).critical(55).alarmLine(stockAlarmLineEntity3).alarmLine(stockAlarmLineEntity5).build(),
                StockAlarmEntity.builder().name("alarm-delete").warning(55).critical(55).alarmLine(stockAlarmLineEntity3).alarmLine(stockAlarmLineEntity5).build()

        };

        this.stockAlarmDao.saveAll(List.of(stocksAlarms));
        LogManager.getLogger(this.getClass()).warn("        ------- stockAlarms");

        CreditSaleEntity[] creditSales = {
                CreditSaleEntity.builder().id("1lh67i9fds68h3d7809l982376mn").reference("dsfdsf54fds").ticketEntity(tickets[0]).
                        payed(false).build(),
                CreditSaleEntity.builder().id("145657i9fds68h3d7809l982376mn").reference("hjf45jfdsffds").ticketEntity(tickets[1]).
                        payed(false).build(),
                CreditSaleEntity.builder().id("1lh67i68h3d78dssd09l982376mn").reference("cbbn76bvdsffds").ticketEntity(tickets[2]).
                        payed(true).build(),
        };
        this.creditSaleDao.saveAll(List.of(creditSales));
        LogManager.getLogger(this.getClass()).warn("        ------- credit sales");

        CreditEntity[] credits = {
                CreditEntity.builder().id("1lh9fds68h3d7809l982sdg376mn").reference("sdgfsgfdg53")
                        .userReference("53354324").creditSaleEntities(List.of(creditSales[1], creditSales[2]))
                        .build(),
                CreditEntity.builder().id("5465ds68h3d7809l982sdg376mn").reference("456gfsgfdg53")
                        .userReference("5666534324").creditSaleEntities(List.of(creditSales[0]))
                        .build(),
                CreditEntity.builder().id("777885ds68h3d7809l982sdg376mn").reference("44366sgfdg53")
                        .userReference("345436324").creditSaleEntities(List.of()).build(),
        };
        this.creditDao.saveAll(List.of(credits));
        LogManager.getLogger(this.getClass()).warn("        ------- credits");

        RgpdEntity[] rgpds = {
                RgpdEntity.builder().id("1lh9dps68h3d7809l982sd452d8a")
                        .userMobile("123456789")
                        .rgpdType(RgpdType.ADVANCED)
                        .build()
        };
        this.rgpdDao.saveAll(List.of(rgpds));
        LogManager.getLogger(this.getClass()).warn("        ------- rgpds");

        CustomerDiscountEntity[] customersDiscounts = {
                CustomerDiscountEntity.builder().id("1").note("discount1").registrationDate(LocalDateTime.now()).discount(30.0).minimumPurchase(50.0).user("66").build(),
                CustomerDiscountEntity.builder().id("2").note("discount2").registrationDate(LocalDateTime.now()).discount(10.0).minimumPurchase(40.0).user("77").build(),
                CustomerDiscountEntity.builder().id("3").note("discount3").registrationDate(LocalDateTime.now()).discount(25.5).minimumPurchase(35.0).user("88").build(),
                CustomerDiscountEntity.builder().id("4").note("discount4").registrationDate(LocalDateTime.now()).discount(80.0).minimumPurchase(100.0).user("99").build(),
        };
        this.customerDiscountDao.saveAll(List.of(customersDiscounts));
        LogManager.getLogger(this.getClass()).warn("        ------- customer discount");

        BudgetEntity[] budgets = {
                new BudgetEntity("1", date, List.of(shoppingList[0], shoppingList[1])),
                new BudgetEntity("2", date, List.of(shoppingList[2], shoppingList[3])),
                new BudgetEntity("3", date, List.of(shoppingList[4], shoppingList[5])),


        };
        this.budgetDao.saveAll(Arrays.asList(budgets));
        LogManager.getLogger(this.getClass()).warn("        ------- budgets");

        MessageEntity[] messageEntities = {
                MessageEntity.builder().id("1").subject("Message 1").text("Message text 1").userFrom("6").userTo("666666001").isRead(Boolean.TRUE).creationDate(LocalDate.now()).build(),
                MessageEntity.builder().id("2").subject("Message 2").text("Message text 2").userFrom("666666001").userTo("6").isRead(Boolean.TRUE).creationDate(LocalDate.now()).build(),
                MessageEntity.builder().id("3").subject("Message 3").text("Message text 3").userFrom("666666001").userTo("6").isRead(Boolean.FALSE).creationDate(LocalDate.now()).build(),
                MessageEntity.builder().id("4").subject("Message 4").text("Message text 4").userFrom("6").userTo("666666001").isRead(Boolean.FALSE).creationDate(LocalDate.now()).build()
        };
        this.messengerDao.saveAll(List.of(messageEntities));
        LogManager.getLogger(this.getClass()).warn("        ------- messages");

        InvoiceEntity[] invoices = {
                InvoiceEntity.builder().id("invc_ID_1A2B3C4D5E").number("invc_N_1A2B3C4D5E").ticketEntity(tickets[0]).creationDate(LocalDateTime.now())
                        .baseTax(new BigDecimal("16.53")).taxValue(new BigDecimal("3.47")).build()
        };
        this.invoiceDao.saveAll(Arrays.asList(invoices));
        LogManager.getLogger(this.getClass()).warn("        ------- invoices");

        LocalDate salespeopleTime=LocalDate.of(2021,Month.APRIL,1);
        LocalDate salespeopleTime2=LocalDate.of(2021,Month.APRIL,2);
        SalespeopleEntity[] salespeople={
            SalespeopleEntity.builder()
                .id("1").salesperson("Rosaria")
                .salesDate(salespeopleTime).numArticle(2).finalValue(new BigDecimal(23)).articleEntityList(List.of(articles[0]))
                .ticketEntityList(List.of(tickets[0]))
                .build(),
            SalespeopleEntity.builder()
                .id("2").salesperson("Nacho")
                .salesDate(salespeopleTime2).numArticle(5).finalValue(new BigDecimal(25.3)).articleEntityList(List.of(articles[0]))
                .ticketEntityList(List.of(tickets[1]))
                .build(),

        };
        this.salespeopleDao.saveAll(Arrays.asList(salespeople));
        LogManager.getLogger(this.getClass()).warn("        ------  salespeople");


        ProviderInvoiceEntity[] providerInvoiceEntities = {
                ProviderInvoiceEntity.builder()
                        .id("1")
                        .number(1111)
                        .creationDate(LocalDateTime.of(2021, 1, 1, 10, 20))
                        .baseTax(new BigDecimal("1000"))
                        .taxValue(new BigDecimal("10"))
                        .providerEntity(providers[0])
                        .orderId("1")
                        .build(),
                ProviderInvoiceEntity.builder()
                        .id("2")
                        .number(2222)
                        .creationDate(LocalDateTime.of(2021, 5, 1, 11, 30))
                        .baseTax(new BigDecimal("2000"))
                        .taxValue(new BigDecimal("20"))
                        .providerEntity(providers[1])
                        .orderId("2")
                        .build()
        };
        this.providerInvoiceDao.saveAll(List.of(providerInvoiceEntities));
        LogManager.getLogger(this.getClass()).warn("        ------  provider invoices");

    }

}



