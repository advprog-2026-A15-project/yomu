package id.ac.ui.cs.advprog.yomu.forum.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    void defaults_createdAtAndRepliesAreInitialized() {
        Comment comment = new Comment();

        assertThat(comment.getCreatedAt()).isNotNull();
        assertThat(comment.getReplies()).isNotNull();
        assertThat(comment.getReplies()).isEmpty();
    }

    @Test
    void settersAndGetters_storeValues() {
        Comment comment = new Comment();
        comment.setIsiKomentar("Isi komentar");

        assertThat(comment.getIsiKomentar()).isEqualTo("Isi komentar");
    }
}

