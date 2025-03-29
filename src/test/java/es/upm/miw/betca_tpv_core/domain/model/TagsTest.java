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
    void testAllArgsConstructor() {
        Tags tag = new Tags("Technology", "IT", "Programming-related tags");
        assertThat(tag.getName()).isEqualTo("Technology");
        assertThat(tag.getGroup()).isEqualTo("IT");
        assertThat(tag.getDescription()).isEqualTo("Programming-related tags");
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

    @Test
    void testEqualsAndHashCode() {
        Tags tag1 = new Tags("Technology", "IT", "Programming-related tags");
        Tags tag2 = new Tags("Technology", "IT", "Programming-related tags");
        Tags tag3 = new Tags("Science", "Education", "Scientific research");

        assertThat(tag1).isEqualTo(tag2);
        assertThat(tag1).hasSameHashCodeAs(tag2);
        assertThat(tag1).isNotEqualTo(tag3);
    }

    @Test
    void testToString() {
        Tags tag = new Tags("Gaming", "Entertainment", "Video games related tags");
        String expectedString = "Tags(name=Gaming, group=Entertainment, description=Video games related tags)";
        assertThat(tag.toString()).isEqualTo(expectedString);
    }
}
