package org.zaproxy.zap.extension.byPass;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.byPass.ui.ByPassTableModel;
import org.zaproxy.zap.extension.byPass.ui.ZapTable;
import org.zaproxy.zap.model.ScanController;
import org.zaproxy.zap.view.ScanPanel2;

public class ByPassPanel extends ScanPanel2<ByPassModule, ScanController<ByPassModule>> {
	
	private JButton scanButton = null;
	private ZapTable resultsTable;
	private JScrollPane workPane;
	private ExtensionByPass extension = null;
	private static final ByPassTableModel EMPTY_RESULTS_MODEL = new ByPassTableModel();

	public ByPassPanel(ExtensionByPass extensionByPass) {
		super("spider", ByPassUtils.BYPASS_ICON, extensionByPass, null);
		super.setName(ExtensionByPass.getMessageString("title.windows.ByPass"));
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
			resultsTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			resultsTable.setSortOrderCycle(SortOrder.ASCENDING, SortOrder.DESCENDING, SortOrder.UNSORTED);
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
			
			/*resultsTable.addMouseListener(new java.awt.event.MouseAdapter() { 
			    @Override
			    public void mousePressed(java.awt.event.MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						// Select table item
					    int row = resultsTable.rowAtPoint( e.getPoint() );
					    if ( row < 0 || !resultsTable.getSelectionModel().isSelectedIndex( row ) ) {
					    	resultsTable.getSelectionModel().clearSelection();
					    	/*if ( row >= 0 ) {
					    		resultsTable.getSelectionModel().setSelectionInterval( row, row );
					    	}
					    }else {
				    		resultsTable.getSelectionModel().setSelectionInterval( row, row );
					    	View.getSingleton().getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
					    }
			        }
			    }
			});*/
		}
		return resultsTable;
	}
	
	@Override
	protected int addToolBarElements(JToolBar toolBar, Location location, int gridX) {
		getStopScanButton().setToolTipText(ExtensionByPass.getMessageString("tool.tip.stop.button"));
		getPauseScanButton().setToolTipText(ExtensionByPass.getMessageString("tool.tip.pause.button"));
		return gridX;
	}
	
	protected void switchView(ByPassTableModel model, int progress) {
		if (model != null) {
			this.getScanResultsTable().setModel(model);
			this.setScanResultsTableColumnSizes();
			this.setTabFocus();
			this.getProgressBar().setValue(progress);
		} else {
			this.getScanResultsTable().setModel(EMPTY_RESULTS_MODEL);
		}
	}
	
	public boolean isMultipleSelected(){
		final int[] selectedRow = this.resultsTable.getSelectedRows();
		if(selectedRow.length > 1){
			return true;
		}
		return false;
	}
	
	@Override
	protected JButton getNewScanButton() {
		if (scanButton == null) {
			scanButton = new JButton(ExtensionByPass.getMessageString("title.windows.ByPass"));
			scanButton.setIcon(ByPassUtils.BYPASS_ICON);
			scanButton.setToolTipText(ExtensionByPass.getMessageString("tool.tip.start.button"));
			scanButton.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					extension.showSpiderDialog();
				}
			});
		}
		return scanButton;
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
		final int selectedRow = this.resultsTable.getSelectedRow();
		ByPassTableModel model = (ByPassTableModel)this.resultsTable.getModel();
		if (selectedRow != -1 && model != null) {
			return model.getMessageAtIndex(selectedRow);
		}
		return null;
	}
	
	public List<HttpMessage> getSelectedMessages() {
		List<HttpMessage> message = new ArrayList<>();
		final int[] selectedRow = this.resultsTable.getSelectedRows();
		ByPassTableModel model = (ByPassTableModel)resultsTable.getModel();
		if(selectedRow.length > 0){
			for(int i: selectedRow){
				message.add(model.getMessageAtIndex(i));
			}
			return message;
		}
		return null;
	}

	@Override
	protected int getNumberOfScansToShow() {
		return 0;
	}

	@Override
	protected void switchView(final ByPassModule scanner) {
		if (View.isInitialised() && !EventQueue.isDispatchThread()) {
			try {
				EventQueue.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						switchView(scanner);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
			}
			return;
		}
		if (scanner != null) {
			this.getScanResultsTable().setModel(scanner.getResultsTableModel());
			this.setScanResultsTableColumnSizes();
			this.getProgressBar().setValue(scanner.getProgress());
			if(scanner.getState().name().equals("FINISHED")){
				this.getStopScanButton().setEnabled(false);
				this.getPauseScanButton().setEnabled(false);
				this.getProgressBar().setEnabled(false);
			}
		} else {
			this.getScanResultsTable().setModel(EMPTY_RESULTS_MODEL);
		}
	}

	public void finishScan(){
		this.getStopScanButton().setEnabled(false);
		this.getPauseScanButton().setEnabled(false);
		this.getProgressBar().setEnabled(false);
	}

}