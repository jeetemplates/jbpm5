package com.jeetemplates.bpm.async;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

public class ThreadSignalJBPM extends java.lang.Thread {

    private StatefulKnowledgeSession ksession;

    private String processId;

    public ThreadSignalJBPM(StatefulKnowledgeSession ksession, String processId) {
        this.ksession = ksession;
        this.processId = processId;
    }

    @Override
    public void run() {
        ProcessInstance process = ksession.startProcess(processId);
        ksession.signalEvent("Signal_CONTINUE", "", process.getId());
    }
}
