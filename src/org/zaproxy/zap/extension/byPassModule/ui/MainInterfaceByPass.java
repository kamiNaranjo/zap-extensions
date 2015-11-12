package org.zaproxy.zap.extension.byPassModule.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.zaproxy.zap.extension.byPassModule.ByPassModule;
import org.zaproxy.zap.view.AbstractFormDialog;

public class MainInterfaceByPass extends AbstractFormDialog{

	private static final long serialVersionUID = 1L;
	//TODO: CAMBIAR A ARCHIVO DE INTERNALIZACION
	private static final String  TITLE = "BYPASS";
	private JPanel mainPanel;
	private JButton acceptButton;
	private HttpMessageSelectorPanel treeSities;
	
	public MainInterfaceByPass(JFrame owner){
		super(owner, TITLE, false);
		owner.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		super.add(getMainPanel());
		acceptButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if(treeSities.validate()){
					new ByPassModule(treeSities.getSelectedMessage());
				}else{
					System.out.println("._.");
				}
				
			}
		});
	}
	
	public JPanel getMainPanel(){
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		treeSities = new HttpMessageSelectorPanel();
		mainPanel.add(new JLabel("Seleccione url a atacar"));
		mainPanel.add(treeSities.getPanel());
		mainPanel.add(acceptButton = new JButton("Atacar"));
		mainPanel.setVisible(true);
		return mainPanel;
	}	

	@Override
	protected String getConfirmButtonLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JPanel getFieldsPanel() {
		// TODO Auto-generated method stub
		return null;
	}

}
