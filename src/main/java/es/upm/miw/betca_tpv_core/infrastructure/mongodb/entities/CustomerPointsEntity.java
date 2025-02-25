package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.CustomerPoints;
import es.upm.miw.betca_tpv_core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class CustomerPointsEntity {

    @Id
    private String id;

    private Integer value;

    private LocalDateTime lastDate;

    @Indexed(unique = true)
    private String userMobileNumber;

    private User user;

    public CustomerPoints toCustomerPoints() {
        return CustomerPoints.builder()
                .value(this.value)
                .lastDate(this.lastDate)
                .user(this.user)
                .build();
    }
}