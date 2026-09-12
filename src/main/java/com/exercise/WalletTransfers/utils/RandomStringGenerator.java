package com.exercise.WalletTransfers.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class RandomStringGenerator {
    private static volatile AtomicReference<RandomStringGenerator> randomStringUtil;

    org.apache.commons.text.RandomStringGenerator generator;

    private RandomStringGenerator() {
        char[][] pairs = {{'a', 'z'}, {'0', '9'}, {'A', 'Z'}};
        generator = new org.apache.commons.text.RandomStringGenerator.Builder()
                .withinRange(pairs).get();
    }

    public static RandomStringGenerator getInstance() {
        AtomicReference<RandomStringGenerator> instance = randomStringUtil;
        if (instance != null) {
            return instance.get();
        }

        synchronized (RandomStringGenerator.class) {
            if (randomStringUtil == null) {
                randomStringUtil = new AtomicReference<>(new RandomStringGenerator());
            }

            return randomStringUtil.get();
        }
    }

    public String generateString(int len) {
        return generator.generate(len);
    }
}
