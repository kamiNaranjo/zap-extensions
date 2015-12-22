package org.zaproxy.zap.extension.byPass;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.table.TableModel;

import org.parosproxy.paros.network.HtmlParameter;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.model.GenericScanner2;

public class ByPassModule implements GenericScanner2{

	private final Lock lock;
	private static List<HtmlParameter> cookieArray;
	private static List<HttpMessage> arrayMessages;
	private ByPassThread byPassThread;
	private ExecutorService executor;
    private State state;
    private int scanId;
    private String displayName;
    List<String> resources;
    
    static enum State {
		NOT_STARTED,
		RUNNING,
		PAUSED,
		FINISHED
	};
	
    public ByPassModule(List<String> cookies, List<String> resources, TargetByPass target, int scanId, ExtensionByPass extension){
    	lock = new ReentrantLock();
    	this.scanId = scanId;
    	this.resources = resources;
		cookieArray = target.getCookieArray();
		arrayMessages = target.getArrayMessages();
		displayName = arrayMessages.get(0).getRequestHeader().getVersion() + " - " + arrayMessages.get(0).getRequestHeader().getHostName();
		state = State.NOT_STARTED;
		byPassThread = new ByPassThread(cookies, cookieArray, target.getTarget(), arrayMessages, extension, ByPassModule.this);
		executor = Executors.newFixedThreadPool(1);
	}
	

    public void deleteResource(){
    	if(resources != null){
    		for(HttpMessage message:arrayMessages){
    			for(String resource: resources){
    				if(message.getRequestHeader().getURI().toString().endsWith(resource)){
    					arrayMessages.remove(message);
    					break;
    				}
    			}
    		}
    	}
    }

	@Override
	public void run() {
		lock.lock();
		System.err.println(Thread.currentThread().getName());
		try {
			if (State.NOT_STARTED.equals(state)) {
				state = State.RUNNING;
				executor.execute(byPassThread);
				executor.shutdown();
			}
		} finally {
			lock.unlock();
		}
		
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public int getMaximum() {
		return 100;
	}

	@Override
	public int getProgress() {
		return byPassThread.getProgress();
	}

	@Override
	public int getScanId() {
		return scanId;
	}

	@Override
	public boolean isPaused() {
		return this.byPassThread.isPaused();
	}

	@Override
	public boolean isRunning() {
		return this.byPassThread.isRunning();
	}

	@Override
	public boolean isStopped() {
		return this.byPassThread.isStopped();
	}

	@Override
	public void pauseScan() {
		lock.lock();
		try {
			if (State.RUNNING.equals(state)) {
				byPassThread.pauseScan();
				state = State.PAUSED;
			}
		} finally {
			lock.unlock();
		}		
	}

	@Override
	public void resumeScan() {
		lock.lock();
		try {
			if (State.PAUSED.equals(state)) {
				byPassThread.resumeScan();
				state = State.RUNNING;
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void setDisplayName(String arg0) {
		this.displayName = arg0;		
	}

	@Override
	public void setScanId(int id) {
		this.scanId = id;		
	}

	@Override
	public void stopScan() {
		lock.lock();
		try {
			if (!State.NOT_STARTED.equals(state) && !State.FINISHED.equals(state)) {
				byPassThread.stopScan();
				executor.shutdownNow();
				state = State.FINISHED;
			}
		} finally {
			lock.unlock();
		}
	}
	
	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}


	public TableModel getResultsTableModel() {
		return byPassThread.getResultsModel();
	}
	
	public void finishedTread(){
		this.state = State.FINISHED;
	}
	
}
