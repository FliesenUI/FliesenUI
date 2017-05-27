package com.bright_side_it.fliesenui.generator.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.bright_side_it.fliesenui.base.util.BaseConstants.LanguageFlavor;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.generator.logic.FLUIControlCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.ImageStreamURLConnectionCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JavaConstantsCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JavaDTOBuilerCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JavaDTOCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JavaScreenCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JavaScreenListenerCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JavaScreenReplyCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JavaScreenRequestCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JavaSharedReplyInterfaceCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JavaTestWriterCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.ScreenManagerCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.ScreenManagerInterfaceCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.StringClassCreatorLogic;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.logic.DefinitionResourceLogic;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectResource;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceFormat;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceType;
import com.bright_side_it.fliesenui.project.model.SharedReplyInterface;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class JavaGeneratorService {
    public void generateJava(Project project, Set<ProjectResource> upToDateResources, File javaBaseDir, LanguageFlavor languageFlavor) throws Exception {
        Collection<ScreenDefinition> screenDefinitionsToUpdate = new ArrayList<>();
        DefinitionResourceLogic logic = new DefinitionResourceLogic();
        for (ScreenDefinition i : project.getScreenDefinitionsMap().values()) {
            if (!upToDateResources.contains(logic.create(ResourceType.SCREEN, ResourceFormat.XML, i.getID()))) {
                log("screen " + i.getID() + " needs to be updated");
                screenDefinitionsToUpdate.add(i);
            } else {
                log("screen " + i.getID() + " DOES NOT need to be updated");
            }
        }

        new ScreenManagerCreatorLogic().generateScreenManager(project, javaBaseDir, languageFlavor);
        new JavaTestWriterCreatorLogic().createJava(project, javaBaseDir);
        new StringClassCreatorLogic().generateStringClass(project, javaBaseDir);
        new ScreenManagerInterfaceCreatorLogic().generateScreenManagerInterface(project, javaBaseDir, languageFlavor);
        if (languageFlavor == LanguageFlavor.JAVA){
        	new FLUIControlCreatorLogic().generateFLUIControl(project.getScreenDefinitionsMap().values(), javaBaseDir);
        }
        new ImageStreamURLConnectionCreatorLogic().generateImageStreamURLConnection(project, javaBaseDir, languageFlavor);

        File screenPackageDir = GeneratorUtil.getScreenPackageDir(javaBaseDir);
        JavaSharedReplyInterfaceCreatorLogic javaSharedReplyInterfaceCreatorLogic = new JavaSharedReplyInterfaceCreatorLogic();
        JavaScreenCreatorLogic javaScreenCreatorLogic = new JavaScreenCreatorLogic();
        JavaScreenRequestCreatorLogic javaScreenRequestCreatorLogic = new JavaScreenRequestCreatorLogic();
        JavaScreenReplyCreatorLogic javaScreenReplyCreatorLogic = new JavaScreenReplyCreatorLogic();
        JavaScreenListenerCreatorLogic javaScreenListenerCreatorLogic = new JavaScreenListenerCreatorLogic();

        Map<String, List<String>> screenToReplySignaturesMap = new TreeMap<>();
        for (ScreenDefinition i : project.getScreenDefinitionsMap().values()) {
        	screenToReplySignaturesMap.put(i.getID(), javaScreenReplyCreatorLogic.getMethodSignatures(project, i));
        }
        
        for (SharedReplyInterface i: BaseUtil.toEmptyMapIfNull(project.getProjectDefinition().getSharedReplyInterfaces()).values()){
        	javaSharedReplyInterfaceCreatorLogic.createInterface(project, i, screenPackageDir, screenToReplySignaturesMap);
        }
        
        for (ScreenDefinition i : screenDefinitionsToUpdate) {
            javaScreenCreatorLogic.createJava(project, i, screenPackageDir);
            javaScreenReplyCreatorLogic.createJava(project, i, screenPackageDir);
            javaScreenListenerCreatorLogic.createJava(project, i, screenPackageDir);
            javaScreenRequestCreatorLogic.createJava(project, i, screenPackageDir);
        }

        
        File dtoPackageDir = GeneratorUtil.getDTOPackageDir(javaBaseDir);
        JavaDTOCreatorLogic javaDTOCreatorLogic = new JavaDTOCreatorLogic();
        JavaDTOBuilerCreatorLogic dtoBuilerCreatorLogic = new JavaDTOBuilerCreatorLogic(); 
        for (DTODefinition i : project.getDTODefinitionsMap().values()) {
            if (!upToDateResources.contains(logic.create(ResourceType.DTO, ResourceFormat.XML, i.getID()))) {
                log("DTO " + i.getID() + " needs to be updated");
                javaDTOCreatorLogic.createJava(i, dtoPackageDir);
                dtoBuilerCreatorLogic.createJava(i, dtoPackageDir);
            } else {
                log("DTO " + i.getID() + " DOES NOT need to be updated");
            }
        }
        
        File corePackageDir = GeneratorUtil.getCorePackageDir(javaBaseDir);
        new JavaConstantsCreatorLogic().createJava(project, corePackageDir);
    }

    private void log(String message) {
        System.out.println("JavaGeneratorService> " + message);
    }

}
