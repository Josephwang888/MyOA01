package com.shop.pojo;

public class EmployeeCustom extends Employee {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String roleId;
	private String rolename;
	private String manager;
	
	
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	
}
