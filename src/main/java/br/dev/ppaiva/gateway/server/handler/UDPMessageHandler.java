package br.dev.ppaiva.gateway.server.handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public final class UDPMessageHandler extends MessageHandler {

	private DatagramSocket socket;
	private DatagramPacket receivedPacket;

	public UDPMessageHandler(String taskName, String data, DatagramSocket socket, DatagramPacket receivedPacket) {
		super(taskName, data);
		this.socket = socket;
		this.receivedPacket = receivedPacket;
	}

	@Override
	public void run() {
		DatagramPacket datagramResponse = new DatagramPacket(receivedPacket.getData(), receivedPacket.getData().length,
				receivedPacket.getAddress(), receivedPacket.getPort());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socket.send(datagramResponse);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
