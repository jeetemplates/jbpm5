/**
 * 
 */
package com.jeetemplates.bpm.async;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

import com.jeetemplates.bpm.handler.ReceiveTaskHandler;


/**
 * Thread which starts process.
 * 
 * @author jeetemplates
 */
public class StartProcessThread extends Thread {

    private StatefulKnowledgeSession ksession;

    private Long processInstanceId;

    private ReceiveTaskHandler rth;

    public StartProcessThread(StatefulKnowledgeSession ksession, Long processInstanceId, ReceiveTaskHandler rth){
        this.ksession = ksession;
        this.processInstanceId = processInstanceId;
        this.rth = rth;
    }

    @Override
    public void run() {
        ProcessInstance process = ksession.startProcessInstance(processInstanceId);
        rth.messageReceived(process.getId(), "Message_START_PROCESS", "AHAHAH");
    }

}
