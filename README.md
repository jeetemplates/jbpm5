Integration of jBPM 5 into a Java SE environment
====
Official documentation : http://docs.jboss.org/jbpm/v5.4/userguide/
<br/>
Used http://docs.jboss.org/jbpm/v5.4/userguide/ch.integration.html to do this template.
<br/>
<br/>
About :
- jBPM 5.5.0.Final : workflow engine
- Drools 5.6.0.Final : knowledge repository (base & session) (JBPM dependency)
- Spring 3.0.6.RELEASE (JBPM dependency for integration with Spring)
- JPA 1 / Hibernate 3.3.2.GA (JBPM dependency for persistence)
- JTA 1.1 / JBoss JTA 4.16.6.Final (Container environment for jBPM transactions)
- HSQLDB
- Simple process (start process, timer 10s, end process)
- jBPM / Drools configuration, Spring integration
- Performance test
- Three strategies for knowledge sessions :
  - One knowledge session per process instance (applicationContext-onesessionperprocess.xml)
  - Singleton knowledge session  (applicationContext-singleton.xml)
  - Pool of knowledge sessions (applicationContext-pool.xml)
- Dispose knowledge session after process is finished (CMTDisposeCommand & ProcessEventListener, see http://planet.jboss.org/post/dispose_session_in_cmt_environment;jsessionid=ABD191959760F400D857A9FDA5DB0058)
- Reload knowledge sessions
- Restart processes (using custome ReceiveTaskHandler to persist receive task result. See com.jeetemplates.bpm.main.MainSessionPool.reloadProcess)
- To use JTA, a fake container with local JNDI server has been implemented (see package com.jeetemplates.bpm.util.container. Loaded in init-container.xml)
- Launch example by looking at main classes in package com.jeetemplates.bpm.main

/!\ Important /!\
- jBPM editor is not enough to create BPMN 2.0 processes (Does not provide all BPMN2 activities). Use http://www.eclipse.org/bpmn2-modeler/
- Look in the code of Handler (e.g. ServiceTaskHandler, ReceiveTaskHandler) for modelisation or develop your own handlers
- Knowledge session execute command is synchronized
- LocalTaskService is not thread safe
- LocalContainerEntityManagerFactory is not XA datasource compliant (see Javadoc)
- Store result of workitem (receive task, human task) to replay it in case of server crash
- Knowledge session must be loaded (in JVM) if timers need to be fired
- Before use of jBPM API, open a transaction and close it (especially to avoid NullPointerException on kruntime in ProcessInstanceImpl object)
- jBPM does not provide "out of the box" clustering architecture
- jBPM does not provide "out of the box" process restart
- Dissociate jBPM & Task Service (communication with HornetQ) to avoid nested transactions and potential problems

Useful links :
- jBPM 5 Developer Guide http://www.amazon.com/jBPM-Developer-Guide-Mauricio-Salatino/dp/1847195687
- http://blog.akquinet.de/2012/04/13/use-jbpm5-embedded-within-a-java-ee-6-application/
- https://community.jboss.org/thread/169261
- https://community.jboss.org/thread/228042