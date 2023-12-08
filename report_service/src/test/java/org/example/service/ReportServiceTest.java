package org.example.service;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.example.base.BaseRepositoryContainerTest;
import org.example.dao.repository.IReportRepository;
import org.example.service.api.IReportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
@DisplayName("Testing report service")
public class ReportServiceTest extends BaseRepositoryContainerTest {

    @RegisterExtension
    public static WireMockExtension categoryServer = WireMockExtension.newInstance()
            .options(wireMockConfig().port(9595))
            .build();

    @RegisterExtension
    public static WireMockExtension productServer = WireMockExtension.newInstance()
            .options(wireMockConfig().port(9599))
            .build();

    @Autowired
    private IReportRepository repository;

    @Autowired
    private IReportService reportService;

    @AfterEach
    public void deleteAllData() {
        repository.deleteAll();
    }

    @Test
    public void test() {
        productServer.stubFor(get(urlEqualTo("/internal/all_products"))
                .willReturn(aResponse()
                        .withStatus(200)));
        reportService.getProductsToBuyDTO();
    }

}
