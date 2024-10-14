package br.dev.ppaiva.gateway.heartbeat;

import java.util.ArrayList;
import java.util.List;

import br.dev.ppaiva.gateway.heartbeat.types.VotaAIServer;

public abstract class VotaAI {
	public volatile static List<VotaAIServer> servers = new ArrayList<>();
	private volatile static int incrementalId = 0;

	public synchronized static VotaAIServer getAvailableServer() {
		VotaAIServer server = null;
		while (server == null) {
			server = servers.get(incrementalId % servers.size());
			if (server == null || !server.getActive()) {
				server = null;
			}
			incrementalId++;
		}
		return server;
	}
}