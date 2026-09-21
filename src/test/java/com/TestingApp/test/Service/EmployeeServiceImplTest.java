package com.TestingApp.test.Service;

import com.TestingApp.test.DTO.EmployeeDTO;
import com.TestingApp.test.Entity.Employee;
import com.TestingApp.test.Repository.EmployeeRepository;
import com.TestingApp.test.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Import(TestcontainersConfiguration.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private EmployeeServiceImpl employeeServiceImpl;

     private Employee employee;
     private EmployeeDTO mockemployeeDTO;

    @BeforeEach
    void setUp() {
        employeeServiceImpl = new EmployeeServiceImpl(employeeRepository, modelMapper);

        employee=Employee.builder()
                .id(1L)
                .name("John")
                .email("John@222.com")
                .salary(1000.0)
                .build();
        mockemployeeDTO=EmployeeDTO.builder()
                .id(1L)
                .name("John")
                .email("John@222.com")
                .salary(1000.0)
                .build();
    }

    @Test
    void getEmployeeById_whenEmployeeExists_thenReturnEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeDTO result = employeeServiceImpl.getEmployeeById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("John@222.com");
    }

    @Test
    void getEmployeeById_whenEmployeeDoesNotExist_thenThrow() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> employeeServiceImpl.getEmployeeById(99L));
    }

    @Test
    void getallEmployeees_whenEmployeeExists_thenReturnAllEmployees() {
        //arrange
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        //act
        List<EmployeeDTO> result = employeeServiceImpl.getAllEmployees();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void createEmployee_whenValidInput_thenReturnSavedEmployee() {
        // no need to stub modelMapper at all — it's real
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDTO result = employeeServiceImpl.createEmployee(mockemployeeDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
    }

    @Test
   void testDeleteEmployee_whenEmployeeDoesNotExist_thenThrow() {
        when(employeeRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(
                () -> employeeServiceImpl.deleteEmployee(1L))
                .isInstanceOf(RuntimeException.class).hasMessage("Employee not found with id: "+1L);

        verify(employeeRepository,never()).deleteById(anyLong());
   }

   @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        assertThatCode(
                () -> employeeServiceImpl.deleteEmployee(1L))
                .doesNotThrowAnyException();
        verify(employeeRepository,times(1)).deleteById(1L);
   }

   @Test
 void testWhenupdateEmployee_whenEmployeeExists_thenUpdateEmployee() {

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
       when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

       EmployeeDTO result =employeeServiceImpl.updateEmployee(1L,mockemployeeDTO);
       assertThat(result).isNotNull();
       assertThat(result.getName()).isEqualTo("John");
       verify(employeeRepository,times(1)).save(any(Employee.class));

   }
   @Test
    void testWhenupdateEmployee_whenEmployeeDoesNotExist_thenThrow() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(
                () -> employeeServiceImpl.updateEmployee(1L,mockemployeeDTO))
                .isInstanceOf(RuntimeException.class).hasMessage("Employee not found");

        verify(employeeRepository,never()).save(any(Employee.class));
   }
}