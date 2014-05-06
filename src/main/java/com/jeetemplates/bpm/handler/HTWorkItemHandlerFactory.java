/**
 * 
 */
package com.jeetemplates.bpm.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.GenericHTWorkItemHandler;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Factory to create Human Task Work Item Handler.
 * See spring-task.xml to declare this factory.
 * 
 * @author jeetemplates
 */
public class HTWorkItemHandlerFactory {

    /**
     * Logger.
     */
    private Logger logger = LoggerFactory.getLogger(HTWorkItemHandlerFactory.class);

    /**
     * Ip of task server.
     */
    private String ipAddress;

    /**
     * Port of task server.
     */
    private int port;

    /**
     * List of handlers.
     */
    private List<GenericHTWorkItemHandler> handlers = null;

    /**
     * Init method.
     */
    public void init() {
        handlers = new ArrayList<GenericHTWorkItemHandler>();
    }

    /**
     * Create a work item handler using HornetQ
     * 
     * @param ksession
     *            : {@link StatefulKnowledgeSession}.
     * @return work item handler connected to task server hornetq
     */
    public GenericHTWorkItemHandler createHTWorkItemHandler(StatefulKnowledgeSession ksession) {
        AsyncTaskService asyncTaskService = new AsyncHornetQTaskClient(UUID.randomUUID().toString());
        TaskService taskService = new SyncTaskServiceWrapper(asyncTaskService);
        GenericHTWorkItemHandler htWorkItemHandler = new MyHornetQHTWorkItemHandler(taskService, ksession, true, ipAddress, port);
        htWorkItemHandler.connect();
        handlers.add(htWorkItemHandler);
        return htWorkItemHandler;
    }

    /**
     * Destroy method : disconnect all clients.
     */
    public void destroy() {
        for (GenericHTWorkItemHandler client : handlers) {
            try {
                logger.info("Disposing GenericHTWorkItemHandler");
                client.dispose();
            }
            catch (Exception e) {
                logger.info("Impossible to dispose GenericHTWorkItemHandler");
            }
        }
    }

    /**
     * @param ipAddress
     *            the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

}
