/**
 * 
 */
package com.jeetemplates.bpm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * @author jeetemplates
 */
@Entity
public class WorkItemResult {

    @Id
    @Column
    private Long workItemId;

    @Lob
    @Column(length = 2147483647)
    private byte[] result;

    /**
     * Default constructor for Hibernate
     */
    public WorkItemResult() {

    }

    public WorkItemResult(Long workItemId, byte[] result) {
        this.workItemId = workItemId;
        this.result = result.clone();
    }

    /**
     * @return the workItemId
     */
    public Long getWorkItemId() {
        return workItemId;
    }

    /**
     * @param workItemId
     *            the workItemId to set
     */
    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }

    /**
     * @return the result
     */
    public byte[] getResult() {
        return result;
    }

    /**
     * @param result
     *            the result to set
     */
    public void setResult(byte[] result) {
        this.result = result;
    }

}
