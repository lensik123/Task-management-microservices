package ru.baysarov.task.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "time_entries")
@Getter
@Setter
@ToString
public class TimeEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @Column(name = "user_id")
  private Integer userId;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "hours")
  private Float hours;


}