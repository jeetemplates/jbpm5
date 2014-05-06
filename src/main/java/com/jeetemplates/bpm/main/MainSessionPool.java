package com.jeetemplates.bpm.main;

import java.util.HashMap;
import java.util.List;

import org.drools.persistence.info.WorkItemInfo;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jeetemplates.bpm.ksession.KnowledgeSessionManager;
import com.jeetemplates.bpm.SpringApplicationContext;
import com.jeetemplates.bpm.async.ThreadJBPM;
import com.jeetemplates.bpm.cache.Cache;
import com.jeetemplates.bpm.cache.Cache.CacheKey;
import com.jeetemplates.bpm.handler.ReceiveTaskHandler;
import com.jeetemplates.bpm.persistence.JbpmDao;
import com.jeetemplates.bpm.async.CompleteWorkItemThread;
import com.jeetemplates.bpm.async.StartProcessThread;

/**
 * @author jeetemplates
 */
public class MainSessionPool {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SpringApplicationContext.setApplicationContext(new ClassPathXmlApplicationContext("applicationContext-pool.xml"));
		KnowledgeSessionManager manager = (KnowledgeSessionManager) SpringApplicationContext.getBean("poolKnowledgeSessionManager");
		Cache.put(CacheKey.MAP_RECEIVE_TASK_HANDLER, new HashMap<String, Object>());
		for (int i = 0; i < 800; i++) {
			System.out.println("################################# " + i);
			StatefulKnowledgeSession ksession = manager.getSession();
			Thread t1 = new ThreadJBPM(ksession, "test2", (ReceiveTaskHandler) Cache.get(CacheKey.MAP_RECEIVE_TASK_HANDLER).get(String.valueOf(ksession.getId())));
			t1.start();
		}
		// for (int i = 0; i < 5000; i++) {
		// System.out.println("################################# " + i);
		// StatefulKnowledgeSession ksession = manager.getSession();
		// Thread t1 = new ThreadSignalJBPM(ksession, "test1-timer");
		// t1.start();
		// }
		// JbpmDao jbpmDao = (JbpmDao)
		// SpringApplicationContext.getBean("jbpmDao");
		// List<ProcessInstanceInfo> listProcesses =
		// jbpmDao.retrieveActiveProcesses();
		// for (ProcessInstanceInfo process : listProcesses) {
		// reloadProcess(manager, process.getProcessInstanceId().intValue(),
		// process.getProcessInstanceId());
		// }
		// ReceiveTaskHandler rth = (ReceiveTaskHandler)
		// Cache.get(CacheKey.MAP_RECEIVE_TASK_HANDLER).get(String.valueOf(1870));
		// rth.messageReceived(2317, "Message_START_PROCESS", "");
		Thread.sleep(100000);
	}

	private static void reloadProcess(KnowledgeSessionManager manager, Integer ksessionId, Long processInstanceId) {
		JbpmDao jbpmDao = (JbpmDao) SpringApplicationContext.getBean("jbpmDao");
		StatefulKnowledgeSession ksession = manager.loadSession(ksessionId);
		ProcessInstance process = ksession.getProcessInstance(processInstanceId);
		if (process.getState() == 1) { // Process en cours
			List<WorkItemInfo> results = jbpmDao.findWorkItemInfo(processInstanceId);
			if (results.size() > 0) { // Si le processus a une tâche en cours
				boolean executed = false;
				int index = 0;
				while (!executed && index < results.size()) {
					WorkItemInfo it = results.get(index);
					if ("Receive Task".equalsIgnoreCase(it.getName())) {
						ReceiveTaskHandler rth = (ReceiveTaskHandler) Cache.get(CacheKey.MAP_RECEIVE_TASK_HANDLER).get(String.valueOf(ksession.getId()));
						WorkItem workItem = ((org.drools.process.instance.WorkItemManager) ksession.getWorkItemManager()).getWorkItem(it.getId());
						if (index == 0) { // Si c'est le premier
							rth.executeWorkItem(workItem, ksession.getWorkItemManager());
						} else {
							new CompleteWorkItemThread(ksession, workItem.getId(), jbpmDao.findResult(workItem.getId())).start();
						}
						executed = true;
					} else if ("Human Task".equalsIgnoreCase(it.getName())) {
						WorkItem workItem = ((org.drools.process.instance.WorkItemManager) ksession.getWorkItemManager()).getWorkItem(it.getId());
						new CompleteWorkItemThread(ksession, workItem.getId(), jbpmDao.findResult(workItem.getId())).start();
						executed = true;
					}
					index++;
				}
			}
		} else if (process.getState() == 0) { // Pending
			ReceiveTaskHandler rth = (ReceiveTaskHandler) Cache.get(CacheKey.MAP_RECEIVE_TASK_HANDLER).get(String.valueOf(ksession.getId()));
			new StartProcessThread(ksession, processInstanceId, rth).start();
		}
	}
}
