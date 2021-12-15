package ru.yanchikdev;


import javax.swing.*;
import java.util.function.Consumer;

public class ThrottleFunction{

    Consumer function;
    Timer throttler;


    ThrottleFunction(Consumer function, int delay){
        this.function = function;
        this.throttler = new Timer(delay, (evt)->{
            function.accept(null);
            ((Timer) evt.getSource()).stop();
        });
    }

    public void run() {
        if(!throttler.isRunning()){
            throttler.start();
        }
    }
}
