/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2010 psiinon@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.zaproxy.zap.extension.byPass.popup;

import java.awt.Component;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;
import org.parosproxy.paros.extension.manualrequest.ManualRequestEditorDialog;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.byPass.ExtensionByPass;


public class PopupMenuResend extends ExtensionPopupMenuItem {

private static final long serialVersionUID = 2598282233227430069L;
		
	private ExtensionByPass extension = null;
    
    public PopupMenuResend() {
        super();
 		initialize();
    }

    /**
     * @param label
     */
    public PopupMenuResend(String label) {
        super(label);
    }
    
	private void initialize() {
        this.setText(Constant.messages.getString("history.resend.popup"));	// ZAP: i18n

        this.addActionListener(new java.awt.event.ActionListener() { 

        	@Override
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        	    ManualRequestEditorDialog dialog = extension.getResendDialog();
        	    HttpMessage msg = extension.messageSelected();       	    
                dialog.setMessage(msg);
                dialog.setVisible(true);
               
        	}
        });
	}
	
    @Override
    public boolean isEnableForComponent(Component invoker) {
    	if (invoker.getName() != null && invoker.getName().equals("ByPasssResult")) {
    		HttpMessage msg = extension.messageSelected();
        	if (msg != null) {
        		if(extension.getByPassPanel().isMultipleSelected())
        			this.setEnabled(false);
        		else
        			this.setEnabled(true);
        		return true;
        	}
        }
        return false;
    }
    
    public void setExtension(ExtensionByPass extension) {
        this.extension = extension;
    }
}