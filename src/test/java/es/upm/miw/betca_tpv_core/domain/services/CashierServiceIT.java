package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.CashierClose;
import es.upm.miw.betca_tpv_core.domain.services.utils.MovementType;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.CashMovementDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

@TestConfig
class CashierServiceIT {

    @Autowired
    private CashierService cashierService;

    private static Stream<Arguments> invalidCashMovementDtoProvider() {
        return Stream.of(
                Arguments.of(new CashMovementDto(null, null, null)),
                Arguments.of(new CashMovementDto(MovementType.WITHDRAWAL, null, null)),
                Arguments.of(new CashMovementDto(MovementType.DEPOSIT, null, null)),
                Arguments.of(new CashMovementDto(MovementType.WITHDRAWAL, BigDecimal.valueOf(0), null)),
                Arguments.of(new CashMovementDto(MovementType.DEPOSIT, BigDecimal.valueOf(0), null)),
                Arguments.of(new CashMovementDto(MovementType.WITHDRAWAL, BigDecimal.valueOf(-1), null)),
                Arguments.of(new CashMovementDto(MovementType.DEPOSIT, BigDecimal.valueOf(-1), null)),
                Arguments.of(new CashMovementDto(MovementType.WITHDRAWAL, BigDecimal.valueOf(1), null)),
                Arguments.of(new CashMovementDto(MovementType.DEPOSIT, BigDecimal.valueOf(1), null)),
                Arguments.of(new CashMovementDto(MovementType.WITHDRAWAL, BigDecimal.valueOf(1), "")),
                Arguments.of(new CashMovementDto(MovementType.DEPOSIT, BigDecimal.valueOf(1), ""))
        );
    }

    private static Stream<Arguments> validCashMovementDtoProvider() {
        return Stream.of(
                Arguments.of(new CashMovementDto(MovementType.WITHDRAWAL, BigDecimal.TEN, "10e withdrawal")),
                Arguments.of(new CashMovementDto(MovementType.DEPOSIT, BigDecimal.TEN, "10e deposit"))
        );
    }

    @Test
    void testCloseBadRequestException() {
        StepVerifier.create(this.cashierService.close(new CashierClose(BigDecimal.ZERO, BigDecimal.ZERO, "")))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void testOpenBadRequestException() {
        StepVerifier.create(this.cashierService.createOpened())
                .expectComplete()
                .verify();
        StepVerifier.create(this.cashierService.createOpened())
                .expectError(BadRequestException.class)
                .verify();
        StepVerifier.create(this.cashierService.close(new CashierClose(BigDecimal.ZERO, BigDecimal.ZERO, "")))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testFindAllByClosureDateBetween() {
        StepVerifier.create(this.cashierService.findAllByClosureDateBetween(LocalDate.of(1970, 1, 1), LocalDate.of(1970, 1, 2)))
                .expectNextMatches(cashier ->
                        cashier.getClosureDate().isAfter(LocalDate.of(1970, 1, 1).atStartOfDay()) &&
                                cashier.getClosureDate().isBefore(LocalDate.of(1970, 1, 2).atStartOfDay())
                ).expectComplete()
                .verify();
        StepVerifier.create(this.cashierService.findAllByClosureDateBetween(LocalDate.of(1970, 1, 1), LocalDate.of(1971, 1, 1)))
                .expectNextCount(7)
                .expectComplete()
                .verify();
    }

    @ParameterizedTest()
    @MethodSource("invalidCashMovementDtoProvider")
    void testAddMovementsWithNonValidDto(CashMovementDto subject) {
        StepVerifier.create(this.cashierService.addMovement(subject))
                .expectError()
                .verify();
    }

    @ParameterizedTest
    @MethodSource("validCashMovementDtoProvider")
    void testAddMovementsWithValidDto(CashMovementDto subject) {
        this.cashierService.createOpened()
                .then(this.cashierService.addMovement(subject))
                .then(this.cashierService.findLast())
                .flatMap(lastCashier -> this.cashierService.close(new CashierClose(
                        lastCashier.getCashSales(),
                        lastCashier.getCardSales(),
                        "cashier close for testing"
                )))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }
}
