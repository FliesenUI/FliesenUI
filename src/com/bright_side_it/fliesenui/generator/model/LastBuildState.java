package com.bright_side_it.fliesenui.generator.model;

import java.util.SortedMap;

import com.bright_side_it.fliesenui.project.model.DefinitionResource;

public class LastBuildState {
    private SortedMap<DefinitionResource, Long> resourceToLastBuildTime;

    public SortedMap<DefinitionResource, Long> getResourceToLastBuildTime() {
        return resourceToLastBuildTime;
    }

    public void setResourceToLastBuildTime(SortedMap<DefinitionResource, Long> resourceToLastBuildTime) {
        this.resourceToLastBuildTime = resourceToLastBuildTime;
    }

}
