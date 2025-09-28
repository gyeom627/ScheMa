package com.schema.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JsResult;
import android.webkit.ConsoleMessage;
import android.webkit.WebSettings;
import android.util.Log;
import android.widget.Toast;
import android.os.Message; // New
import android.webkit.WebResourceRequest; // New
import androidx.appcompat.app.AppCompatActivity;
import com.schema.app.R;

public class DaumPostcodeActivity extends AppCompatActivity {

    private WebView webView;

    // Daum Postcode API URL (replace with the actual URL from the guide)
    // This is a placeholder. The actual URL might be a local HTML file that loads the Daum script.
    private static final String DAUM_POSTCODE_URL = "file:///android_asset/daum_postcode.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_postcode);

        webView = findViewById(R.id.webview_daum_postcode);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true); // Enable DOM storage
        webView.getSettings().setSupportMultipleWindows(true); // Support multiple windows (if needed)
        webView.getSettings().setAllowFileAccess(true); // Allow file access
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true); // Allow universal access from file URLs
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // Allow mixed content
        webView.addJavascriptInterface(new AndroidBridge(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // The Daum Postcode script should be loaded and executed by the HTML itself.
                // No explicit action needed here if daum_postcode.html handles it.
            }
        });

        // Add WebChromeClient to handle JavaScript alerts and console messages for debugging and popups
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d("DaumPostcode", "JS Alert: " + message);
                Toast.makeText(DaumPostcodeActivity.this, message, Toast.LENGTH_LONG).show();
                result.confirm();
                return true;
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("DaumPostcode", consoleMessage.message() + " -- From line " +
                        consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return true;
            }
        });

        webView.loadUrl(DAUM_POSTCODE_URL);
    }

    

    private class AndroidBridge {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {
            // data will be a JSON string containing address information
            // Example: {"zonecode":"06130","address":"서울 강남구 테헤란로 427","addressEnglish":"427, Teheran-ro, Gangnam-gu, Seoul, Korea"}
            Intent resultIntent = new Intent();
            resultIntent.putExtra("address_data", data);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}