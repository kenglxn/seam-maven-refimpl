package org.open18.auth;

public class PasswordBean {
	private String password;
	private String confirm;

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public boolean verify() {
		return confirm != null && confirm.equals(password);
	}
}
