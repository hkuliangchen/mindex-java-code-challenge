package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String id) {
        LOG.debug("Getting employee's reporting structure with id [{}]", id);

        if (id == null || id.isEmpty()) {
            throw new NullPointerException("Employee does not exist.");
        }

        Employee employee = read(id);

        if (employee == null) {
            return null;
        }

        int numberOfReports = getDirectReports(id);
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(numberOfReports);

        return reportingStructure;
    }

    @Override
    public List<Compensation> getCompensation(String id) {
        if (id == null || id.isEmpty()) {
            throw new NullPointerException("Employee does not exist.");
        }

        return compensationRepository.findByEmployeeId(id);
    }

    @Override
    public Compensation createCompensation(Compensation compensation) {
        compensation.setCompensationId(UUID.randomUUID().toString());
        compensationRepository.insert(compensation);
        return compensation;
    }

    private int getDirectReports(String id) {
        Employee employee = read(id);
        int directReportNumber = 0;
        List<Employee> directReports = employee.getDirectReports();

        if (directReports == null || directReports.isEmpty()) {
            return directReportNumber;
        } else {
            for (Employee directReport : directReports) {
                directReportNumber += getDirectReports(directReport.getEmployeeId()) + 1;
            }
        }

        return directReportNumber;
    }
}
