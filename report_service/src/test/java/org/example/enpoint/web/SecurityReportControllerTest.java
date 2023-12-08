package org.example.enpoint.web;

import org.example.config.JwtSecurityConfig;
import org.example.dao.repository.IReportRepository;
import org.example.service.api.IReportFileFormerService;
import org.example.service.api.IReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {JwtSecurityConfig.class, ReportController.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IReportService reportService;

    @MockBean
    private IReportRepository reportRepository;

    @MockBean
    private IReportFileFormerService reportFileFormerService;


    @Test
    public void unauthenticatedAccessFails() throws Exception {
        mockMvc.perform(post("/report/full"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void authenticatedAccessSucceeds() throws Exception {
        mockMvc.perform(post("/report/full").header("gateway", "true"))
                .andExpect(status().isOk());
    }

    @Test
    public void unauthorizedAndUnauthenticatedAccessIsUnauthorized() throws Exception {
        mockMvc.perform(delete("/report/all").header("gateway", "true"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void unauthorizedAccessIsForbidden() throws Exception {
        mockMvc.perform(delete("/report/all").header("gateway", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_perform_all_operations")
    public void authorizedAccessIsAllowed() throws Exception {
        mockMvc.perform(delete("/report/all").header("gateway", "true"))
                .andExpect(status().isNoContent());
    }


}
