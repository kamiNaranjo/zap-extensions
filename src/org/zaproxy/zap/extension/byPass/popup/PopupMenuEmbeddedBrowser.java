package org.zaproxy.zap.extension.byPass.popup;

import java.awt.Component;

import javax.swing.JTree;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.byPass.ExtensionByPass;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class PopupMenuEmbeddedBrowser extends ExtensionPopupMenuItem {
	
	private static final long serialVersionUID = 1L;
	private ExtensionByPass extension = null;
    private Component lastInvoker = null;
    // ZAP: Changed to support BrowserLauncher
    private BrowserLauncher launcher = null;
    private boolean supported = true;
    
    public PopupMenuEmbeddedBrowser() {
        super();
 		initialize();
    }

    public PopupMenuEmbeddedBrowser(String label) {
        super(label);
        initialize();
    }

	private void initialize() {
        this.setText(Constant.messages.getString("history.browser.popup"));

        this.setActionCommand("");
        
        this.addActionListener(new java.awt.event.ActionListener() { 

        	@Override
        	public void actionPerformed(java.awt.event.ActionEvent e) {
                HistoryReference ref = null;
                HttpMessage msg = null;
                if (lastInvoker == null) {
                    return;
                }
                if (lastInvoker.getName().equalsIgnoreCase("ByPasssResult")) {
                	msg = extension.messageSelected();
                    showBrowser(msg);                                   

                } else if (lastInvoker.getName().equals("treeSite")) {
                    JTree tree = (JTree) lastInvoker;
                    SiteNode node = (SiteNode) tree.getLastSelectedPathComponent();
                    ref = node.getHistoryReference();
                    showBrowser(ref);
                }
        	}
        });		
	}
	
	private BrowserLauncher getBrowserLauncher() {
		if (! supported) {
			return null;
		}
		if (launcher == null) {
			try {
				launcher = new BrowserLauncher();
			} catch (BrowserLaunchingInitializingException e) {
				supported = false;
			} catch (UnsupportedOperatingSystemException e) {
				supported = false;
			}
		}
		return launcher;
	}
	
	  private void showBrowser(HttpMessage ref) {
	    	if (! supported) {
	    		return;
	    	}
	        try {
	            this.getBrowserLauncher().openURLinBrowser(ref.getRequestHeader().getURI().toString());

	        } catch (Exception e) {
	            extension.getView().showWarningDialog(Constant.messages.getString("history.browser.warning"));
	        }
	  }
	    
	        
    private void showBrowser(HistoryReference ref) {
    	if (! supported) {
    		return;
    	}
        try {
            this.getBrowserLauncher().openURLinBrowser(ref.getURI().toString());

        } catch (Exception e) {
            extension.getView().showWarningDialog(Constant.messages.getString("history.browser.warning"));
        }
        
    }

    
    @Override
    public boolean isEnableForComponent(Component invoker) {
        lastInvoker = null;
        if ( !supported) {
        	return false;
        }
        if (invoker.getName() == null) {
            return false;
        }
        
        if (invoker.getName().equalsIgnoreCase("ByPasssResult")) {
        	if(extension.getByPassPanel().isMultipleSelected())
    			this.setEnabled(false);
        	else
    			this.setEnabled(true);
            lastInvoker = invoker;
            return true;
        } else if (invoker.getName().equals("treeSite")) {
        	JTree tree = (JTree) invoker;
        	lastInvoker = tree;
            SiteNode node = (SiteNode) tree.getLastSelectedPathComponent();
            this.setEnabled(node != null && node.getHistoryReference() != null);
            return true;
        }
        return false;
    }
    
   public void setExtension(ExtensionByPass extension) {
        this.extension = extension;
    }
	

}
