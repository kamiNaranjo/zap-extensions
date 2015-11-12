package org.zaproxy.zap.extension.byPassModule;

import org.parosproxy.paros.network.HttpMessage;

public class ByPassModule {

	public ByPassModule(HttpMessage urlSelect){	
		System.out.println("COOKIES: " + urlSelect.getCookieParams());
	}
}
