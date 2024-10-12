package br.dev.ppaiva.gateway.server.types;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import br.dev.ppaiva.gateway.server.types.enums.UDPDataMethod;
import br.dev.ppaiva.gateway.server.types.enums.UDPDataVersion;

public class UDPDataMessage {
	private UDPDataVersion version;
	private UDPDataMethod method;
	private InetAddress localAddress;
	private String data;

	public UDPDataMessage(UDPDataVersion version, UDPDataMethod method, Object data) throws UnknownHostException {
		this.version = version;
		this.method = method;
		this.data = new Gson().toJson(data);
		this.localAddress = InetAddress.getLocalHost();
	}

	public UDPDataMessage(UDPDataVersion version, UDPDataMethod method, String jsonData) throws UnknownHostException {
		this.version = version;
		this.method = method;
		this.data = jsonData;
		this.localAddress = InetAddress.getLocalHost();
	}

	public UDPDataVersion getVersion() {
		return version;
	}

	public UDPDataMethod getMethod() {
		return method;
	}

	public InetAddress getLocalAddress() {
		return localAddress;
	}

	public String getData() {
		return data;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getMethod().getMethod());
		sb.append(" ");
		sb.append(getVersion().getVersion());
		sb.append("\n");
		sb.append(localAddress.getHostAddress());
		sb.append("\n");
		sb.append(data);
		sb.append("\n");

		return sb.toString();
	}
}
