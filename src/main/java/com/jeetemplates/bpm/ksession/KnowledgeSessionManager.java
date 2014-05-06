package com.jeetemplates.bpm.ksession;

import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.task.utils.OnErrorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeetemplates.bpm.cache.Cache;
import com.jeetemplates.bpm.cache.Cache.CacheKey;
import com.jeetemplates.bpm.handler.ReceiveTaskHandler;
import com.jeetemplates.bpm.persistence.JbpmDao;

public abstract class KnowledgeSessionManager {

    /**
     * Logger.
     */
    private Logger logger = LoggerFactory.getLogger(KnowledgeSessionManager.class);

    private TransactionManager transactionManager;

    private UserTransaction userTransaction;

    private TransactionSynchronizationRegistry transactionSyncRegistry;

    private EntityManagerFactory entityManagerFactory;

    private KnowledgeBase kbase;

    private JbpmDao jbpmDao;

    private TaskService internalTaskService;

    /**
     * Get {@link StatefulKnowledgeSession}.
     *
     * @return {@link StatefulKnowledgeSession}
     */
    public abstract StatefulKnowledgeSession getSession();

    /**
     * Get {@link StatefulKnowledgeSession} by id. Enable audit.
     *
     * @param ksessionId : id of the {@link StatefulKnowledgeSession}
     * @return {@link StatefulKnowledgeSession}
     */
    public StatefulKnowledgeSession getSessionWithAudit() {
        StatefulKnowledgeSession ksession = getSession();
        registerAudit(ksession);
        return ksession;
    }

    /**
     * Get {@link StatefulKnowledgeSession} by id.
     *
     * @param ksessionId : id of the {@link StatefulKnowledgeSession}
     * @return {@link StatefulKnowledgeSession}
     */
    public abstract StatefulKnowledgeSession getSession(Integer ksessionId);

    /**
     * Get {@link StatefulKnowledgeSession} by id. Enable audit.
     *
     * @param ksessionId : id of the {@link StatefulKnowledgeSession}
     * @return {@link StatefulKnowledgeSession}
     */
    public StatefulKnowledgeSession getSessionWithAudit(Integer ksessionId) {
        StatefulKnowledgeSession ksession = getSession(ksessionId);
        registerAudit(ksession);
        return ksession;
    }

    /**
     * Create a new {@link StatefulKnowledgeSession}.
     *
     * @return new {@link StatefulKnowledgeSession}
     */
    protected StatefulKnowledgeSession createSession() {
        // Create new session.
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, getConfiguration(), getEnvironment());
        registerHandlerAndListener(ksession);
        logger.debug("Created new StatefulKnowledgeSession : id=" + ksession.getId());
        return ksession;
    }

    /**
     * Create a new {@link StatefulKnowledgeSession}. Enable audit.
     *
     * @return new {@link StatefulKnowledgeSession}
     */
    protected StatefulKnowledgeSession createSessionWithAudit() {
        StatefulKnowledgeSession ksession = createSession();
        registerAudit(ksession);
        return ksession;
    }

    /**
     * Load {@link StatefulKnowledgeSession} from database with the given id.
     *
     * @param ksessionId : id of the {@link StatefulKnowledgeSession} to load.
     * @return {@link StatefulKnowledgeSession} loaded.
     */
    public StatefulKnowledgeSession loadSession(int ksessionId) {
        StatefulKnowledgeSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, getConfiguration(), getEnvironment());
        registerHandlerAndListener(ksession);
        return ksession;
    }

    /**
     * Return null {@link KnowledgeSessionConfiguration} (=> default
     * configuration for session).
     *
     * @return null
     */
    private KnowledgeSessionConfiguration getConfiguration() {
        return null;
    }

    /**
     * Get new {@link Environment} for {@link StatefulKnowledgeSession}.
     *
     * @return new {@link Environment}
     */
    private Environment getEnvironment() {
        Environment environment = KnowledgeBaseFactory.newEnvironment();
        environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManagerFactory);
        environment.set(EnvironmentName.TRANSACTION_MANAGER, transactionManager);
        environment.set(EnvironmentName.TRANSACTION, userTransaction);
        environment.set(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY, transactionSyncRegistry);
        return environment;
    }

    /**
     * Register handlers and listeners for {@link StatefulKnowledgeSession}.
     *
     * @param ksession : {@link StatefulKnowledgeSession}
     */
    protected void registerHandlerAndListener(StatefulKnowledgeSession ksession) {
        // Human task handler
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", createLocalHTWorkItemHandler(ksession));
        // Service task handler
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());
        // Receive task handler
        ReceiveTaskHandler rth = new ReceiveTaskHandler(ksession, jbpmDao);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", rth);
        ksession.addEventListener(new com.jeetemplates.bpm.listener.PoolProcessEventListener());
        // Cache used to store receive task for future use.
        Cache.get(CacheKey.MAP_RECEIVE_TASK_HANDLER).put(String.valueOf(ksession.getId()), rth);
    }

    private org.jbpm.task.TaskService createTaskService() {
        return new LocalTaskService(internalTaskService);
    }

    private LocalHTWorkItemHandler createLocalHTWorkItemHandler(StatefulKnowledgeSession ksession) {
        LocalHTWorkItemHandler ht = new LocalHTWorkItemHandler(createTaskService(), ksession, OnErrorAction.LOG);
        ht.setOwningSessionOnly(true);
        ht.connect();
        return ht;
    }

    private void registerAudit(StatefulKnowledgeSession ksession) {
        new JPAWorkingMemoryDbLogger(ksession);
    }

    /**
     * @param transactionManager the transactionManager to set
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * @param userTransaction the userTransaction to set
     */
    public void setUserTransaction(UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    /**
     * @param transactionSyncRegistry the transactionSyncRegistry to set
     */
    public void setTransactionSyncRegistry(TransactionSynchronizationRegistry transactionSyncRegistry) {
        this.transactionSyncRegistry = transactionSyncRegistry;
    }

    /**
     * @param entityManagerFactory the entityManagerFactory to set
     */
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * @param kbase the kbase to set
     */
    public void setKbase(KnowledgeBase kbase) {
        this.kbase = kbase;
    }

    /**
     * @param jbpmDao the jbpmDao to set
     */
    public void setJbpmDao(JbpmDao jbpmDao) {
        this.jbpmDao = jbpmDao;
    }

    /**
     * @param internalTaskService
     */
    public void setInternalTaskService(TaskService internalTaskService) {
        this.internalTaskService = internalTaskService;
    }

}
