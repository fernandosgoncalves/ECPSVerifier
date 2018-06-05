package parser;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

public class EcpsVerifierInvoke extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			 WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new EcpsVerifier());
			 dialog.setMinimumPageSize(100, 400);
			 dialog.open();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
