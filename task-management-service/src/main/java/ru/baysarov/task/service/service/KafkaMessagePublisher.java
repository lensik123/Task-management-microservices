package ru.baysarov.task.service.service;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * Сервис для публикации сообщений в Kafka.
 */
@Service
@Slf4j
public class KafkaMessagePublisher {

  private final KafkaTemplate<String, Object> template;

  public KafkaMessagePublisher(KafkaTemplate<String, Object> template) {
    this.template = template;
  }

  /**
   * Отправляет сообщение в указанный топик Kafka.
   *
   * @param topic   название топика, в который отправляется сообщение
   * @param message сообщение, которое необходимо отправить
   * @param <T>     тип сообщения
   */
  public <T> void sendToTopic(String topic, T message) {
    String messageType = message.getClass().getName();
    log.info("Sending to {} topic: {}", topic, messageType);
    CompletableFuture<SendResult<String, Object>> future = template.send(
        MessageBuilder.withPayload(message)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .setHeader("type", messageType)
            .build()
    );
    try {
      future.whenComplete((result, ex) -> {
        if (ex == null) {
          System.out.println(
              "Sent message=[" + message.toString() + "] with offset=[" + result.getRecordMetadata()
                  .offset() + "]");
        } else {
          System.out.println(
              "Unable to send message=[" + message.toString() + "] due to : " + ex.getMessage());
        }
      });
    } catch (Exception e) {
      System.out.println("ERROR : " + e.getMessage());
    }
  }
}
