package br.dev.ppaiva.gateway.server.types.enums;

public enum UDPDataMethod {
	RECEIVE("RECEIVE"), DELIVER("DELIVER");

	private String method;

	private UDPDataMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public UDPDataMethod from(String method) {
		for (UDPDataMethod m : UDPDataMethod.values()) {
			if (m.getMethod().equals(method)) {
				return m;
			}
		}

		return null;
	}
}
