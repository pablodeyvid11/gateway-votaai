package br.dev.ppaiva.gateway.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.google.gson.Gson;

import br.dev.ppaiva.gateway.heartbeat.types.VotaAIServer;
import br.dev.ppaiva.gateway.server.handler.requests.Response;
import br.dev.ppaiva.gateway.server.types.enums.CodeResponse;
import br.dev.ppaiva.gateway.server.types.enums.Status;

public class TCPClient implements Client {

	@Override
	public Response<?> run(String method, String path, String body, VotaAIServer server) throws IOException {

		Socket connection = new Socket(server.getLocation(), server.getPort());
		connection.setSoTimeout(5000);

		OutputStream output = connection.getOutputStream();
		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuilder sb = new StringBuilder();
		sb.append(method);
		sb.append(" ");
		sb.append(path);
		sb.append("\n\n");
		sb.append(body);
		sb.append("\n\n");

		output.write(sb.toString().getBytes(StandardCharsets.UTF_8));
		output.flush();

		try {
			StringBuilder responseBuilder = new StringBuilder();
			String line;

			while ((line = input.readLine()) != null) {
				responseBuilder.append(line).append("\n");
			}

			String receivedMessageString = responseBuilder.toString();
			String[] tokens = tokenize(receivedMessageString);

			String status = tokens[0];
			String code = tokens[1];
			String data = tokens[2];

			Object dataObject = new Gson().fromJson(data, Object.class);

			Response<?> responseData = new Response<>(CodeResponse.valueOf(status),
					Status.valueOf(Integer.parseInt(code)), dataObject);

			return responseData;

		} catch (Exception e) {
			return new Response<>(CodeResponse.ERROR, Status.REQUEST_TIMEOUT, null);
		} finally {
			output.close();
			input.close();
			connection.close();
		}
	}

	private String[] tokenize(String data) {
		try {
			String[] dataTokens = data.split("\n");

			String body = String.join("\n", Arrays.copyOfRange(dataTokens, 2, dataTokens.length));
			body = body.substring(0, body.lastIndexOf("}") + 1);

			String status = dataTokens[0];
			String code = dataTokens[1];

			String[] response = { status, code, body };
			return response;
		} catch (Exception e) {
			return null;
		}
	}
}
