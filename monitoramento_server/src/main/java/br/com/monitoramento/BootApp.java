package br.com.monitoramento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import br.com.monitoramento.controller.MonitorServiceController;

@SpringBootApplication
@EnableScheduling
@ComponentScan({ "br.com.monitoramento.integration.task" })
public class BootApp {
	
	public static void main(String[] args) throws Exception {
        SpringApplication.run(new Object[] { BootApp.class, MonitorServiceController.class }, args);
    }
}
