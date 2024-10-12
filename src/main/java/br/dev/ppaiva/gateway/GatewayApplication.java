package br.dev.ppaiva.gateway;

import br.dev.ppaiva.gateway.server.factory.ServerFactory;
import br.dev.ppaiva.gateway.server.types.enums.ServerType;

public class GatewayApplication {
	public static void main(String[] args) {
		ServerFactory.build(8080, ServerType.TCP).start();
	}
}
