package in.co.saionline.closureeclipse;

import in.co.saionline.closureeclipse.builder.ToggleNatureAction;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	// This class is required only for Dynamic label of 'Toggle the Nature'
	// @see http://stackoverflow.com/questions/6635269/dynamic-label-of-a-popup-action-in-eclipse-plugin-development
	
	@Override
	public void earlyStartup() {
		new ToggleNatureAction();
	}
}
