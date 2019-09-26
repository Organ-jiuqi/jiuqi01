package com.jiuqi.mt2.dna.mobile.wxapproval.entity;

/**
 * 职员的实体
 * @author liuzihao
 */
public class StaffEntity {
	private String recid;
	private String namecode;
	private String email;
	private String tel;
	private String linkuser;
	private String name;
	
	public String getRecid() {
		return recid;
	}
	public void setRecid(String recid) {
		this.recid = recid;
	}
	public String getLinkuser() {
		return linkuser;
	}
	public void setLinkuser(String linkuser) {
		this.linkuser = linkuser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getNamecode() {
		return namecode;
	}
	public void setNamecode(String namecode) {
		this.namecode = namecode;
	}
	
}
