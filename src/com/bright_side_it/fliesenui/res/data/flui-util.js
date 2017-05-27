// Logging (that can be disabled by disabling one row)
// #########################################

var log = function(message){
//	console.log(message);
}


// FILTER LOGIC
// #########################################

/**
 If optinalPropery is null, the result is a string with all items of the existingItemsMap.
 If optinalPropery is provided, the result is a string the property value of the existingItemsMap.
 */
buildExistingItemsString = function(existingItemsMap, optinalPropery){
	var existingItems = "";
	if (optinalPropery != null){
		var item = existingItemsMap[optinalPropery];
		if (item != null){
			existingItems += item.toLowerCase();
		}
	} else {
		for (i in existingItemsMap){
			var item = existingItemsMap[i];
			if (item != null){
				existingItems += item.toLowerCase() + "\n";
			}
		}
	}
	return existingItems;
}

/**
  param searchItemsList: list of items to search
  param existingItemsMap: values of each column
  param optinalPropery: property to check or null to check in all values
 */
containsAll = function(searchItemsList, existingItemsMap, optinalPropery){
	// log("############ Start: containsAll ####################");
	var existingItemsString = buildExistingItemsString(existingItemsMap, optinalPropery);
	// log("containsAll: existingItemsString = '" + existingItemsString + "'");
	for (var i in searchItemsList){
		// log("containsAll: checking i = " + i + " = '" + searchItemsList[i] + "'");
		if (existingItemsString.indexOf(searchItemsList[i].toLowerCase()) <= -1){
			// log("containsAll: not found = '" + searchItemsList[i].toLowerCase() + "'");
		    return false;
		}
	}
	// log("############ End:   containsAll ####################");
	return true; 
}

/**
  param searchItemsList: list of items to search
  param existingItemsMap: values of each column
  param optinalPropery: property to check or null to check in all values
 */
containsAny = function(searchItemsList, existingItemsMap, optinalPropery){
	var existingItemsString = buildExistingItemsString(existingItemsMap, optinalPropery);
	// log("containsAny: existingItemsString = '" + existingItemsString + "'");
	for (var i in searchItemsList){
		// log("containsAny: checking i = " + i + " = '" + searchItemsList[i] + "'");
		if (existingItemsString.indexOf(searchItemsList[i].toLowerCase()) > -1){
		    return true;
		}
	}
	return false; 
}

/**
 returns a list containing items. Processing quotes, columns and strings as separators
 */
parseExpressions = function(searchTextRaw){
	var searchText = searchTextRaw.trim();
	var result = [];
	var inQuotes = false;
	var currentItem = "";
	
	for (var i = 0, len = searchText.length; i < len; i++ ){
		var c = searchText[i];
		if (c == '"'){
			if (inQuotes){
				inQuotes = false;
				result.push(currentItem);
				currentItem = "";
			} else {
				inQuotes = true;
			}
		} else if ((c == ' ') && (!inQuotes)){
			if (currentItem.length > 0){
				result.push(currentItem);
			}
			currentItem = "";
		} else if ((c == ':') && (!inQuotes)){
			if (currentItem.length > 0){
				result.push(currentItem);
			}
			result.push(":");
			currentItem = "";
		} else {
			currentItem += c;
		}
	}
	if (currentItem.length > 0){
		result.push(currentItem);
	}
	return result;
}

/**
 map contains for each key a list of values. The first char in the key is either + (for include in search) or - (for exclude in search)
 the +/- may be applied in the key or the value. This method makes sure it will be put in the key
 keys and values are converted to lowercase
 */
addToListInMap = function(map, key, valueItem){
	if (valueItem == ''){
		return;
	}
	var useKey = key;
	var useValueItem = valueItem;  
	if (valueItem[0] == '-'){
		useKey = "-" + useKey;
		useValueItem = valueItem.substring(1).trim();
	}
	if (useValueItem == ''){
		return;
	}
	if (useKey == ''){
		useKey = "+";
	}
	if ((useKey[0] != '+') && (useKey[0] != '-')){
		useKey = '+' + useKey;
	}
	useKey = useKey.toLowerCase();
	useValueItem = useValueItem.toLowerCase();
	var currentList = map[useKey];
	if (typeof currentList == "undefined"){
		map[useKey] = [];
	}
	map[useKey].push(useValueItem);
}

/**
 creates a map. key starts for "+" to include or "-" to exclude followed by the attribute name.
 If the key is just "+" or "-" then there is no attribute (filter for all attributes)
 */
parseColumnNameToSearchTermsMap = function(searchText){
	var result = new Object();
	var expressions = parseExpressions(searchText);
	// log("parseColumnNameToSearchTermsMap expressions = " + JSON.stringify(expressions));
	var length = expressions.length;
	var i = 0;
	while (i < length){
		// log("parseColumnNameToSearchTermsMap i = " + i + " = '" + expressions[i] + "'");
		if (i + 2 <= length){
			if (expressions[i + 1] == ':'){
				var expressionToAdd = "";
				if (i + 2 < length){
					expressionToAdd = expressions[i + 2];
				}
				addToListInMap(result, expressions[i], expressionToAdd);
				i += 3;
			} else {
				addToListInMap(result, "", expressions[i]);
				i ++;
			}
		} else {
			addToListInMap(result, "", expressions[i]);
			i ++;
		}
	}
	return result;
}

/**
 creates a map. key starts for "+" to include or "-" to exclude.
 followed by "c0", "c1", "c2" etc. for the column index, "all" for all columns.
 there is an extra entry '?' that contains all unknown column names in a list.
 */
createSearchTermsMap = function(searchText, columnNames){
	var result = new Object();
	var columnNameToSearchTermsMap = parseColumnNameToSearchTermsMap(searchText);
	var columnNameToIndexMap = new Object();
	for (var i in columnNames){
		columnNameToIndexMap[columnNames[i].toLowerCase()] = "c" + i;
	}
	//log("createSearchTermsMap columnNameToIndexMap = " + JSON.stringify(columnNameToIndexMap));
	
	for (var i in columnNameToSearchTermsMap){
		var key = "?";
		
		if (i == '+'){
			key = "+all";
		} else if (i == '-'){
			key = "-all";
		} else if (i == ''){
			key = "?";
		} else {
			key = columnNameToIndexMap[i.substring(1)];
			if (typeof key == "undefined"){
				key = "?";
			} else {
				key = i[0] + key;
			}
		}
		
		if (key == '?'){
			var currentList = result[key];
			if (typeof currentList == "undefined"){
				result[key] = [];
			}
			result[key].push(i.substring(1));
		} else {
			result[key] = columnNameToSearchTermsMap[i];			
		}
	}
	
	return result;
}

filterBySearchTerms = function(row, searchTermsMap){
	for (var i in searchTermsMap){
		var searchTerms = searchTermsMap[i];
		
		//log("filterBySearchTerms i = '" + i + "', searchTerms = " + JSON.stringify(searchTerms));
		
		if (i != '?'){
			if (i == '+all'){
				if (!containsAll(searchTerms, row, null)){
					//log("filterBySearchTerms not matched (a)");
					return false;
				}
			} else if (i == '-all'){
				if (containsAny(searchTerms, row, null)){
					//log("filterBySearchTerms not matched (b)");
					return false;
				}
			} else {
				var prefix = i[0];
				var columnID = i.substring(1);
				if (prefix == '+'){
					if (!containsAll(searchTerms, row, columnID)){
						//log("filterBySearchTerms not matched (c)");
						return false;
					}
				} else if (prefix == '-'){
					if (containsAny(searchTerms, row, columnID)){
						//log("filterBySearchTerms not matched (d)");
						return false;
					}
				}					
			}
		}
	}
	return true;
}

// NEXT COLUMN LOGIC
// #########################################

countQuotesBefore = function(text, startPos){
	var result = 0;
	var pos = startPos;
	while (pos > 0){
		pos --;
		if (text[pos] == '"'){
			result ++;
		}
	}
	return result;	
}

findExpressionStart = function(text, startPos, separatorChar){
	var pos = startPos;
	while (pos > 0){
		pos --;
		if (text[pos] == separatorChar){
			return pos +1;
		}
	}
	return 0;
}

findMatchingCompletionIndex = function(expression, listOfColumns){
	var expressionLength = expression.length;
	if (expressionLength == 0){
		return 0;
	}
	for (var i in listOfColumns){
		var item = listOfColumns[i].toLowerCase();
		if (item.length >= expressionLength){
			if (item.substring(0, expressionLength) == expression){
				return i;
			}
		}
	}
	return null;
}

removeEmptyItems = function(list){
	var result = [];
	for (var i in list){
		if (list[i] != ""){
			result.push(list[i]);
		}
	}
	return result;
}

updateTextAndCursorToNextColumn = function(currentText, cursorPos, listOfColumns){
	var useListOfColumns = removeEmptyItems(listOfColumns);
    var result = new Object();
	log ("updateTextAndCursorToNextColumn: listOfColumns = " + JSON.stringify(listOfColumns) + ", useListOfColumns = " + JSON.stringify(useListOfColumns));
	if (useListOfColumns.size == 0){
		result.text = currentText;
		result.cursorPos = cursorPos;
		return result;
	}
	
	log("updateTextAndCursorToNextColumn: currentText = '" + currentText + "', cursorPos = " + cursorPos);
	log("updateTextAndCursorToNextColumn: step 1");
	var quotesBefore = countQuotesBefore(currentText, cursorPos);
	log("updateTextAndCursorToNextColumn: quotesBefore = " + quotesBefore);
	var separatorChar = ' ';
	if (quotesBefore % 2 == 1){
		separatorChar = '"'; //: open quote: then read until quote start
	}
	
	log("updateTextAndCursorToNextColumn: step 2");
	
	var expressionStart = findExpressionStart(currentText, cursorPos, separatorChar);
	var expression = currentText.substring(expressionStart, cursorPos).toLowerCase();
	log("updateTextAndCursorToNextColumn: expression = '" + expression + "'");
	if ((expression.length > 0) && (expression[expression.length - 1] == ':')){
		expression = expression.substring(0, expression.length - 1);
		log("updateTextAndCursorToNextColumn: expression w/o colon = '" + expression + "'");
	}
	
	var textBeforeExpression = currentText.substring(0, expressionStart);
	if ((textBeforeExpression.length > 0) && (textBeforeExpression[textBeforeExpression.length - 1] == '"')){
		textBeforeExpression = textBeforeExpression.substring(0, textBeforeExpression.length - 1);
		log("updateTextAndCursorToNextColumn: textBeforeExpression w/o quote = '" + textBeforeExpression + "'");
	}
	
	var matchingCompletionIndex = findMatchingCompletionIndex(expression, useListOfColumns);
	
	var matchingItem = useListOfColumns[0];
	if (matchingCompletionIndex != null){
		matchingItem = useListOfColumns[matchingCompletionIndex];
	}
	log("updateTextAndCursorToNextColumn: matchingItem = '" + matchingItem + "'");
	
	if (matchingItem.toLowerCase() == expression.toLowerCase()){ //: asked for auto-complete when item is already there -> put next item in
		var nextIndex = matchingCompletionIndex;
		nextIndex ++;
		log("updateTextAndCursorToNextColumn: nextIndex = " + nextIndex);
		log("updateTextAndCursorToNextColumn: useListOfColumns.length = " + useListOfColumns.length);
		if (nextIndex >= useListOfColumns.length){
			nextIndex = 0;
		}
		matchingItem = useListOfColumns[nextIndex];
		log("updateTextAndCursorToNextColumn: matchingItem after applying logic for next item= '" + matchingItem + "'");
	}
	
	
	if (matchingItem.indexOf(" ") > -1){
		matchingItem = '"' + matchingItem + '"';
	}
		
	result.text = textBeforeExpression + matchingItem + ":" + currentText.substring(cursorPos);
	result.cursorPos = textBeforeExpression.length + matchingItem.length + 1;
	
	return result;
}

// MISC LOGIC
// #########################################
findIndex = function(dataArray, value, property){
	var index = 0;
	var len = dataArray.length;
	if (len == "undefined"){
		len = dataArray.size;
	}
	while (index < dataArray.length){
		var item = dataArray[index];
		if (item[property] == value){
			return index;
		}
		index ++;
	}
	return null;
}

createListFromProperty = function(dataArray, property){
	var result = [];
	var index = 0;
	var len = dataArray.length;
	if (len == "undefined"){
		len = dataArray.size;
	}
	while (index < dataArray.length){
		result.push(dataArray[index][property]);
		index ++;
	}
	return result;
}
