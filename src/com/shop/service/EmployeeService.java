package com.shop.service;

import java.util.List;

import com.shop.pojo.Employee;
import com.shop.pojo.SysRole;

public interface EmployeeService {

	//根据员工主键查询员工
	Employee findEmployeeManager(Long id);
	
	//根据员工账号查找员工
	Employee findEmployeeByName(String name);
	
	
	List<Employee> findEmployee();
	
    List<SysRole> findRole();
    
    void Saveemployee(Employee record);
    
  
}
