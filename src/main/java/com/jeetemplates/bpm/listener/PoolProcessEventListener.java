package com.jeetemplates.bpm.listener;

import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.runtime.StatefulKnowledgeSession;

public class PoolProcessEventListener extends DefaultProcessEventListener implements org.drools.event.process.ProcessEventListener {

    public PoolProcessEventListener() {

    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        System.out.println("#### Session id : " + ((StatefulKnowledgeSession) event.getKnowledgeRuntime()).getId());
        System.out.println("#### Process started : " + event.getProcessInstance().getId());
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        System.out.println("######################################################################## Process ended : " + event.getProcessInstance().getId());
    }
}
