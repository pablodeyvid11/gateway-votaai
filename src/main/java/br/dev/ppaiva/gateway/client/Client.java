package br.dev.ppaiva.gateway.client;

import java.io.IOException;

import br.dev.ppaiva.gateway.heartbeat.types.VotaAIServer;
import br.dev.ppaiva.gateway.server.handler.requests.Response;

public interface Client {
	Response<?> run(String method, String path, String body, VotaAIServer server) throws IOException;
}