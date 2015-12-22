package org.zaproxy.zap.extension.byPass.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.parosproxy.paros.network.HtmlParameter;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.byPass.ExtensionByPass;
import org.zaproxy.zap.view.AbstractFormDialog;

public class CookiesSelectInterface extends AbstractFormDialog{
	
	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	private JButton acceptButton;
	private JLabel topLabel;
	private List<HtmlParameter> cookies;
	private JPanel checkBoxPanel;
	private JPanel resourcesStatics;
	private JPanel srcPanel;
	private ExtensionByPass extension;
	private AbstractFormDialog mainInterface;
	private static List<String> listStaticResource;
	private List<HttpMessage> messages;
	
	public CookiesSelectInterface(ExtensionByPass extension, JFrame owner, List<HttpMessage> messages, List<HtmlParameter> cookieArray, AbstractFormDialog mainInterface) {
		super(owner, ExtensionByPass.getMessageString("title.windows.selecCookie"), false);
		listStaticResource = Arrays.asList(
				".js", ".css", ".json", ".asx", ".swf",
				".img", ".png", ".gif", ".x-icon", ".ico",
				".mpwg3", ".mp4");
		this.mainInterface = mainInterface;
		this.cookies = cookieArray;
		this.extension = extension;
		this.messages = messages;
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JScrollPane scroll = new JScrollPane(getMainPanel());
		panel.add(scroll);
		panel.add(getConfirmButton());
		super.add(panel);
		owner.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	protected JPanel getMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(selectCookiesPanel(), BorderLayout.NORTH);
		mainPanel.add(createPanelResources(), BorderLayout.CENTER);
		mainPanel.setVisible(true);
		super.pack();
		return mainPanel;
	}
	
	private JPanel selectCookiesPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(getLabelHead(ExtensionByPass.getMessageString("label.cookiesSelect.ByPass")), BorderLayout.NORTH);
		panel.add(createCheckBoxPanel(), BorderLayout.CENTER);
		return panel;
	}
	
	private JPanel createCheckBoxPanel() {
		checkBoxPanel = new JPanel();
		int posX = 2;
		int posY = cookies.size()/posX;
		checkBoxPanel.setLayout(new GridLayout(posY, posX));
		checkBoxPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		checkBoxPanel.setBackground(Color.WHITE);
		final JCheckBox checkBoxAll = new JCheckBox(ExtensionByPass.getMessageString("message.atack.all"));
		if(cookies.size() > 1){
			checkBoxPanel.add(checkBoxAll);
		}
		for(HtmlParameter cookie : cookies) {
			JCheckBox checkBox = new JCheckBox(cookie.getName());
			checkBoxPanel.add(checkBox);
		}
		checkBoxAll.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource()== checkBoxAll && checkBoxAll.isSelected()) {
					selectAll(checkBoxPanel, true);
				}else{
					selectAll(checkBoxPanel, false);
				}
			}
		});	
		checkBoxPanel.setVisible(true);
		return checkBoxPanel;
	}
	
	protected JLabel getLabelHead(String text) {
		topLabel = new JLabel(text);
		topLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		return topLabel;
	}
	
	private JPanel createPanelResources(){
		resourcesStatics = new JPanel();
		resourcesStatics.setLayout(new BorderLayout());
		resourcesStatics.setBorder(new EmptyBorder(10, 10, 10, 10));
		final JCheckBox checkBox = new JCheckBox(ExtensionByPass.getMessageString("label.delete.resources"));
		resourcesStatics.add(checkBox, BorderLayout.NORTH);
		final List<String> resourcesMessage = new ArrayList<>();
		for(HttpMessage message: messages){
			String uri = message.getRequestHeader().getURI().toString();
			for(String resource:listStaticResource){
				if(uri.endsWith(resource)){
					if(!resourcesMessage.contains(resource)){
						resourcesMessage.add(resource);
					break;
					}
				}
			}
		}
		resourcesStatics.add(createSRCBoxPanel(resourcesMessage), BorderLayout.CENTER);
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource()== checkBox && checkBox.isSelected()) {
					srcPanel.setVisible(true);
				}else{
					srcPanel.setVisible(false);
					selectAll(srcPanel, false);
				}
			}
		});	
		resourcesStatics.setVisible(true);
		return resourcesStatics;
	}
	
	private JPanel createSRCBoxPanel(List<String> resourcesMessage) {
		srcPanel = new JPanel();
		int posX = 3;
		int posY = resourcesMessage.size()/posX;
		srcPanel.setLayout(new GridLayout(posY, posX));
		srcPanel.setBackground(Color.WHITE);
		final JCheckBox checkBoxAll = new JCheckBox(ExtensionByPass.getMessageString("message.atack.all"));
		if(resourcesMessage.size() > 1){
			srcPanel.add(checkBoxAll);
		}
		for(String rsc : resourcesMessage) {
			srcPanel.add(new JCheckBox(rsc));
		}
		srcPanel.setVisible(false);
		checkBoxAll.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource()== checkBoxAll && checkBoxAll.isSelected()) {
					selectAll(srcPanel, true);
				}else{
					selectAll(srcPanel, false);
				}
			}
		});	
		return srcPanel;
	}
	
	protected JButton getConfirmButton() {
		acceptButton = new JButton(getConfirmButtonLabel());
		acceptButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				mainInterface.dispose();
				dispose();
				extension.setCookiesSelected(getCookiesSelected());
				extension.setResourcesSelected(getResourcesSelected());
				extension.startScan(null, null, null, null);
			}
		});
		
		return acceptButton;
	}
	
	@Override
	protected String getConfirmButtonLabel() {
		return ExtensionByPass.getMessageString("label.confirm.button.atack");
	}
	
	@Override
	protected JPanel getFieldsPanel() {
		return null;
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
	
	public void selectAll(JPanel panelSelect, boolean select){
		Component[] components = panelSelect.getComponents();
		for(Component component : components) {
			if(component.getClass().isAssignableFrom(JCheckBox.class)) {
				JCheckBox checkBox = (JCheckBox) component;
				checkBox.setSelected(select);
			}
		}
	}
	
	private List<String> getResourcesSelected() {
		List<String> resoSelected = new ArrayList<>();
		Component[] components = srcPanel.getComponents();
		for(Component component : components) {
			if(component.getClass().isAssignableFrom(JCheckBox.class)) {
				JCheckBox checkBox = (JCheckBox) component;
				if(checkBox.isSelected()) {
					if(checkBox.getText() == ExtensionByPass.getMessageString("message.atack.all")){
						selectAll(checkBoxPanel, true);
					}else{
						resoSelected.add(checkBox.getText());
					}
				}else{
					if(checkBox.getText() == ExtensionByPass.getMessageString("message.atack.all")){
						selectAll(checkBoxPanel, false);
					}
				}
			}
		}
		return resoSelected;
	}
}