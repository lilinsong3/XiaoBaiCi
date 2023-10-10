package com.github.lilinsong3.lib;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RandomTestCase {
    public static void testRandom() {
        long startTime =  System.currentTimeMillis();
        int random1 = ThreadLocalRandom.current().nextInt(1, 320350);
        int random2 = ThreadLocalRandom.current().nextInt(1, 320350);
        int random3 = ThreadLocalRandom.current().nextInt(1, 320350);
        System.out.println("耗时：" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime));
        System.out.println("random1：" + random1);
        System.out.println("random2：" + random2);
        System.out.println("random3：" + random3);
    }
}
