package org.zaproxy.zap.extension.byPass.popup;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.byPass.ExtensionByPass;

public class PopupMenuCopyUrls extends ExtensionPopupMenuItem implements ClipboardOwner {

	private static final long serialVersionUID = 1L;
	private ExtensionByPass extension = null;

    /**
     * @param label
     */
    public PopupMenuCopyUrls(String label) {
        super(label);
        initialize();
    }
    
    public PopupMenuCopyUrls() {
        super();
        initialize();
    }
    
    private void initialize() {
        this.setText(Constant.messages.getString("stdexts.copyurls.popup"));
        this.setActionCommand("");
        this.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
		    	for (HttpMessage msg : extension.messagesSelected()) {
		    	    sb.append(msg.getRequestHeader().getURI().toString());
		    	    sb.append("\n");
		    	}
		        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		        clipboard.setContents( new StringSelection(sb.toString()), PopupMenuCopyUrls.this);
			} 
        });      
    }

	@Override
	public boolean isEnableForComponent(Component invoker) {	
		if (invoker.getName().equalsIgnoreCase("ByPasssResult")) {
			return true;
		} 
		return false;
	}
	
	public void setExtension(ExtensionByPass extension) {
		this.extension = extension;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub
		
	}
		
}
