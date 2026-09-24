package com.TestingApp.test.Contoller;

import com.TestingApp.test.DTO.EmployeeDTO;
import com.TestingApp.test.Entity.Employee;
import com.TestingApp.test.Repository.EmployeeRepository;
import com.TestingApp.test.TestcontainersConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "100000")
@Import(TestcontainersConfiguration.class)
class EmployeeControllerTestIT {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private WebTestClient webTestClient;

    private Employee testemployee;
    private EmployeeDTO testemployeeDTO;
    @BeforeEach
    void setUp() {
        testemployee = Employee.builder()

                .name("John")
                .email("John@222.com")
                .salary(1000.0)
                .build();

        testemployeeDTO=EmployeeDTO.builder()

                        .name("John")
                        .email("John@222.com")
                        .salary(1000.0)
                        .build();
        employeeRepository.deleteAll();
    }

    @Test
    void testgetEmployeeById_success(){
        Employee savedemployee=employeeRepository.save(testemployee);
        webTestClient.get()
                .uri("/employee/"+savedemployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedemployee.getId())
                .jsonPath("$.name").isEqualTo("John")
                .jsonPath("$.email").isEqualTo("John@222.com")
                .jsonPath("$.salary").isEqualTo(1000.0);
    }
    @Test
    void testgetEmployeeById_fail(){
        webTestClient.get()
                .uri("/employee/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createemployee_success(){
        Employee savedemployee=employeeRepository.save(testemployee);
        webTestClient.post()
                .uri("/employee")
                .bodyValue(testemployeeDTO)
                .exchange()
                .expectStatus().isCreated();

    }
    @Test
    void testUpdateEmployee_success(){
        Employee savedemployee=employeeRepository.save(testemployee);
        testemployeeDTO.setName("John");
        testemployeeDTO.setSalary(1000.0);
        webTestClient.put()
                .uri("/employee/"+savedemployee.getId())
                .bodyValue(testemployeeDTO)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    void testUpdateWhen_failure(){
        Employee savedemployee=employeeRepository.save(testemployee);
        testemployeeDTO.setName("John");
        testemployeeDTO.setSalary(1000.0);

        webTestClient.put()
                .uri("/employee/"+999)
                .bodyValue(testemployeeDTO)
                .exchange()
                .expectStatus().is5xxServerError();

    }
    @Test
    void deleteEmployeeWhenitsNotExistThrowEx(){
        webTestClient.delete()
                .uri("/employee/"+testemployee.getId())
                .exchange()
                .expectStatus().isBadRequest();

    }
    @Test
    void deleteEmployeeWhenitspresent(){
        Employee savedemployee=employeeRepository.save(testemployee);
        webTestClient.delete()
                .uri("/employee/"+savedemployee.getId())
                .exchange()
                .expectStatus().isNoContent();
    }
}
