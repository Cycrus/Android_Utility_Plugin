package com.example.unityplugin;

import android.app.Activity;
import android.webkit.WebView;

public class WebViewProperties {
    private WebView webview;

    public void receiveWebView(WebView newWebview) {
        webview = newWebview;
    }

    public void setWebViewUserAgent(String agent)
    {
        webview.getSettings().setUserAgentString(agent);
    }
}
