package br.dev.ppaiva.gateway.heartbeat.types;

public class VotaAIServer {

	private String location;
	private Integer port;
	private Boolean active;

	public VotaAIServer(String location, Integer port, Boolean active) {
		this.location = location;
		this.port = port;
		this.active = active;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "VotaAIServer [location=" + location + ", port=" + port + ", active=" + active + "]";
	}

}
