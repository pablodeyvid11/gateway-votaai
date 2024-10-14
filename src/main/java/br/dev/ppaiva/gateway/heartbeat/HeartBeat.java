package br.dev.ppaiva.gateway.heartbeat;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.dev.ppaiva.gateway.client.Client;
import br.dev.ppaiva.gateway.client.HTTPClient;
import br.dev.ppaiva.gateway.client.TCPClient;
import br.dev.ppaiva.gateway.client.UDPClient;
import br.dev.ppaiva.gateway.heartbeat.types.VotaAIServer;
import br.dev.ppaiva.gateway.server.handler.requests.Response;
import br.dev.ppaiva.gateway.server.types.enums.CodeResponse;
import br.dev.ppaiva.gateway.server.types.enums.ServerType;

public class HeartBeat {
	private static final Logger logger = LogManager.getLogger();

	private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
	private ServerType serverType;

	public HeartBeat(ServerType serverType) {
		this.serverType = serverType;
	}

	public void start() {
		for (VotaAIServer server : VotaAI.servers) {
			Runnable heartBeatServer = new HeartBeatThread(server);
			executor.submit(heartBeatServer);
		}

	}

	class HeartBeatThread implements Runnable {

		private final VotaAIServer server;
		private Long interval;

		public HeartBeatThread(VotaAIServer server) {
			this.server = server;
			this.interval = 1000L;
		}

		@Override
		public void run() {
			while (true) {
				String path = "/health";
				String method = "GET";
				String body = "{}";

				Client client = null;
				switch (serverType) {
				case UDP:
					client = new UDPClient();
					break;
				case TCP:
					client = new TCPClient();
					break;
				case HTTP:
					client = new HTTPClient();
					break;
				default:
					break;
				}

				Response<?> returned = null;
				try {
					returned = client.run(method, path, body, server);
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (returned == null || returned.getCode().equals(CodeResponse.ERROR)) {
					server.setActive(false);
					logger.info(String.format("%s:%d is down", server.getLocation(), server.getPort()));
					if (interval >= 3000L) {
						interval = 1000l;
					} else {
						interval += 1000l;
					}
				} else {
					server.setActive(true);
					logger.info(String.format("%s:%d is healty", server.getLocation(), server.getPort()));
					interval = 1000L;
				}

				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public VotaAIServer getServer() {
			return server;
		}
	}
}