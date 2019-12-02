package com.activiti.sdk.test.utils;

import org.apache.commons.lang3.StringUtils;

import com.activiti.sdk.test.BaseIntegrationTestSDK;

public class UserBean {

	private String email;
	private String password;
	private String name;
	private String surname;
	private String[] memberOf;
	private String externalId;

	public static UserBean createUser(String email, String[] memberOf) {
		return createUser(email, BaseIntegrationTestSDK.passwordForAllUsers, memberOf);
	}

	public static UserBean createUser(String email, String password, String[] memberOf) {
		UserBean user = new UserBean();
		user.setEmail(email);
		user.setPassword(password);
		user.setName(email.substring(0, email.indexOf("@")));
		user.setSurname(StringUtils.EMPTY);
		user.setMemberOf(memberOf);
		return user;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String[] getMemberOf() {
		return memberOf;
	}

	public void setMemberOf(String[] memberOf) {
		this.memberOf = memberOf;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
}
