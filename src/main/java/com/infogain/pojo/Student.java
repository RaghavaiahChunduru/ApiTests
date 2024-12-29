package com.infogain.pojo;

import com.infogain.customstratagies.BooleanRandomStrategy;
import com.infogain.customstratagies.GpaAttributeStrategy;
import lombok.Data;
import uk.co.jemos.podam.common.PodamIntValue;
import uk.co.jemos.podam.common.PodamStrategyValue;
import uk.co.jemos.podam.common.PodamStringValue;

@Data
public class Student {

  @PodamStringValue(length = 10)
  private String name;

  @PodamIntValue(minValue = 18, maxValue = 25)
  private int age;

  @PodamStrategyValue(GpaAttributeStrategy.class)
  private double gpa;

  @PodamStrategyValue(BooleanRandomStrategy.class)
  private boolean fullTime;
}
