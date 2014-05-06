package com.jeetemplates.bpm.async;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

import com.jeetemplates.bpm.handler.ReceiveTaskHandler;

public class ThreadJBPM extends java.lang.Thread {

	private StatefulKnowledgeSession ksession;

	private String processId;

	private ReceiveTaskHandler rth;

	public ThreadJBPM(StatefulKnowledgeSession ksession, String processId, ReceiveTaskHandler rth) {
		this.ksession = ksession;
		this.processId = processId;
		this.rth = rth;
	}

	@Override
	public void run() {
		ProcessInstance process = ksession.startProcess(processId);
		rth.messageReceived(process.getId(), "Message_START_PROCESS", "AHAHAH");
		try {
			Thread.sleep(45000);
			rth.messageReceived(process.getId(), "Message_RECEIVE_1", null);
			Thread.sleep(35000);
			rth.messageReceived(process.getId(), "Message_RECEIVE_2", null);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
