package es.upm.miw.betca_tpv_core.infrastructure.api;


import es.upm.miw.betca_tpv_core.configuration.JwtUtil;
import es.upm.miw.betca_tpv_core.infrastructure.api.http_errors.Role;
import es.upm.miw.betca_tpv_core.infrastructure.api.http_errors.UnauthorizedException;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.test.web.reactive.server.WebTestClient;

@Service
public class RestClientTestService {

    @Value("${server.servlet.contextPath}")
    private String contextPath;

    private String token;

    private boolean isRole(Role role) {
        try {/**/
            return this.token != null && JwtUtil.role(this.token).equals(role.name());
        } catch (UnauthorizedException e) {
            LogManager.getLogger(this.getClass()).error("------- is role exception: " + role);
        }
        return false;
    }

    private WebTestClient login(Role role, String user, String name, WebTestClient webTestClient) {
        if (!this.isRole(role)) {
            this.token = JwtUtil.createToken(user, name, role.name());

        }
        return webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer " + this.token).build();
    }

    public WebTestClient loginAdmin(WebTestClient webTestClient) {
        return this.login(Role.ADMIN, "666666000", "adm", webTestClient);
    }

    public WebTestClient loginManager(WebTestClient webTestClient) {
        return this.login(Role.MANAGER, "666666001", "man", webTestClient);
    }

    public WebTestClient loginOperator(WebTestClient webTestClient) {
        return this.login(Role.OPERATOR, "666666002", "ope", webTestClient);
    }

    public void logout() {
        this.token = null;
    }

    public String getToken() {
        return token;
    }

}