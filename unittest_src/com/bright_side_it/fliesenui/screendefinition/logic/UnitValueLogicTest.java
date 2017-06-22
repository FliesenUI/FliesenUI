package com.bright_side_it.fliesenui.screendefinition.logic;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bright_side_it.fliesenui.screendefinition.model.UnitValue;
import com.bright_side_it.fliesenui.screendefinition.model.UnitValue.Unit;

public class UnitValueLogicTest {
	
	@Test
	public void testParse_simplePercent() throws Exception{
		String string = "10%";
		UnitValue result = new UnitValueLogic().parse(string, null, null);
		assertEquals(10d, result.getValue(), 0);
		assertEquals(UnitValue.Unit.PERCENT, result.getUnit());
	}

	@Test
	public void testParse_simplePercentWithSpace() throws Exception{
		String string = "10 %";
		UnitValue result = new UnitValueLogic().parse(string, null, null);
		assertEquals(10d, result.getValue(), 0);
		assertEquals(UnitValue.Unit.PERCENT, result.getUnit());
	}

	@Test
	public void testParse_simpleCM() throws Exception{
		String string = "10cm";
		UnitValue result = new UnitValueLogic().parse(string, null, null);
		assertEquals(10d, result.getValue(), 0);
		assertEquals(UnitValue.Unit.CM, result.getUnit());
	}

	@Test
	public void testParse_simpleCMWithFraction() throws Exception{
		String string = "10.2cm";
		UnitValue result = new UnitValueLogic().parse(string, null, null);
		assertEquals(10.2d, result.getValue(), 0);
		assertEquals(UnitValue.Unit.CM, result.getUnit());
	}
	
	@Test
	public void testParse_cmWithDefaultUnit() throws Exception{
		String string = "10.2";
		UnitValue result = new UnitValueLogic().parse(string, Unit.CM, null);
		assertEquals(10.2d, result.getValue(), 0);
		assertEquals(UnitValue.Unit.CM, result.getUnit());
	}
	
	@Test
	public void testParse_simpleMM() throws Exception{
		String string = "10mm";
		UnitValue result = new UnitValueLogic().parse(string, null, null);
		assertEquals(10d, result.getValue(), 0);
		assertEquals(UnitValue.Unit.MM, result.getUnit());
	}

	@Test
	public void testParse_simplePixel() throws Exception{
		String string = "10px";
		UnitValue result = new UnitValueLogic().parse(string, null, null);
		assertEquals(10d, result.getValue(), 0);
		assertEquals(UnitValue.Unit.PIXEL, result.getUnit());
	}
	
	@Test
	public void testParse_empty() throws Exception{
		String string = "";
		UnitValue result = new UnitValueLogic().parse(string, null, null);
		assertEquals(null, result);
	}
}
