package org.zaproxy.zap.extension.byPassModule.ui;

import org.zaproxy.zap.view.table.DefaultHistoryReferencesTableModel;

public class ByPassTableModel extends DefaultHistoryReferencesTableModel{
	
	public ByPassTableModel() {
        super(new Column[] {
        		Column.METHOD,
        		Column.URL,
        		Column.STATUS_CODE,
        		Column.STATUS_REASON,
                Column.REQUEST_TIMESTAMP,
                Column.RESPONSE_TIMESTAMP,
                Column.SIZE_RESPONSE_HEADER,
                Column.SIZE_RESPONSE_BODY });
    }

}
