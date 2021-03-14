package com.shop.pojo;

import java.io.Serializable;

public class SysUserRole implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

    private String sysUserId;

    private String sysRoleId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(String sysUserId) {
        this.sysUserId = sysUserId == null ? null : sysUserId.trim();
    }

    public String getSysRoleId() {
        return sysRoleId;
    }

    public void setSysRoleId(String sysRoleId) {
        this.sysRoleId = sysRoleId == null ? null : sysRoleId.trim();
    }
}