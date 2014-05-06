/**
 * 
 */
package com.jeetemplates.bpm.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pool connection for {@link StatefulKnowledgeSession}.
 * 
 * @author jeetemplates
 */
public class PooledKnowledgeSession {

    /**
     * Logger.
     */
    private Logger logger = LoggerFactory.getLogger(PooledKnowledgeSession.class);

    /**
     * Pool. (Session ID, {@link StatefulKnowledgeSession}).
     */
    private Map<Integer, StatefulKnowledgeSession> pool = null;

    /**
     * Ids of sessions which are in the pool.
     */
    private List<Integer> ids = null;

    /**
     * Max session active. Default 10.
     */
    private Integer maxPoolSize;

    /**
     * Next session to access.
     */
    private Integer next;

    /**
     * Default constructor. Pool size = 10.
     */
    public PooledKnowledgeSession() {
        logger.debug("PoolKnowledgeSession initialized with default max pool size (10)");
        this.pool = new HashMap<Integer, StatefulKnowledgeSession>();
        this.ids = new ArrayList<Integer>();
        this.maxPoolSize = 10;
        this.next = 0;
    }

    /**
     * Constructor with max active pool size.
     * 
     * @param maxPoolSize
     *            : max pool size.
     */
    public PooledKnowledgeSession(Integer maxPoolSize) {
        logger.debug("PoolKnowledgeSession initialized with max pool size = " + maxPoolSize);
        this.pool = new HashMap<Integer, StatefulKnowledgeSession>();
        this.ids = new ArrayList<Integer>();
        this.maxPoolSize = maxPoolSize;
        this.next = 0;
    }

    /**
     * Add a session in a pool.
     * 
     * @param ksession
     *            : session to add
     */
    public void add(StatefulKnowledgeSession ksession) {
        logger.debug("StatefulKnowledgeSession added in pool (kessionId = " + ksession.getId() + ")");
        pool.put(ksession.getId(), ksession);
        ids.add(ksession.getId());
        logger.trace("Pool size : " + getPoolSize());
    }

    /**
     * Get a session in a pool by id.
     * 
     * @param ksessionId
     *            : id of the session to get
     * @return session
     */
    public StatefulKnowledgeSession get(Integer ksessionId) {
        logger.debug("StatefulKnowledgeSession retrieved in pool (kessionId = " + ksessionId + ")");
        return pool.get(ksessionId);
    }

    /**
     * Remove a session from the pool
     * 
     * @param ksessionId
     *            : id of the session to remove
     */
    public void remove(Integer ksessionId) {
        logger.debug("StatefulKnowledgeSession removed from pool (kessionId = " + ksessionId + ")");
        pool.remove(ksessionId);
        ids.remove(ksessionId);
        logger.trace("Pool size : " + getPoolSize());
    }

    /**
     * @return the next session
     */
    public StatefulKnowledgeSession getNext() {
        logger.debug("getNext StatefulKnowledgeSession");
        if (pool.isEmpty()) {
            return null;
        }
        if (next >= ids.size()) {
            next = 0;
        }
        StatefulKnowledgeSession ksession = pool.get(ids.get(next++));
        logger.debug("Next StatefulKnowledgeSession (kessionId = " + ksession.getId() + ")");
        return ksession;
    }

    /**
     * Return the pool size.
     * 
     * @return pool size.
     */
    public Integer getPoolSize() {
        return pool.size();
    }

    /**
     * @return the maxPoolSize
     */
    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * @param maxPoolSize
     *            the maxPoolSize to set
     */
    public void setMaxPoolSize(Integer maxPoolSize) {
        logger.debug("Setting max pool size to " + maxPoolSize);
        this.maxPoolSize = maxPoolSize;
    }

}
