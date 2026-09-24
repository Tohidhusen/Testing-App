package com.TestingApp.test.Service;
import com.TestingApp.test.DTO.EmployeeDTO;
import com.TestingApp.test.Entity.Employee;
import com.TestingApp.test.Repository.EmployeeRepository;
import com.TestingApp.test.advice.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {

        log.info("Creating new employee with name: {}", employeeDTO.getName());

        Employee employee = modelMapper.map(employeeDTO, Employee.class);

        Employee savedEmployee = employeeRepository.save(employee);

        log.info("Employee created successfully with id: {}", savedEmployee.getId());

        return modelMapper.map(savedEmployee, EmployeeDTO.class);
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {

        log.info("Fetching all employees");

        List<Employee> employees = employeeRepository.findAll();

        log.info("Total employees found: {}", employees.size());

        return employees.stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {

        log.info("Fetching employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new ResourceNotFoundException();
                });

        log.info("Employee found: {}", employee.getName());

        return modelMapper.map(employee, EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {

        log.info("Updating employee with id: {}", id);

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot update. Employee not found with id: {}", id);
                    return new RuntimeException("Employee not found");
                });

        existingEmployee.setName(employeeDTO.getName());
        existingEmployee.setEmail(employeeDTO.getEmail());
        existingEmployee.setSalary(employeeDTO.getSalary());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        log.info("Employee updated successfully with id: {}", id);

        return modelMapper.map(updatedEmployee, EmployeeDTO.class);
    }

    @Override
    public void deleteEmployee(Long id) {

        log.info("Deleting employee with id: {}", id);

        if (!employeeRepository.existsById(id)) {
            log.error("Cannot delete. Employee not found with id: {}", id);
            throw new RuntimeException("Employee not found with id: " + id);
        }

        employeeRepository.deleteById(id);

        log.info("Employee deleted successfully with id: {}", id);
    }
}

