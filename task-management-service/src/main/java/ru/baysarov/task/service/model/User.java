package ru.baysarov.task.service.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


//TODO: проверить нужен ли этот класс вообще.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

  private int id;
  private String email;

}
