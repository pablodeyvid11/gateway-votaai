package br.dev.ppaiva.gateway.server.handler;

public abstract class MessageHandler implements Runnable {

	protected String taskName;
	protected String data;

	public MessageHandler(String taskName, String data) {
		this.taskName = taskName;
		this.data = data;
	}
}
