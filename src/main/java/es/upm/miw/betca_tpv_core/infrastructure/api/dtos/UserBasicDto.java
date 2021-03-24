package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;


import es.upm.miw.betca_tpv_core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBasicDto {
    private String mobile;
    private String name;

    public UserBasicDto(User user) {
        this.mobile = user.getMobile();
        this.name = user.getFirstName() +" "+ user.getFamilyName();
    }
}
