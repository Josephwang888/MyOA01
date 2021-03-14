package com.shop.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.shop.pojo.ActiveUser;
import com.shop.pojo.Employee;
import com.shop.pojo.MenuTree;
import com.shop.pojo.SysPermission;
import com.shop.service.EmployeeService;
import com.shop.service.SysService;

public class CustomRealm extends AuthorizingRealm {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SysService sysService;
	//授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection princ) {
		ActiveUser au=(ActiveUser) princ.getPrimaryPrincipal();
		List<SysPermission> permissions=null;
		try {
			permissions=sysService.findPermissionListByUserId(au.getUsercode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> permissionList=new ArrayList<String>();
		for(SysPermission per:permissions) {
			permissionList.add(per.getPercode());
		}
		SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
		info.addStringPermissions(permissionList);
		return info;
	}
	//认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String username = (String) token.getPrincipal();
		Employee user=employeeService.findEmployeeByName(username);
		if(user==null)
		 return null;
		List<MenuTree> menuTree=sysService.loadMenuTree();
		ActiveUser au=new ActiveUser();
		au.setId(user.getId());
		au.setUsercode(user.getName());
		au.setUserid(user.getName());
		au.setUsername(user.getName());
		au.setManagerId(user.getManagerId());
		au.setMenuTree(menuTree);
		SimpleAuthenticationInfo info=new SimpleAuthenticationInfo(au,user.getPassword(),ByteSource.Util.bytes(user.getSalt()),"CustomRealm");
		return info;
	}

}
