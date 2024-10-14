package ru.baysarov.statistic.controller;


import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.baysarov.statistic.dto.TaskDto;
import ru.baysarov.statistic.dto.TasksStatisticResponse;

@RequestMapping("api/v1/statistic")
@RestController
@Slf4j
public class StatisticController {

  @GetMapping()
  public TasksStatisticResponse allTasksStatus(){

    return new TasksStatisticResponse(new ArrayList<TaskDto>());
  }


  //TODO: корректность англ. проверить
  @GetMapping("/time_spent")
  public void timeSpent(@RequestHeader("X-auth-user-email") String email) {


  }
}
