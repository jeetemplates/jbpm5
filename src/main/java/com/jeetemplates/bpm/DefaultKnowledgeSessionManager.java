/**
 * 
 */
package com.jeetemplates.bpm;

import com.jeetemplates.bpm.ksession.KnowledgeSessionManager;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * @author jeetemplates
 */
public class DefaultKnowledgeSessionManager extends KnowledgeSessionManager {

    /**
     * @see com.jeetemplates.bpm.KnowledgeSessionManager#getSession()
     */
    @Override
    public StatefulKnowledgeSession getSession() {
        return createSession();
    }

    /**
     * @see com.jeetemplates.bpm.KnowledgeSessionManager#getSession(java.lang.Integer)
     */
    @Override
    public StatefulKnowledgeSession getSession(Integer ksessionId) {
        return loadSession(ksessionId);
    }

}
