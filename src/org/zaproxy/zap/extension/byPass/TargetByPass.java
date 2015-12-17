package org.zaproxy.zap.extension.byPass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.network.HtmlParameter;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.byPass.ui.CookiesSelectInterface;
import org.zaproxy.zap.extension.byPass.ui.HttpMessageSelectorPanel;
import org.zaproxy.zap.extension.byPass.ui.MainInterfaceByPass;

public class TargetByPass {
	
	private static List<HtmlParameter> cookieArray;
	private static List<String> cookieName;
    private static final Logger LOGGER = Logger.getLogger(ByPassModule.class);
	private static List<HttpMessage> arrayMessages;
	private ExtensionByPass extension;
	private MainInterfaceByPass mainInterface;

	public TargetByPass(HttpMessageSelectorPanel target, ExtensionByPass extension, MainInterfaceByPass mainInterface){
		cookieArray = new ArrayList<>();
		cookieName = new ArrayList<>();
		arrayMessages = new ArrayList<>();
		this.mainInterface = mainInterface;
		this.extension = extension;
		
		if(target.isHaveChild())
			getUrlChildren(target.getSitieSelect());
		else{
			arrayMessages.add(target.getSelectedMessage());
			getCookiesByHttpMessage(target.getSelectedMessage());	
		}
		showCookiesToDelete();		
	}
	
	public void getCookiesByHttpMessage(HttpMessage urlSelected){
		Iterator<HtmlParameter> cookiesParam = urlSelected.getCookieParams().iterator();
		while (cookiesParam.hasNext()) {
			HtmlParameter cookie = (HtmlParameter) cookiesParam.next();
			if(!cookieName.contains(cookie.getName())){
				cookieArray.add(cookie);
				cookieName.add(cookie.getName());
			}
		}
	}
	
	private void getUrlChildren(SiteNode siteSelected){
		try {
			for(int i = 0; i < siteSelected.getChildCount(); i++){
				if(siteSelected.getChildAt(i).isLeaf()){
					SiteNode node = (SiteNode) siteSelected.getChildAt(i);
					HttpMessage selectedHttpMessage = ((SiteNode) node.getUserObject()).getHistoryReference().getHttpMessage();
					getCookiesByHttpMessage(selectedHttpMessage);
					arrayMessages.add(selectedHttpMessage);
				}else{
					getUrlChildren((SiteNode) siteSelected.getChildAt(i));
				}
			}	
		} catch (HttpMalformedHeaderException | DatabaseException e) {
			 LOGGER.error("Failed to read the message: ", e);
			 JOptionPane.showMessageDialog( null, ExtensionByPass.getMessageString("message.errorReadMessage"),
					 ExtensionByPass.getMessageString("title.windows.ByPassError"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void showCookiesToDelete(){
		if(cookieArray!= null && !cookieArray.isEmpty()){
			extension.setTargetByPass(TargetByPass.this);
			CookiesSelectInterface cookieInterface = new CookiesSelectInterface(extension,
					MainInterfaceByPass.getOwnerFrame(), cookieArray, mainInterface);
			cookieInterface.pack();
			cookieInterface.setVisible(true);
		}else{
			View.getSingleton().showMessageDialog(ExtensionByPass.getMessageString("message.dontContainCookie"));
		}
	}

	public  List<HtmlParameter> getCookieArray() {
		return cookieArray;
	}

	public  List<HttpMessage> getArrayMessages() {
		return arrayMessages;
	}

	public  void setCookieArray(List<HtmlParameter> cookieArray) {
		TargetByPass.cookieArray = cookieArray;
	}

	public  void setArrayMessages(List<HttpMessage> arrayMessages) {
		TargetByPass.arrayMessages = arrayMessages;
	}
}
