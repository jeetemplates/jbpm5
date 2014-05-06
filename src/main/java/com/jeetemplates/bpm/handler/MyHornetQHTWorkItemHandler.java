/**
 *
 */
package com.jeetemplates.bpm.handler;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.HornetQHTWorkItemHandler;
import org.jbpm.task.TaskService;

/**
 * Using constructor for ip address and port.
 *
 * @author jeetemplates
 */
public class MyHornetQHTWorkItemHandler extends HornetQHTWorkItemHandler {

    /**
     * Constructor
     *
     * @param taskService : task service
     * @param session : {@link StatefulKnowledgeSession}
     * @param owningSessionOnly : event fired only on session owning the handler
     * @param ipAddress : ip address of the task server
     * @param port : port of the task server
     */
    public MyHornetQHTWorkItemHandler(TaskService taskService, KnowledgeRuntime session, boolean owningSessionOnly, String ipAddress, int port) {
        super(taskService, session, owningSessionOnly);
        setIpAddress(ipAddress);
        setPort(port);
    }

}
