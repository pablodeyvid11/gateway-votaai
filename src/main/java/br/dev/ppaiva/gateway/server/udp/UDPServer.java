package br.dev.ppaiva.gateway.server.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.dev.ppaiva.gateway.server.Server;
import br.dev.ppaiva.gateway.server.handler.UDPMessageHandler;

public class UDPServer extends Server {
	private static final Logger logger = LogManager.getLogger();

	public UDPServer(int port) {
		super(port);
	}

	@Override
	public void startServer() {
		logger.info("UDP SERVER STARTED ON PORT " + port);
		try (DatagramSocket socket = new DatagramSocket(port)){
			Random random = new Random(System.currentTimeMillis());
			while (true) {
				byte[] receivedMessage = new byte[1024];
				DatagramPacket receivedPacket = new DatagramPacket(receivedMessage, receivedMessage.length);
				socket.receive(receivedPacket);
				String requestName = String.format("%d%d", System.currentTimeMillis(), random.nextLong(1000, 9999));
				Runnable udpMsgHandler = new UDPMessageHandler(requestName, new String(receivedPacket.getData()),
						socket, receivedPacket);

				executorHandle.execute(udpMsgHandler);

				logger.info(String.format("RECEIVED REQUEST #%s", requestName));
				logger.info(String.format("REQUEST #%s FROM %s:%d scheduled", requestName,
						receivedPacket.getAddress().toString(), receivedPacket.getPort()));
			}
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (java.lang.IllegalArgumentException e) {
			logger.error(e);
			e.printStackTrace();
		}

		logger.info("UDP SERVER TERMINATING...");
	}
}
