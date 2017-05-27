package com.bright_side_it.fliesenui.project.logic;

import com.bright_side_it.fliesenui.project.model.ProjectResource;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceFormat;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceType;
import com.google.gson.Gson;

public class DefinitionResourceLogic {
    public String toString(ProjectResource resourceInfo) {
        return new Gson().toJson(resourceInfo);
        //        return resourceInfo.getResourceType() + STRING_REPRESANTATION_SEPARATOR + resourceInfo.getId();
    }

    public ProjectResource fromString(String string) throws Exception {
        try {
            return new Gson().fromJson(string, ProjectResource.class);
        } catch (Exception e) {
            throw new Exception("Could not read DefinitionResource from string >>" + string + "<<", e);
        }
        //        ResourceInfo result = new ResourceInfo();
        //        String[] items = string.split(STRING_REPRESANTATION_SEPARATOR);
        //        result.setResourceType(ResourceType.valueOf(items[0]));
        //        result.setId(items[1]);
        //        return result;
    }

    public String createAsString(ResourceType resourceType, ResourceFormat resourceFormat, String id) {
        return toString(create(resourceType, resourceFormat, id));
    }

    public ProjectResource create(ResourceType resourceType, ResourceFormat resourceFormat, String id) {
        ProjectResource definitionResource = new ProjectResource();
        definitionResource.setResourceType(resourceType);
        definitionResource.setResourceFormat(resourceFormat);
        definitionResource.setId(id);
        return definitionResource;
    }

	public ResourceFormat getFormatFromType(ResourceType type) throws Exception{
		switch (type) {
		case DTO:
			return ResourceFormat.XML;
		case IMAGE_ASSET:
			return ResourceFormat.IMAGE;
		case PLUGIN:
			return ResourceFormat.XML;
		case PROJECT:
			return ResourceFormat.XML;
		case SCREEN:
			return ResourceFormat.XML;
		case STRING_RESOURCE:
			return ResourceFormat.XML;
		default:
			throw new Exception("Unknown resource type: " + type);
		}
	}
}
