package in.co.saionline.closureeclipse.builder;

import in.co.saionline.closureeclipse.SettingsManager;
import in.co.saionline.closureeclipse.SettingsManager.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;

public class ClosureBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "in.co.saionline.ClosureEclipse.ClosureBuilder";

	class JSResourceVisitor implements IResourceVisitor {

		List<IResource> jsResources = new ArrayList<IResource>();

		public boolean visit(IResource resource) {
			if (resource instanceof IFile && !resource.isDerived()
					&& resource.getName().endsWith(".js")) {

				// Respect the exclude property of individual file.....
				if (SettingsManager.getFileExclude((IFile) resource) == false)
					jsResources.add(resource);
			}
			return true; // return true to continue visiting children.
		}

		public IResource[] getResources() {
			return jsResources.toArray(new IResource[]{});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor)
			throws CoreException {

		// We shall always consider as Full Build. since we do currently not
		// have any mechanism for Incremental Builds
		switch (kind) {
			case FULL_BUILD :
			case CLEAN_BUILD :
			case INCREMENTAL_BUILD :
			case AUTO_BUILD :
				fullBuild(monitor);
		}

		return null;
	}

	private void runCompiler(IResource[] resources) {
		try {
			
			// Project Settings
			SettingsManager.Settings settings = SettingsManager.getProjectSettings(getProject());
			
			Compiler.setLoggingLevel(Level.SEVERE);

			Compiler compiler = new Compiler(new ClosureErrorManager(getProject()));
			CompilerOptions options = getOptions(settings);

			ArrayList<JSSourceFile> sources = new ArrayList<JSSourceFile>();

			// Initialize with default externs of the library...
			List<JSSourceFile> externs = CommandLineRunner.getDefaultExterns();
			
			if(settings.externsList.length() > 0) {
				String[] list = settings.externsList.split(";");
				for(String file : list) {
					System.out.println("EXTERN: " + file);
					externs.add(JSSourceFile.fromFile(file));
				}
			}
			

			for (IResource resource : resources) {
				IFile file = (IFile) resource;
				// System.out.print(file.getRawLocation().makeAbsolute().toOSString());
				JSSourceFile jsFile = JSSourceFile.fromFile(file.getRawLocation().makeAbsolute()
						.toFile());
				sources.add(jsFile);
			}

			// Delete all the earlier markers
			getProject().deleteMarkers(ClosureErrorManager.MARKER_TYPE, false,
					IResource.DEPTH_INFINITE);

			Result result = compiler.compile(externs.toArray(new JSSourceFile[]{}), sources.toArray(new JSSourceFile[]{}),
					options);

			if (result.success) {
				// System.out.println(compiler.toSource());
				// TODO: Write to the output file (follow output_wrapper)
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// since we anyways are not equipped for Incremental Builds yet!
			forgetLastBuiltState();
		}
	}

	private CompilerOptions getOptions(Settings settings) {
		CompilerOptions options = new CompilerOptions();

		settings.compilationLevel.setOptionsForCompilationLevel(options); // --compilation_level
		settings.warningLevel.setOptionsForWarningLevel(options); // --warning_level
		
		// Report variables whose type is not declared, and unable to determine automatically
		options.reportUnknownTypes = settings.reportUnknownTypes;
		return options;
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		JSResourceVisitor visitor = new JSResourceVisitor();
		getProject().accept(visitor);
		runCompiler(visitor.getResources());
	}
}
