package es.upm.miw.betca_tpv_core.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

class TagsTest {

    @Test
    void testNoArgsConstructor() {
        Tags tag = new Tags();
        assertNull(tag.getName());
        assertNull(tag.getGroup());
        assertNull(tag.getDescription());
    }



    @Test
    void testBuilder() {
        Tags tag = Tags.builder()
                .name("Sports")
                .group("Entertainment")
                .description("All sports-related tags")
                .build();

        assertThat(tag.getName()).isEqualTo("Sports");
        assertThat(tag.getGroup()).isEqualTo("Entertainment");
        assertThat(tag.getDescription()).isEqualTo("All sports-related tags");
    }

    @Test
    void testSettersAndGetters() {
        Tags tag = new Tags();
        tag.setName("Music");
        tag.setGroup("Entertainment");
        tag.setDescription("Music-related tags");

        assertThat(tag.getName()).isEqualTo("Music");
        assertThat(tag.getGroup()).isEqualTo("Entertainment");
        assertThat(tag.getDescription()).isEqualTo("Music-related tags");
    }


}
