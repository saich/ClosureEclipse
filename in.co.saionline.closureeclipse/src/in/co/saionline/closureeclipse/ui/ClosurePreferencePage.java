package in.co.saionline.closureeclipse.ui;

import in.co.saionline.closureeclipse.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ClosurePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public ClosurePreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		// set preference store
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Closure Compiler Settings");
	}

	@Override
	protected void createFieldEditors() {
		
		// TODO: Add correct fields here, like JAR location and other global settings
		// Compilation Level
		addField(new RadioGroupFieldEditor("level", "&Compilation Level", 3, new String[][]{
				{"Whitespace only", "whitespace"},
				{"Simple Optimizations", "simple"},
				{"Advanced Optimizations", "advanced"}
		}, getFieldEditorParent()));
		
		// Output wrapper
		addField(new StringFieldEditor("output_wrapper", "Output Wrapper", getFieldEditorParent()));
		
	}

}