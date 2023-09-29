package com.example.unityplugin;

import android.view.KeyEvent;
import android.webkit.WebView;

public class WebViewCustomInput {
    private WebView webview;

    public void receiveWebView(WebView newWebview) {
        System.out.println("[Webview] Initializing WebView plugin.");
        webview = newWebview;
    }

    public void sendKey(String key) {
        webview.post(new Runnable() {
           @Override
           public void run() {
               KeyEvent downKeyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_K);
               webview.dispatchKeyEvent(downKeyEvent);
               KeyEvent upKeyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_K);
               webview.dispatchKeyEvent(upKeyEvent);
           }
        });
    }

    public void debugPrint() {
        webview.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("[Webview] Plugin is initialized: " + webview.getUrl());
            }
        });
    }
}
