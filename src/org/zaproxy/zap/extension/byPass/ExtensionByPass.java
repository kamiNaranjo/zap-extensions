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
import java.util.ArrayList;
import java.util.List;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ViewDelegate;
import org.parosproxy.paros.extension.manualrequest.ManualRequestEditorDialog;
import org.parosproxy.paros.extension.manualrequest.http.impl.ManualHttpRequestEditorDialog;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.byPass.popup.PopupMenuCopyUrls;
import org.zaproxy.zap.extension.byPass.popup.PopupMenuEmbeddedBrowser;
import org.zaproxy.zap.extension.byPass.popup.PopupMenuGenerateForm;
import org.zaproxy.zap.extension.byPass.popup.PopupMenuResend;
import org.zaproxy.zap.extension.byPass.ui.ByPassTableModel;
import org.zaproxy.zap.extension.byPass.ui.MainInterfaceByPass;
import org.zaproxy.zap.model.ScanController;
import org.zaproxy.zap.model.Target;
import org.zaproxy.zap.users.User;

public class ExtensionByPass extends ExtensionAdaptor implements ScanController<ByPassModule>{

	protected static final String PREFIX = "byPass";
	public static final String NAME = "ExtensionByPass";
    private ByPassPanel byPassPanel = null;
    private ByPassScanController scanController = null;
    private List<String> cookiesSelected;
    private List<String> resourcesSelected;
    private TargetByPass targetByPass = null;
	private PopupMenuResend popupMenuResend = null;
	private PopupMenuEmbeddedBrowser popupMenuBrowser = null;
	private PopupMenuCopyUrls popupMenuCopy = null;
	private ManualRequestEditorDialog resendDialog = null;
	private PopupMenuGenerateForm popupMenuGenerateForm = null;

    public ExtensionByPass() {
        super();
 		initialize();
    }

    public ExtensionByPass(String name) {
        super(name);
    }

	private void initialize() {
		cookiesSelected = new ArrayList<>();
		resourcesSelected = new ArrayList<>();
        this.setName(NAME);
        this.scanController = new ByPassScanController(this);
	}
	
	@Override
	public void hook(ExtensionHook extensionHook) {
	    super.hook(extensionHook);
	    if (getView() != null) {
	    	extensionHook.getHookMenu().addPopupMenuItem(getPopupMenuResend());
	    	extensionHook.getHookMenu().addPopupMenuItem(getPopupMenuEmbeddedBrowser());
	    	extensionHook.getHookMenu().addPopupMenuItem(getPopupMenuCopyUrls());
	    	extensionHook.getHookMenu().addPopupMenuItem(getPopupMenuGenerateForm());
	        extensionHook.getHookView().addStatusPanel(getByPassPanel());
	    }
	}
	
	private PopupMenuResend getPopupMenuResend() {
		if(popupMenuResend == null){
			popupMenuResend = new PopupMenuResend();
			popupMenuResend.setExtension(this);
		}
		return popupMenuResend;
	}
	
	private PopupMenuEmbeddedBrowser getPopupMenuEmbeddedBrowser() {
		if(popupMenuBrowser == null){
			popupMenuBrowser = new PopupMenuEmbeddedBrowser();
			popupMenuBrowser.setExtension(this);
		}
		return popupMenuBrowser;
	}
	
	private PopupMenuCopyUrls getPopupMenuCopyUrls() {
		if (popupMenuCopy == null) {
			popupMenuCopy = new PopupMenuCopyUrls();
			popupMenuCopy.setExtension(this);
		}
		return popupMenuCopy;
	}

	public ManualRequestEditorDialog getResendDialog() {
		if (resendDialog == null) {
			resendDialog = new ManualHttpRequestEditorDialog(true, "resend", "ui.dialogs.resend");
			resendDialog.setTitle(Constant.messages.getString("manReq.resend.popup"));	// ZAP: i18n
		}
		return resendDialog;
	}
	
	private PopupMenuGenerateForm getPopupMenuGenerateForm() {
		if (popupMenuGenerateForm == null) {
			popupMenuGenerateForm = new PopupMenuGenerateForm();
			popupMenuGenerateForm.setExtension(this);
		}
		return popupMenuGenerateForm;
	}
	
	
	public HttpMessage messageSelected(){
		return byPassPanel.getSelectedMessage();
	}
	
	public List<HttpMessage> messagesSelected(){
		return byPassPanel.getSelectedMessages();
	}
	
	
	public ByPassPanel getByPassPanel(){
		if(byPassPanel == null){
			byPassPanel = new ByPassPanel(this);
		}			
		return byPassPanel;
	}
	
	public void finishScanPanel(){
		byPassPanel.finishScan();
	}
	
	public static String getMessageString (String key) {
		return Constant.messages.getString(PREFIX + "." + key);
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
		return Constant.messages.getString(PREFIX + ".ext.topmenu.desc");
	}

	@Override
	public URL getURL() {
		try {
			return new URL(Constant.ZAP_EXTENSIONS_PAGE);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public void showSpiderDialog() {
		MainInterfaceByPass mainInterface = new MainInterfaceByPass(this, getView().getMainFrame());
		mainInterface.pack();
		mainInterface.setVisible(true);
	}
	
	public void showResults(ByPassTableModel model, int progress){
		this.getByPassPanel().switchView(model, progress);
	}

	public void showResults(ByPassModule model){
		this.getByPassPanel().switchView(model);
	}

	@Override
	public void pauseAllScans() {
		scanController.pauseAllScans();
	}

	@Override
	public void pauseScan(int id) {
		this.scanController.pauseScan(id);
	}

	@Override
	public int removeAllScans() {
		return scanController.removeAllScans();
	}

	@Override
	public int removeFinishedScans() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public void resumeAllScans() {
		scanController.resumeAllScans();
	}

	@Override
	public void resumeScan(int arg0) {
		scanController.resumeScan(arg0);
	}
	
	@Override
	public int startScan(String name, Target target, User user, Object[] contextSpecificObjects) {
		int id = this.scanController.startScan(name, target, user, contextSpecificObjects);
    	if (View.isInitialised()) {
    		ByPassModule scanner = this.scanController.getScan(id);
			this.getByPassPanel().scannerStarted(scanner);
    	}
    	return id;
	}
	
	public void setResourcesToSkiped(List<String> resourcesSelected){
		this.setResourcesSelected(resourcesSelected);
	}

	@Override
	public void stopAllScans() {
		scanController.stopAllScans();		
	}

	@Override
	public void stopScan(int id) {
		this.scanController.stopScan(id);
	}

	@Override
	public List<ByPassModule> getActiveScans() {
		return this.scanController.getActiveScans();
	}

	@Override
	public List<ByPassModule> getAllScans() {
		return this.scanController.getAllScans();
	}

	@Override
	public ByPassModule getLastScan() {
		return this.scanController.getLastScan();
	}

	@Override
	public ByPassModule getScan(int id) {
		return this.scanController.getScan(id);
	}

	@Override
	public ByPassModule removeScan(int id) {
		return this.scanController.removeScan(id);
	}

	public List<String> getCookiesSelected() {
		return cookiesSelected;
	}

	public void setCookiesSelected(List<String> cookiesSelected) {
		this.cookiesSelected = cookiesSelected;
	}

	public TargetByPass getTargetByPass() {
		return targetByPass;
	}

	public void setTargetByPass(TargetByPass targetByPass2) {
		this.targetByPass = targetByPass2;
	}

	public List<String> getResourcesSelected() {
		return resourcesSelected;
	}

	public void setResourcesSelected(List<String> resourcesSelected) {
		this.resourcesSelected = resourcesSelected;
		targetByPass.deleteResource(resourcesSelected);
	}
	  
}