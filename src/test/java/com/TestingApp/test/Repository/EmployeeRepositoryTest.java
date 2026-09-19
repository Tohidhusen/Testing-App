package com.TestingApp.test.Repository;

import com.TestingApp.test.Entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import org.springframework.context.annotation.Import;
import com.TestingApp.test.TestcontainersConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Import(TestcontainersConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {
    @Autowired
    private  EmployeeRepository employeeRepository;
    private  Employee employee;

    @BeforeEach
    public void Setup() {
        employee = Employee.builder()
                .name("John")
                .email("john@123")
                .salary(1000.0)
                .build();
    }

    @Test
    void testwhenEmailIsValid_thenReturnEmployee() {

        //Arrange ,given
        employeeRepository.save(employee);

        //Act ,when
        List<Employee> employeeList= employeeRepository.findByEmail(employee.getEmail());

        //Assert ,then
        assertThat(employeeList).isNotEmpty();
        assertThat(employeeList.get(0).getEmail()).isEqualTo(employee.getEmail());


    }

    @Test
    void testwhenEmailIsNotValid_thenReturnEmpty() {
        String email="invalid@123";
        List<Employee> employeeList= employeeRepository.findByEmail(email);

        assertThat(employeeList).isEmpty();
    }
}