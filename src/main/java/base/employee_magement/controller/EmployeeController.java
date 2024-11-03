package base.employee_magement.controller;

import base.employee_magement.model.Employee;
import base.employee_magement.repository.EmployeeRepository;
import base.employee_magement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping()
    public ResponseEntity<Iterable<Employee>> getAllEmployees(){
        Iterable<Employee> employeeList = employeeService.findAll();
        return new ResponseEntity<>(employeeList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id){
        Optional<Employee> employeeOptional = employeeService.findById(id);
        if(employeeOptional.isPresent()){
            return new ResponseEntity<>(employeeOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found" ,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee){
        try {
            Employee employeeCreate = employeeService.save(employee);
            return new ResponseEntity<>(employeeCreate, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException exception) {
            throw new RuntimeException("Email has already existed!!!!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employee){
        Optional<Employee> employeeOptional = employeeService.findById(id);
        if(employeeOptional.isPresent()){
            try {
                Employee employeeGet = employeeOptional.get();
                employeeGet.setName(employee.getName());
                employeeGet.setAge(employee.getAge());
                employeeGet.setPosition(employee.getPosition());
                employeeGet.setEmail(employee.getEmail());
                Employee updateEmployee = employeeService.save(employeeGet);
                return new ResponseEntity<>(updateEmployee, HttpStatus.OK);
            } catch (DataIntegrityViolationException exception){
                throw new RuntimeException("Email: " + employee.getEmail() + "has already existed!!!!");
            }
        } else {
            return new ResponseEntity<>("Employee not found" ,HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id){
        Optional<Employee> employeeOptional = employeeService.findById(id);
        if(employeeOptional.isPresent()){
            employeeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Employee not found" ,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployee(@RequestParam String name){
        List<Employee> employees = employeeService.findEmployeesByNameContainingIgnoreCase(name);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Employee>> getSortedEmployee(@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String direction){
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        List<Employee> employees = employeeRepository.findAll(sort);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
}
