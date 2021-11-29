package es.upm.miw.betca_tpv_core.infrastructure.api;


import es.upm.miw.betca_tpv_core.configuration.JwtService;
import es.upm.miw.betca_tpv_core.infrastructure.api.http_errors.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.reactive.server.WebTestClient;

@Service
public class RestClientTestService {
    private JwtService jwtService;
    private String token;

    @Autowired
    public RestClientTestService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    private boolean isRole(Role role) {
        return this.token != null && jwtService.role(this.token).equals(role.name());
    }

    private WebTestClient login(Role role, String user, String name, WebTestClient webTestClient) {
        if (!this.isRole(role)) {
            this.token = jwtService.createToken(user, name, role.name());

        }
        return webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer " + this.token).build();
    }

    public WebTestClient loginAdmin(WebTestClient webTestClient) {
        return this.login(Role.ADMIN, "6", "adm", webTestClient);
    }

    public WebTestClient loginManager(WebTestClient webTestClient) {
        return this.login(Role.MANAGER, "666666001", "man", webTestClient);
    }

    public WebTestClient loginOperator(WebTestClient webTestClient) {
        return this.login(Role.OPERATOR, "666666002", "ope", webTestClient);
    }

    public WebTestClient loginCustomer(WebTestClient webTestClient) {
        return this.login(Role.OPERATOR, "66", "customer", webTestClient);
    }

    public void logout() {
        this.token = null;
    }

    public String getToken() {
        return token;
    }

}
