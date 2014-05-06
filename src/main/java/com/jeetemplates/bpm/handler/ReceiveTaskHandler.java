package com.jeetemplates.bpm.handler;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.node.CompositeContextNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeetemplates.bpm.persistence.JbpmDao;

/**
 * New ReceiveTaskHandler. Here you have the method :
 * messageReceived(long processInstanceId, String messageId, Object message)
 * and can pass the processInstanceId parameter (which you dont have in jBPM ReceiveTaskHandler library)
 * Used to resume process after lost of the session.
 * 
 * @author jeetemplates
 */
public class ReceiveTaskHandler implements WorkItemHandler {

    private static Logger logger = LoggerFactory.getLogger(ReceiveTaskHandler.class.getName());

    private Map<String, Long> waiting = new HashMap<String, Long>();

    private KnowledgeRuntime ksession;

    private JbpmDao workItemResultDao;

    /**
     * Default constructor
     * 
     * @param ksession
     *            the ksession
     */
    public ReceiveTaskHandler(KnowledgeRuntime ksession, JbpmDao workItemResultDao) {
        this.ksession = ksession;
        this.workItemResultDao = workItemResultDao;
    }

    /**
     * @param ksession
     *            the ksession to set
     */
    public void setKnowledgeRuntime(KnowledgeRuntime ksession) {
        this.ksession = ksession;
    }

    private String constructKey(long processInstanceId, String messageId) {
        return processInstanceId + "|" + messageId;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        long processInstanceId = workItem.getProcessInstanceId();
        String messageId = (String) workItem.getParameter("MessageId");
        waiting.put(constructKey(processInstanceId, messageId), workItem.getId());
        // If waiting map previously contained a mapping for messageId, the old value is replaced !!!
    }

    private Long findWorkItemId(NodeInstance prmNodeInstance, String prmMessageId) {
        Long workItemId = null;
        if (prmNodeInstance instanceof WorkItemNodeInstance) {
            WorkItemNodeInstance workItemNode = (WorkItemNodeInstance) prmNodeInstance;
            WorkItem workItem = ((org.drools.process.instance.WorkItemManager)ksession.getWorkItemManager()).getWorkItem(workItemNode.getWorkItemId());
            if (workItem.getName().equals("Receive Task") && workItem.getParameter("MessageId").equals(prmMessageId)) {
                workItemId = workItem.getId();
            }
        }

        if (prmNodeInstance instanceof CompositeContextNodeInstance) {
            for (NodeInstance ni : ((CompositeContextNodeInstance) prmNodeInstance).getNodeInstances(false)) {
                workItemId = findWorkItemId(ni, prmMessageId);
                if (workItemId != null) {
                    break;
                }
            }
        }
        return workItemId;
    }

    /**
     * Receive a message and send it to the process
     * 
     * @param processInstanceId
     *            Process instance ID
     * @param messageId
     *            Mesage ID
     * @param message
     *            Message
     * @throws IllegalArgumentException
     *             when the system doesn't find a workItem matching the given processInstanceId
     */
    public void messageReceived(long processInstanceId, String messageId, Object message) throws IllegalArgumentException {
        Long workItemId = waiting.get(constructKey(processInstanceId, messageId));
        if (workItemId == null) {
            // See if this is a work item persisted pefore the re-start of the server
            ProcessInstance pi = ksession.getProcessInstance(processInstanceId);
            final RuleFlowProcessInstance workflowProcessInstance = ((RuleFlowProcessInstance) pi);
            for (NodeInstance nodeInstance : workflowProcessInstance.getNodeInstances()) {
                try {
                    workItemId = findWorkItemId(nodeInstance, messageId);
                }
                catch (NullPointerException e) {
                    // do nothing. this happens when you try to send an incorrect message.
                    logger.error("ReceiveTaskHandler : NullPointerException - incorrect message send ("+messageId+") on nodeInstance " + nodeInstance.getNodeName());
                }
                if (workItemId != null) {
                    break;
                }
            }
        }

        if (workItemId != null) {
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Message", message);
            workItemResultDao.insertResult(workItemId, results);
            ksession.getWorkItemManager().completeWorkItem(workItemId, results);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        long processInstanceId = workItem.getProcessInstanceId();
        String messageId = (String) workItem.getParameter("MessageId");
        waiting.remove(constructKey(processInstanceId, messageId));
    }

}