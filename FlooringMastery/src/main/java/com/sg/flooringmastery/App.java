package com.sg.flooringmastery;

import com.sg.flooringmastery.controller.FlooringController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args){
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        appContext.scan("com.sg.flooringmastery");
        appContext.refresh();

        FlooringController controller = appContext.getBean("flooringController", FlooringController.class);
        controller.run();

    }
}
