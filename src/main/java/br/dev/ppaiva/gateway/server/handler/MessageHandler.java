package br.dev.ppaiva.gateway.server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.dev.ppaiva.gateway.server.handler.requests.RequestDispatcher;

public abstract class MessageHandler implements Runnable {

	protected static final Logger logger = LogManager.getLogger();

	protected String taskName;
	protected RequestDispatcher requestDispatcher;

	public MessageHandler(String taskName) {
		this.taskName = taskName;
		this.requestDispatcher = RequestDispatcher.getInstance();
	}
}
