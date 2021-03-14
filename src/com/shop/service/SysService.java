package com.shop.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shop.pojo.MenuTree;
import com.shop.pojo.SysPermission;
import com.shop.pojo.SysRole;
import com.shop.pojo.SysUserRole;
import com.shop.pojo.SysUserRoleExample;




public interface SysService {
	
	//根据用户账号查询用户信息
	//public Employee findSysUserByUserCode(String userCode)throws Exception;
	
	//根据用户id查询权限范围的菜单
	public List<SysPermission> findMenuListByUserId(String userid) throws Exception;
	
	//根据用户id查询权限范围的url
	public List<SysPermission> findPermissionListByUserId(String userid) throws Exception;
	
	public List<MenuTree> loadMenuTree();
	
	public List<SysRole> findAllRoles();
	
	public SysRole findRolesAndPermissionsByUserId(String userId);
	
	public void addRoleAndPermissions(SysRole role,int[] permissionIds);
	
	//查询所有menu类permission
	public List<SysPermission> findAllMenus();
	
	public void addSysPermission(SysPermission permission);
	
	//根据用户ID查询其所有的菜单和权限
	public List<SysPermission> findMenuAndPermissionByUserId(String userId);
	public List<MenuTree> getAllMenuAndPermision();
	
	//根据角色ID查询权限
	public List<SysPermission> findPermissionsByRoleId(String roleId);
	
	public void updateRoleAndPermissions(String roleId,int[] permissionIds);

	public List<SysRole> findRolesAndPermissions();
	
	public SysUserRole selectdByUserId(String user_id);
	
	 SysRole selectByPrimaryKey(String id);
	 
	 public void updateByPrimaryKey(SysUserRole record);
	 
	 void updateByUserId(SysUserRole record);
	 
	 void deleteRoled(String id);
	 void insertUserrole(SysUserRole userrole);
	 
	 public List<SysUserRole> findUserRoles();
}
