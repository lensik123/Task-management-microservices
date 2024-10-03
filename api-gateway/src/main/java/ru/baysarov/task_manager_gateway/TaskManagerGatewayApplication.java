package ru.baysarov.task_manager_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class TaskManagerGatewayApplication {
  public static void main(String[] args) {
    SpringApplication.run(TaskManagerGatewayApplication.class, args);
  }
}

