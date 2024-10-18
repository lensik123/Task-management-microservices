package ru.baysarov.statistic.service;

import ru.baysarov.statistic.dto.TimeEntryDto;

public interface TimeEntryListener {

  void onTimeEntry(TimeEntryDto timeEntryDto);
}
