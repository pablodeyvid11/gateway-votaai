package br.dev.ppaiva.gateway.server.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.dev.ppaiva.gateway.server.Server;
import br.dev.ppaiva.gateway.server.handler.TCPMessageHandler;

public class TCPServer extends Server {

	private static final Logger logger = LogManager.getLogger();

	public TCPServer(Integer port) {
		super(port);
	}

	@Override
	public void startServer() {
		logger.info("TCP SERVER STARTED ON PORT " + port);
		Random random = new Random(System.currentTimeMillis());

		try (ServerSocket server = new ServerSocket(port)) {

			while (true) {

				try {
					Socket socket = server.accept();

					String requestName = String.format("%d%d", System.currentTimeMillis(), random.nextLong(1000, 9999));

					Runnable tcpMsgHandler = new TCPMessageHandler(requestName, socket);
					executorHandle.execute(tcpMsgHandler);

					logger.info(String.format("RECEIVED REQUEST #%s", requestName));
					logger.info(String.format("REQUEST #%s FROM %s:%d scheduled", requestName,
							socket.getInetAddress().toString(), socket.getPort()));

				} catch (IOException e) {
					logger.error(e);
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (java.lang.IllegalArgumentException e) {
			logger.error(e);
			e.printStackTrace();
		}

		logger.info("TCP SERVER TERMINATING...");
	}
}
