package org.zaproxy.zap.extension.byPass;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.byPass.ui.ByPassTableModel;
import org.zaproxy.zap.extension.byPass.ui.ScanPanelByPass;
import org.zaproxy.zap.extension.byPass.ui.ZapTable;

public class ByPassPanel extends ScanPanelByPass {
	
	private JButton scanButton = null;
	private ZapTable resultsTable;
	private JScrollPane workPane;
	private ExtensionByPass extension = null;
	private static final ByPassTableModel EMPTY_RESULTS_MODEL = new ByPassTableModel();
	
	public ByPassPanel(ExtensionByPass extensionByPass) {
		super("spider", ByPassUtils.BYPASS_ICON, extensionByPass, null);
		this.extension = extensionByPass;
	}
	
	private static final long serialVersionUID = 1L;

	@Override
	protected Component getWorkPanel() {
		if (workPane == null) {
			workPane = new JScrollPane();
			workPane.setName("ByPassResultPanel");
			workPane.setViewportView(getScanResultsTable());
		}
		return workPane;
	}
	
	/**
	 * Sets the spider results table column sizes.
	 */
	private void setScanResultsTableColumnSizes() {
		//Method
		resultsTable.getColumnModel().getColumn(0).setMinWidth(40);
		resultsTable.getColumnModel().getColumn(0).setPreferredWidth(50); 
		//URL
		resultsTable.getColumnModel().getColumn(1).setMinWidth(90);
		resultsTable.getColumnModel().getColumn(1).setPreferredWidth(300);
		//CODE
		resultsTable.getColumnModel().getColumn(2).setMinWidth(40);
		resultsTable.getColumnModel().getColumn(2).setPreferredWidth(50); 
		//RAZON
		resultsTable.getColumnModel().getColumn(3).setMinWidth(50);
		resultsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		//RTT
		resultsTable.getColumnModel().getColumn(4).setMinWidth(50);
		resultsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		
		resultsTable.getColumnModel().getColumn(5).setMinWidth(50);
		resultsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
		
		resultsTable.getColumnModel().getColumn(6).setMinWidth(50);
		resultsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
		
		resultsTable.getColumnModel().getColumn(7).setMinWidth(50);
		resultsTable.getColumnModel().getColumn(7).setPreferredWidth(80);
	}

	private JXTable getScanResultsTable() {
		if (resultsTable == null) {
			resultsTable = new ZapTable(EMPTY_RESULTS_MODEL);
			resultsTable.setColumnSelectionAllowed(false);
			resultsTable.setCellSelectionEnabled(false);
			resultsTable.setRowSelectionAllowed(true);
			resultsTable.setAutoCreateRowSorter(true);
			this.setScanResultsTableColumnSizes();
			resultsTable.setName("ByPasssResult");
			resultsTable.setDoubleBuffered(true);
			resultsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);			
			resultsTable.setComponentPopupMenu(new JPopupMenu() {
				private static final long serialVersionUID = 6608291059686282641L;
				@Override
				public void show(Component invoker, int x, int y) {
					View.getSingleton().getPopupMenu().show(invoker, x, y);
				}
			});
			resultsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(final ListSelectionEvent evt) {
						if (!evt.getValueIsAdjusting()) {
							HttpMessage message = getSelectedMessage();
							if (message != null) {
								displayMessageInHttpPanel(message);
						}
					}
				}
			});
		}
		return resultsTable;
	}

	@Override
	protected int addToolBarElements(JToolBar toolBar, Location location, int gridX) {
		return gridX;
	}
	
	@Override
	protected void switchView(ByPassTableModel model) {
		if (model != null) {
			this.getScanResultsTable().setModel(model);
			this.setScanResultsTableColumnSizes();
		} else {
			this.getScanResultsTable().setModel(EMPTY_RESULTS_MODEL);
		}
		
	}
	
	@Override
	protected JButton getNewScanButton() {
		if (scanButton == null) {
			scanButton = new JButton(ExtensionByPass.getMessageString("title.windows.ByPass"));
			scanButton.setIcon(ByPassUtils.BYPASS_ICON);
			scanButton.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					extension.showSpiderDialog();
				}
			});
		}
		return scanButton;
	}

	@Override
	protected void switchView(String site) {
		
	}
	
	protected void displayMessageInHttpPanel(final HttpMessage msg) {
		if (msg == null) {
			return;
		}
		if (msg.getRequestHeader().isEmpty()) {
			View.getSingleton().getRequestPanel().clearView(true);
		} else {
			View.getSingleton().getRequestPanel().setMessage(msg);
		}

		if (msg.getResponseHeader().isEmpty()) {
			View.getSingleton().getResponsePanel().clearView(false);
		} else {
			View.getSingleton().getResponsePanel().setMessage(msg, true);
		}
	}
	
	public HttpMessage getSelectedMessage() {
		final int selectedRow = resultsTable.getSelectedRow();
		ByPassTableModel model = (ByPassTableModel)resultsTable.getModel();
		if (selectedRow != -1 && model != null) {
			return model.getMessageAtIndex(selectedRow);
		}
		return null;
	}

/*	@Override
	protected JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar(0, 100);
			progressBar.setValue(0);
			progressBar.setSize(new Dimension(80,20));
			progressBar.setStringPainted(true);
			progressBar.setEnabled(false);
		}
		return progressBar;
	}	*/
}
