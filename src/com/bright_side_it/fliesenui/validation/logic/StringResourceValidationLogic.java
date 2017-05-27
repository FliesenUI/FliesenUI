package com.bright_side_it.fliesenui.validation.logic;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.stringres.dao.StringResourceDAO;
import com.bright_side_it.fliesenui.stringres.model.StringResource;
import com.bright_side_it.fliesenui.stringres.model.StringResourceItem;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class StringResourceValidationLogic {
    private static final Set<Character> ALLOWED_CHARS_IN_IDS = createAllowedCharsInIDs();


	public void validate(Project project) {
		if (project.getStringResourceMap() == null){
			return;
		}
		if (project.getStringResourceMap().isEmpty()){
			return;
		}
		StringResource defaultLanguage = project.getStringResourceMap().get(BaseConstants.DEFAULT_LANGUAGE_ID);
		Set<String> stringIDsInDefaultLanguage = new HashSet<>();
		if (defaultLanguage != null){
			stringIDsInDefaultLanguage = new HashSet<>(defaultLanguage.getStrings().keySet());
			validateStringIDs(project, defaultLanguage);
			validateStringIDDoesNotOccurInLowerAndUpperCase(project, defaultLanguage);
			validateNoStringIDThatOccursInLanguageButNotInDefaultLanguage(project, project.getStringResourceMap(), stringIDsInDefaultLanguage);
		} else {
			validateNoDefaultLanguageButOtherLanguages(project, project.getStringResourceMap());
		}
		
	}

	
    private void validateNoDefaultLanguageButOtherLanguages(Project project, Map<String, StringResource> stringResourceMap) {
    	if (stringResourceMap == null){
    		return;
    	}
    	if (stringResourceMap.isEmpty()){
    		return;
    	}
    	if (stringResourceMap.containsKey(BaseConstants.DEFAULT_LANGUAGE_ID)){
    		return;
    	}
    	if (!stringResourceMap.isEmpty()){
    		StringResource firstStringResource = stringResourceMap.get(stringResourceMap.keySet().iterator().next());
            ValidationUtil.addError(project, firstStringResource, firstStringResource.getNodePath(), null, ProblemType.STRING_RESOURCE_DEFAULT_LANGUAGE_MISSING_WHILE_OTHER_LANGUAGE_IS_PROVIDED,
                    "There is a string resource for a language but there is no default string resource");
    	}
	}


	private void validateNoStringIDThatOccursInLanguageButNotInDefaultLanguage(Project project, Map<String, StringResource> stringResourceMap, Set<String> stringIDsInDefaultLanguage) {
    	for (String language: stringResourceMap.keySet()){
    		if (!language.equals(BaseConstants.DEFAULT_LANGUAGE_ID)){
    			for (Entry<String, StringResourceItem> entry: stringResourceMap.get(language).getStrings().entrySet()){
    				if (!stringIDsInDefaultLanguage.contains(entry.getKey())){
    	                ValidationUtil.addError(project, stringResourceMap.get(language), entry.getValue().getNodePath(), StringResourceDAO.NAME_ATTRIBUTE_NAME, ProblemType.STRING_RESOURCE_NAME_DOES_NOT_OCCUR_IN_DEFAULT_LANGUAGE,
    	                        "This string resource does not exist in the default language");
    				}
    			}
    		}
    	}
	}

	private void validateStringIDDoesNotOccurInLowerAndUpperCase(Project project, StringResource stringResource) {
    	Set<String> stringIDsInEnumCase = new HashSet<String>();
    	
    	for (Entry<String, StringResourceItem> entry: stringResource.getStrings().entrySet()){
    		String stringIDInEnumCase = BaseUtil.toStringEnumID(entry.getKey());
    		if (stringIDsInEnumCase.contains(stringIDInEnumCase)){
                ValidationUtil.addError(project, stringResource, entry.getValue().getNodePath(), StringResourceDAO.NAME_ATTRIBUTE_NAME, ProblemType.STRING_RESOURCE_NAME_OCCURES_MULTIPLE_TIMES,
                        "There is another string resource with the same name if case is ignored");
    		}
    		stringIDsInEnumCase.add(stringIDInEnumCase);
    	}
	}

	private void validateStringIDs(Project project, StringResource stringResource) {
    	for (Entry<String, StringResourceItem> entry: stringResource.getStrings().entrySet()){
    		validateStringResourceIDOfEnty(project, stringResource, entry);
    	}
	}
	
	private void validateStringResourceIDOfEnty(Project project, StringResource stringResource, Entry<String, StringResourceItem> entry){
        for (char i : entry.getKey().toCharArray()) {
            if (!ALLOWED_CHARS_IN_IDS.contains(i)) {
                ValidationUtil.addError(project, stringResource, entry.getValue().getNodePath(), StringResourceDAO.NAME_ATTRIBUTE_NAME, ProblemType.STRING_RESOURCE_NAME_CONTAINS_WRONG_CHAR,
                        "The name of the string resource entry contains the wrong character '" + i + "'. Only a-z, A-Z, 0-9 and '_' are allowed");
                return;
            }
        }
	}

	private static Set<Character> createAllowedCharsInIDs() {
        Set<Character> result = new HashSet<>();
        for (char i = 'a'; i <= 'z'; i++) {
            result.add(i);
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            result.add(i);
        }
        for (char i = '0'; i <= '9'; i++) {
            result.add(i);
        }
        result.add('_');
        return result;
    }

}
