package com.shop.service.impl;

import java.util.List;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.EmployeeMapper;
import com.shop.mapper.SysRoleMapper;
import com.shop.pojo.Employee;
import com.shop.pojo.EmployeeExample;
import com.shop.pojo.SysRole;
import com.shop.pojo.SysRoleExample;
import com.shop.service.EmployeeService;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private SysRoleMapper sysRoleMapper;
	@Override
	public Employee findEmployeeManager(Long id) {
		// TODO Auto-generated method stub
		return employeeMapper.selectByPrimaryKey(id);
	}

	@Override
	public Employee findEmployeeByName(String name) {
		EmployeeExample example = new EmployeeExample();
		EmployeeExample.Criteria cri = example.createCriteria();
		cri.andNameEqualTo(name);
		List<Employee> list = employeeMapper.selectByExample(example);
		if(list.size()>0)
			return list.get(0);
		return null;
	}

	@Override
	public List<Employee> findEmployee() {
		EmployeeExample example = new EmployeeExample();
		return employeeMapper.selectByExample(example);
	}

	@Override
	public List<SysRole> findRole() {
		SysRoleExample roleexample=new SysRoleExample();
		
		return sysRoleMapper.selectByExample(roleexample);
	}

	@Override
	public void Saveemployee(Employee record) {		
		Md5Hash md5=new Md5Hash(record.getPassword(),"qwer",2);
		record.setPassword(md5.toString());
		record.setSalt("qwer");
		employeeMapper.insert(record);
		
	}



}
