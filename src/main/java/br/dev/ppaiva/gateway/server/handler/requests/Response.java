package br.dev.ppaiva.gateway.server.handler.requests;

import com.google.gson.Gson;

import br.dev.ppaiva.gateway.server.types.enums.CodeResponse;
import br.dev.ppaiva.gateway.server.types.enums.Status;

public class Response<T> {

	private CodeResponse code;
	private Status status;
	private String message;
	private T entity;

	public Response() {
	}

	public Response(CodeResponse code, Status status, String message, T entity) {
		this.code = code;
		this.status = status;
		this.message = message;
		this.entity = entity;
	}

	public Response(CodeResponse code, Status status, T entity) {
		this.code = code;
		this.status = status;
		this.entity = entity;
	}

	public Response(CodeResponse code, Status status, String message) {
		this.code = code;
		this.status = status;
		this.message = message;
	}

	public CodeResponse getCode() {
		return code;
	}

	public void setCode(CodeResponse code) {
		this.code = code;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(code.toString());
		sb.append(" ");
		sb.append(status.value());
		sb.append("\n");

		if (message != null && !"".equals(message.trim())) {
			sb.append(message);
			sb.append("\n");
		}

		if (entity != null) {
			sb.append("\n");

			Gson gson = new Gson();

			sb.append(gson.toJson(entity));
			sb.append("\n");
		}

		return sb.toString();
	}
}
