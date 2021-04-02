package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.persistence.StaffPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;

@Service
public class StaffService {

    private final StaffPersistence staffPersistence;

    @Autowired
    public StaffService(StaffPersistence staffPersistence) {
        this.staffPersistence = staffPersistence;
    }

    public Mono<LoginOrder> login(String phone) {
        LoginOrder loginOrder = LoginOrder.builder()
                .loginDate(LocalDateTime.now())
                .phone(phone)
                .build();
        return staffPersistence.findByLoginDateAndPhone(LocalDate.now(), phone)
                .switchIfEmpty(staffPersistence.saveLogin(loginOrder))
                .next();
    }

    public Mono<Login> logout(String phone) {
        return staffPersistence.findLastLogin(phone)
                .switchIfEmpty(Mono.error(new NotFoundException("Not found login for user: " + phone)))
                .map(login -> {
                    login.setLogoutDate(LocalDateTime.now());
                    return login;
                })
                .flatMap(staffPersistence::saveLogout);
    }

    public Flux<StaffTime> findByMonth(StaffTimeOrder staffTimeOrder) {
        return staffPersistence.findInRangeByPhone(staffTimeOrder.getStartDate(),staffTimeOrder.getEndDate(), staffTimeOrder.getPhone())
                .map(login -> StaffTime.builder()
                        .hours(login.getTotal())
                        .time(login.getLoginDate().getMonth().name())
                        .build())
                .groupBy(StaffTime::getTime)
                .flatMap(stringStaffTimeGroupedFlux -> stringStaffTimeGroupedFlux
                        .reduce((a,b) -> StaffTime.builder()
                        .hours(a.getHours() + b.getHours())
                        .time(a.getTime())
                        .build()));

    }

    public Flux<StaffTime> findByDays(StaffTimeOrder staffTimeOrder) {
        return staffPersistence.findInRangeByPhone(staffTimeOrder.getStartDate(),staffTimeOrder.getEndDate(), staffTimeOrder.getPhone())
                .map(login -> StaffTime.builder()
                        .hours(login.getTotal())
                        .time(Year.of(Year.now().getValue()).atDay(login.getLoginDate().getDayOfYear()).toString())
                        .build())
                .groupBy(StaffTime::getTime)
                .flatMap(stringStaffTimeGroupedFlux -> stringStaffTimeGroupedFlux
                        .reduce((a,b) -> StaffTime.builder()
                                .hours(a.getHours() + b.getHours())
                                .time(a.getTime())
                                .build()));
    }

    public Flux<StaffReport> findReports(String month) {
        LocalDate startDate = LocalDate.of(Year.now().getValue(), Month.valueOf(month).getValue(), 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        return staffPersistence.findInRange(startDate, endDate)
            .map(login -> StaffReport.builder()
                    .hours(login.getTotal())
                    .user(login.getPhone())
                    .build())
            .groupBy(StaffReport::getUser)
            .flatMap(stringStaffTimeGroupedFlux -> stringStaffTimeGroupedFlux
                    .reduce((a,b) -> StaffReport.builder()
                            .hours(a.getHours() + b.getHours())
                            .user(a.getUser())
                            .build()));
    }
}
