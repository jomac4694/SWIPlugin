package moretesting.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JOptionPane;
import javax.xml.ws.handler.MessageContext;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.handlers.HandlerUtil;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.MessageBox;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler implements IConsoleListener, IPatternMatchListener {
	private String pattern;
	private int matchCount = 0;
	public SampleHandler() {
		pattern = "\\d+";
		matchCount = 0;
		ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(this);
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart workBench = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IFileEditorInput file = (IFileEditorInput) workBench.getSite().getPage().getActiveEditor().getEditorInput();
		if (file == null)
			try {
				throw new FileNotFoundException();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()+file.getFile().getFullPath().toString(),
				this.matchCount + " matches found");
		try {
			File jr = new File(System.getProperty("user.dir")+"\\SWIntegrity.jar");
			URL url = jr.toURI().toURL();
			String jarURL = "jar:" + url + "!/";
			URL urls[] = {new URL(jarURL)};
			URLClassLoader ucl = new URLClassLoader(urls);
			Class c = ucl.loadClass("SIT");
			Method m = c.getDeclaredMethod("main", String[].class);
			String[] params = {"-j", ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()+file.getFile().getFullPath().toString()};
			m.invoke(null, (Object) params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void consolesAdded(IConsole[] arg0) {
		for (IConsole c: arg0) {
			if (c instanceof TextConsole) {
				((TextConsole) c).addPatternMatchListener((IPatternMatchListener) this);
			}
		}
		
	}
	
	@Override
	public void consolesRemoved(IConsole[] consoles) {
		for(IConsole console : consoles) {
			if(console instanceof TextConsole) {
				((TextConsole) console).removePatternMatchListener(this);
			}
		}
	}

	@Override
	public void connect(TextConsole arg0) {
		
	}

	@Override
	public void disconnect() {
		
	}

	@Override
	public void matchFound(PatternMatchEvent arg0) {
		this.matchCount++;
	}

	@Override
	public int getCompilerFlags() {
		return 0;
	}

	@Override
	public String getLineQualifier() {
		return null;
	}

	@Override
	public String getPattern() {
		return pattern;
	}
	
	

}
