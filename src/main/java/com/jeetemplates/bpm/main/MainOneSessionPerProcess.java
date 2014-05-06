/**
 *
 */
package com.jeetemplates.bpm.main;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jeetemplates.bpm.ksession.KnowledgeSessionManager;
import com.jeetemplates.bpm.SpringApplicationContext;
import com.jeetemplates.bpm.async.ThreadJBPM;
import com.jeetemplates.bpm.cache.Cache;
import com.jeetemplates.bpm.handler.ReceiveTaskHandler;

/**
 * @author jeetemplates
 */
public class MainOneSessionPerProcess {

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        SpringApplicationContext.setApplicationContext(new ClassPathXmlApplicationContext("applicationContext-onesessionperprocess.xml"));
        KnowledgeSessionManager manager = (KnowledgeSessionManager) SpringApplicationContext.getBean("knowledgeSessionManager");
        Cache.put(Cache.CacheKey.MAP_RECEIVE_TASK_HANDLER, new HashMap<String, Object>());
        for (int i = 0; i < 9000; i++) {
            StatefulKnowledgeSession ksession = manager.getSession();
            ThreadJBPM t1 = new ThreadJBPM(ksession, "com.jeetemplates.bpm.process", (ReceiveTaskHandler) Cache.get(Cache.CacheKey.MAP_RECEIVE_TASK_HANDLER).get(String.valueOf(ksession.getId())));
            t1.start();
        }
    }

}
