package br.dev.ppaiva.gateway.server.types.enums;

public enum DataMethod {
	GET("GET"), POST("POST");

	private String method;

	private DataMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public DataMethod from(String method) {
		for (DataMethod m : DataMethod.values()) {
			if (m.getMethod().equals(method)) {
				return m;
			}
		}

		return null;
	}
}
