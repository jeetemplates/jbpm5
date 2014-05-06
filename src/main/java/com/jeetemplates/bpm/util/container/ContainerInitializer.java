/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeetemplates.bpm.util.container;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

/**
 * @author paoesco
 */
public class ContainerInitializer {

    private InitialContext context = null;

    public ContainerInitializer(DataSource dataSource, TransactionManager transactionManager, UserTransaction userTransaction, TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
        try {
            // Init JNDI to bind HSQLDB datasource and transaction manager
            context = createContext();
            context.bind("jdbc/jbpm-ds", dataSource);
            context.bind("java:/TransactionManager", transactionManager);
            context.bind("java:/UserTransaction", userTransaction);
            context.bind("java:/TransactionSynchronizationRegistry", transactionSynchronizationRegistry);
        } catch (NamingException ex) {
            Logger.getLogger(ContainerInitializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private InitialContext createContext() throws NamingException {
        MyContextFactoryBuilder builder = new MyContextFactoryBuilder();
        NamingManager.setInitialContextFactoryBuilder(builder);
        context = new InitialContext(); // properties are defined in jndi.properties
        return context;
    }

    public void destroy() {
        try {
            context.close();
        } catch (NamingException ex) {
            Logger.getLogger(ContainerInitializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
