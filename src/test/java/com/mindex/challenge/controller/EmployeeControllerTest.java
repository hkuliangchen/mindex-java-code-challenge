package com.mindex.challenge.controller;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    private static final String url = "/employee";

    private Employee employee3;
    private Employee employee4;
    private Employee employee5;
    private ReportingStructure reportingStructure;

    @Before
    public void setup() {
        employee4 = Employee.builder()
                .employeeId("62c1084e-6e34-4630-93fd-9153afb65309")
                .firstName("Pete")
                .lastName("Best")
                .position("Developer II")
                .department("Engineering")
                .build();
        employee5 = Employee.builder()
                .employeeId("c0c2293d-16bd-4603-8e08-638a9d18b22c")
                .firstName("George")
                .lastName("Harrison")
                .position("Developer III")
                .department("Engineering")
                .build();
        employee3 = Employee.builder()
                .employeeId("03aa1462-ffa9-4978-901b-7c001562cf6f")
                .firstName("Ringo")
                .lastName("Starr")
                .position("Developer V")
                .department("Engineering")
                .directReports(new ArrayList<>(Arrays.asList(employee4, employee5)))
                .build();
        reportingStructure = ReportingStructure.builder()
                .employee(employee3)
                .numberOfReports(2)
                .build();
    }

    @Test
    public void testGetReportingStructure() throws Exception {
        final String _url = url + "/03aa1462-ffa9-4978-901b-7c001562cf6f/reportingStructure";

        when(employeeService.getReportingStructure(anyString())).thenReturn(reportingStructure);

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.get(_url)
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
}
