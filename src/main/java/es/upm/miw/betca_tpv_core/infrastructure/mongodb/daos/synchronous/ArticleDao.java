package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;


import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArticleDao extends MongoRepository< ArticleEntity, String > {
    List< ArticleEntity > findByBarcode(String barcode);
}
