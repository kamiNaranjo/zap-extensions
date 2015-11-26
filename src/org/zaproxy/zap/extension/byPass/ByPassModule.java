package org.zaproxy.zap.extension.byPass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.network.HtmlParameter;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpSender;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.byPass.ui.ByPassTableModel;
import org.zaproxy.zap.extension.byPass.ui.CookiesSelectInterface;
import org.zaproxy.zap.extension.byPass.ui.MainInterfaceByPass;

public class ByPassModule {

	private static List<HtmlParameter> cookieArray;
	private static List<String> cookieName;
	private static List<HttpMessage> arrayMessages;
	private static ByPassTableModel resultsModel;
    private static final Logger LOGGER = Logger.getLogger(ByPassModule.class);
    private ExtensionByPass extension;
	
	public ByPassModule(ExtensionByPass extension, HttpMessage urlSelected){
		this.extension = extension;
		cookieArray = new ArrayList<>();
		cookieName = new ArrayList<>();
		arrayMessages = new ArrayList<>();
		arrayMessages.add(urlSelected);
		getCookiesByHttpMessage(urlSelected);
	}
	
	public ByPassModule(ExtensionByPass extension, SiteNode sitieSelected){
		this.extension = extension;
		cookieArray = new ArrayList<>();
		cookieName = new ArrayList<>();
		arrayMessages = new ArrayList<>();
		getUrlChildren(sitieSelected);
		showCookiesToDelete();		
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

	public ByPassTableModel getMessageWithOutCookies(List<String> cookies){
		resultsModel = new ByPassTableModel();
		for(HttpMessage message:arrayMessages) {
			HttpMessage urlWithOutCookie = message.cloneAll();
			for(String cookieToDelete:cookies){
				for (HtmlParameter cookie:cookieArray) {
					if(cookie.getName().equals(cookieToDelete)){
						urlWithOutCookie.getCookieParams().remove(cookie);
						cookieArray.remove(cookie);
						break;
					}
				}
			}
			resultsModel.addSResul(sendMessageWithOutCookies(urlWithOutCookie));
		}
		return resultsModel;
	}
	
	public void showCookiesToDelete(){
		if(cookieArray!= null && !cookieArray.isEmpty()){
			CookiesSelectInterface cookieInterface = new CookiesSelectInterface(this, extension,
					MainInterfaceByPass.getOwnerFrame(), cookieArray);
			cookieInterface.pack();
			cookieInterface.setVisible(true);
		}else{
			View.getSingleton().showMessageDialog(ExtensionByPass.getMessageString("message.dontContainCookie"));
		}
	}
	
	public static HttpMessage sendMessageWithOutCookies(HttpMessage messageToSend){
		HttpSender sender = new HttpSender(Model.getSingleton().getOptionsParam().getConnectionParam(),
                true, HttpSender.FUZZER_INITIATOR);
		try {
			sender.sendAndReceive(messageToSend);
		} catch (IOException e) {
			 LOGGER.error("Exception to try sendAndReceive message");
		}
		return messageToSend;
	}
	
}
