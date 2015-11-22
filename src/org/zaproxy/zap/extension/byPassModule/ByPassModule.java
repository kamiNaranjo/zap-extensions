package org.zaproxy.zap.extension.byPassModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HtmlParameter;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpSender;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.byPassModule.ui.ByPassResultInterface;
import org.zaproxy.zap.extension.byPassModule.ui.CookiesSelectInterface;
import org.zaproxy.zap.extension.byPassModule.ui.MainInterfaceByPass;

public class ByPassModule {

	static List<HtmlParameter> cookieArray;
	static HttpMessage urlSelect;
	private static final Logger LOG = Logger.getLogger(ByPassModule.class);
	
	public ByPassModule(HttpMessage urlSelected){	
		urlSelect = urlSelected;
		List<String> cookiesName = new ArrayList<>();
		cookieArray = new ArrayList<>();
		Iterator<HtmlParameter> cookiesParam = urlSelect.getCookieParams().iterator();
		while (cookiesParam.hasNext()) {
			HtmlParameter cookie = (HtmlParameter) cookiesParam.next();
			cookieArray.add(cookie);
			cookiesName.add(cookie.getName());
		}
		if(cookiesName!= null && !cookiesName.isEmpty()){
			CookiesSelectInterface cookieInterface = new CookiesSelectInterface(
					MainInterfaceByPass.getOwnerFrame(), cookiesName);
			cookieInterface.pack();
			cookieInterface.setVisible(true);
		}else{
			View.getSingleton().showMessageDialog(ExtensionByPass.getMessageString("message.dontContainCookie"));
		}
		
	}

	public static void getMessageWithOutCookies(List<String> cookies){
		HttpMessage urlWithOutCookie = urlSelect.cloneAll();
		for(String cookieToDelete:cookies){
			for (int i = 0; i < cookieArray.size(); i++) {
				if(cookieArray.get(i).getName().equals(cookieToDelete)){
					urlWithOutCookie.getCookieParams().remove(cookieArray.get(i));
					cookieArray.remove(i);
					break;
				}
			}
		}
		ByPassResultInterface resultInterface = new ByPassResultInterface(MainInterfaceByPass.getOwnerFrame(), sendMessageWithOutCookies(urlWithOutCookie));
		resultInterface.pack();
		resultInterface.setVisible(true);
	}
	
	public static HttpMessage sendMessageWithOutCookies(HttpMessage messageToSend){
		HttpSender sender = new HttpSender(Model.getSingleton().getOptionsParam().getConnectionParam(),
                true, HttpSender.FUZZER_INITIATOR);
		try {
			sender.sendAndReceive(messageToSend);
		} catch (IOException e) {
			 LOG.error("Exception to try sendAndReceive message");
		}
		return messageToSend;
	}
	
}
