package br.dev.ppaiva.gateway.server.types.enums;

public enum UDPDataVersion {
	_1_0("1.0");

	private String version;

	private UDPDataVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public UDPDataVersion from(String version) {
		for (UDPDataVersion v : UDPDataVersion.values()) {
			if (v.getVersion().equals(version)) {
				return v;
			}
		}

		return null;
	}
}
