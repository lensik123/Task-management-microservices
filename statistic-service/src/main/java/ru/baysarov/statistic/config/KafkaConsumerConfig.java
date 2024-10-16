package ru.baysarov.statistic.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;

import java.util.HashMap;
import java.util.Map;
import ru.baysarov.statistic.dto.TaskDto;
import ru.baysarov.statistic.dto.TimeEntryDto;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TaskDto> taskDtoListenerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TaskDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(taskDtoConsumerFactory());
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TimeEntryDto> timeEntryDtoListenerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TimeEntryDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(timeEntryDtoConsumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, TaskDto> taskDtoConsumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), taskDtoJsonDeserializer());
  }

  @Bean
  public ConsumerFactory<String, TimeEntryDto> timeEntryDtoConsumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), timeEntryDtoJsonDeserializer());
  }

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9097,localhost:9098,localhost:9099");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "task_group");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    return props;
  }

  @Bean
  public JsonDeserializer<TaskDto> taskDtoJsonDeserializer() {
    DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
    Map<String, Class<?>> mappings = new HashMap<>();
    mappings.put("ru.baysarov.task.service.dto.TaskDtoOut", TaskDto.class);
    typeMapper.setIdClassMapping(mappings);

    JsonDeserializer<TaskDto> deserializer = new JsonDeserializer<>(TaskDto.class);
    deserializer.setTypeMapper(typeMapper);
    deserializer.setUseTypeMapperForKey(true);
    deserializer.addTrustedPackages("*");

    return deserializer;
  }

  @Bean
  public JsonDeserializer<TimeEntryDto> timeEntryDtoJsonDeserializer() {
    DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
    Map<String, Class<?>> mappings = new HashMap<>();
    mappings.put("ru.baysarov.task.service.dto.TimeEntryDtoOut", TimeEntryDto.class);
    typeMapper.setIdClassMapping(mappings);

    JsonDeserializer<TimeEntryDto> deserializer = new JsonDeserializer<>(TimeEntryDto.class);
    deserializer.setTypeMapper(typeMapper);
    deserializer.setUseTypeMapperForKey(true);
    deserializer.addTrustedPackages("*");

    return deserializer;
  }
}
