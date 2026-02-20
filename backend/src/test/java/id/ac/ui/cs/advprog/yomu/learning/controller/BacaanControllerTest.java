package id.ac.ui.cs.advprog.yomu.learning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.service.BacaanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
class BacaanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BacaanService bacaanService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Bacaan bacaan;
    private UUID id;

    @BeforeEach
    void setUp() throws Exception {
        id = UUID.randomUUID();
        bacaan = new Bacaan();
        bacaan.setId(id);
        bacaan.setJudul("Judul Test");
        bacaan.setIsiTeks("Konten Test");

        // Create controller instance and inject mock service via reflection
        BacaanController controller = new BacaanController();
        Field serviceField = BacaanController.class.getDeclaredField("bacaanService");
        serviceField.setAccessible(true);
        serviceField.set(controller, bacaanService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAll() throws Exception {
        when(bacaanService.findAll()).thenReturn(List.of(bacaan));

        mockMvc.perform(get("/api/bacaan"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].judul").value("Judul Test"));
    }

    @Test
    void create() throws Exception {
        when(bacaanService.create(any(Bacaan.class))).thenReturn(bacaan);

        mockMvc.perform(post("/api/bacaan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bacaan)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.judul").value("Judul Test"));
    }

    @Test
    void getById() throws Exception {
        when(bacaanService.findById(id)).thenReturn(bacaan);

        mockMvc.perform(get("/api/bacaan/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.isiTeks").value("Konten Test"));
    }

    @Test
    void update() throws Exception {
        when(bacaanService.update(eq(id), any(Bacaan.class))).thenReturn(bacaan);

        Bacaan updateRequest = new Bacaan();
        updateRequest.setJudul("Updated");
        updateRequest.setIsiTeks("Updated konten");

        mockMvc.perform(put("/api/bacaan/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void deleteBacaan() throws Exception {
        doNothing().when(bacaanService).delete(id);

        mockMvc.perform(delete("/api/bacaan/{id}", id))
                .andExpect(status().isOk());
    }
}