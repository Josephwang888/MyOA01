package com.shop.mapper;

import java.util.List;

import com.shop.pojo.EmployeeCustom;
import com.shop.pojo.MenuTree;
import com.shop.pojo.SysPermission;
import com.shop.pojo.SysRole;


public interface SysPermissionMapperCustom {
	
	//根据用户id查询菜单
	public List<SysPermission> findMenuListByUserId(String userid)throws Exception;
	//根据用户id查询权限url
	public List<SysPermission> findPermissionListByUserId(String userid)throws Exception;

	public List<MenuTree> getMenuTree();
	
	public List<SysPermission> getSubMenu();
	
	public List<EmployeeCustom> findUserAndRoleList();
	
	public SysRole findRoleAndPermissionListByUserId(String userId);
	
	public List<SysRole> findRoleAndPermissionList();
	
	public List<SysPermission> findMenuAndPermissionByUserId(String userId);
	
	public List<MenuTree> getAllMenuAndPermision();
	
	public List<SysPermission> findPermissionsByRoleId(String roleId);
}
