/**
 * 
 */
package com.jeetemplates.bpm.command;

import javax.transaction.Synchronization;
import javax.transaction.TransactionManager;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * Used to dispose a ksession after end of a process.
 * 
 * @author jeetemplates
 */
public class CMTDisposeCommand implements GenericCommand<Void> {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 7019250098499177419L;

    private TransactionManager transactionManager = null;

    public CMTDisposeCommand(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Void execute(Context context) {

        final StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        try {
            transactionManager.getTransaction().registerSynchronization(new Synchronization() {

                @Override
                public void beforeCompletion() {
                    // not used here
                }

                @Override
                public void afterCompletion(int arg0) {
                    ksession.dispose();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
