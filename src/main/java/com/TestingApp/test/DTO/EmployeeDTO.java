package com.TestingApp.test.DTO;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class EmployeeDTO {
    private Long id;
    private String name;
    private String email;
    private double salary;
}
