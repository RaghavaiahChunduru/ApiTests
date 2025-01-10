package com.infogain.utils;

import com.github.javafaker.Faker;

public final class FakerUtil {

    private FakerUtil() {
    }

    private static final ThreadLocal<Faker> fakerInstance = ThreadLocal.withInitial(Faker::new);

    public static Faker getInstance() {
        return fakerInstance.get();
    }
}
