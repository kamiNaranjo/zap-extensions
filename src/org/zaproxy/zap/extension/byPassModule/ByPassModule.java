package org.zaproxy.zap.extension.byPassModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;

import org.apache.log4j.Logger;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.network.HtmlParameter;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpSender;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.byPassModule.ui.CookiesSelectInterface;
import org.zaproxy.zap.extension.byPassModule.ui.MainInterfaceByPass;

public class ByPassModule {

	private static List<HtmlParameter> cookieArray;
	private static List<String> cookieName;
	private static List<HttpMessage> arrayMessages;
	private final JTree messagesTree;
    private static final Logger LOGGER = Logger.getLogger(ByPassModule.class);
  
	
	public ByPassModule(HttpMessage urlSelected, JTree messagesTree){
		this.messagesTree = messagesTree;
		cookieArray = new ArrayList<>();
		cookieName = new ArrayList<>();
		arrayMessages = new ArrayList<>();
		arrayMessages.add(urlSelected);
		getCookiesByHttpMessage(urlSelected);
		showCookiesToDelete();
	}
	
	public ByPassModule(SiteNode sitieSelected, JTree messagesTree){
		this.messagesTree = messagesTree;
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
					SiteNode node = (SiteNode) messagesTree.getLastSelectedPathComponent();
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

	public static void getMessageWithOutCookies(List<String> cookies){
		List<HttpMessage> urlsWithOutCookie = new ArrayList<>();
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
			urlsWithOutCookie.add(sendMessageWithOutCookies(urlWithOutCookie));
		}
	}
	
	public void showCookiesToDelete(){
		if(cookieArray!= null && !cookieArray.isEmpty()){
			CookiesSelectInterface cookieInterface = new CookiesSelectInterface(
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
