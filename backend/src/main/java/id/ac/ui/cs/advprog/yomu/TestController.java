package id.ac.ui.cs.advprog.yomu;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TestController {

    @GetMapping("/api/test")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Halo dari Spring Boot & Supabase!");
        response.put("status", "Berhasil Tersambung");
        return response;
    }
}