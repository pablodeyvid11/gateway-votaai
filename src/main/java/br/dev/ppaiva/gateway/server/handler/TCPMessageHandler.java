package br.dev.ppaiva.gateway.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.google.gson.Gson;

import br.dev.ppaiva.gateway.client.Client;
import br.dev.ppaiva.gateway.client.TCPClient;
import br.dev.ppaiva.gateway.heartbeat.VotaAI;
import br.dev.ppaiva.gateway.heartbeat.types.VotaAIServer;
import br.dev.ppaiva.gateway.server.handler.requests.Response;

public final class TCPMessageHandler extends MessageHandler {

	private Socket socket;

	public TCPMessageHandler(String taskName, Socket socket) {
		super(taskName);
		this.socket = socket;
	}

	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			StringBuilder data = new StringBuilder();
			String line;

			// lendo o METODO /ROTA
			line = reader.readLine();
			data.append(line);
			data.append("\n\n");

			// lendo linha vazia
			reader.readLine();

			while ((line = reader.readLine()) != null) {
				if (line.equals("")) {
					break;
				}
				data.append(line).append("\n");
			}

			String[] tokens = tokenize(data.toString());
			if (tokens != null) {
				String method = tokens[0];
				String path = tokens[1];
				String body = tokens[2];

				Client client = new TCPClient();
				VotaAIServer votaAiServer = VotaAI.getAvailableServer();
				logger.info("Intercepting packet... Sending to " + votaAiServer.getLocation() + ":"
						+ votaAiServer.getPort());

				Response<?> response = client.run(method, path, body, votaAiServer);
				Gson gson = new Gson();

				String bodyResponse = gson.toJson(response).replace("\\u0000", "").trim();

				String responseTCP = String.format("%s\n%d\n\n%s\n", response.getCode().toString(),
						response.getStatus().value(), bodyResponse);

				socket.getOutputStream().write(responseTCP.getBytes(StandardCharsets.UTF_8));
				socket.getOutputStream().flush();
			}
		} catch (Exception e) {
			logger.error(e);

			String responseTCP = String.format("ERROR\n500\n\n%s\n", e.getMessage());

			try {
				socket.getOutputStream().write(responseTCP.getBytes(StandardCharsets.UTF_8));
				socket.getOutputStream().flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			e.printStackTrace();
		}

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String[] tokenize(String data) {
		try {
			String[] dataTokens = data.split("\n");

			String body = String.join("\n", Arrays.copyOfRange(dataTokens, 1, dataTokens.length));
			body = body.substring(0, body.lastIndexOf("}") + 1);

			String method = dataTokens[0].split(" ")[0];
			String path = dataTokens[0].split(" ")[1];

			String[] response = { method, path, body };
			return response;
		} catch (Exception e) {
			return null;
		}
	}
}
