/**
 * 
 */
package com.jeetemplates.bpm.async;

import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;

/**
 * Thread which completes work item.
 * 
 * @author jeetemplates
 */
public class CompleteWorkItemThread extends Thread {

    /**
     * {@link StatefulKnowledgeSession} which will manage the process.
     */
    private StatefulKnowledgeSession ksession;

    /**
     * Work item id to complete.
     */
    private Long workItemId;

    /**
     * Resuls.
     */
    private Map<String, Object> result;

    /**
     * Constructor.
     * 
     * @param ksession
     *            : {@link StatefulKnowledgeSession}
     * @param workItemId
     *            : work item id to complete
     * @param result
     *            : result to pass
     */
    public CompleteWorkItemThread(StatefulKnowledgeSession ksession, Long workItemId, Map<String, Object> result) {
        this.ksession = ksession;
        this.workItemId = workItemId;
        this.result = result;
    }

    @Override
    public void run() {
        ksession.getWorkItemManager().completeWorkItem(workItemId, result);
    }

}
