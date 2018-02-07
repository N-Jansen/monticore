package de.monticore.ecore.creator.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;

/**
 * This class creates a modeling project for a given Ecore mmetamodel.
 * It automatically copies the necessary dependencies and creates the 
 * project landscape. The process is customized for MontiCore projects.
 * 
 * @author Nico Jansen
 */
public class EcoreModelingProjectCreator {
	
	private String modelProjectName;
	private File modelProject;
	private File meta_inf;
	private File model;
	private File bin;
	private File src;
	private File ecoreFile;

	/**
	 * The standard constructor for the project creator.
	 * @param ecoreFile The Ecore file for which the project is generated.
	 */
	public EcoreModelingProjectCreator(File ecoreFile) {
		this.ecoreFile = ecoreFile;
	}

	/**
	 * Creates the Ecore modeling project.
	 * 
	 * @param workspaceDirectory The workspace directory, 
	 * in which the new project will be created.
	 */
	public void create(File workspaceDirectory) {
		modelProjectName = ecoreFile.getParentFile().getName();

		// directories
		modelProject = new File(workspaceDirectory.getAbsolutePath() + "\\" + modelProjectName + ".model");
		modelProject.mkdirs();
		bin = new File(modelProject.getAbsolutePath() + "\\bin");
		bin.mkdirs();
		meta_inf = new File(modelProject.getAbsolutePath() + "\\META-INF");
		meta_inf.mkdirs();
		model = new File(modelProject.getAbsolutePath() + "\\model");
		model.mkdirs();
		src = new File(modelProject.getAbsolutePath() + "\\src");
		src.mkdirs();
		
		// files
		createManifest(meta_inf, modelProjectName);
		createGraphicalModel(model, modelProjectName);
		createGenModel(model, modelProjectName);
		createClasspath(modelProject);
		createProject(modelProject, modelProjectName);
		
		// Ecore file
		File targetEcoreFile = new File(model.getAbsoluteFile() + "\\" + ecoreFile.getName());
		copyFile(ecoreFile, targetEcoreFile);

		// Ecore file dependencies
		copyEcoreDependencies(new File(ecoreFile.getParentFile().getParentFile().getAbsolutePath() + "\\"), new File(modelProject.getAbsolutePath() + "\\"));
	}

	/**
	 * Copies the Ecore metamodel into the modeling project.
	 * 
	 * @param sourceFile The source of the metamodel.
	 * @param targetFile The destination of the metamodel.
	 */
	private void copyFile(File sourceFile, File targetFile) {
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
        	fileInputStream = new FileInputStream(sourceFile);
        	fileOutputStream = new FileOutputStream(targetFile);
            sourceChannel = fileInputStream.getChannel();
            destChannel = fileOutputStream.getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                sourceChannel.close();
                destChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	/**
	 * Copies the Ecore metamodel dependencies recursively.
	 * 
	 * @param source The source file or directory.
	 * @param target The target file or directory.
	 */
	private void copyEcoreDependencies(File source, File target) {
		File[] fileList = source.listFiles();
		for (File f : fileList) {
			if (f.isDirectory()) {
				File dir = new File(target.getAbsolutePath() + "\\" + f.getName());
				dir.mkdir();
				copyEcoreDependencies(f, dir);
				continue;
			}
			copyFile(f, new File(target.getAbsolutePath() + "\\" + f.getName()));
		}
	}

	private void createManifest(File meta_inf, String modelProjectName) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(meta_inf.getAbsolutePath() + "//MANIFEST.MF", "UTF-8");
			writer.println("Manifest-Version: 1.0");
			writer.println("Bundle-ManifestVersion: 2");
			writer.println("Bundle-Name: " + modelProjectName);
			writer.println("Bundle-SymbolicName: " + modelProjectName + "; singleton:=true");
			writer.println("Bundle-Version: 0.1.0.qualifier");
			writer.println("Require-Bundle: org.eclipse.emf.ecore;visibility:=reexport, org.eclipse.core.runtime");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	
	private void createGraphicalModel(File model, String modelProjectName) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(model.getAbsolutePath() + "//"+ modelProjectName + ".aird", "UTF-8");
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:description=\"http://www.eclipse.org/sirius/description/1.1.0\" xmlns:description_1=\"http://www.eclipse.org/sirius/diagram/description/1.1.0\" xmlns:diagram=\"http://www.eclipse.org/sirius/diagram/1.1.0\" xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" xmlns:notation=\"http://www.eclipse.org/gmf/runtime/1.0.2/notation\" xmlns:viewpoint=\"http://www.eclipse.org/sirius/1.1.0\" xsi:schemaLocation=\"http://www.eclipse.org/sirius/description/1.1.0 http://www.eclipse.org/sirius/1.1.0#//description http://www.eclipse.org/sirius/diagram/description/1.1.0 http://www.eclipse.org/sirius/diagram/1.1.0#//description\">");
			writer.println("<viewpoint:DAnalysis xmi:id=\"_1gwfEPDNEee6-JHsKBOtBA\" selectedViews=\"_1tNmsPDNEee6-JHsKBOtBA\" version=\"12.1.0.201708031200\">");
			writer.println("<semanticResources>" + modelProjectName + ".ecore</semanticResources>");
			writer.println("<ownedViews xmi:type=\"viewpoint:DView\" xmi:id=\"_1tNmsPDNEee6-JHsKBOtBA\">");
			writer.println("<viewpoint xmi:type=\"description:Viewpoint\" href=\"platform:/plugin/org.eclipse.emf.ecoretools.design/description/ecore.odesign#//@ownedViewpoints[name='Design']\"/>");
			writer.println("<ownedRepresentationDescriptors xmi:type=\"viewpoint:DRepresentationDescriptor\" xmi:id=\"_10RQ8PDNEee6-JHsKBOtBA\" name=\""+ modelProjectName + "\" repPath=\"#_1vVGkPDNEee6-JHsKBOtBA\">");
			writer.println("<description xmi:type=\"description_1:DiagramDescription\" href=\"platform:/plugin/org.eclipse.emf.ecoretools.design/description/ecore.odesign#//@ownedViewpoints[name='Design']/@ownedRepresentations[name='Entities']\"/>");
			writer.println("<target xmi:type=\"ecore:EPackage\" href=\"" + modelProjectName + ".ecore#/\"/>");
			writer.println("</ownedRepresentationDescriptors>");
			writer.println("</ownedViews>");
			writer.println("</viewpoint:DAnalysis>");
			writer.println("<diagram:DSemanticDiagram xmi:id=\"_10NmkPDNEee6-JHsKBOtBA\" name=\""+ modelProjectName + "\" uid=\"_1vVGkPDNEee6-JHsKBOtBA\">");
			writer.println("<ownedAnnotationEntries xmi:type=\"description:AnnotationEntry\" xmi:id=\"_10NmkfDNEee6-JHsKBOtBA\" source=\"DANNOTATION_CUSTOMIZATION_KEY\">");
			writer.println("<data xmi:type=\"diagram:ComputedStyleDescriptionRegistry\" xmi:id=\"_10NmkvDNEee6-JHsKBOtBA\"/>");
			writer.println("</ownedAnnotationEntries>");
			writer.println("<ownedAnnotationEntries xmi:type=\"description:AnnotationEntry\" xmi:id=\"_11FwUPDNEee6-JHsKBOtBA\" source=\"GMF_DIAGRAMS\">");
			writer.println("<data xmi:type=\"notation:Diagram\" xmi:id=\"_11FwUfDNEee6-JHsKBOtBA\" type=\"Sirius\" element=\"_10NmkPDNEee6-JHsKBOtBA\" measurementUnit=\"Pixel\">");
			writer.println("<styles xmi:type=\"notation:DiagramStyle\" xmi:id=\"_11FwUvDNEee6-JHsKBOtBA\"/>");
			writer.println("</data>");
			writer.println("</ownedAnnotationEntries>");
			writer.println("<description xmi:type=\"description_1:DiagramDescription\" href=\"platform:/plugin/org.eclipse.emf.ecoretools.design/description/ecore.odesign#//@ownedViewpoints[name='Design']/@ownedRepresentations[name='Entities']\"/>");
			writer.println("<filterVariableHistory xmi:type=\"diagram:FilterVariableHistory\" xmi:id=\"_10NmlPDNEee6-JHsKBOtBA\"/>");
			writer.println("<activatedLayers xmi:type=\"description_1:Layer\" href=\"platform:/plugin/org.eclipse.emf.ecoretools.design/description/ecore.odesign#//@ownedViewpoints[name='Design']/@ownedRepresentations[name='Entities']/@defaultLayer\"/>");
			writer.println("<activatedLayers xmi:type=\"description_1:AdditionalLayer\" href=\"platform:/plugin/org.eclipse.emf.ecoretools.design/description/ecore.odesign#//@ownedViewpoints[name='Design']/@ownedRepresentations[name='Entities']/@additionalLayers[name='Package']\"/>");
			writer.println("<activatedLayers xmi:type=\"description_1:AdditionalLayer\" href=\"platform:/plugin/org.eclipse.emf.ecoretools.design/description/ecore.odesign#//@ownedViewpoints[name='Design']/@ownedRepresentations[name='Entities']/@additionalLayers[name='Validation']\"/>");
			writer.println("<target xmi:type=\"ecore:EPackage\" href=\""+ modelProjectName + ".ecore#/\"/>");
			writer.println("</diagram:DSemanticDiagram>");
			writer.println("</xmi:XMI>");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void createGenModel(File model, String modelProjectName) {
		String modelName = modelProjectName.substring(0, 1).toUpperCase() + modelProjectName.substring(1);
		PrintWriter writer;
		try {
			writer = new PrintWriter(model.getAbsolutePath() + "//"+ modelProjectName + ".genmodel", "UTF-8");
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<genmodel:GenModel xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			writer.println("xmlns:genmodel=\"http://www.eclipse.org/emf/2002/GenModel\" modelDirectory=\"/" + modelProjectName + ".model/src-gen\" creationIcons=\"false\" editDirectory=\"/" + modelProjectName + ".edit/src-gen\"");
			writer.println("editorDirectory=\"/" + modelProjectName + ".editor/src-gen\" modelPluginID=\"" + modelProjectName + "\" modelName=\"" + modelName + "\" rootExtendsClass=\"org.eclipse.emf.ecore.impl.MinimalEObjectImpl$Container\"");
			writer.println("codeFormatting=\"true\" importerID=\"org.eclipse.emf.importer.ecore\" complianceLevel=\"8.0\"");
			writer.println("copyrightFields=\"false\" operationReflection=\"true\" importOrganizing=\"true\">");
			writer.println("<foreignModel>" + modelProjectName + ".ecore</foreignModel>");
			writer.println("<testsDirectory xsi:nil=\"true\"/>");
			writer.println("<genPackages prefix=\"" + modelProjectName + "\" disposableProviderFactory=\"true\" ecorePackage=\"" + modelProjectName + ".ecore#/\"/>");
			writer.println("</genmodel:GenModel>");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	private void createClasspath(File modelProject) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(modelProject.getAbsolutePath() + "//.classpath", "UTF-8");
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<classpath>");
			writer.println("<classpathentry kind=\"src\" path=\"src\"/>");
			writer.println("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8\"/>");
			writer.println("<classpathentry kind=\"con\" path=\"org.eclipse.pde.core.requiredPlugins\"/>");
			writer.println("<classpathentry kind=\"output\" path=\"bin\"/>");
			writer.println("</classpath>");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	
	private void createProject(File modelProject, String modelProjectName) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(modelProject.getAbsolutePath() + "//.project", "UTF-8");
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<projectDescription>");
			writer.println("<name>" + modelProjectName + ".model</name>");
			writer.println("<comment></comment>");
			writer.println("<projects>");
			writer.println("</projects>");
			writer.println("<buildSpec>");
			writer.println("<buildCommand>");
			writer.println("<name>org.eclipse.jdt.core.javabuilder</name>");
			writer.println("<arguments>");
			writer.println("</arguments>");
			writer.println("</buildCommand>");
			writer.println("<buildCommand>");
			writer.println("<name>org.eclipse.pde.ManifestBuilder</name>");
			writer.println("<arguments>");
			writer.println("</arguments>");
			writer.println("</buildCommand>");
			writer.println("<buildCommand>");
			writer.println("<name>org.eclipse.pde.SchemaBuilder</name>");
			writer.println("<arguments>");
			writer.println("</arguments>");
			writer.println("</buildCommand>");
			writer.println("</buildSpec>");
			writer.println("<natures>");
			writer.println("<nature>org.eclipse.sirius.nature.modelingproject</nature>");
			writer.println("<nature>org.eclipse.jdt.core.javanature</nature>");
			writer.println("<nature>org.eclipse.pde.PluginNature</nature>");
			writer.println("</natures>");
			writer.println("</projectDescription>");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	public File getModelProject() {
		return this.modelProject;
	}
}
