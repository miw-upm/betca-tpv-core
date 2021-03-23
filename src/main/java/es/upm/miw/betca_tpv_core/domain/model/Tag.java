package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag {
    @NotBlank
    private String name;
    @NotBlank
    private String group;
    @NotBlank
    private String description;
    private List<Article> articleList;

    public static Tag ofNameGroupDescription(Tag tag) {
        return Tag.builder()
                .name(tag.getName())
                .group(tag.getGroup())
                .description(tag.getDescription())
                .build();
    }
}
