package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Login;
import es.upm.miw.betca_tpv_core.domain.model.LoginOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document
@Builder
@AllArgsConstructor
public class LoginEntity {
    @Id
    private String id;
    private String phone;
    private LocalDateTime loginDate;
    private LocalDateTime logoutDate;

    public static LoginEntity fromLoginOrder(LoginOrder loginOrder) {
        return LoginEntity.builder()
                .phone(loginOrder.getPhone())
                .loginDate(loginOrder.getLoginDate())
                .build();
    }

    public static LoginEntity fromLogin(Login login) {
        return LoginEntity.builder()
                .id(login.getId())
                .phone(login.getPhone())
                .loginDate(login.getLoginDate())
                .logoutDate(login.getLogoutDate())
                .build();
    }

    public Login toLogin() {
        return Login.builder()
                .id(this.id)
                .phone(this.phone)
                .loginDate(this.loginDate)
                .logoutDate(this.logoutDate)
                .build();
    }

    public LoginOrder toLoginOrder() {
        return LoginOrder.builder()
                .id(this.id)
                .phone(this.phone)
                .loginDate(this.loginDate)
                .build();
    }
}
