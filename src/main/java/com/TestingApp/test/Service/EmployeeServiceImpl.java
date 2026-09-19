package com.TestingApp.test.Service;

import com.TestingApp.test.Entity.Employee;
import com.TestingApp.test.Repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    public Employee createEmployee(Employee employee) {

        log.info("Creating new employee with name: {}", employee.getName());

        Employee savedEmployee = employeeRepository.save(employee);

        log.info("Employee created successfully with id: {}",
                savedEmployee.getId());

        return savedEmployee;
    }

    @Override
    public List<Employee> getAllEmployees() {

        log.info("Fetching all employees");

        List<Employee> employees = employeeRepository.findAll();

        log.info("Total employees found: {}", employees.size());

        return employees;
    }

    @Override
    public Employee getEmployeeById(Long id) {

        log.info("Fetching employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new RuntimeException("Employee not found");
                });

        log.info("Employee found: {}", employee.getName());

        return employee;
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {

        log.info("Updating employee with id: {}", id);

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot update. Employee not found with id: {}", id);
                    return new RuntimeException("Employee not found");
                });

        existingEmployee.setName(employee.getName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setSalary(employee.getSalary());

        Employee updatedEmployee =
                employeeRepository.save(existingEmployee);

        log.info("Employee updated successfully with id: {}", id);

        return updatedEmployee;
    }

    @Override
    public void deleteEmployee(Long id) {

        log.info("Deleting employee with id: {}", id);

        if (!employeeRepository.existsById(id)) {
            log.error("Cannot delete. Employee not found with id: {}", id);
            throw new RuntimeException("Employee not found");
        }

        employeeRepository.deleteById(id);

        log.info("Employee deleted successfully with id: {}", id);
    }
}

