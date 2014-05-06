/**
 * 
 */
package com.jeetemplates.bpm.persistence;

import java.util.List;
import java.util.Map;

import org.drools.persistence.info.WorkItemInfo;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;

/**
 * @author jeetemplates
 */
public interface JbpmDao {

    void insertResult(Long workItemId, Map<String, Object> result);

    Map<String, Object> findResult(Long workItemId);

    List<WorkItemInfo> findWorkItemInfo(Long processInstanceId);

    void deleteButNot(Long processInstanceId, Long workItemId);

    List<ProcessInstanceInfo> retrieveActiveProcesses();

}
