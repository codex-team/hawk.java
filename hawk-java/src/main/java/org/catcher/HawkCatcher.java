package org.catcher;

public class HawkCatcher implements Thread.UncaughtExceptionHandler{

    private Thread.UncaughtExceptionHandler basicHandler;

    public HawkCatcher(){

    }

    public void init(){
        basicHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e){
        System.out.printf("Exception in thread %s: %s\n", t.getName(), e.getMessage());
        e.printStackTrace();
    }
}
