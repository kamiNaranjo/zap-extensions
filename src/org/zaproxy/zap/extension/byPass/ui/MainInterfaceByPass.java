package org.zaproxy.zap.extension.byPass.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.zaproxy.zap.extension.byPass.ExtensionByPass;
import org.zaproxy.zap.extension.byPass.TargetByPass;
import org.zaproxy.zap.view.AbstractFormDialog;

public class MainInterfaceByPass extends AbstractFormDialog{

	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	private JButton acceptButton;
	private HttpMessageSelectorPanel treeSities;
	private JLabel topLabel;
	private static JFrame owner;
	private ExtensionByPass extension;
	
	public MainInterfaceByPass(ExtensionByPass extension, JFrame ownerFrame){
		super(ownerFrame, ExtensionByPass.getMessageString("title.windows.ByPass"), false);
		owner = ownerFrame;
		this.extension = extension;
		treeSities = new HttpMessageSelectorPanel();
		super.add(getFieldsPanel());
		ownerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	protected JButton getConfirmButton() {
		acceptButton = new JButton(getConfirmButtonLabel());
		acceptButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if(treeSities.validate()){
					new TargetByPass(treeSities, extension, MainInterfaceByPass.this);
				}
			}
		});
		return acceptButton;
	}

	@Override
	protected String getConfirmButtonLabel() {
		return ExtensionByPass.getMessageString("label.confirm.button.ByPass");
	}

	public static JFrame getOwnerFrame(){
		return owner;
	}
	
	@Override
	protected JPanel getFieldsPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(getLabelHead());
		mainPanel.add(treeSities.getPanel());
		mainPanel.add(getConfirmButton());
		mainPanel.setVisible(true);
		return mainPanel;
	}
	
	protected JLabel getLabelHead() {
		topLabel = new JLabel(ExtensionByPass.getMessageString("label.atack.message.ByPass"));
		topLabel.setBorder(new EmptyBorder(10, 3, 10, 3));
		return topLabel;
	}

}
