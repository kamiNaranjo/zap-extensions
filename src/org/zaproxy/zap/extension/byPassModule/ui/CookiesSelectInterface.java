package org.zaproxy.zap.extension.byPassModule.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.zaproxy.zap.extension.byPassModule.ByPassModule;
import org.zaproxy.zap.extension.byPassModule.ExtensionByPass;
import org.zaproxy.zap.view.AbstractFormDialog;

public class CookiesSelectInterface extends AbstractFormDialog{
	
	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	private JButton acceptButton;
	private JLabel topLabel;
	private List<String> cookies;
	private JPanel checkBoxPanel;
	
	public CookiesSelectInterface(JFrame owner, List<String> cookiesName) {
		super(owner, ExtensionByPass.getMessageString("title.windows.selecCookie"), false);
		this.cookies = cookiesName;
		super.add(getMainPanel());
		owner.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}

	protected JLabel getLabelHead() {
		topLabel = new JLabel(ExtensionByPass.getMessageString("label.cookiesSelect.ByPass"));
		topLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		return topLabel;
	}

	protected JButton getConfirmButton() {
		acceptButton = new JButton(getConfirmButtonLabel());
		acceptButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				ByPassModule.getMessageWithOutCookies(getCookiesSelected());
			}
		});
		
		return acceptButton;
	}
	
	@Override
	protected String getConfirmButtonLabel() {
		return ExtensionByPass.getMessageString("label.confirm.button.atack");
	}

	protected JPanel getMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(getLabelHead());
		JPanel checkPanel = createCheckBoxPanel();
		checkPanel.setSize(getLabelHead().getSize());
		mainPanel.add(checkPanel);
		mainPanel.add(getConfirmButton());
		mainPanel.setVisible(true);
		return mainPanel;
	}
	
	private JPanel createCheckBoxPanel() {
		checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
		checkBoxPanel.setBackground(Color.WHITE);
		for(String cookie : cookies) {
			JCheckBox checkBox = new JCheckBox(cookie);
			checkBoxPanel.add(checkBox);
		}
		
		return checkBoxPanel;
	}
	
	private List<String> getCookiesSelected() {
		List<String> cookiesSelected = new ArrayList<>();
		Component[] components = checkBoxPanel.getComponents();
		for(Component component : components) {
			if(component.getClass().isAssignableFrom(JCheckBox.class)) {
				JCheckBox checkBox = (JCheckBox) component;
				if(checkBox.isSelected()) {
					cookiesSelected.add(checkBox.getText());
				}
			}
		}
		
		return cookiesSelected;
	}

	@Override
	protected JPanel getFieldsPanel() {
		return null;
	}
}