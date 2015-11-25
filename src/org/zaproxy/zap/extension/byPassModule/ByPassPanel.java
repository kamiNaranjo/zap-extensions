package org.zaproxy.zap.extension.byPassModule;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.MainFrame;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.byPassModule.ui.MainInterfaceByPass;

public class ByPassPanel extends AbstractPanel{
	
	private static final long serialVersionUID = 1L;
	private JButton acceptButton;
	private JFrame mainFrame;
	
	public ByPassPanel(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
		initialize(false, null);
	}

	public ByPassPanel(MainFrame mainFrame, List<HttpMessage> resultsArray) {
		super();
		this.mainFrame = mainFrame;
		initialize(true, resultsArray);
	}
	
	private void initialize(boolean show, List<HttpMessage> resultsArray){
		this.setLayout(new CardLayout());
		if (Model.getSingleton().getOptionsParam().getViewParam().getWmUiHandlingOption() == 0) {
			this.setSize(474, 251);
	    }
		BoxLayout mainLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(mainLayout);
	    this.setName(ExtensionByPass.getMessageString("title.windows.ByPass"));
	    this.setIcon(ByPassUtils.BYPASS_ICON);
	    this.add(getFieldsPanel());
	    if(show)
	    	this.add(tableResults(resultsArray));
	    if (View.isInitialised()) {
	    	View.getSingleton().getMainFrame().getMainFooterPanel().addFooterToolbarRightLabel(new JLabel("FOOTER"));
	    }
	}
	
	protected JPanel getFieldsPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.add(getConfirmButton());
		mainPanel.setVisible(true);
		return mainPanel;
	}
	
	protected JButton getConfirmButton() {
		acceptButton = new JButton(ExtensionByPass.getMessageString("title.windows.ByPass"), ByPassUtils.BYPASS_ICON);
		acceptButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				MainInterfaceByPass interfaceByPass = new MainInterfaceByPass(mainFrame);
            	interfaceByPass.pack();
            	interfaceByPass.setVisible(true);
			}
		});
		return acceptButton;
	}
	
	private static JPanel tableResults(List<HttpMessage> resultsArray){
		JTable tableResults = new JTable();
		DefaultTableModel tableModelResult = new DefaultTableModel(0, 0);
		String columnNames[] = {
			ExtensionByPass.getMessageString("label.column.ulr"),
			ExtensionByPass.getMessageString("label.column.method"),
			ExtensionByPass.getMessageString("label.column.code"),
			ExtensionByPass.getMessageString("label.column.reason"),
			ExtensionByPass.getMessageString("label.column.rtt"),
			ExtensionByPass.getMessageString("label.column.size.header"),
			ExtensionByPass.getMessageString("label.column.size.body")
		};
		
		tableModelResult.setColumnIdentifiers(columnNames);
		for(HttpMessage results:resultsArray){
			tableModelResult.addRow(new Object[] {
				results.getRequestHeader().getHeader("REFERER"),
				results.getRequestHeader().getMethod(),
				results.getResponseHeader().getStatusCode(),
				results.getResponseHeader().getReasonPhrase(),
				results.getTimeElapsedMillis(),
				results.getResponseHeader().getContentLength(),
				results.getResponseBody().length()
			});
		}
		tableResults.setModel(tableModelResult);
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(tableResults);
		return tablePanel;
	}
	
}
