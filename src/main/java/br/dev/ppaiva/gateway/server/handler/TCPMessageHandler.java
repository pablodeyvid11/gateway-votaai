package br.dev.ppaiva.gateway.server.handler;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public final class TCPMessageHandler extends MessageHandler {

	private Socket nextClient;

	public TCPMessageHandler(String taskName, String data, Socket nextClient) {
		super(taskName, data);
		this.nextClient = nextClient;
	}

	@Override
	public void run() {
		String responseMessage = "Processed: " + data;
		try {

			nextClient.getOutputStream().write(responseMessage.getBytes(StandardCharsets.UTF_8));
			nextClient.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			nextClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
