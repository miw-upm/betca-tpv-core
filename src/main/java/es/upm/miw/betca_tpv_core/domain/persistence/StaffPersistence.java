package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Login;
import es.upm.miw.betca_tpv_core.domain.model.LoginOrder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.stream.Stream;

@Repository
public interface StaffPersistence {
    Mono<LoginOrder> saveLogin(LoginOrder loginOrder);
    Mono<Login> saveLogout(Login login);
    Mono<Login> findLastLogin(String phone);
    Mono<LoginOrder> findByLoginDateAndPhone(LocalDate loginDate, String phone);
    Stream<Login> findInRangeByPhone(LocalDate startDate, LocalDate endDate, String phone);
    Stream<Login> findInRange(LocalDate startDate, LocalDate endDate);
}
