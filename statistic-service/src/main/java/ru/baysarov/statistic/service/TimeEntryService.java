package ru.baysarov.statistic.service;


import ru.baysarov.statistic.dto.TimeEntryDto;

public interface TimeEntryService {
  void saveTimeEntry(TimeEntryDto timeEntryDtoIn);
}
