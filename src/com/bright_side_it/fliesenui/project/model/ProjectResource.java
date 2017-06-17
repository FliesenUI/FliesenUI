package com.bright_side_it.fliesenui.project.model;

public class ProjectResource implements Comparable<ProjectResource> {
    public enum ResourceType {
        SCREEN, DTO, PROJECT, PLUGIN, IMAGE_ASSET, STRING_RESOURCE, COLOR_PALETTE
    }
    public enum ResourceFormat{XML, IMAGE}

    private ResourceType resourceType;
    private ResourceFormat resourceFormat;
    private String id;
    private String filePath;

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
	public ResourceFormat getResourceFormat() {
		return resourceFormat;
	}

	public void setResourceFormat(ResourceFormat resourceFormat) {
		this.resourceFormat = resourceFormat;
	}

    @Override
    public int compareTo(ProjectResource other) {
        if (other == null) {
            return 1;
        }
        if (other.resourceType == null) {
            if (resourceType != null) {
                return 1;
            }
        } else if (resourceType == null) {
            return -1;
        }
        int result = this.resourceType.ordinal() - other.resourceType.ordinal();
        if (result != 0) {
            return result;
        }
        
        if (other.resourceFormat == null) {
            if (resourceFormat != null) {
                return 1;
            }
        } else if (resourceFormat == null) {
            return -1;
        }
        if (resourceFormat == null){
        	System.out.println("Found resourceFormat = null for object: " + id + ", type = " + resourceType);
        }
        
        
        if ((resourceFormat != null) && (other.resourceFormat != null)){
        	result = this.resourceFormat.ordinal() - other.resourceFormat.ordinal();
        	if (result != 0) {
        		return result;
        	}
        }

        if (other.id == null) {
            if (id != null) {
                return 1;
            }
        } else if (id == null) {
            return -1;
        }

        return id.compareTo(other.id);
    }

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


}
