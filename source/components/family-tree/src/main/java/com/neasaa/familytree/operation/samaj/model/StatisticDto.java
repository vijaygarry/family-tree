package com.neasaa.familytree.operation.samaj.model;

import java.io.Serial;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StatisticDto {
  @Serial private static final long serialVersionUID = 1L;

  private String label;
  private String key;
  private int value;
}
