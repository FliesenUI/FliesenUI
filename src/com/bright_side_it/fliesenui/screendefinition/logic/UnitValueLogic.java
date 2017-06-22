package com.bright_side_it.fliesenui.screendefinition.logic;

import java.util.Map;
import java.util.TreeMap;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.screendefinition.model.UnitValue;
import com.bright_side_it.fliesenui.screendefinition.model.UnitValue.Unit;

public class UnitValueLogic {
	private static final Map<String, UnitValue.Unit> STRING_TO_UNIT_MAP = createStringToUnitMap();
	private static final Map<UnitValue.Unit, String> UNIT_TO_STRING_MAP = BaseUtil.invertMap(STRING_TO_UNIT_MAP);

	private static Map<String, Unit> createStringToUnitMap() {
		Map<String, Unit> result = new TreeMap<>();
		result.put("cm", Unit.CM);
		result.put("mm", Unit.MM);
		result.put("px", Unit.PIXEL);
		result.put("%", Unit.PERCENT);
		
		return result;
	}
	
	public String toCSSString(UnitValue unitValue){
		return "" + unitValue.getValue() + UNIT_TO_STRING_MAP.get(unitValue.getUnit());
	}
	
	public UnitValue parse(String string, Unit defaultUnit, UnitValue valueIfNullOrEmpty) throws Exception{
		if (string == null){
			return valueIfNullOrEmpty;
		}
		String useString = string.trim().toLowerCase();
		if (useString.isEmpty()){
			return valueIfNullOrEmpty;
		}
		
		String valueString = useString;
		Unit unit = defaultUnit;
		
		for (Map.Entry<String, Unit> i: STRING_TO_UNIT_MAP.entrySet()){
			if (useString.endsWith(i.getKey())){
				valueString = useString.substring(0, useString.length() - i.getKey().length()).trim();
				unit = i.getValue();
			}
		}
		if (unit == null){
			throw new Exception("Unknown unit in text '" + useString + ". Possible values: " + STRING_TO_UNIT_MAP.keySet());
		}
		
		Double value = null;
		try{
			value = Double.valueOf(valueString);
		} catch (Exception e) {
			throw new Exception("Could not read number value from text '" + valueString + "'");
		}
		
		UnitValue result = new UnitValue();
		result.setUnit(unit);
		result.setValue(value);
		
		return result;
	}

}
