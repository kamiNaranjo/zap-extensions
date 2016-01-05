package org.zaproxy.zap.extension.byPass.popup;

import java.awt.Component;
import java.awt.event.ActionEvent;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.network.HttpRequestHeader;
import org.zaproxy.zap.extension.anticsrf.AntiCsrfAPI;
import org.zaproxy.zap.extension.api.API;
import org.zaproxy.zap.extension.byPass.ExtensionByPass;
import org.zaproxy.zap.utils.DesktopUtils;

public class PopupMenuGenerateForm extends ExtensionPopupMenuItem {

	private static final long serialVersionUID = 1L;
	private ExtensionByPass extension = null;

	public PopupMenuGenerateForm(String label) {
        super(label);
        initialize();
    }
    
    public PopupMenuGenerateForm() {
        super();
        initialize();
    }

    private void initialize() {
        this.setText(Constant.messages.getString("anticsrf.genForm.popup"));
        this.setActionCommand("");
        this.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DesktopUtils.openUrlInBrowser(AntiCsrfAPI.getAntiCsrfFormUrl(extension.messageSelected().getHistoryRef().getHistoryId()));

			} 
        });      
    }

    
    @Override
	public boolean isEnableForComponent(Component invoker) {
    	HistoryReference href = extension.messageSelected().getHistoryRef();
		if (invoker.getName().equalsIgnoreCase("ByPasssResult")) {
			if (API.getInstance().isEnabled() && DesktopUtils.canOpenUrlInBrowser()) {
				try {
					if (!extension.getByPassPanel().isMultipleSelected() && HttpRequestHeader.POST.equals(href.getMethod()) && href.getRequestBodyLength() > 0) {
						this.setEnabled(true);
					}else{
						this.setEnabled(false);
					}
					return true;
				} catch (Exception e) {
					// Ignore - this is 'just' for a right click menu
				}
			}
			return false;
		} 
		return false;
	}
	
	public void setExtension(ExtensionByPass extension) {
		this.extension = extension;
	}
}
