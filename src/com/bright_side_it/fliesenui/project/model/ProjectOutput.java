package com.bright_side_it.fliesenui.project.model;

import java.io.File;

import com.bright_side_it.fliesenui.base.util.BaseConstants.LanguageFlavor;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;

public class ProjectOutput {
	private NodePath nodePath;
	private LanguageFlavor languageFlavor;
	
	private String sourceDirPath;
	private String androidProjectPath;

	private File sourceDirFileObject;
	private File androidProjectFileObject;
	
	
	public LanguageFlavor getLanguageFlavor() {
		return languageFlavor;
	}
	public void setLanguageFlavor(LanguageFlavor languageFlavor) {
		this.languageFlavor = languageFlavor;
	}
	public NodePath getNodePath() {
		return nodePath;
	}
	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}
	public String getSourceDirPath() {
		return sourceDirPath;
	}
	public void setSourceDirPath(String sourceDirPath) {
		this.sourceDirPath = sourceDirPath;
	}
	public String getAndroidProjectPath() {
		return androidProjectPath;
	}
	public void setAndroidProjectPath(String androidProjectPath) {
		this.androidProjectPath = androidProjectPath;
	}
	public File getSourceDirFileObject() {
		return sourceDirFileObject;
	}
	public void setSourceDirFileObject(File sourceDirFileObject) {
		this.sourceDirFileObject = sourceDirFileObject;
	}
	public File getAndroidProjectFileObject() {
		return androidProjectFileObject;
	}
	public void setAndroidProjectFileObject(File androidProjectFileObject) {
		this.androidProjectFileObject = androidProjectFileObject;
	}

}
