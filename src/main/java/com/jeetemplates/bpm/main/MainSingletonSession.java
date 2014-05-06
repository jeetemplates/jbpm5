package com.jeetemplates.bpm.main;

import java.util.HashMap;

import org.drools.runtime.StatefulKnowledgeSession;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jeetemplates.bpm.ksession.KnowledgeSessionManager;
import com.jeetemplates.bpm.SpringApplicationContext;
import com.jeetemplates.bpm.async.ThreadJBPM;
import com.jeetemplates.bpm.cache.Cache;
import com.jeetemplates.bpm.cache.Cache.CacheKey;
import com.jeetemplates.bpm.handler.ReceiveTaskHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author jeetemplates
 */
public class MainSingletonSession {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationContext applicationContext = null;
        try {
            applicationContext = new ClassPathXmlApplicationContext("applicationContext-singleton.xml");
            SpringApplicationContext.setApplicationContext(applicationContext);
            Cache.put(CacheKey.MAP_RECEIVE_TASK_HANDLER, new HashMap<String, Object>());
            KnowledgeSessionManager manager = (KnowledgeSessionManager) SpringApplicationContext.getBean("singletonKnowledgeSessionManager");
            StatefulKnowledgeSession ksession = manager.getSession();
            for (int i = 0; i < 1; i++) {
                Thread t1 = new ThreadJBPM(ksession, "com.jeetemplates.bpm.process-servicetask-timer", (ReceiveTaskHandler) Cache.get(CacheKey.MAP_RECEIVE_TASK_HANDLER).get(
                        String.valueOf(ksession.getId())));
                t1.start();
            }
//            StatefulKnowledgeSession ksession = manager.getSession(1886);
//            RuleFlowProcessInstance process = (RuleFlowProcessInstance) ksession.getProcessInstance(2332);
//            process.setKnowledgeRuntime((InternalKnowledgeRuntime) ksession.getKnowledgeBase().newStatefulKnowledgeSession(KnowledgeBaseFactory.newKnowledgeSessionConfiguration(), KnowledgeBaseFactory.newEnvironment()));
//            for (org.drools.runtime.process.NodeInstance nodeDrools : process.getNodeInstances(true)) {
//                NodeInstance nodeJbpm = (NodeInstance) nodeDrools;
//                System.out.println("node id : " + nodeJbpm.getNodeId());
//            }
//                ksession.getWorkItemManager().completeWorkItem(2307, null);
            //        ReceiveTaskHandler rth = (ReceiveTaskHandler) Cache.get(CacheKey.MAP_RECEIVE_TASK_HANDLER).get(String.valueOf(1883));
            //        rth.messageReceived(2330, "Message_START_PROCESS", "");

            ThreadJBPM.sleep(15000);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (applicationContext != null) {
                ((ConfigurableApplicationContext) applicationContext).close();
            }
        }
    }

}
