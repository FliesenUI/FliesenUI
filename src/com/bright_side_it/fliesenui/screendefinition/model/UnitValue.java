package com.bright_side_it.fliesenui.screendefinition.model;

public class UnitValue {
	public enum Unit{PERCENT, CM, MM, PIXEL}
	
	private Unit unit;
	private double value;

	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
}
