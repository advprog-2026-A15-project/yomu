package id.ac.ui.cs.advprog.yomu.forum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomu.authentication.service.CustomUserDetailsService;
import id.ac.ui.cs.advprog.yomu.authentication.service.JwtService;
import id.ac.ui.cs.advprog.yomu.forum.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.forum.dto.CreateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.forum.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Spring Security JWT filter untuk tes unit Controller
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Comment mockComment;
    private CommentResponse mockCommentResponse;
    private UUID commentId;

    @BeforeEach
    void setUp() {
        commentId = UUID.randomUUID();

        mockComment = new Comment();
        mockComment.setId(commentId);
        mockComment.setIsiKomentar("Test komentar");

        mockCommentResponse = new CommentResponse();
        mockCommentResponse.setId(commentId);
        mockCommentResponse.setIsiKomentar("Test komentar");
        mockCommentResponse.setUsername("Naeru");
    }

    @Test
    void testGetAllComments_Success() throws Exception {
        Mockito.when(commentService.findAll()).thenReturn(Arrays.asList(mockCommentResponse));

        mockMvc.perform(get("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(commentId.toString()))
                .andExpect(jsonPath("$[0].isiKomentar").value("Test komentar"))
                .andExpect(jsonPath("$[0].username").value("Naeru"));
    }

    @Test
    void testGetCommentById_Success() throws Exception {
        Mockito.when(commentService.findById(commentId)).thenReturn(mockComment);

        mockMvc.perform(get("/api/comment/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.isiKomentar").value("Test komentar"));
    }

    @Test
    void testCreateComment_Success() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setIsiKomentar("Test komentar");
        request.setBacaanId(UUID.randomUUID());

        Mockito.when(commentService.create(any(CreateCommentRequest.class))).thenReturn(mockComment);

        mockMvc.perform(post("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.isiKomentar").value("Test komentar"));
    }

    @Test
    void testUpdateComment_Success() throws Exception {
        Comment updateData = new Comment();
        updateData.setIsiKomentar("Komentar yang diupdate");

        Comment updatedComment = new Comment();
        updatedComment.setId(commentId);
        updatedComment.setIsiKomentar("Komentar yang diupdate");

        Mockito.when(commentService.update(eq(commentId), any(Comment.class))).thenReturn(updatedComment);

        mockMvc.perform(put("/api/comment/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.isiKomentar").value("Komentar yang diupdate"));
    }

    @Test
    void testDeleteComment_Success() throws Exception {
        Mockito.doNothing().when(commentService).delete(commentId);

        mockMvc.perform(delete("/api/comment/{id}", commentId))
                .andExpect(status().isOk());

        Mockito.verify(commentService, Mockito.times(1)).delete(commentId);
    }
}