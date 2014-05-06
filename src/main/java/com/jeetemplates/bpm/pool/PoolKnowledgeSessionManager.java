package com.jeetemplates.bpm.pool;

import org.drools.runtime.StatefulKnowledgeSession;

import com.jeetemplates.bpm.ksession.KnowledgeSessionManager;

/**
 * Manager for pooling {@link StatefulKnowledgeSession}.
 * 
 * @author jeetemplates
 */
public class PoolKnowledgeSessionManager extends KnowledgeSessionManager {

    /**
     * {@link PooledKnowledgeSession}.
     */
    private PooledKnowledgeSession pool;

    /**
     * Load session from database.
     * 
     * @param ksessionId
     *            : id of the session to load.
     * @param cache
     *            : cache of the receive tasks
     * @return {@link StatefulKnowledgeSession} loaded from id
     */
    @Override
    public StatefulKnowledgeSession loadSession(int ksessionId) {
        StatefulKnowledgeSession ksession = super.loadSession(ksessionId);
        pool.add(ksession);
        return ksession;

    }

    /**
     * Get a {@link StatefulKnowledgeSession}. Pool managed.
     * 
     * @return {@link StatefulKnowledgeSession}
     */
    @Override
    public synchronized StatefulKnowledgeSession getSession() {
        StatefulKnowledgeSession ksession = null;
        if (pool.getPoolSize() < pool.getMaxPoolSize()) {
            ksession = createSession();
            pool.add(ksession);
        }
        else {
            ksession = pool.getNext();
        }
        return ksession;
    }

    /**
     * Dispose a session. Remove it from the pool.
     * 
     * @param ksessionId
     */
    public void disposeSession(Integer ksessionId) {
        pool.remove(ksessionId);
    }

    /**
     * Get a {@link StatefulKnowledgeSession} by id.
     * 
     * @param ksessionId
     *            : id of the {@link StatefulKnowledgeSession} to load.
     * @return {@link StatefulKnowledgeSession} corresponding to the id
     */
    @Override
    public StatefulKnowledgeSession getSession(Integer ksessionId) {
        return pool.get(ksessionId);
    }

    /**
     * @param pool the pool to set
     */
    public void setPool(PooledKnowledgeSession pool) {
        this.pool = pool;
    }

}
