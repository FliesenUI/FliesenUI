package com.bright_side_it.fliesenui.base.model;

import java.util.List;

import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ResourceDefinition;

public class AssistValueList implements AssistValueListProvider {
    private List<AssistValue> values;

    public AssistValueList(List<AssistValue> values) {
        this.values = values;
    }

    @Override
    public List<AssistValue> getValues(Project project, ResourceDefinition resourceDefinition, DTODefinition tableDTODefinition, String replacedText) {
        return values;
    }
    
    public List<AssistValue> getValues() {
    	return values;
	}

}
