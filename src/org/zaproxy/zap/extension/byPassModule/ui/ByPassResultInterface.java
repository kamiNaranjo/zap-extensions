package org.zaproxy.zap.extension.byPassModule.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.byPassModule.ExtensionByPass;
import org.zaproxy.zap.view.AbstractFormDialog;

public class ByPassResultInterface extends AbstractFormDialog{
	
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private HttpMessage results;
	
	public ByPassResultInterface(JFrame owner, HttpMessage resultsSend) {
		super(owner, ExtensionByPass.getMessageString("title.windows.results"), false);
		results = resultsSend;
		setResizable(false);
		super.add(getMainPanel());
		owner.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public JTable getTableResultPanel(){
		String columnNames[] = {
			ExtensionByPass.getMessageString("label.column.ulr"),
			ExtensionByPass.getMessageString("label.column.method"),
			ExtensionByPass.getMessageString("label.column.code"),
			ExtensionByPass.getMessageString("label.column.reason"),
			ExtensionByPass.getMessageString("label.column.rtt"),
			ExtensionByPass.getMessageString("label.column.size.header"),
			ExtensionByPass.getMessageString("label.column.size.body")
		};
		
		String rowsContent[][] = {{
			results.getRequestHeader().getURI().toString(),
			results.getRequestHeader().getMethod(),
			Integer.toString(results.getResponseHeader().getStatusCode()),
			results.getResponseHeader().getReasonPhrase(),
			Integer.toString(results.getTimeElapsedMillis()),
			Integer.toString(results.getResponseHeader().getContentLength()),
			Integer.toString(results.getResponseBody().length())
		}};
		
		JTable tableResults = new JTable(rowsContent, columnNames);
		tableResults.setEnabled(false);
		tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		resizeColumnWidth(tableResults);
		tableResults.setPreferredScrollableViewportSize(tableResults.getPreferredSize());
		tableResults.setFillsViewportHeight(true);
		return tableResults;
	}
	
	public void resizeColumnWidth(JTable table) {
	    final TableColumnModel columnModel = table.getColumnModel();
	    for (int column = 0; column < table.getColumnCount(); column++) {
	        int width = 50; // Min width
	        for (int row = 0; row < table.getRowCount(); row++) {
	            TableCellRenderer renderer = table.getCellRenderer(row, column);
	            Component comp = table.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}
	
	protected JPanel getMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(new JScrollPane(getTableResultPanel()), BorderLayout.CENTER);
		mainPanel.setVisible(true);
		return mainPanel;
	}
	
	@Override
	protected JPanel getFieldsPanel() {
		return null;
	}

	@Override
	protected String getConfirmButtonLabel() {
		return null;
	}
}
