package com.shop.mapper;

import java.util.List;

import com.shop.pojo.SysPermission;
import com.shop.pojo.TreeMenu;

public interface SysPermissionCustomMapper {

	
	public List<TreeMenu> getTreeMenu();
	
	public List<SysPermission> getSubMenu(int id);
}
