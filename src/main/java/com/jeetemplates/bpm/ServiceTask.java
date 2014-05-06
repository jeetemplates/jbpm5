package com.jeetemplates.bpm;

import com.jeetemplates.bpm.async.ThreadJBPM;


public class ServiceTask {

    public String execute(String message) {
        System.out.println("BEGIN service task");
        try {
        	long random = (long) Math.random();
            ThreadJBPM.sleep(random * 1000L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("END service task");
        return message;
    }

}
