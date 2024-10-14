package br.dev.ppaiva.gateway.idempotent.types;

import java.time.LocalDateTime;
import java.util.Objects;

import br.dev.ppaiva.gateway.server.handler.requests.Response;

public class RequestData {
	private String UUID;
	private String method;
	private String path;
	private String body;
	private Response<?> response;
	private LocalDateTime timestamp;

	public RequestData() {
	}

	public RequestData(String UUID, String method, String path, String body, Response<?> response,
			LocalDateTime timestamp) {
		this.UUID = UUID;
		this.method = method;
		this.path = path;
		this.body = body;
		this.response = response;
		this.timestamp = timestamp;
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Response<?> getResponse() {
		return response;
	}

	public void setResponse(Response<?> response) {
		this.response = response;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		return Objects.hash(UUID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestData other = (RequestData) obj;
		return Objects.equals(UUID, other.UUID);
	}

}
