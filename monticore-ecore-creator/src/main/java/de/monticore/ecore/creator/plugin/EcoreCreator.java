package de.monticore.ecore.creator.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import de.monticore.emf._ast.ASTEPackage;
import de.monticore.emf.util.AST2ModelFiles;

/**
 * Plugin to generate ecore files in a MontiCore project.
 */
@Mojo(name = "generate_ecore")
public class EcoreCreator extends AbstractMojo {

	/**
	 * The calling MontiCore project.
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;

	/**
	 * The method is called, when executing the plugin. It generates the ecore files
	 * in the calling MontiCore project.
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File projectDir = project.getBasedir();
		File grammarDir = new File(projectDir.getAbsolutePath() + "\\src\\main\\grammars");
		File ePackageDir = new File(
				projectDir.getAbsolutePath() + "\\target\\generated-sources\\monticore\\sourcecode");

		if (!grammarDir.exists()) {
			getLog().info("No grammar directory found. Ecore file generation aborted!");
			return;
		}

		if (!ePackageDir.exists()) {
			getLog().info("Directory for generated sourcecode not found. Ecore file generation aborted!");
			return;
		}

		getLog().info("Generating ecore files.");
		File[] grammarFiles = grammarDir.listFiles();
		for (File grammarFile : grammarFiles) {
			String name = grammarFile.getName();
			if (name.endsWith(".mc4")) {
				getLog().info("Generating ecore file for grammar " + name);
				int nameLength = name.length() - 4;
				String packageName = name.substring(0, nameLength).toLowerCase() + "._ast";
				String ePackage = packageName + "." + name.substring(0, nameLength).toUpperCase() + "Package";
				createEcore(ePackage, projectDir);
				getLog().info("Generating ecore file for grammar " + name + " successful.");
			}
		}
	}

	/**
	 * The method which generates the ecore metamodels.
	 * 
	 * @param ePackage The ePacke for which the ecore file is created.
	 * @param projectDir The project directory foe the ecore generation.
	 */
	private void createEcore(String ePackage, File projectDir) {
		try {
			File file = new File(projectDir + "\\target\\classes\\");
			URLClassLoader classLoader = new URLClassLoader(new URL[] { file.toURI().toURL() },
					ASTEPackage.class.getClassLoader());
			Class<?> clazz = classLoader.loadClass(ePackage);
			Field field = clazz.getField("eINSTANCE");
			Object astEPackage = field.get(clazz);
			 AST2ModelFiles.get().serializeAST((ASTEPackage) astEPackage);
			classLoader.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
