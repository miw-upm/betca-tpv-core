package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Objects;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    @NotBlank
    private String subject;
    @NotBlank
    private String text;
    private User userFrom;
    private User userTo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    public void doDefault() {
        if (Objects.isNull(creationDate)) {
            this.creationDate = LocalDate.now();
        }
    }
}
