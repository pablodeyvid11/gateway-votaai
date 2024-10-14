package br.dev.ppaiva.gateway.server.handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import com.google.gson.Gson;

import br.dev.ppaiva.gateway.client.Client;
import br.dev.ppaiva.gateway.client.UDPClient;
import br.dev.ppaiva.gateway.heartbeat.VotaAI;
import br.dev.ppaiva.gateway.heartbeat.types.VotaAIServer;
import br.dev.ppaiva.gateway.idempotent.Idempotent;
import br.dev.ppaiva.gateway.server.handler.requests.Response;

public final class UDPMessageHandler extends MessageHandler {

	private DatagramSocket socket;
	private DatagramPacket receivedPacket;

	public UDPMessageHandler(String taskName, DatagramSocket socket, DatagramPacket receivedPacket) {
		super(taskName);
		this.socket = socket;
		this.receivedPacket = receivedPacket;
	}

	@Override
	public void run() {

		String data = new String(receivedPacket.getData());

		String[] tokens = tokenize(data);

		String method = tokens[0];
		String path = tokens[1];
		String body = tokens[2];

		
		DatagramPacket datagramResponse = null;
		try {
			Client client = new UDPClient();

			VotaAIServer votaAiServer = VotaAI.getAvailableServer();
			
			Response<?> response;
			
			response = Idempotent.getSavedResponse(method, path, body);
			if(response != null) {
				logger.info("Package already processed... Redirecting back.");
			} else {
				logger.info("Intercepting packet... Sending to " + votaAiServer.getLocation() + ":" + votaAiServer.getPort());
				response = client.run(method, path, body, votaAiServer);
				Idempotent.saveRequest(method, path, body, response);
			}

			Gson gson = new Gson();

			String bodyResponse = gson.toJson(response).replace("\\u0000", "").trim();

			String responseUDP = String.format("%s\n%d\n\n%s\n", response.getCode().toString(),
					response.getStatus().value(), bodyResponse);

			datagramResponse = new DatagramPacket(responseUDP.getBytes(), responseUDP.length(),
					receivedPacket.getAddress(), receivedPacket.getPort());

		} catch (Exception e) {
			logger.error(e);

			String responseUDP = String.format("ERROR\n\n%s\n", e.getMessage());

			datagramResponse = new DatagramPacket(responseUDP.getBytes(), responseUDP.length(),
					receivedPacket.getAddress(), receivedPacket.getPort());
		}

		try {
			socket.send(datagramResponse);
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
