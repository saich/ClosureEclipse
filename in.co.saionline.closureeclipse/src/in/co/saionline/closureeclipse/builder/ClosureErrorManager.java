package in.co.saionline.closureeclipse.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.ErrorManager;
import com.google.javascript.jscomp.JSError;

public class ClosureErrorManager implements ErrorManager {

	public static final String MARKER_TYPE = "in.co.saionline.ClosureEclipse.closureProblem";
	
	private double mTypedPercent;
	private IProject mProject;

	public ClosureErrorManager(IProject	 project) {
		mProject = project;
	}

	@Override
	public void report(CheckLevel level, JSError error) {
		try {
			IFile file = mProject.getWorkspace().getRoot().getFileForLocation(new Path(error.sourceName));
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, error.description);
			marker.setAttribute(IMarker.SEVERITY, getMarkerSeverity(level));
			marker.setAttribute(IMarker.LINE_NUMBER, error.lineNumber);
		} catch (CoreException e) {
		}
	}

	private int getMarkerSeverity(CheckLevel level) {
		switch (level) {
		case ERROR:
			return IMarker.SEVERITY_ERROR;
		case WARNING:
			return IMarker.SEVERITY_WARNING;
		case OFF:
		default:
			return IMarker.SEVERITY_INFO;
		}
	}

	@Override
	public void setTypedPercent(double typedPercent) {
		// TODO: Have a limit of Typed Percent from the Properties to show as warning..
		mTypedPercent = typedPercent;
	}

	@Override
	public double getTypedPercent() {
		return mTypedPercent;
	}

	@Override
	public void generateReport() {
	}

	@Override
	public int getErrorCount() {
		return 0;
	}

	@Override
	public JSError[] getErrors() {
		return new JSError[0];
	}

	@Override
	public int getWarningCount() {
		return 0;
	}

	@Override
	public JSError[] getWarnings() {
		return new JSError[0];
	}

}
