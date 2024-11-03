package base.employee_magement.service;

import base.employee_magement.model.Employee;

import java.util.List;

public interface IEmployeeService extends IGeneralService<Employee>{
    List<Employee> findEmployeesByNameContainingIgnoreCase(String name);
}
