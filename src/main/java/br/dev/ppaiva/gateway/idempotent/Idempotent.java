package br.dev.ppaiva.gateway.idempotent;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import br.dev.ppaiva.gateway.idempotent.types.RequestData;
import br.dev.ppaiva.gateway.server.handler.requests.Response;

public abstract class Idempotent {

	// MINUTES
	private static Long timoutRequestAlive = 1l;

	private volatile static Set<RequestData> requests = new HashSet<>();

	public synchronized static Response<?> getSavedResponse(String method, String path, String body) {
		Set<String> toRemove = new HashSet<>();
		for (RequestData rd : requests) {

			boolean late = false;
			if (rd.getTimestamp().isBefore(LocalDateTime.now())) {
				late = true;
				toRemove.add(rd.getUUID());
			}

			if (rd.getUUID().equals(generateUUID(method, path, body)) && !late) {
				return rd.getResponse();
			}
		}

		// Cleaning
		for (String rm : toRemove) {
			requests.removeIf((rd) -> rd.getUUID().equals(rm));
		}

		return null;
	}

	public synchronized static void saveRequest(String method, String path, String body, Response<?> response) {
		RequestData rd = new RequestData();
		rd.setUUID(generateUUID(method, path, body));
		rd.setMethod(method);
		rd.setPath(path);
		rd.setBody(body);
		rd.setResponse(response);
		rd.setTimestamp(LocalDateTime.now().plusMinutes(timoutRequestAlive));
		requests.add(rd);
	}

	protected static String generateUUID(String method, String path, String body) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.toLowerCase());
		sb.append(path.toLowerCase());
		sb.append(body.trim().toLowerCase());
		return sb.toString().hashCode() + "";
	}
}