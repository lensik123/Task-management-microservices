package ru.baysarov.task.service.service;

import ru.baysarov.task.service.dto.TimeEntryDtoIn;

public interface TimeEntryService {
  void saveTimeEntry(Integer taskId,String userEmail, TimeEntryDtoIn timeEntryDtoIn);
}
