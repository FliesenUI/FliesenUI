package com.bright_side_it.fliesenui.base.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.bright_side_it.fliesenui.base.util.BaseUtil;

public class BaseUtilTest {
    @Test
    public void test_getDTOInstanceName_normal() {
        String result = BaseUtil.getDTOInstanceName("test.hello");
        assertEquals("test", result);
    }

    @Test
    public void test_getDTOInstanceName_noSubField() {
        String result = BaseUtil.getDTOInstanceName("test");
        assertEquals("test", result);
    }

    @Test
    public void test_getDTOFieldChain_simple() {
        List<String> result = BaseUtil.getDTOFieldChain("test.one.two.three");
        assertEquals(3, result.size());
        assertEquals("one", result.get(0));
        assertEquals("two", result.get(1));
        assertEquals("three", result.get(2));
    }

    @Test
    public void test_getDTOFieldChain_noItems() {
        List<String> result = BaseUtil.getDTOFieldChain("test");
        assertEquals(0, result.size());
    }
}
