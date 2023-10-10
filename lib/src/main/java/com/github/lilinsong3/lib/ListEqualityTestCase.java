package com.github.lilinsong3.lib;

import java.util.ArrayList;
import java.util.List;

public class ListEqualityTestCase {
    private static List<Integer> bList = new ArrayList<>(4);
    public static void testListEquality() {
        bList.add(1);
        bList.add(2);
        final List<Integer> aList = new ArrayList<>(getList());
        aList.add(3);
        System.out.println("a: " + aList);
        System.out.println("b: " + bList);
        System.out.println("a==b: " + (aList == bList));
    }
    public static void setList(List<Integer> list) {
        bList = list;
    }

    public static List<Integer> getList() {
        return bList;
    }
}
