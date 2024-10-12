package br.dev.ppaiva.gateway.server.factory;

import br.dev.ppaiva.gateway.server.Server;
import br.dev.ppaiva.gateway.server.tcp.TCPServer;
import br.dev.ppaiva.gateway.server.types.enums.ServerType;
import br.dev.ppaiva.gateway.server.udp.UDPServer;

public class ServerFactory {
	public static Server build(int port, ServerType type) {

		switch (type) {
		case HTTP:
			throw new IllegalArgumentException("Invalid server type");
		case TCP:
			return new TCPServer(port);
		case UDP:
			return new UDPServer(port);
		default:
			throw new IllegalArgumentException("Invalid server type");
		}
	}
}
