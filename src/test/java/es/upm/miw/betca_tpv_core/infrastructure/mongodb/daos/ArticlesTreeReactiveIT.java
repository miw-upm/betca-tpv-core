package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.ArticlesTreeDao;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestConfig
class ArticlesTreeReactiveIT {

    @Autowired
    private ArticlesTreeDao articlesTreeDao;

    @Test
    void testFindAll() {
        List< ArticlesTreeEntity > list = this.articlesTreeDao.findByReference("root");
        assertFalse(list.isEmpty());
    }

}
