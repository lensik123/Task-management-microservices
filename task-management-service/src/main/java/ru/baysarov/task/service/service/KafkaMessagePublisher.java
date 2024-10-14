package ru.baysarov.task.service.service;


import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.baysarov.task.service.dto.TaskDtoOut;

@Service
public class KafkaMessagePublisher {

  private final KafkaTemplate<String, Object> template;

  public KafkaMessagePublisher(KafkaTemplate<String, Object> template) {
    this.template = template;
  }

  public void sendTaskToTopic(String topic, TaskDtoOut taskDtoOut) {
    CompletableFuture<SendResult<String, Object>> future = template.send(topic, taskDtoOut);
    try {
      future.whenComplete((result, ex) -> {
        if (ex == null) {
          System.out.println(
              "Sent message=[" + taskDtoOut.toString() + "] with offset=[" + result.getRecordMetadata().offset()
                  + "]");
        } else {
          System.out.println("Unable to send message=[" + taskDtoOut.toString() + "] due to : " + ex.getMessage());
        }
      });
    } catch (Exception e) {
      System.out.println("ERROR : " + e.getMessage());
    }
  }
}
