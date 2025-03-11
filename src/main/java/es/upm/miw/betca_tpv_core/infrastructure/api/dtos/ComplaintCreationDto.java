package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComplaintCreationDto {

    @NotBlank
    @NotNull
    private String barcode;
    @NotBlank
    @NotNull
    private String description;
    @NotNull
    @NotBlank
    private String userMobile;

    public Complaint toComplaint(){
        Complaint complaint = new Complaint();
        BeanUtils.copyProperties(this,complaint);
        complaint.setState("OPEN");
        complaint.setReply("");
        complaint.setRegistrationDate(LocalDateTime.now());

        return complaint;
    }

}
