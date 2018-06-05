package parser;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.internal.resources.File;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

public class EcpsVerifier extends Wizard {

	protected MainPage mainPage;

	protected String path = "";
	protected String file = "";

	protected Aadl2Xml aadl2xml;

	public EcpsVerifier() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		System.out.println("Starting transformation process");
		File fselected;
		Folder foselected;

		if (mainPage.getSelectedFile() != null) {
			fselected = mainPage.getSelectedFile();
			file = fselected.getLocation().toOSString();
		}

		if (mainPage.getSelectedFolder() != null) {
			foselected = mainPage.getSelectedFolder();
			path = foselected.getLocation().toOSString();
		}

		if(!file.isEmpty() && !path.isEmpty() && !mainPage.getFileName().isEmpty()){
			try {
				aadl2xml = new Aadl2Xml(file, mainPage.getFileName()+".xml", path+"/");
			} catch (Exception e) {
				//MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "error", e.getMessage());
				e.printStackTrace();
				return false;
			}
		}

		if(aadl2xml != null)
			aadl2xml.uppaalFileGeneration();

		return true;

	}

	@Override
	public String getWindowTitle() {
		return "ECPS Verifier";
	}

	@Override
	public void addPages() {
		mainPage = new MainPage();
		addPage(mainPage);
	}
}
