/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
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
package org.zaproxy.zap.extension.byPass;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ViewDelegate;
import org.zaproxy.zap.extension.byPass.ui.ByPassTableModel;
import org.zaproxy.zap.extension.byPass.ui.MainInterfaceByPass;
import org.zaproxy.zap.extension.httppanel.HttpPanelRequest;
import org.zaproxy.zap.extension.httppanel.HttpPanelResponse;

public class ExtensionByPass extends ExtensionAdaptor{

    private static ResourceBundle messages;
    private ByPassPanel byPassPanel = null;

    public ExtensionByPass() {
        super();
 		initialize();
    }

    public ExtensionByPass(String name) {
        super(name);
    }

	private void initialize() {
        this.setName("ExtensionByPass");
        messages = ResourceBundle.getBundle(this.getClass().getPackage().getName() + ".resources.Messages", Constant.getLocale());
	}
	
	@Override
	public void hook(ExtensionHook extensionHook) {
	    super.hook(extensionHook);
	    if (getView() != null) {
	        extensionHook.getHookView().addStatusPanel(getByPassPanel());
	    }
	}
	
	public ByPassPanel getByPassPanel(){
		if(byPassPanel == null){
			byPassPanel = new ByPassPanel(this);
		}			
		return byPassPanel;
	}
	
	public static String getMessageString (String key) {
		return messages.getString(key);
	}
	
	 @Override
	 public void initView(ViewDelegate view) {
		 super.initView(view);
		 
	 }

	@Override
	public String getAuthor() {
		return Constant.ZAP_TEAM;
	}

	@Override
	public String getDescription() {
		return messages.getString("ext.topmenu.desc");
	}

	@Override
	public URL getURL() {
		try {
			return new URL(Constant.ZAP_EXTENSIONS_PAGE);
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	public HttpPanelRequest getPanelRequest(){
		return getView().getRequestPanel();
	}
	
	public HttpPanelResponse getPanelResponse(){
		return getView().getResponsePanel();
	}

	public void showSpiderDialog() {
		MainInterfaceByPass mainInterface = new MainInterfaceByPass(this, getView().getMainFrame());
		mainInterface.pack();
		mainInterface.setVisible(true);
	}
	
	public void showResults(ByPassTableModel model){
		this.getByPassPanel().switchView(model);
	}
}