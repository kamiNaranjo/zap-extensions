package org.zaproxy.zap.extension.byPass.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.byPass.ExtensionByPass;

public class ByPassTableModel extends AbstractTableModel{
	
	/** The column names. */
	private static final String[] COLUMN_NAMES = { 
			ExtensionByPass.getMessageString("label.column.method"),
			ExtensionByPass.getMessageString("label.column.ulr"),
			ExtensionByPass.getMessageString("label.column.code"),
			ExtensionByPass.getMessageString("label.column.reason"),
			ExtensionByPass.getMessageString("label.column.rtt"),
			ExtensionByPass.getMessageString("label.column.size.header"),
			ExtensionByPass.getMessageString("label.column.size.body"), 
			ExtensionByPass.getMessageString("label.column.equals")};

	private static final int COLUMN_COUNT = COLUMN_NAMES.length;

	private static final long serialVersionUID = 1L;
	private List<HttpMessage> resultsArray;
	private List<Boolean> equalsArray;
	
	public ByPassTableModel() {
        super();
        resultsArray = new ArrayList<>();
        equalsArray = new ArrayList<>();
    }

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	public void removeAllElements() {
		synchronized (resultsArray) {
			resultsArray.clear();
			fireTableDataChanged();
		}
	}
	@Override
	public int getRowCount() {
		return resultsArray.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		HttpMessage result = resultsArray.get(rowIndex);
		boolean equal = equalsArray.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return result.getRequestHeader().getMethod();
		case 1:
			return result.getRequestHeader().getURI();
		case 2:
			return result.getResponseHeader().getStatusCode();
		case 3:
			return result.getResponseHeader().getReasonPhrase();
		case 4:
			return result.getTimeElapsedMillis();
		case 5: 
			return result.getResponseHeader().getContentLength();
		case 6:
			return result.getResponseBody().length();	
		case 7:
			return equal;
		default:
			return null;
		}
	}
	
	public HttpMessage getMessageAtIndex(int index){
		if(resultsArray != null && !resultsArray.isEmpty()){
			return resultsArray.get(index);
		}
		return null;
	}
	
	public void addSResul(HttpMessage result, boolean equals) {
		synchronized (resultsArray) {
			resultsArray.add(result);
			equalsArray.add(equals);
			try {
				fireTableRowsInserted(resultsArray.size() - 1, resultsArray.size() - 1);
			} catch (IndexOutOfBoundsException e) {
				// Happens occasionally but seems benign
			}
		}
	}


}
