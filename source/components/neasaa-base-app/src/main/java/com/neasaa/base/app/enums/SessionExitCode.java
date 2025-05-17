package com.neasaa.base.app.enums;

public enum SessionExitCode {
	ACTIVE_SESSION(0), USER_LOGOUT(1), SESSION_TIMEOUT(2), RELOGIN(3), ADMIN_TERMINATION(4);

	private int exitCode;

	private SessionExitCode(int aExitCode) {
		this.exitCode = aExitCode;
	}

	public int getExitCode() {
		return this.exitCode;
	}
}
