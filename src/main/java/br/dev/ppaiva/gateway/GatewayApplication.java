package br.dev.ppaiva.gateway;

import java.util.Arrays;

import br.dev.ppaiva.gateway.heartbeat.HeartBeat;
import br.dev.ppaiva.gateway.heartbeat.VotaAI;
import br.dev.ppaiva.gateway.heartbeat.types.VotaAIServer;
import br.dev.ppaiva.gateway.server.factory.ServerFactory;
import br.dev.ppaiva.gateway.server.types.enums.ServerType;

public class GatewayApplication {
	public static void main(String[] args) {

		VotaAIServer s1 = new VotaAIServer("localhost", 9090, false);
		VotaAIServer s2 = new VotaAIServer("localhost", 9091, false);
		VotaAIServer s3 = new VotaAIServer("localhost", 9092, false);

		VotaAI.servers.addAll(Arrays.asList(s1, s2, s3));

		HeartBeat hb = new HeartBeat(ServerType.HTTP);
		hb.start();

		ServerFactory.build(8080, ServerType.HTTP).start();
	}
}
