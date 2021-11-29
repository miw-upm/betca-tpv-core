package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Provider;
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
@Document
@Builder
@AllArgsConstructor
public class ProviderEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String company;
    @Indexed(unique = true)
    private String nif;
    private String phone;
    private String address;
    private String email;
    private String note;
    private Boolean active;

    public ProviderEntity(Provider provider) {
        BeanUtils.copyProperties(provider, this);
    }

    public Provider toProvider() {
        Provider provider = new Provider();
        BeanUtils.copyProperties(this, provider);
        return provider;
    }
}
