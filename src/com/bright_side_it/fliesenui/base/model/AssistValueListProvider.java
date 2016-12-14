package com.bright_side_it.fliesenui.base.model;

import java.util.List;

import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ResourceDefinition;

public interface AssistValueListProvider {
    List<AssistValue> getValues(Project project, ResourceDefinition resourceDefinition, DTODefinition dtoDefinition, String replacedText);
}
