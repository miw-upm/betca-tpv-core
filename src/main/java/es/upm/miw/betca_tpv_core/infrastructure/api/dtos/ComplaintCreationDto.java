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

    @NotBlank(message = "Barcode is required")
    @NotNull(message = "Barcode is required")
    private String barcode;
    @NotBlank(message = "Description is required")
    @NotNull(message = "Description is required")
    private String description;
    @NotNull(message = "UserMobile is required")
    @NotBlank(message = "UserMobile is required")
    private String userMobile;

    public Complaint toComplaint(){
        Complaint complaint = new Complaint();
        BeanUtils.copyProperties(this,complaint);
        complaint.setReply("");
        complaint.setRegistrationDate(LocalDateTime.now());

        return complaint;
    }

}
