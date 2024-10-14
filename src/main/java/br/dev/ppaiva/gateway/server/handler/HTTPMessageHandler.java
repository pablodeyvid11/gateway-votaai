package br.dev.ppaiva.gateway.server.handler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import com.google.gson.Gson;

import br.dev.ppaiva.gateway.server.handler.requests.Response;
import br.dev.ppaiva.gateway.server.types.enums.Status;

public final class HTTPMessageHandler extends MessageHandler {

	private Socket socket;

	public HTTPMessageHandler(String taskName, Socket socket) {
		super(taskName);
		this.socket = socket;
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

			String headerLine = in.readLine();
			StringTokenizer tokenizer = new StringTokenizer(headerLine);

			String method = tokenizer.nextToken();

			if (!(method.equals("GET") || method.equals("POST"))) {
				sendResponse(socket, Status.valueOf(405), "Method Not Allowed");
				return;
			}

			String path = tokenizer.nextToken();
//			Connection
			in.readLine();

//			Content-Length
			in.readLine();

//			Content-Type
			in.readLine();

//			Host
			in.readLine();

//			User-Agent
			in.readLine();

//			blank
			in.readLine();

			StringBuilder data = new StringBuilder();

			String line = "";

			while ((line = in.readLine()) != null) {
				if (line.equals("")) {
					break;
				}
				data.append(line).append("\n");
			}

			String body = data.toString();

			Response<?> response = (Response<?>) requestDispatcher.dispatch(method, path, body);
			Gson gson = new Gson();

			String bodyResponse = gson.toJson(response).replace("\\u0000", "").trim();

			StringBuilder responseBuffer = new StringBuilder();

			responseBuffer.append(bodyResponse);

			sendResponse(socket, response.getStatus(), responseBuffer.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendResponse(Socket socket, Status status, String responseString) {
		String statusLine;
		String serverHeader = "Server: WebServer\r\n";
		String contentTypeHeader = "Content-Type: application/json\r\n";
		try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());) {

			statusLine = "HTTP/1.0 " + status.value() + " " + status.getReasonPhrase() + "\r\n";
			String contentLengthHeader = "Content-Length: " + responseString.length() + "\r\n";

			out.writeBytes(statusLine);
			out.writeBytes(serverHeader);
			out.writeBytes(contentTypeHeader);
			out.writeBytes(contentLengthHeader);
			out.writeBytes("\r\n");
			out.writeBytes(responseString);

			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
