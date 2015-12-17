package org.zaproxy.zap.extension.byPass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.zaproxy.zap.model.ScanController;
import org.zaproxy.zap.model.Target;
import org.zaproxy.zap.users.User;

public class ByPassScanController implements ScanController<ByPassModule>{

	private ExtensionByPass extension;
	private final Lock spiderScansLock;
	private int scanIdCounter;
	private Map<Integer, ByPassModule> spiderScanMap;
	private List<ByPassModule> spiderScanList;

	public ByPassScanController (ExtensionByPass extension) {
		this.spiderScansLock = new ReentrantLock();
		this.extension = extension;
		this.spiderScanMap = new HashMap<>();
		this.spiderScanList = new ArrayList<ByPassModule>();
	}

	@Override
	public int startScan(String name, Target target, User user, Object[] contextSpecificObjects) {
		spiderScansLock.lock();
		try {
			int id = this.scanIdCounter++;
			ByPassModule scan = new ByPassModule(extension.getCookiesSelected(), extension.getTargetByPass(), id, extension);
			this.spiderScanMap.put(id, scan);
			this.spiderScanList.add(scan);
			scan.run();
			return id;
		} finally {
			spiderScansLock.unlock();
		}
	}
	
	@Override
	public ByPassModule getScan(int id) {
		return this.spiderScanMap.get(id);
	}
	
	@Override
	public ByPassModule getLastScan() {
		spiderScansLock.lock();
		try {
			if (spiderScanList.size() == 0) {
				return null;
			}
			return spiderScanList.get(spiderScanList.size()-1);
		} finally {
			spiderScansLock.unlock();
		}
	}
	
	@Override
	public List<ByPassModule> getAllScans() {
		List<ByPassModule> list = new ArrayList<ByPassModule>();
		spiderScansLock.lock();
		try {
			for (ByPassModule scan : spiderScanList) {
				list.add(scan);
			}
			return list;
		} finally {
			spiderScansLock.unlock();
		}
	}
	
	@Override
	public List<ByPassModule> getActiveScans() {
		List<ByPassModule> list = new ArrayList<ByPassModule>();
		spiderScansLock.lock();
		try {
			for (ByPassModule scan : spiderScanList) {
				if (!scan.isStopped()) {
					list.add(scan);
				}
			}
			return list;
		} finally {
			spiderScansLock.unlock();
		}
	}
	
	@Override
	public ByPassModule removeScan(int id) {
		spiderScansLock.lock();

		try {
			ByPassModule ascan = this.spiderScanMap.get(id);
			if (! spiderScanMap.containsKey(id)) {
				//throw new IllegalArgumentException("Unknown id " + id);
				return null;
			}
			ascan.stopScan();
			spiderScanMap.remove(id);
			spiderScanList.remove(ascan);
			return ascan;
		} finally {
			spiderScansLock.unlock();
		}
	}
	
	public int getTotalNumberScans() {
		return spiderScanMap.size();
	}
	
	@Override
	public void stopAllScans() {
		spiderScansLock.lock();
		try {
			for (ByPassModule scan : spiderScanMap.values()) {
				scan.stopScan();
			}
		} finally {
			spiderScansLock.unlock();
		}
	}
	
	@Override
	public void pauseAllScans() {
		spiderScansLock.lock();
		try {
			for (ByPassModule scan : spiderScanMap.values()) {
				scan.pauseScan();
			}
		} finally {
			spiderScansLock.unlock();
		}
	}
	
	@Override
	public void resumeAllScans() {
		spiderScansLock.lock();
		try {
			for (ByPassModule scan : spiderScanMap.values()) {
				scan.resumeScan();
			}
		} finally {
			spiderScansLock.unlock();
		}
	}
	
	@Override
	public int removeAllScans() {
		spiderScansLock.lock();
		try {
			int count = 0;
			for (Iterator<ByPassModule> it = spiderScanMap.values().iterator(); it.hasNext();) {
				ByPassModule ascan = it.next();
				ascan.stopScan();
				it.remove();
				spiderScanList.remove(ascan);
				count++;
			}
			return count;
		} finally {
			spiderScansLock.unlock();
		}
	}

	@Override
	public int removeFinishedScans() {
		spiderScansLock.lock();
		try {
			int count = 0;
			for (Iterator<ByPassModule> it = spiderScanMap.values().iterator(); it.hasNext();) {
				ByPassModule scan = it.next();
				if (scan.isStopped()) {
					scan.stopScan();
					it.remove();
					spiderScanList.remove(scan);
					count ++;
				}
			}
			return count;
		} finally {
			spiderScansLock.unlock();
		}
	}

	@Override
	public void stopScan(int id) {
		spiderScansLock.lock();
		try {
			if (this.spiderScanMap.containsKey(id)) {
				this.spiderScanMap.get(id).stopScan();
			}
		} finally {
			spiderScansLock.unlock();
		}
	}

	@Override
	public void pauseScan(int id) {
		spiderScansLock.lock();
		try {
			if (this.spiderScanMap.containsKey(id)) {
				this.spiderScanMap.get(id).pauseScan();
			}
		} finally {
			spiderScansLock.unlock();
		}
	}

	@Override
	public void resumeScan(int id) {
		spiderScansLock.lock();
		try {
			if (this.spiderScanMap.containsKey(id)) {
				this.spiderScanMap.get(id).resumeScan();
			}
		} finally {
			spiderScansLock.unlock();
		}
	}
	
	public void reset() {
		this.removeAllScans();
		spiderScansLock.lock();
		try {
			this.scanIdCounter = 0;
		} finally {
			spiderScansLock.unlock();
		}
	}
}
