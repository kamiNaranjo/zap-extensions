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

	public class ByPassThread implements Runnable, GenericScanner{
	
	private boolean isPaused = false;
	private ByPassTableModel resultsModel;
	private static final Logger LOGGER = Logger.getLogger(ByPassThread.class);
    private List<String> cookies;
  	private List<HtmlParameter> cookieArray;
    private List<HttpMessage> arrayMessages;
    private ExtensionByPass extension;
    private ByPassModule module;
	protected int progress;
	protected int id;
	private boolean isRunning;
	private SiteNode startNode;
	
	public ByPassThread(List<String> cookies, List<HtmlParameter> cookieArray, SiteNode siteNode, List<HttpMessage> arrayMessages, ExtensionByPass extension, ByPassModule module){
		this.cookies = cookies;
		this.extension = extension;
		this.cookieArray = cookieArray;
		this.arrayMessages = arrayMessages;
		this.module = module;
		this.startNode = siteNode;
		resultsModel = new ByPassTableModel();
	}
	
	public void scanProgress(int progress) {
		if (progress > this.progress) {
			this.progress = progress;
		}
	}
	
	public void getMessageWithOutCookies(){
		int size = arrayMessages.size();
		int iterator = 0;
		List<HttpCookie> cookiesHTTP = new ArrayList<>();
		for(HttpMessage message:arrayMessages) {
			while(!isRunning){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					 LOGGER.error("Exception to slepping thread");
				}
			}
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
			iterator++;
			progress = (int) ((iterator*100)/size);
			extension.showResults(module);
		}
		module.finishedTread();
		extension.finishScanPanel();
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

	/**
	 * **/
	
	public void run(){
		this.isRunning = true;
		this.isPaused = false;
		getMessageWithOutCookies();
	}
	
	@Override
	public void stopScan(){
		this.isRunning = false;
		this.isPaused = false;
		extension.showResults(module);
		Thread.currentThread().interrupt();
		module.finishedTread();
		extension.finishScanPanel();
	};

	@Override
	public boolean isStopped(){
		return false;
	}
	
	@Override
	public void pauseScan(){
		this.isRunning = false;
		this.isPaused = true;
	}
	
	@Override
	public boolean isPaused() {
		return this.isPaused;
	}

	@Override
	public boolean isRunning() {
		return this.isRunning;
	}

	@Override
	public void reset() {
		this.resultsModel.removeAllElements();
		extension.showResults(module);
	}

	@Override
	public void resumeScan() {
		this.isRunning = true;
		this.isPaused = false;
	}
	/**
	 * **/
	@Override
	public int getProgress(){
		return progress;
	}

	public ByPassTableModel getMessagesTableModel() {
	    return resultsModel;
	}
	
	public SiteNode getSiteNode() {
		return startNode;
	}

	public void setSiteNote(SiteNode node) {
		this.startNode = node;
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
	
	 public  ByPassTableModel getResultsModel() {
		return resultsModel;
	}

	public void setResultsModel(ByPassTableModel resultsModel) {
		this.resultsModel = resultsModel;
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
		return startNode.toString();
	}

	@Override
	public SiteNode getStartNode() {
		return startNode;
	}

	@Override
	public void setStartNode(SiteNode arg0) {
		this.startNode = arg0;		
	}

	public int getScanId() {
		return id;
	}	
	
	@Override
	public boolean getJustScanInScope() {
		// TODO Auto-generated method stub
		return false;
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
	public void setTechSet(TechSet arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		run();	
	}
}
