package com.github.lilinsong3.lib;

public class DefaultValueTestCase {
    public static class TestClass {
        public Integer id = 0;
        public String name = "de";
        public Boolean tested = true;

        public TestClass(String name, Boolean tested) {
            this.name = name;
            this.tested = tested;
        }

        @Override
        public String toString() {
            return "TestClass [id: " + id + ", name: " + name + ", tested: " + tested + "]";
        }
    }

    private static void testDefaultValue() {
        System.out.println(new TestClass("default", false));
    }
}
