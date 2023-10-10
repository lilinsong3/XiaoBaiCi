package com.github.lilinsong3.xiaobaici.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class StringUtilTest {

    @Test
    public void computeMatchableRanges() {
        assertFalse(StringUtil.computeMatchableRanges("三十六行，行行出状元", "行行").isEmpty());
    }
}