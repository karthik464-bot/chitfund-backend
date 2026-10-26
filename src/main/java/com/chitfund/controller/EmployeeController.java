package com.chitfund.controller;

import com.chitfund.model.Employee;
import com.chitfund.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Viewable by both ADMIN and USER
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ADMIN', 'USER')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // Viewable by both ADMIN and USER
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ADMIN', 'USER')")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ADMIN only: Create employee
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ADMIN')")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    // ADMIN only: Update employee
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ADMIN')")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
            return ResponseEntity.ok(updatedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ADMIN only: Delete employee
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}