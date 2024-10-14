package br.dev.ppaiva.gateway.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import com.google.gson.Gson;

import br.dev.ppaiva.gateway.heartbeat.types.VotaAIServer;
import br.dev.ppaiva.gateway.server.handler.requests.Response;
import br.dev.ppaiva.gateway.server.types.enums.CodeResponse;
import br.dev.ppaiva.gateway.server.types.enums.Status;

public class UDPClient implements Client {

	@Override
	public Response<?> run(String method, String path, String body, VotaAIServer server) throws IOException {

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress inetAddress = InetAddress.getByName(server.getLocation());

		clientSocket.setSoTimeout(5000);

		StringBuilder sb = new StringBuilder();
		sb.append(method);
		sb.append(" ");
		sb.append(path);
		sb.append("\n");
		sb.append(body);

		byte[] sendMessage = sb.toString().getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, inetAddress, server.getPort());
		clientSocket.send(sendPacket);

		byte[] receivedMessage = new byte[2048];

		DatagramPacket receivePacket = new DatagramPacket(receivedMessage, receivedMessage.length);

		try {
			clientSocket.receive(receivePacket);
			String receivedMessageString = new String(receivePacket.getData(), 0, receivePacket.getLength());

			String[] tokens = tokenize(receivedMessageString);

			String status = tokens[0];
			String code = tokens[1];
			String data = tokens[2];

			Object dataObject = new Gson().fromJson(data, Object.class);

			Response<?> responseData = new Response<>(CodeResponse.valueOf(status),
					Status.valueOf(Integer.parseInt(code)), dataObject);

			clientSocket.close();
			return responseData;
		} catch (SocketTimeoutException e) {
			return new Response<>(CodeResponse.ERROR, Status.REQUEST_TIMEOUT, null);
		} finally {
			clientSocket.close();
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
