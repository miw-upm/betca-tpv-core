package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(collection = "tags") // Especifica el nombre de la colección
public class TagsEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String group;
    private String description;

    public TagsEntity(Tags tag) {
        BeanUtils.copyProperties(tag, this);
    }

    public Tags toTag() {
        Tags tag = new Tags();
        BeanUtils.copyProperties(this, tag);
        return tag;
    }
}
