package in.co.saionline.closureeclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.WarningLevel;

public class SettingsManager {

	public static class Settings {
		public String outputPath;
		public String outputWrapper;
		public CompilationLevel compilationLevel;
		public WarningLevel warningLevel;
		public CheckLevel reportUnknownTypes;
		public String externsList;
	}

	private static QualifiedName qOptLevel = new QualifiedName("optlevel", "optlevel");
	private static QualifiedName qOutputWrapper = new QualifiedName("outputwrapper", "outputwrapper");
	private static QualifiedName qOutputPath = new QualifiedName("outputpath", "outputpath");
	private static QualifiedName qWarningLevel = new QualifiedName("warningLevel", "warningLevel");
	private static QualifiedName qReportUnknownTypes = new QualifiedName("unknowntypes", "unknowntypes");
	private static QualifiedName qExcludeFile = new QualifiedName("excludefile", "excludefile");
	private static QualifiedName qExternsList = new QualifiedName("externsList", "externsList");

	private static final String OPT_WHITESPACE = "whitespace";
	private static final String OPT_SIMPLE = "simple";
	private static final String OPT_ADVANCED = "advanced";

	private static final String WARN_VERBOSE = "verbose";
	private static final String WARN_DEFAULT = "default";
	private static final String WARN_QUIET = "quiet";

	private static final String REPORT_OFF = "off";
	private static final String REPORT_WARNING = "warning";
	private static final String REPORT_ERROR = "error";

	public static Settings getDefaultSettings() {
		Settings settings = new Settings();
		settings.outputPath = "bin/output.js";
		settings.outputWrapper = "";
		settings.compilationLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS;
		settings.warningLevel = WarningLevel.VERBOSE;
		settings.reportUnknownTypes = CheckLevel.OFF;
		settings.externsList = "";
		return settings;
	}

	public static CheckLevel convertStringToCheckLevel(String value) {
		CheckLevel level = CheckLevel.OFF;
		if (value != null) {
			if (value.equals(REPORT_OFF))
				level = CheckLevel.OFF;
			else if (value.equals(REPORT_WARNING))
				level = CheckLevel.WARNING;
			else if (value.equals(REPORT_ERROR))
				level = CheckLevel.ERROR;
		}
		return level;
	}

	public static String convertCheckLevelToString(CheckLevel level) {
		String value = "";
		switch (level) {
			case OFF :
				value = REPORT_OFF;
				break;
			case WARNING :
				value = REPORT_WARNING;
				break;
			case ERROR :
				value = REPORT_ERROR;
				break;
			default :
				break;
		}
		return value;
	}

	public static WarningLevel convertStringToWarningLevel(String value) {
		WarningLevel level = WarningLevel.VERBOSE;
		if (value != null) {
			if (value.equals(WARN_DEFAULT))
				level = WarningLevel.DEFAULT;
			else if (value.equals(WARN_QUIET))
				level = WarningLevel.QUIET;
			else if (value.equals(WARN_VERBOSE))
				level = WarningLevel.VERBOSE;
		}
		return level;
	}

	public static String convertWarningLevelToString(WarningLevel level) {
		String value = "";
		switch (level) {
			case DEFAULT :
				value = WARN_DEFAULT;
				break;
			case QUIET :
				value = WARN_QUIET;
				break;
			case VERBOSE :
				value = WARN_VERBOSE;
				break;
			default :
				break;
		}
		return value;
	}

	public static CompilationLevel convertStringToCompilationLevel(String value) {
		CompilationLevel level = CompilationLevel.SIMPLE_OPTIMIZATIONS;
		if (value != null) {
			if (value.equals(OPT_WHITESPACE))
				level = CompilationLevel.WHITESPACE_ONLY;
			else if (value.equals(OPT_SIMPLE))
				level = CompilationLevel.SIMPLE_OPTIMIZATIONS;
			else if (value.equals(OPT_ADVANCED))
				level = CompilationLevel.ADVANCED_OPTIMIZATIONS;
		}
		return level;
	}

	public static String convertCompilationLevelToString(CompilationLevel level) {
		String value = "";

		switch (level) {
			case WHITESPACE_ONLY :
				value = OPT_WHITESPACE;
				break;
			case SIMPLE_OPTIMIZATIONS :
				value = OPT_SIMPLE;
				break;
			case ADVANCED_OPTIMIZATIONS :
				value = OPT_ADVANCED;
				break;
			default :
				break;
		}
		return value;
	}

	public static Settings getProjectSettings(IProject project) {
		Settings settings = getDefaultSettings();
		if (project != null) {
			try {
				// Output Path
				String value = project.getPersistentProperty(qOutputPath);
				if (value != null)
					settings.outputPath = value;

				// Output Wrapper
				value = project.getPersistentProperty(qOutputWrapper);
				if (value != null)
					settings.outputWrapper = value;

				// Compilation Level
				value = project.getPersistentProperty(qOptLevel);
				settings.compilationLevel = convertStringToCompilationLevel(value);

				// Warning Level
				settings.warningLevel = convertStringToWarningLevel(project.getPersistentProperty(qWarningLevel));

				// Report Unknown Types
				settings.reportUnknownTypes = convertStringToCheckLevel(project
						.getPersistentProperty(qReportUnknownTypes));

				// Externs List
				String externsList = project.getPersistentProperty(qExternsList);
				if(externsList != null)
					settings.externsList = externsList;

			} catch (CoreException e) {
			}
		}
		return settings;
	}

	public static boolean setProjectSettings(IProject project, Settings settings) {
		boolean bSuccess = false;

		// In case of NULL settings, lets use the default settings
		if (settings == null)
			settings = getDefaultSettings();

		try {
			project.setPersistentProperty(qOutputPath, settings.outputPath);
			project.setPersistentProperty(qOutputWrapper, settings.outputWrapper);
			project.setPersistentProperty(qOptLevel, convertCompilationLevelToString(settings.compilationLevel));
			project.setPersistentProperty(qWarningLevel, convertWarningLevelToString((settings.warningLevel)));
			project.setPersistentProperty(qReportUnknownTypes, convertCheckLevelToString(settings.reportUnknownTypes));
			project.setPersistentProperty(qExternsList, settings.externsList);
			bSuccess = true;
		} catch (CoreException e) {
		}
		return bSuccess;
	}

	public static boolean setFileExclude(IFile file, boolean exclude) {
		boolean bSuccess = false;
		try {
			file.setPersistentProperty(qExcludeFile, String.valueOf(exclude));
			bSuccess = true;
		} catch (CoreException e) {
		}
		return bSuccess;
	}

	public static boolean getFileExclude(IFile file) {
		boolean bExcluded = false;
		try {
			String excluded = file.getPersistentProperty(qExcludeFile);
			if (excluded != null)
				bExcluded = Boolean.parseBoolean(excluded);
		} catch (CoreException e) {
		}
		return bExcluded;
	}
}
