/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeetemplates.bpm.util.container;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * @author paoesco
 */
public class MyContextFactory implements InitialContextFactory {

    private Context myContext;

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        if (myContext == null) {
            myContext = new MyContext();
        }
        return myContext;
    }

}
