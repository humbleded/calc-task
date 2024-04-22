package com.hc.calc.task;

import com.hc.calc.task.config.LoadCalcConfig;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.thread.ThreadPool;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Holger
 * @date 2018//4/26
 */
@SpringBootApplication
@EnableAutoConfiguration
public class CalcTaskApplication implements CommandLineRunner {

    @Autowired
    LoadCalcConfig loadCalcConfig;

    @Autowired
    private BeanFactory factory;
    
    @Autowired
    private ThreadPool threadPool;
    
    @Autowired
    private DataService dataService;

    public static void main(String[] args) {
	SpringApplication.run(CalcTaskApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
	threadPool.startPool(dataService,factory);
	
    }
}
