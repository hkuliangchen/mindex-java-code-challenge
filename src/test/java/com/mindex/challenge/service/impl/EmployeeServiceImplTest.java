package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;

    private Employee employee1;
    private Employee employee2;
    private Employee employee3;
    private Employee employee4;
    private Employee employee5;

    @InjectMocks
    private EmployeeService employeeService = new EmployeeServiceImpl();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reportingStructure";

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
        employee2 = Employee.builder()
                .employeeId("b7839309-3348-463b-a7e3-5de1c168beb3")
                .firstName("Paul")
                .lastName("McCartney")
                .position("Developer I")
                .department("Engineering")
                .build();
        employee1 = Employee.builder()
                .employeeId("16a596ae-edd3-4847-99fe-c4518e82c86f")
                .firstName("John")
                .lastName("Lennon")
                .position("Development Manager")
                .department("Engineering")
                .directReports(new ArrayList<>(Arrays.asList(employee2, employee3)))
                .build();
    }

    @Test
    public void testGetReportingStructureOfJohnLennon() {
        // test John Lennon's ReportingStructure
        ReportingStructure mockReportingStructure = ReportingStructure.builder()
                .employee(employee1)
                .numberOfReports(4)
                .build();
        when(employeeService.getReportingStructure(anyString())).thenReturn(mockReportingStructure);
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employee1.getEmployeeId()).getBody();
        assertEquals(employee1.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEmployeeEquivalence(employee1, reportingStructure.getEmployee());
        assertEquals(4, reportingStructure.getNumberOfReports());

        // test Ringo Starr's ReportingStructure
        mockReportingStructure.setEmployee(employee3);
        mockReportingStructure.setNumberOfReports(2);
        reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employee3.getEmployeeId()).getBody();
        assertEquals(employee3.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEmployeeEquivalence(employee3, reportingStructure.getEmployee());
        assertEquals(2, reportingStructure.getNumberOfReports());

        // test George Harrison's ReportingStructure
        mockReportingStructure.setEmployee(employee5);
        mockReportingStructure.setNumberOfReports(0);
        reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employee5.getEmployeeId()).getBody();
        assertEquals(employee5.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEmployeeEquivalence(employee5, reportingStructure.getEmployee());
        assertEquals(0, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
