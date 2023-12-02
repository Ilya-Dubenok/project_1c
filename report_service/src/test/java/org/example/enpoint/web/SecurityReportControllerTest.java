package org.example.enpoint.web;

import org.example.dao.repository.IReportRepository;
import org.example.service.api.IReportFileFormerService;
import org.example.service.api.IReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc
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
        mockMvc.perform(post("/report/full")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void authenticatedAccessSucceeds() throws Exception {
        mockMvc.perform(post("/report/full").header("gateway", "true")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @Test
    public void deleteMethod_Csrf_NoMockUser() throws Exception {
        mockMvc.perform(delete("/report/all").header("gateway", "true")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_none")
    public void deleteMethod_Csrf_WithMockUser() throws Exception {
        mockMvc.perform(delete("/report/all").header("gateway", "true")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_none")
    public void deleteMethod_NoCsrf_WithMockUser_NoAuthorities() throws Exception {
        mockMvc.perform(delete("/report/all").header("gateway", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "perform_all_operations")
    public void deleteMethod_NoCsrf_WithMockUser_ValidAuthorities() throws Exception {
        mockMvc.perform(delete("/report/all").header("gateway", "true"))
                .andExpect(status().isNoContent());
    }


}
