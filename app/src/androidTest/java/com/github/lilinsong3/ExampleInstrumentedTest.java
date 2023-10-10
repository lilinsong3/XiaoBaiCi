package com.github.lilinsong3;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.lilinsong3.xiaobaici.util.StringUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        assertEquals("com.github.lilinsong3.xiaobaici", appContext.getPackageName());
        assertFalse(StringUtil.computeMatchableRanges("三十六行，行行出状元", "行行").isEmpty());
    }
}