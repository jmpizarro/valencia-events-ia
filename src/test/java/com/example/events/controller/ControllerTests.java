package com.example.events.controller;

import com.example.events.repository.AdminConfigRepository;
import com.example.events.repository.EventRepository;
import com.example.events.repository.EventSummaryRepository;
import com.example.events.service.OpenAiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import com.example.events.model.AdminConfig;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({EventController.class, AdminController.class})
public class ControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventRepository eventRepository;
    @MockBean
    private EventSummaryRepository eventSummaryRepository;
    @MockBean
    private AdminConfigRepository adminConfigRepository;
    @MockBean
    private OpenAiService openAiService;

    @Test
    void healthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("healthy"));
    }

    @Test
    void getEventsReturnsEmptyList() throws Exception {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void adminLoginSuccess() throws Exception {
        AdminConfig config = new AdminConfig();
        config.setPassword("password");
        when(adminConfigRepository.findAll()).thenReturn(List.of(config));
        String body = "{\"username\":\"admin\",\"password\":\"password\"}";
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("admin-token"));
    }

    @Test
    void adminLoginFailure() throws Exception {
        AdminConfig config = new AdminConfig();
        config.setPassword("password");
        when(adminConfigRepository.findAll()).thenReturn(List.of(config));
        String body = "{\"username\":\"user\",\"password\":\"bad\"}";
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword() throws Exception {
        AdminConfig config = new AdminConfig();
        config.setPassword("password");
        when(adminConfigRepository.findAll()).thenReturn(List.of(config));
        String body = "{\"oldPassword\":\"password\",\"newPassword\":\"newpass\"}";
        mockMvc.perform(put("/api/admin/password")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated"));
    }
}
