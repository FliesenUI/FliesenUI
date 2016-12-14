package com.bright_side_it.fliesenui.project.dao;

import java.io.File;

import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.google.gson.Gson;

public class ProjectDAO {
    public void writeToFile(File file, Project project) throws Exception {
        String json = new Gson().toJson(project);
        FileUtil.writeStringToFile(file, json);
    }

    public Project readFromFile(File file) throws Exception {
        String json = FileUtil.readFileAsString(file);
        return new Gson().fromJson(json, Project.class);
    }
}
