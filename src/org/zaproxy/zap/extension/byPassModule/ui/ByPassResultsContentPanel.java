package org.zaproxy.zap.extension.byPassModule.ui;

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
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.common.AbstractParam;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.byPassModule.ByPassUtils;
import org.zaproxy.zap.extension.byPassModule.ExtensionByPass;
import org.zaproxy.zap.model.GenericScanner;
import org.zaproxy.zap.view.ScanPanel;

public class ByPassResultsContentPanel extends ScanPanel {
	
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
	
	public ByPassResultsContentPanel(ExtensionByPass extension) {
		super("spider", ByPassUtils.BYPASS_ICON, extension, null);
		this.extension = extension;
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
		resultsTable.getColumnModel().getColumn(0).setMinWidth(80);
		resultsTable.getColumnModel().getColumn(0).setPreferredWidth(90); // processed

		resultsTable.getColumnModel().getColumn(1).setMinWidth(60);
		resultsTable.getColumnModel().getColumn(1).setPreferredWidth(70); // method

		resultsTable.getColumnModel().getColumn(2).setMinWidth(300); // name

		resultsTable.getColumnModel().getColumn(3).setMinWidth(50);
		resultsTable.getColumnModel().getColumn(3).setPreferredWidth(250); // flags
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
			foundCountNameLabel.setText(Constant.messages.getString("spider.toolbar.found.label"));
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
		if (ScanPanel.Location.afterProgressBar == location) {
			toolBar.add(getStartScanButton(), getGBC(gridX++,0));
			toolBar.add(getFoundCountNameLabel(), getGBC(gridX++, 0, 0, new Insets(0, 5, 0, 0)));
			toolBar.add(getFoundCountValueLabel(), getGBC(gridX++, 0));
		}
		return gridX;
	}
	
	protected JButton getStartScanButton() {
		if (scanButton == null) {
			scanButton = new JButton(Constant.messages.getString("spider.toolbar.button.new"));
			scanButton.setIcon(ByPassUtils.BYPASS_ICON);
			scanButton.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent e) {
					extension.showSpiderDialog();
				}
			});
		}
		return scanButton;
	}

	@Override
	protected GenericScanner newScanThread(String arg0, AbstractParam arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void switchView(String arg0) {
		// TODO Auto-generated method stub
		
	}




}
