package com.jeetemplates.bpm.listener;

import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessStartedEvent;

import com.jeetemplates.bpm.command.CMTDisposeCommand;

public class ProcessEventListener extends DefaultProcessEventListener implements org.drools.event.process.ProcessEventListener {

    private CMTDisposeCommand disposeCommand = null;

    public ProcessEventListener(CMTDisposeCommand disposeCommand) {
        this.disposeCommand = disposeCommand;
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        //        System.out.println("#### Session id : " + ((StatefulKnowledgeSession) event.getKnowledgeRuntime()).getId());
        //        System.out.println("#### Process started : " + event.getProcessInstance().getId());
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        System.out.println("################################################################################################ Process ended : " + event.getProcessInstance().getId());
        //        ((StatefulKnowledgeSession) event.getKnowledgeRuntime()).execute(disposeCommand);
    }
}
