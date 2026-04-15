package id.ac.ui.cs.advprog.yomu.forum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomu.forum.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.forum.dto.CreateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.dto.UpdateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.forum.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CommentService commentService;

    private MockMvc mockMvc;
    private UUID commentId;

    @BeforeEach
    void setUp() throws Exception {
        commentId = UUID.randomUUID();

        CommentController controller = new CommentController();
        Field serviceField = CommentController.class.getDeclaredField("commentService");
        serviceField.setAccessible(true);
        serviceField.set(controller, commentService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @Test
    void getAll_returnsCommentResponses() throws Exception {
        CommentResponse response = new CommentResponse();
        response.setId(commentId);
        response.setIsiKomentar("komentar");
        response.setUsername("owner");

        when(commentService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/comment"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(commentId.toString()))
                .andExpect(jsonPath("$[0].username").value("owner"));
    }

    @Test
    void create_returnsCreatedComment() throws Exception {
        UUID bacaanId = UUID.randomUUID();

        Comment saved = new Comment();
        saved.setId(commentId);
        saved.setIsiKomentar("isi baru");

        when(commentService.create(any(CreateCommentRequest.class))).thenReturn(saved);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setIsiKomentar("isi baru");
        request.setBacaanId(bacaanId);

        mockMvc.perform(post("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.isiKomentar").value("isi baru"));
    }

    @Test
    void create_returnsBadRequestWhenPayloadInvalid() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setIsiKomentar("   ");

        mockMvc.perform(post("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returnsComment() throws Exception {
        Comment saved = new Comment();
        saved.setId(commentId);
        saved.setIsiKomentar("isi komentar");

        when(commentService.findById(commentId)).thenReturn(saved);

        mockMvc.perform(get("/api/comment/{id}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.isiKomentar").value("isi komentar"));
    }

    @Test
    void update_returnsUpdatedComment() throws Exception {
        Comment updated = new Comment();
        updated.setId(commentId);
        updated.setIsiKomentar("isi edit");

        when(commentService.update(eq(commentId), any(UpdateCommentRequest.class))).thenReturn(updated);

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar("isi edit");

        mockMvc.perform(put("/api/comment/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.isiKomentar").value("isi edit"));
    }

    @Test
    void update_returnsBadRequestWhenPayloadInvalid() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar(" ");

        mockMvc.perform(put("/api/comment/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_callsServiceAndReturnsOk() throws Exception {
        doNothing().when(commentService).delete(commentId);

        mockMvc.perform(delete("/api/comment/{id}", commentId))
                .andExpect(status().isOk());
    }
}

