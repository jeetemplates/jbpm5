/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeetemplates.bpm.util.container;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

/**
 *
 * @author paoesco
 */
public class MyContextFactoryBuilder implements InitialContextFactoryBuilder {

    private InitialContextFactory myContextFactory;

    @Override
    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
        if (myContextFactory == null) {
            myContextFactory = new MyContextFactory();
        }
        return myContextFactory;
    }

}
