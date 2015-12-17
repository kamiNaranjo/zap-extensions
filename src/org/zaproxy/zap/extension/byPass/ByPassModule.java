package org.zaproxy.zap.extension.byPass;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.parosproxy.paros.network.HtmlParameter;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.model.GenericScanner2;

public class ByPassModule implements GenericScanner2{

	private final Lock lock;
	private static List<HtmlParameter> cookieArray;
	private static List<HttpMessage> arrayMessages;
	private ByPassThread byPassThread = null;
    private State state;
    private int scanId;
    
    private static enum State {
		NOT_STARTED,
		RUNNING,
		PAUSED,
		FINISHED
	};
	
    public ByPassModule(List<String> cookies, TargetByPass target, int scanId, ExtensionByPass extension){
    	lock = new ReentrantLock();
    	this.scanId = scanId;
		cookieArray = target.getCookieArray();
		arrayMessages = target.getArrayMessages();
		state = State.NOT_STARTED;
		byPassThread = new ByPassThread(cookies, cookieArray, arrayMessages, extension);
	}
	

	@Override
	public void run() {
		lock.lock();
		try {
			if (State.NOT_STARTED.equals(state)) {
				byPassThread.run();
				state = State.RUNNING;
			}
		} finally {
			lock.unlock();
		}
		
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		
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
				state = State.FINISHED;
			}
		} finally {
			lock.unlock();
		}
	}
	
}
