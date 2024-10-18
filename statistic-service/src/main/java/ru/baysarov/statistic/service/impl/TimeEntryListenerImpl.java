package ru.baysarov.statistic.service.impl;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.baysarov.statistic.dto.TimeEntryDto;
import ru.baysarov.statistic.service.TimeEntryService;

/**
 * Слушатель для обработки событий, связанных с записями о времени, получаемых из Kafka.
 */
@Service
public class TimeEntryListenerImpl {

  private final TimeEntryService timeEntryService;


  public TimeEntryListenerImpl(TimeEntryService timeEntryService) {
    this.timeEntryService = timeEntryService;
  }

  /**
   * Слушает события на тему "time_entry" и обрабатывает их.
   *
   * @param timeEntryDto объект записи о времени, полученный из Kafka
   */
  @KafkaListener(topics = "time_entry", groupId = "time_entry_group", containerFactory = "timeEntryDtoListenerFactory")
  public void listen(TimeEntryDto timeEntryDto) {
    System.out.println(timeEntryDto);
    timeEntryService.saveTimeEntry(timeEntryDto);
  }
}
