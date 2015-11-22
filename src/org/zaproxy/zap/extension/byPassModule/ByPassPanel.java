package org.zaproxy.zap.extension.byPassModule;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.view.View;

public class ByPassPanel extends AbstractPanel{
	
	private static final long serialVersionUID = 1L;
	private JButton acceptButton;

	public ByPassPanel(){
		super();
		 this.setLayout(new CardLayout());
	        if (Model.getSingleton().getOptionsParam().getViewParam().getWmUiHandlingOption() == 0) {
	        	this.setSize(474, 251);
	        }
	        this.setName(ExtensionByPass.getMessageString("title.windows.ByPass"));
	        this.add(getFieldsPanel());
	        
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
				
			}
		});
		return acceptButton;
	}
}
