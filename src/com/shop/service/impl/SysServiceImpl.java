package com.shop.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.SysPermissionMapper;
import com.shop.mapper.SysPermissionMapperCustom;
import com.shop.mapper.SysRoleMapper;
import com.shop.mapper.SysRolePermissionMapper;
import com.shop.mapper.SysUserRoleMapper;
import com.shop.pojo.MenuTree;
import com.shop.pojo.SysPermission;
import com.shop.pojo.SysPermissionExample;
import com.shop.pojo.SysRole;
import com.shop.pojo.SysRolePermission;
import com.shop.pojo.SysRolePermissionExample;
import com.shop.pojo.SysUserRole;
import com.shop.pojo.SysUserRoleExample;
import com.shop.service.SysService;

@Service
public class SysServiceImpl implements SysService {
	
	@Autowired
	private SysPermissionMapperCustom sysPermissionMapperCustom;
	@Autowired
	private SysRoleMapper roleMapper;
	@Autowired
	private SysRolePermissionMapper rolePermissionMapper;
	@Autowired
	private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMapper SysRoleMapper;
	@Override
	public List<SysPermission> findMenuListByUserId(String userid)
			throws Exception {
		return sysPermissionMapperCustom.findMenuListByUserId(userid);
	}

	@Override
	public List<SysPermission> findPermissionListByUserId(String userid)
			throws Exception {
		return sysPermissionMapperCustom.findPermissionListByUserId(userid);
	}

	@Override
	public List<MenuTree> loadMenuTree() {
		return sysPermissionMapperCustom.getMenuTree();
	}

	@Override
	public List<SysRole> findAllRoles() {
		return roleMapper.selectByExample(null);
	}

	//根据用户帐号，查询所有角色和其权限列表
	@Override
	public SysRole findRolesAndPermissionsByUserId(String userId) {
		return sysPermissionMapperCustom.findRoleAndPermissionListByUserId(userId);
	}

	@Override
	public void addRoleAndPermissions(SysRole role, int[] permissionIds) {
		//添加角色
		roleMapper.insert(role);
		//添加角色和权限关系表
		for (int i = 0; i < permissionIds.length; i++) {
			SysRolePermission rolePermission = new SysRolePermission();
			//16进制随机码
			String uuid = UUID.randomUUID().toString();
			rolePermission.setId(uuid);
			rolePermission.setSysRoleId(role.getId());
			rolePermission.setSysPermissionId(permissionIds[i]+"");
			rolePermissionMapper.insert(rolePermission);
		}
	}

	@Override
	public List<SysPermission> findAllMenus() {
		SysPermissionExample example = new SysPermissionExample();
		SysPermissionExample.Criteria criteria = example.createCriteria();
		//criteria.andTypeLike("%menu%");
		criteria.andTypeEqualTo("menu");
		return sysPermissionMapper.selectByExample(example);
	}
	@Override
	public List<SysUserRole> findUserRoles() {
		SysUserRoleExample example=new SysUserRoleExample();
		SysUserRoleExample.Criteria criteria=example.createCriteria();
		return sysUserRoleMapper.selectByExample(example);
	}
	@Override
	public void addSysPermission(SysPermission permission) {
		String parentids = "0/1/";
		if (permission.getType() != null && permission.getType().indexOf("permission") != -1) {
			parentids += permission.getParentid() + "/";
		}
		permission.setParentids(parentids);
		sysPermissionMapper.insert(permission);
	}

	@Override
	public List<SysPermission> findMenuAndPermissionByUserId(String userId) {
		return sysPermissionMapperCustom.findMenuAndPermissionByUserId(userId);
	}

	@Override
	public List<MenuTree> getAllMenuAndPermision() {
		return sysPermissionMapperCustom.getAllMenuAndPermision();
	}

	@Override
	public List<SysPermission> findPermissionsByRoleId(String roleId) {
		return sysPermissionMapperCustom.findPermissionsByRoleId(roleId);
	}

	@Override
	public void updateRoleAndPermissions(String roleId, int[] permissionIds) {
		//先删除角色权限关系表中角色的权限关系
		SysRolePermissionExample example = new SysRolePermissionExample();
		SysRolePermissionExample.Criteria criteria = example.createCriteria();
		criteria.andSysRoleIdEqualTo(roleId);
		rolePermissionMapper.deleteByExample(example);
		//重新创建角色权限关系
		for (Integer pid : permissionIds) {
			SysRolePermission rolePermission = new SysRolePermission();
			String uuid = UUID.randomUUID().toString();
			rolePermission.setId(uuid);
			rolePermission.setSysRoleId(roleId);
			rolePermission.setSysPermissionId(pid.toString());
			
			rolePermissionMapper.insert(rolePermission);
		}
	}

	//查询所有角色和其权限列表
	@Override
	public List<SysRole> findRolesAndPermissions() {
		return sysPermissionMapperCustom.findRoleAndPermissionList();
	}

	@Override
	public SysUserRole selectdByUserId(String user_id) {
		SysUserRoleExample example=new SysUserRoleExample();
		SysUserRoleExample.Criteria cri=example.createCriteria();
		cri.andSysUserIdEqualTo(user_id);
		List<SysUserRole> list=sysUserRoleMapper.selectByExample(example);
		if(list.size()>0)
			return list.get(0);
		return null;
	}

	@Override
	public SysRole selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return SysRoleMapper.selectByPrimaryKey(id);
	}

	@Override
	public void updateByPrimaryKey(SysUserRole record) {
		sysUserRoleMapper.updateByPrimaryKey(record);
		
	}

	@Override
	public void updateByUserId(SysUserRole record) {
		sysUserRoleMapper.updateByUserId(record);
		
	}

	@Override
	public void deleteRoled(String id) {
		SysRoleMapper.deleteByPrimaryKey(id);
		
	}

	@Override
	public void insertUserrole(SysUserRole userrole) {
		sysUserRoleMapper.insert(userrole);
		
	}



}
