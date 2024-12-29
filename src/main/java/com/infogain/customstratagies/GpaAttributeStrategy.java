package com.infogain.customstratagies;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import uk.co.jemos.podam.common.AttributeStrategy;

public class GpaAttributeStrategy implements AttributeStrategy<Double> {

  @Override
  public Double getValue(Class<?> attrType, List<Annotation> attrAnnotations) {
    double value = ThreadLocalRandom.current().nextDouble(0.0, 4.0); // Range: 0.0 to 4.0
    return Math.round(value * 100.0) / 100.0; // Round to 2 decimal places
  }
}
