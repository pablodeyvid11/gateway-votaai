package br.dev.ppaiva.gateway.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.google.gson.Gson;

import br.dev.ppaiva.gateway.heartbeat.types.VotaAIServer;
import br.dev.ppaiva.gateway.server.handler.requests.Response;
import br.dev.ppaiva.gateway.server.types.enums.CodeResponse;
import br.dev.ppaiva.gateway.server.types.enums.Status;

public class HTTPClient implements Client {

	@Override
	public Response<?> run(String method, String path, String body, VotaAIServer server) throws IOException {

		InetAddress localAddress = InetAddress.getLocalHost();

		InetAddress serverInetAddress = InetAddress.getByName(server.getLocation());
		Socket connection = new Socket(serverInetAddress, server.getPort());
		connection.setSoTimeout(10000);

		OutputStream output = connection.getOutputStream();
		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuilder sb = new StringBuilder();

		sb.append(method);
		sb.append(" ");
		sb.append(path);
		sb.append(" ");
		sb.append("HTTP/1.1\r\n");
		sb.append("Connection: close\r\n");
		sb.append("Content-Length: ");
		sb.append(body.length());
		sb.append("\r\n");
		sb.append("Content-Type: application/json\r\n");
		sb.append("Host: ");
		sb.append(localAddress.getHostAddress());
		sb.append(":");
		sb.append(connection.getLocalPort());
		sb.append("\r\n");
		sb.append("User-Agent: Mozilla/5.0\r\n");
		sb.append("\r\n");
		sb.append(body);
		sb.append("\r\n\n");

		output.write(sb.toString().getBytes());
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
			e.printStackTrace();
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

			String body = String.join("\n", Arrays.copyOfRange(dataTokens, 6, dataTokens.length));
			body = body.substring(0, body.lastIndexOf("}") + 1);

			String status = dataTokens[0].split(" ")[2];
			String code =  dataTokens[0].split(" ")[1];

			String[] response = { status, code, body };
			return response;
		} catch (Exception e) {
			return null;
		}
	}
}
