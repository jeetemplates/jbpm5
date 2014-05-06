/**
 *
 */
package com.jeetemplates.bpm.ksession;

import com.jeetemplates.bpm.ksession.KnowledgeSessionManager;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * @author jeetemplates
 */
public class SingletonKnowledgeSessionManager extends KnowledgeSessionManager {

    private StatefulKnowledgeSession ksession;

    private Boolean isInitialized = Boolean.FALSE;

    /**
     * @return @see com.jeetemplates.bpm.KnowledgeSessionManager#getSession()
     */
    @Override
    public StatefulKnowledgeSession getSession() {
        if (!isInitialized) {
            ksession = createSession();
            isInitialized = Boolean.TRUE;
        }
        return ksession;
    }

    /**
     * Load {@link StatefulKnowledgeSession}. Replace the singleton "ksession".
     *
     * @return
     * @see
     * com.jeetemplates.bpm.KnowledgeSessionManager#getSession(java.lang.Integer)
     */
    @Override
    public StatefulKnowledgeSession getSession(Integer ksessionId) {
        ksession = loadSession(ksessionId);
        isInitialized = Boolean.TRUE;
        return ksession;
    }

    /**
     * @param ksession the ksession to set
     */
    public void setKsession(StatefulKnowledgeSession ksession) {
        isInitialized = Boolean.TRUE;
        this.ksession = ksession;
    }

}
