package parser;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.internal.resources.File;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import javax.swing.JOptionPane;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.swt.SWT;
import javax.swing.JFrame;

@SuppressWarnings("restriction")
public class MainPage extends WizardPage {

	private ITreeContentProvider fContentProvider;
	private IBaseLabelProvider fLabelProvider;

	private TreeViewer folderViewer;
	private TreeViewer fileViewer;

	private Composite container;

	private Tree folderWidget;
	private Tree fileWidget;

	private Text fileName;

	private int fWidth = 60;
	private int fHeight = 18;

	protected MainPage() {
		super("ECPS Verifier Tool");
		setTitle("ECPS Verifier - Automata generation");
		setDescription("Select file and folder required to perform the transformation.");
	}

	@Override
	public void createControl(Composite parent) {
		/*
		 * createControl creates the visual structures of the main page
		 */

		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		fContentProvider = new BaseWorkbenchContentProvider();
		fLabelProvider = new WorkbenchLabelProvider();

		final ViewerFilter FileFilter = new FileFilter();
		final ViewerFilter AADLFilter = new AADLFileFilter();

		Label fileNameLabel = new Label(container, SWT.NONE);
		fileNameLabel.setText("Define the output filename:");

		GridData dataText = new GridData(GridData.FILL_HORIZONTAL);
		// dataText.widthHint = convertWidthInCharsToPixels(fWidth);

		fileName = new Text(container, SWT.BORDER);
		fileName.setLayoutData(dataText);
		fileName.setText("");
		fileName.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if(!fileName.getText().isEmpty()){
					if(containsIllegals(fileName.getText())){
						JOptionPane.showMessageDialog(new JFrame(), "The filename contains illegal characters!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					if(fileName.getText().contains(" ")){
						//MessageDialog.openInformation(parent.getShell(), "Error", "The filename cannot contain spaces");
						JOptionPane.showMessageDialog(new JFrame(), "The filename cannot contain spaces!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				checkConditions();
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label messageLabel = new Label(container, SWT.NONE);
		messageLabel.setText("Choose the AADL file:");

		fileViewer = createTreeViewer(container, AADLFilter);

		fileViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				checkConditions();
			}
		});

		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = convertWidthInCharsToPixels(fWidth);
		data.heightHint = convertHeightInCharsToPixels(fHeight);
		data.minimumHeight = convertHeightInCharsToPixels(fHeight);

		fileWidget = fileViewer.getTree();
		fileWidget.setLayoutData(data);
		fileWidget.setFont(parent.getFont());

		Label destination = new Label(container, SWT.NONE);
		destination.setText("Choose the destination folder:");

		folderViewer = createTreeViewer(container, FileFilter);

		folderViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				checkConditions();
			}
		});

		folderWidget = folderViewer.getTree();
		folderWidget.setLayoutData(data);
		folderWidget.setFont(parent.getFont());

		setControl(container);
		setPageComplete(false);
	}

	protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
		/*
		 * Create a treeviewer component
		 */
		return new TreeViewer(new Tree(parent, style));
	}

	public class FileFilter extends ViewerFilter {
		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.
		 * viewers.Viewer, java.lang.Object, java.lang.Object) Designed class
		 * that implement a filter to show structures that are not files.
		 */
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element.getClass().toString().equals("class org.eclipse.core.internal.resources.File"))
				return false;
			else
				return true;
		}
	}

	public class AADLFileFilter extends ViewerFilter {
		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.
		 * viewers.Viewer, java.lang.Object, java.lang.Object) Designed class
		 * that implement a filter to show only the AADL files.
		 */
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element.getClass().toString().equals("class org.eclipse.core.internal.resources.File")) {
				File file = (File) element;
				// String fileName = file.getName();

				if (file.getFileExtension().equals("aadl"))
					return true;
				else
					return false;
			} else
				return true;
		}
	}

	public void checkConditions() {
		/*
		 * Check the required conditions on the screen to perform the model
		 * transformation
		 */
		boolean fileSelected = false;
		boolean folderSelected = false;
		boolean fileNameOk = false;

		if (fileViewer.getSelection().isEmpty() != true) {
			TreeSelection auxFile = (TreeSelection) fileViewer.getSelection();
			if (auxFile.getFirstElement().getClass().toString()
					.equals("class org.eclipse.core.internal.resources.File"))
				fileSelected = true;
		}

		if (folderViewer.getSelection().isEmpty() != true) {
			TreeSelection auxFolder = (TreeSelection) folderViewer.getSelection();
			if (auxFolder.getFirstElement().getClass().toString()
					.equals("class org.eclipse.core.internal.resources.Folder"))
				folderSelected = true;
		}

		if (!fileName.getText().isEmpty()) {
			if(!fileName.getText().contains(" ")){
				if(!containsIllegals(fileName.getText())){
					fileNameOk = true;
				}
			}
		}

		setPageComplete(fileSelected && folderSelected && fileNameOk);
	}

	public String getFileName(){
		return fileName.getText();
	}

	public boolean containsIllegals(String toExamine) {
		Pattern pattern = Pattern.compile("[-,.!'~#@*+%{}<>\\[\\]|\"_^]");
		//Pattern pattern = Pattern.compile("[|!\"#@£§$%&/{([)]=}?'»«+*`´~^ºª-<>,;.:");
		Matcher matcher = pattern.matcher(toExamine);
		return matcher.find();
	}

	public File getSelectedFile() {
		/*
		 * Return the selected AADL file used as base to the system
		 * transformation
		 */
		File file;
		try {
			if (fileViewer.getSelection().isEmpty() != true) {
				TreeSelection auxFile = (TreeSelection) fileViewer.getSelection();
				if (auxFile.getFirstElement().getClass().toString()
						.equals("class org.eclipse.core.internal.resources.File")) {
					file = (File) auxFile.getFirstElement();
					return file;
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return null;
	}

	public Folder getSelectedFolder() {
		/*
		 * Return the destination folder used to record the generated automata
		 * file
		 */
		Folder folder;
		try {
			if (folderViewer.getSelection().isEmpty() != true) {
				TreeSelection auxFolder = (TreeSelection) folderViewer.getSelection();
				if (auxFolder.getFirstElement().getClass().toString()
						.equals("class org.eclipse.core.internal.resources.Folder")) {
					folder = (Folder) auxFolder.getFirstElement();
					return folder;
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return null;
	}

	protected TreeViewer createTreeViewer(Composite parent, ViewerFilter filter) {
		/*
		 * Create the treeviewer component and apply the designed filter
		 */
		TreeViewer fViewer;
		int style = SWT.BORDER | SWT.SINGLE;

		fViewer = doCreateTreeViewer(parent, style);
		fViewer.setContentProvider(fContentProvider);
		fViewer.setLabelProvider(fLabelProvider);

		fViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());

		fViewer.setFilters(filter);
		return fViewer;
	}

}
