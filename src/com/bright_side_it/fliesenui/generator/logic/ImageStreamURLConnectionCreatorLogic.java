package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;

import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.LanguageFlavor;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;

public class ImageStreamURLConnectionCreatorLogic {

	// if (languageFlavor == LanguageFlavor.ANDROID){
	// result.append("import android.content.Context;\n");
	// }

	public void generateImageStreamURLConnection(Project project, File javaBaseDir, LanguageFlavor languageFlavor) throws Exception {
		StringBuilder result = new StringBuilder();
		File packageDir = GeneratorUtil.getCorePackageDir(javaBaseDir);
		String className = "ImageStreamURLConnection";

		File destFile = new File(packageDir, className + ".java");

		result.append("package " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ";\n");
		result.append("\n");
		result.append("import java.io.ByteArrayInputStream;\n");
		result.append("import java.io.ByteArrayOutputStream;\n");
		result.append("import java.io.IOException;\n");
		result.append("import java.io.InputStream;\n");
		result.append("import java.io.OutputStream;\n");
		result.append("import java.net.URL;\n");
		result.append("import java.net.URLConnection;\n");
		result.append("public class ImageStreamURLConnection extends URLConnection {\n");
		result.append("    private String streamID;\n");
		result.append("    private FLUIImageStream imageStream;\n");
		result.append("    private FLUIScreenManagerInterface manager;\n");
		result.append("    public ImageStreamURLConnection(URL url, FLUIScreenManagerInterface manager, String streamID) {\n");
		result.append("        super(url);\n");
		result.append("        this.manager = manager;\n");
		result.append("        this.streamID = streamID;\n");
		result.append("    }\n");
		result.append("    \n");
		result.append("    @Override\n");
		result.append("    public void connect() throws IOException {\n");
		result.append("        if (connected) {\n");
		result.append("            return;\n");
		result.append("        }\n");
		result.append("        imageStream = manager.getCustomImageStream(streamID);\n");
		result.append("        connected = true;\n");
		result.append("    }\n");
		result.append("    @Override\n");
		result.append("    public String getHeaderField(String name) {\n");
		result.append("        if (\"Content-Type\".equalsIgnoreCase(name)) {\n");
		result.append("            return getContentType();\n");
		result.append("        } else if (\"Content-Length\".equalsIgnoreCase(name)) {\n");
		result.append("            return \"\" + getContentLength();\n");
		result.append("        }\n");
		result.append("        return null;\n");
		result.append("    }\n");
		result.append("    @Override\n");
		result.append("    public String getContentType() {\n");
		result.append("        if (imageStream == null){\n");
		result.append("            return null;\n");
		result.append("        }\n");
		result.append("        return \"image/\" + imageStream.getContentType();\n");
		result.append("    }\n");
		result.append("    @Override\n");
		result.append("    public int getContentLength() {\n");
		result.append("        return (int)imageStream.getLength();\n");
		result.append("    }\n");
		if (languageFlavor == LanguageFlavor.JAVA) {
			result.append("    @Override\n");
			result.append("    public long getContentLengthLong() {\n");
			result.append("        return imageStream.getLength();\n");
			result.append("    }\n");
		}
		result.append("    @Override\n");
		result.append("    public boolean getDoInput() {\n");
		result.append("        return true;\n");
		result.append("    }\n");
		result.append("    @Override\n");
		result.append("    public InputStream getInputStream() throws IOException {\n");
		result.append("        connect();\n");
		result.append("        if (imageStream == null){\n");
		result.append("            return new ByteArrayInputStream(new byte[0]);\n");
		result.append("        }\n");
		result.append("        return imageStream.getInputStream();\n");
		result.append("    }\n");
		result.append("    @Override\n");
		result.append("    public OutputStream getOutputStream() throws IOException {\n");
		result.append("        return new ByteArrayOutputStream();\n");
		result.append("    }\n");
		result.append("    @Override\n");
		result.append("    public java.security.Permission getPermission() throws IOException {\n");
		result.append("        return null;\n");
		result.append("    }\n");
		result.append("}\n");

		destFile.getParentFile().mkdirs();

		result = GeneratorUtil.addJavaGeneratedCommend(result);
		FileUtil.writeStringToFile(destFile, result.toString());
		destFile.setWritable(false);
	}

}
