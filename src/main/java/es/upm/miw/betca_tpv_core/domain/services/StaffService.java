package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.persistence.StaffPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Stream;

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
        return staffPersistence.saveLogin(loginOrder);
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

    public Stream<StaffTime> findByMonth(StaffTimeOrder staffTimeOrder) {
        Map<Month, Double> hoursPerMonth = new EnumMap<>(Month.class);
        List<StaffTime> staffTimeList = new ArrayList<>();
        staffPersistence.findInRangeByPhone(staffTimeOrder.getStartDate(),staffTimeOrder.getEndDate(), staffTimeOrder.getPhone())
                .forEach(login -> {
                    Double actual = hoursPerMonth.getOrDefault(login.getLoginDate().getMonth(), 0.0);
                    hoursPerMonth.put(login.getLoginDate().getMonth(), actual + login.getTotal());
                });

        hoursPerMonth.forEach((month, aDouble) -> staffTimeList.add(StaffTime.builder()
                .hours(aDouble)
                .time(month.name())
                .build()));
        return staffTimeList.stream();
    }

    public Stream<StaffTime> findByDays(StaffTimeOrder staffTimeOrder) {
        Map<Integer, Double> hoursPerDay = new HashMap<>();
        List<StaffTime> staffTimeList = new ArrayList<>();
        staffPersistence.findInRangeByPhone(staffTimeOrder.getStartDate(),staffTimeOrder.getEndDate(), staffTimeOrder.getPhone())
                .forEach(login -> {
                    Double actual = hoursPerDay.getOrDefault(login.getLoginDate().getDayOfYear(), 0.0);
                    hoursPerDay.put(login.getLoginDate().getDayOfYear(), actual + login.getTotal());
                });

        hoursPerDay.forEach((dayOfYear, aDouble) -> staffTimeList.add(StaffTime.builder()
                .hours(aDouble)
                .time(Year.of(Year.now().getValue()).atDay(dayOfYear).toString())
                .build()));
        return staffTimeList.stream();
    }

    public Stream<StaffReport> findReports(String month) {
        LocalDate startDate = LocalDate.of(Year.now().getValue(), Month.valueOf(month).getValue(), 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        Map<String, Double> hoursPerUser = new HashMap<>();
        List<StaffReport> staffReports = new ArrayList<>();
        staffPersistence.findInRange(startDate, endDate)
                .forEach(login -> {
                    Double actual = hoursPerUser.getOrDefault(login.getPhone(), 0.0);
                    hoursPerUser.put(login.getPhone(), actual + login.getTotal());
                });

        hoursPerUser.forEach((user, aDouble) -> staffReports.add(StaffReport.builder()
                .hours(aDouble)
                .user(user)
                .build()));
        return staffReports.stream();
    }
}
