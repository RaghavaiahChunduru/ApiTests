package com.example.customstratagies;

import uk.co.jemos.podam.common.AttributeStrategy;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BooleanRandomStrategy implements AttributeStrategy<Boolean> {

    @Override
    public Boolean getValue(Class<?> attrType, List<Annotation> attrAnnotations) {
        return ThreadLocalRandom.current().nextBoolean(); // Randomly returns true or false
    }
}
