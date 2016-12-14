package com.bright_side_it.fliesenui.validation.logic;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;

public class ImageAssetValidationLogic {

    public void validate(Project project) {
        Set<String> usedIDs = new TreeSet<String>();
        for (ImageAssetDefinition i : project.getImageAssetDefinitionsMap().values()) {
            if (usedIDs.contains(i.getID())) {
                if (project.getImageAssetDefinitionProblemsMap() == null) {
                    project.setImageAssetDefinitionProblemsMap(new TreeMap<String, ResourceDefinitionProblem>());
                }
                ResourceDefinitionProblem problem = new ResourceDefinitionProblem();
                problem.setType(ProblemType.IMAGE_ASSET_DUPLICATE_ID);
                problem.setMessage("There are multiple image assets with id '" + i.getID() + "'");
                project.getImageAssetDefinitionProblemsMap().put(i.getID(), problem);
            }
        }
    }


}
