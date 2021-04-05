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
public class UserSelectedDto {
    private String mobile;
    private String name;
    private String familyName;
    private String dni;
    private String email;

    public UserSelectedDto(User user){
        this.mobile = user.getMobile();
        this.name = user.getFirstName();
        this.familyName = user.getFamilyName();
        this.dni = user.getDni();
        this.email = user.getEmail();
    }
}
