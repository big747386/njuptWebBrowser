package com.example.app;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.example.app.MainActivity.*;

class MyWebChromeClient extends WebChromeClient {
    public MyWebChromeClient() {
        super();

    }
    @Override
    public void onReceivedTitle(WebView webView, String title) {

    }
}
