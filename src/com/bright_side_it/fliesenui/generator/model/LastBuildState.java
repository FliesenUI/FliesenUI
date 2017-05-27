package com.bright_side_it.fliesenui.generator.model;

import java.util.SortedMap;

import com.bright_side_it.fliesenui.project.model.ProjectResource;

public class LastBuildState {
    private SortedMap<ProjectResource, Long> resourceToLastBuildTime;

    public SortedMap<ProjectResource, Long> getResourceToLastBuildTime() {
        return resourceToLastBuildTime;
    }

    public void setResourceToLastBuildTime(SortedMap<ProjectResource, Long> resourceToLastBuildTime) {
        this.resourceToLastBuildTime = resourceToLastBuildTime;
    }

}
