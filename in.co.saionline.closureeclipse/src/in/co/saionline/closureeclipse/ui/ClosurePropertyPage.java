package in.co.saionline.closureeclipse.ui;

import in.co.saionline.closureeclipse.SettingsManager;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.WarningLevel;

@SuppressWarnings("restriction")
public class ClosurePropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	IAdaptable element;

	Button mRadioWhitespaceOpt;
	Button mRadioSimpleOpt;
	Button mRadioAdvancedOpt;
	Text mOutputPath;
	Text mOutputWrapper;
	
	Button mWarnDefault;
	Button mWarnQuiet;
	Button mWarnVerbose;
	
	Button mUnknownOff;
	Button mUnknownWarn;
	Button mUnknownError;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public ClosurePropertyPage() {
		super();
	}

	@Override
	public IAdaptable getElement() {
		return element;
	}

	@Override
	public void setElement(IAdaptable element) {
		this.element = element;
	}

	@Override
	protected Control createContents(Composite parent) {

		// Composite
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		addCompilerOptionsGroup(composite);
		addOutputGroup(composite);

		setCurrentValues();

		return composite;
	}

	private void addCompilerOptionsGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText("Compiler Options");
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// Label for optimization level
		Composite optLevelComposite = new Composite(group, SWT.NONE);
		optLevelComposite.setLayout(new GridLayout(3, false));
		optLevelComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		Label optlevelLabel = new Label(optLevelComposite, SWT.NONE);
		optlevelLabel.setText("Optimization Level: ");
		optlevelLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		mRadioWhitespaceOpt = new Button(optLevelComposite, SWT.RADIO);
		mRadioSimpleOpt = new Button(optLevelComposite, SWT.RADIO);
		mRadioAdvancedOpt = new Button(optLevelComposite, SWT.RADIO);

		mRadioWhitespaceOpt.setText("Whitespace only");
		mRadioSimpleOpt.setText("Simple Optimizations");
		mRadioAdvancedOpt.setText("Advanced Optimizations");

		// Warning Level
		Composite warnLevelComposite = new Composite(group, SWT.NONE);
		warnLevelComposite.setLayout(new GridLayout(3, false));
		warnLevelComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		Label warnLevelLabel = new Label(warnLevelComposite, SWT.NONE);
		warnLevelLabel.setText("Warning Level: ");
		warnLevelLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		mWarnDefault = new Button(warnLevelComposite, SWT.RADIO);
		mWarnQuiet = new Button(warnLevelComposite, SWT.RADIO);
		mWarnVerbose = new Button(warnLevelComposite, SWT.RADIO);
		
		mWarnDefault.setText("Default");
		mWarnQuiet.setText("Quiet");
		mWarnVerbose.setText("Verbose");
		
		// Report Unknown Types
		Composite unknownTypesComposite = new Composite(group, SWT.NONE);
		unknownTypesComposite.setLayout(new GridLayout(3, false));
		unknownTypesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		Label unknownTypesLabel = new Label(unknownTypesComposite, SWT.NONE);
		unknownTypesLabel.setText("Report Unknown Types: ");
		unknownTypesLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		mUnknownOff = new Button(unknownTypesComposite, SWT.RADIO);
		mUnknownWarn = new Button(unknownTypesComposite, SWT.RADIO);
		mUnknownError = new Button(unknownTypesComposite, SWT.RADIO);
		
		mUnknownOff.setText("Off");
		mUnknownWarn.setText("Warning");
		mUnknownError.setText("Error");
	}

	private void addOutputGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText("Output Options");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// Label for output file
		Label outputFileLabel = new Label(group, SWT.NONE);
		outputFileLabel.setText("Output File:");

		// Output File input
		mOutputPath = new Text(group, SWT.SINGLE | SWT.BORDER);
		mOutputPath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		// Label for output wrapper
		Label outputWrapperLabel = new Label(group, SWT.NONE);
		outputWrapperLabel.setText("Output Wrapper:");

		// Output File input
		mOutputWrapper = new Text(group, SWT.SINGLE | SWT.BORDER);
		mOutputWrapper.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
	}

	private void setCurrentValues() {
		Project project = ((Project) getElement());
		SettingsManager.Settings settings = SettingsManager.getProjectSettings(project);
		setSettings(settings);
	}

	@Override
	public boolean performOk() {
		Project project = ((Project) getElement());

		SettingsManager.Settings settings = new SettingsManager.Settings();
		settings.outputPath = mOutputPath.getText();
		settings.outputWrapper = mOutputWrapper.getText();

		CompilationLevel level = CompilationLevel.SIMPLE_OPTIMIZATIONS;
		if (mRadioWhitespaceOpt.getSelection())
			level = CompilationLevel.WHITESPACE_ONLY;
		else if (mRadioSimpleOpt.getSelection())
			level = CompilationLevel.SIMPLE_OPTIMIZATIONS;
		else if (mRadioAdvancedOpt.getSelection())
			level = CompilationLevel.ADVANCED_OPTIMIZATIONS;

		settings.compilationLevel = level;

		// Warning Level
		WarningLevel warnLevel = WarningLevel.VERBOSE;
		if(mWarnDefault.getSelection())
			warnLevel = WarningLevel.DEFAULT;
		else if(mWarnQuiet.getSelection())
			warnLevel = WarningLevel.QUIET;
		else if(mWarnVerbose.getSelection())
			warnLevel = WarningLevel.VERBOSE;
		
		settings.warningLevel = warnLevel;
		
		// Report Unknown Types
		CheckLevel reportLevel = CheckLevel.OFF;
		if(mUnknownOff.getSelection())
			reportLevel = CheckLevel.OFF;
		else if(mUnknownWarn.getSelection())
			reportLevel = CheckLevel.WARNING;
		else if(mUnknownError.getSelection())
			reportLevel = CheckLevel.ERROR;
		
		settings.reportUnknownTypes = reportLevel;

		SettingsManager.setProjectSettings(project, settings);

		// Seems like Project Properties are changed! Lets build again!!
		try {
			project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		} catch (CoreException e) {
		}

		return true;
	}

	@Override
	protected void performDefaults() {
		setSettings(SettingsManager.getDefaultSettings());
		super.performDefaults();
	}

	private void setSettings(SettingsManager.Settings settings) {
		if (settings != null) {
			mOutputPath.setText(settings.outputPath);
			mOutputWrapper.setText(settings.outputWrapper);
			switch (settings.compilationLevel) {
				case WHITESPACE_ONLY :
					mRadioWhitespaceOpt.setSelection(true);
					break;
				case SIMPLE_OPTIMIZATIONS :
					mRadioSimpleOpt.setSelection(true);
					break;
				case ADVANCED_OPTIMIZATIONS :
					mRadioAdvancedOpt.setSelection(true);
					break;
				default :
					mRadioSimpleOpt.setSelection(true);
			}
			
			// Warning Level
			switch (settings.warningLevel) {
				case DEFAULT :
					mWarnDefault.setSelection(true);
					break;
				case QUIET:
					mWarnQuiet.setSelection(true);
					break;
				case VERBOSE:
					mWarnVerbose.setSelection(true);
					break;
				default :
					mWarnVerbose.setSelection(true);
			}
			
			// Report Unknown Types
			switch (settings.reportUnknownTypes) {
				case OFF :
					mUnknownOff.setSelection(true);
					break;
				case WARNING:
					mUnknownWarn.setSelection(true);
					break;
				case ERROR:
					mUnknownError.setSelection(true);
					break;
				default :
					mUnknownOff.setSelection(true);
			}
		}
	}

}