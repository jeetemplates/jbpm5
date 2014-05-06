/**
 * 
 */
package com.jeetemplates.bpm.persistence.hibernate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.drools.persistence.info.WorkItemInfo;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;

import com.jeetemplates.bpm.SpringApplicationContext;
import com.jeetemplates.bpm.model.WorkItemResult;
import com.jeetemplates.bpm.persistence.JbpmDao;

/**
 * @author jeetemplates
 */
public class JbpmHibernateDao implements JbpmDao {

    private EntityManagerFactory entityManagerFactory;

    private TransactionManager transactionManager;

    @Override
    public void insertResult(Long workItemId, Map<String, Object> result) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            byte[] resultBytes = null;
            if (result != null) {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(byteOut);
                out.writeObject(result);
                resultBytes = byteOut.toByteArray();
            }
            transactionManager.begin();
            entityManager.joinTransaction();
            WorkItemResult entity = new WorkItemResult(workItemId, resultBytes);
            entityManager.persist(entity);
            transactionManager.commit();
            entityManager.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NotSupportedException e) {
            e.printStackTrace();
        }
        catch (SystemException e) {
            e.printStackTrace();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
        catch (RollbackException e) {
            e.printStackTrace();
        }
        catch (HeuristicMixedException e) {
            e.printStackTrace();
        }
        catch (HeuristicRollbackException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> findResult(Long workItemId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String sQuery = "from WorkItemResult where workItemId = :workItemId";
        Query query = entityManager.createQuery(sQuery);
        query.setParameter("workItemId", workItemId);
        List<WorkItemResult> resultList = query.getResultList();
        entityManager.close();
        if (resultList != null && !resultList.isEmpty()) {
            try {
                Map<String, Object> result = null;
                WorkItemResult workItemResult = resultList.get(0);
                if (workItemResult.getResult() != null) {
                    ByteArrayInputStream byteIn = new ByteArrayInputStream(resultList.get(0).getResult());
                    ObjectInputStream in = new ObjectInputStream(byteIn);
                    result = (Map<String, Object>) in.readObject();
                }
                return result;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        // Case no result
        return null;
    }

    /**
     * @param entityManagerFactory
     *            the entityManagerFactory to set
     */
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * @param transactionManager
     *            the transactionManager to set
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WorkItemInfo> findWorkItemInfo(Long processInstanceId) {
        List<WorkItemInfo> result = null;
        EntityManagerFactory emf = (EntityManagerFactory) SpringApplicationContext.getBean("jbpmEMF");
        EntityManager entityManager = emf.createEntityManager();
        String sQuery = "from WorkItemInfo where processInstanceId = :processInstanceId order by workItemId desc";
        Query query = entityManager.createQuery(sQuery);
        query.setParameter("processInstanceId", processInstanceId);
        result = query.getResultList();
        entityManager.close();
        if (result == null) {
            result = new ArrayList<WorkItemInfo>();
        }
        return result;
    }

    @Override
    public void deleteButNot(Long processInstanceId, Long workItemId) {
        try {
            EntityManagerFactory emf = (EntityManagerFactory) SpringApplicationContext.getBean("jbpmEMF");
            EntityManager entityManager = emf.createEntityManager();
            transactionManager.begin();
            entityManager.joinTransaction();
            String sQuery = "delete from WorkItemInfo where processInstanceId = :processInstanceId and workItemId != :workItemId";
            Query query = entityManager.createQuery(sQuery);
            query.setParameter("processInstanceId", processInstanceId);
            query.setParameter("workItemId", workItemId);
            query.executeUpdate();
            transactionManager.commit();
            entityManager.close();
        }
        catch (NotSupportedException e) {
            e.printStackTrace();
        }
        catch (SystemException e) {
            e.printStackTrace();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
        catch (RollbackException e) {
            e.printStackTrace();
        }
        catch (HeuristicMixedException e) {
            e.printStackTrace();
        }
        catch (HeuristicRollbackException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProcessInstanceInfo> retrieveActiveProcesses() {
        List<ProcessInstanceInfo> result = null;
        EntityManagerFactory emf = (EntityManagerFactory) SpringApplicationContext.getBean("jbpmEMF");
        EntityManager entityManager = emf.createEntityManager();
        String sQuery = "from ProcessInstanceInfo where state = 0 or state = 1 ";
        Query query = entityManager.createQuery(sQuery);
        result = query.getResultList();
        entityManager.close();
        if (result == null) {
            result = new ArrayList<ProcessInstanceInfo>();
        }
        return result;
    }
}
