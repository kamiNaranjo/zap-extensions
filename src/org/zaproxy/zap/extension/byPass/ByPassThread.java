package org.zaproxy.zap.extension.byPass;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.ListModel;

import org.apache.log4j.Logger;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.network.HtmlParameter;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpSender;
import org.zaproxy.zap.extension.byPass.ui.ByPassTableModel;
import org.zaproxy.zap.model.Context;
import org.zaproxy.zap.model.GenericScanner;
import org.zaproxy.zap.model.TechSet;
import org.zaproxy.zap.users.User;

	public class ByPassThread extends Thread implements GenericScanner{
	
	private static ByPassTableModel resultsModel;
    private static final Logger LOGGER = Logger.getLogger(ByPassThread.class);
    private List<String> cookies;
  	private List<HtmlParameter> cookieArray;
    private List<HttpMessage> arrayMessages;
    private ExtensionByPass extension;
	protected int progress = 0;
	protected int id = 0;
	
	public ByPassThread(List<String> cookies, List<HtmlParameter> cookieArray, List<HttpMessage> arrayMessages, ExtensionByPass extension){
		this.cookies = cookies;
		this.extension = extension;
		this.cookieArray = cookieArray;
		this.arrayMessages = arrayMessages;
	}
	
	public void scanProgress(int progress) {
		if (progress > this.progress) {
			this.progress = progress;
		}
	}
	
	public void getMessageWithOutCookies(){
		resultsModel = new ByPassTableModel();
		int size = arrayMessages.size();
		int iterator = 0;
		List<HttpCookie> cookiesHTTP = new ArrayList<>();
		for(HttpMessage message:arrayMessages) {
			TreeSet<HtmlParameter> cookieParam = new TreeSet<>();
			HttpMessage urlWithOutCookie;
			for(String cookieToDelete:cookies){
				for (HtmlParameter cookie:cookieArray) {
					cookieParam = message.getCookieParams();
					if(cookie.getName().equals(cookieToDelete)){
						cookieParam.remove(cookie);
					}else{
						cookiesHTTP.add(new HttpCookie(cookie.getName(), cookie.getValue()));
					}
				}
			}
			urlWithOutCookie = new HttpMessage(message.getRequestHeader(), message.getRequestBody());
			urlWithOutCookie.setCookieParams(cookieParam);
			urlWithOutCookie.getRequestHeader().setCookieParams(cookieParam);
			HttpMessage response = sendMessageWithOutCookies(urlWithOutCookie);
			urlWithOutCookie.getRequestHeader().setCookies(cookiesHTTP);
			if(message.getResponseHeader().equals(response.getResponseHeader()) && message.getResponseBody().equals(response.getResponseBody()))
				resultsModel.addSResul(response , true);
			else
				resultsModel.addSResul(response , false);
			extension.showResults(resultsModel);
			iterator++;
			progress = (int) ((iterator*100)/size);
			
		}
	}
	
	public HttpMessage sendMessageWithOutCookies(HttpMessage messageToSend){
		HttpSender sender = new HttpSender(Model.getSingleton().getOptionsParam().getConnectionParam(), true, HttpSender.FUZZER_INITIATOR);
		try {
			sender.sendAndReceive(messageToSend);
		} catch (IOException e) {
			 LOGGER.error("Exception to try sendAndReceive message");
		}
		return messageToSend;
	}

	@Override
	public void run(){
		getMessageWithOutCookies();
	}
	
	@Override
	public void stopScan(){
		Thread.currentThread().interrupt();
	};

	@Override
	public boolean isStopped(){
		return Thread.currentThread().isInterrupted();
	}
	
	@Override
	public int getProgress(){
		return progress;
	}

	@Override
	public void pauseScan(){
		Thread.currentThread().interrupt();
	}
	public ByPassTableModel getMessagesTableModel() {
	    return resultsModel;
	}
	
	public List<String> getCookies() {
		return cookies;
	}

	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}

	public List<HtmlParameter> getCookieArray() {
		return cookieArray;
	}

	public void setCookieArray(List<HtmlParameter> cookieArray) {
		this.cookieArray = cookieArray;
	}

	public List<HttpMessage> getArrayMessages() {
		return arrayMessages;
	}

	public void setArrayMessages(List<HttpMessage> arrayMessages) {
		this.arrayMessages = arrayMessages;
	}

	@Override
	public boolean getJustScanInScope() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ListModel<?> getList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaximum() {
		return 100;
	}

	@Override
	public String getSite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SiteNode getStartNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resumeScan() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setJustScanInScope(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScanAsUser(User arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScanChildren(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScanContext(Context arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStartNode(SiteNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTechSet(TechSet arg0) {
		// TODO Auto-generated method stub
		
	}

	public int getScanId() {
		return id;
	}
}
