package com.bright_side_it.fliesenui.dto.model;

import java.util.List;

import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;

public class DTODefinitionDAOResult {
    private DTODefinition dtoDefinition;
    private List<ResourceDefinitionProblem> problems;

    public DTODefinition getDTODefinition() {
        return dtoDefinition;
    }

    public void setDTODefinition(DTODefinition dtoDefinition) {
        this.dtoDefinition = dtoDefinition;
    }

    public List<ResourceDefinitionProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<ResourceDefinitionProblem> problems) {
        this.problems = problems;
    }


}
