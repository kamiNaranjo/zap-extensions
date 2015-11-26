package org.zaproxy.zap.extension.byPass;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXTable;
import org.parosproxy.paros.common.AbstractParam;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.byPass.ui.ByPassTableModel;
import org.zaproxy.zap.extension.byPass.ui.ScanPanelByPass;
import org.zaproxy.zap.extension.byPass.ui.ZapTable;
import org.zaproxy.zap.model.GenericScanner;

public class ByPassPanel extends ScanPanelByPass {
	
	private JButton scanButton = null;
	/** The results table. */
	private ZapTable resultsTable;
	/** The results pane. */
	private JScrollPane workPane;
	/** The found count name label. */
	private JLabel foundCountNameLabel;

	/** The found count value label. */
	private JLabel foundCountValueLabel;
	
	private ExtensionByPass extension = null;
	
	private static final ByPassTableModel EMPTY_RESULTS_MODEL = new ByPassTableModel();
	
	public ByPassPanel(ExtensionByPass extensionByPass) {
		super("spider", ByPassUtils.BYPASS_ICON, extensionByPass, null);
		this.extension = extensionByPass;
	}
	
	/**
	 * 
	 */
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
		resultsTable.getColumnModel().getColumn(3).setMinWidth(50);
		resultsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		
		resultsTable.getColumnModel().getColumn(4).setMinWidth(50);
		resultsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		
		resultsTable.getColumnModel().getColumn(5).setMinWidth(50);
		resultsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
	}

	private JXTable getScanResultsTable() {
		if (resultsTable == null) {
			// Create the table with a default, empty TableModel and the proper settings
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
		}
		return resultsTable;
	}

	/**
	 * Gets the label storing the name of the count of found URIs.
	 * 
	 * @return the found count name label
	 */
	private JLabel getFoundCountNameLabel() {
		if (foundCountNameLabel == null) {
			foundCountNameLabel = new javax.swing.JLabel();
			foundCountNameLabel.setText(ExtensionByPass.getMessageString("title.windows.ByPass"));
		}
		return foundCountNameLabel;
	}

	/**
	 * Gets the label storing the value for count of found URIs.
	 * 
	 * @return the found count value label
	 */
	private JLabel getFoundCountValueLabel() {
		if (foundCountValueLabel == null) {
			foundCountValueLabel = new javax.swing.JLabel();
			foundCountValueLabel.setText("0");
		}
		return foundCountValueLabel;
	}

	@Override
	protected int addToolBarElements(JToolBar toolBar, Location location, int gridX) {
		if (ScanPanelByPass.Location.afterProgressBar == location) {
			toolBar.add(getFoundCountNameLabel(), getGBC(gridX++, 0, 0, new Insets(0, 5, 0, 0)));
			toolBar.add(getFoundCountValueLabel(), getGBC(gridX++, 0));
		}
		return gridX;
	}
	
	@Override
	protected GenericScanner newScanThread(String arg0, AbstractParam arg1) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		
	}

	
}
