package id.ac.ui.cs.advprog.yomu.forum.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CommentDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createCommentRequest_validPayload_hasNoViolations() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setIsiKomentar("Komentar valid");
        request.setBacaanId(UUID.randomUUID());

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void createCommentRequest_blankComment_hasViolations() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setIsiKomentar(" ");
        request.setBacaanId(UUID.randomUUID());

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("isiKomentar"));
    }

    @Test
    void createCommentRequest_missingBacaanId_hasViolations() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setIsiKomentar("Isi");

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("bacaanId"));
    }

    @Test
    void updateCommentRequest_blankComment_hasViolations() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar(" ");

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("isiKomentar"));
    }

    @Test
    void updateCommentRequest_tooLongComment_hasViolations() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar("a".repeat(1001));

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("isiKomentar"));
    }

    @Test
    void commentResponse_initializesRepliesAsEmptyList() {
        CommentResponse response = new CommentResponse();

        assertThat(response.getReplies()).isNotNull();
        assertThat(response.getReplies()).isEmpty();
    }
}


