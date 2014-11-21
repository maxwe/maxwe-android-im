package org.maxwe.android.im;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;

import java.awt.*;

/**
 * @Description: [Description]
 * @Author: [DingPengwei]
 * @Email: [www.dingpengwei@gmail.com]
 * @CreateDate: [11/21/14 13:57]
 * @UpdateUser: [dingpengwei]
 * @UpdateDate: [11/21/14 13:57]
 * @UpdateRemark: [UpdateRemark]
 * @Version: [v1.0]
 */
public class MainActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    WebView webView;
    Handler handler = new Handler();
    Button btn;
    final String jsonStr = "{\"Developer\":\"Harlan\",\"Place\":\"Nanjing\"}";


    @Override
    protected void onStart() {
        super.onStart();
        this.setContentView(R.layout.main_layout);

        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webView.loadUrl("file:///android_asset/index.html");
        webView.setWebChromeClient(new HarlanWebChromeClient());
        webView.setWebViewClient(new HarlanWebViewClient());
                /*
                 * 为js提供一个方法，注意该方法一般不写在UI线程中
                 * addJavascriptInterface(Object obj, String interfaceName)
                 * obj代表一个java对象，这里我们一般会实现一个自己的类，类里面提供我们要提供给javascript访问的方法
                 * interfaceName则是访问我们在obj中声明的方法时候所用到的js对象，调用方法为window.interfaceName.方法名()
                 */
        webView.addJavascriptInterface(new Object() {
            @SuppressWarnings("unused")
            public void adrdMethod() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "js调用了Android方法");
                    }
                });
            }
        }, "demo");

        webView.addJavascriptInterface(new WebSocketFactory(this.webView),"WebSocketFactory");


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.open :
                webView.loadUrl("javascript:rfInfo(" + jsonStr + ")");
                break;
            case R.id.close :
                break;
        }
    }

    /**
     * webChromeClient主要是将javascript中相应的方法翻译成android本地方法
     * <p/>
     * 例如：我们重写了onJsAlert方法，那么当页面中需要弹出alert窗口时，便
     * 会执行我们的代码，按照我们的Toast的形式提示用户。
     */
    class HarlanWebChromeClient extends WebChromeClient {

        /*此处覆盖的是javascript中的alert方法。
         *当网页需要弹出alert窗口时，会执行onJsAlert中的方法
         * 网页自身的alert方法不会被调用。
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            Log.d(TAG, "弹出了提示框");
                        /*此处代码非常重要，若没有，android就不能与js继续进行交互了，
                         * 且第一次交互后，webview不再展示出来。
                         * result：A JsResult to confirm that the user hit enter.
                         * 我的理解是，confirm代表着此次交互执行完毕。只有执行完毕了，才可以进行下一次交互。
                         */
            result.confirm();
            return true;
        }

        /*此处覆盖的是javascript中的confirm方法。
         *当网页需要弹出confirm窗口时，会执行onJsConfirm中的方法
         * 网页自身的confirm方法不会被调用。
         */
        @Override
        public boolean onJsConfirm(WebView view, String url,String message, JsResult result) {
            Log.d(TAG, "弹出了确认框");
            result.confirm();
            return true;
        }

        /*此处覆盖的是javascript中的confirm方法。
         *当网页需要弹出confirm窗口时，会执行onJsConfirm中的方法
         * 网页自身的confirm方法不会被调用。
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,  JsPromptResult result) {
            Log.d(TAG, "弹出了输入框");
            result.confirm();
            return true;
        }

        /*
         * 如果页面被强制关闭,弹窗提示：是否确定离开？
         * 点击确定 保存数据离开，点击取消，停留在当前页面
         */
        @Override
        public boolean onJsBeforeUnload(WebView view, String url,  String message, JsResult result) {
            Log.d(TAG, "弹出了离开确认框");
            result.confirm();
            return true;
        }
    }

    class HarlanWebViewClient extends WebViewClient {
        /*点击页面的某条链接进行跳转的话，会启动系统的默认浏览器进行加载，调出了我们本身的应用
         * 因此，要在shouldOverrideUrlLoading方法中
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //使用当前的WebView加载页面
            view.loadUrl(url);
            return true;
        }

        /*
         * 网页加载完毕(仅指主页，不包括图片)
         */
        @Override
        public void onPageStarted(WebView view, String url,  Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        /*
         * 网页加载完毕(仅指主页，不包括图片)
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
        }

        /*
         * 加载页面资源
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onLoadResource(view, url);
        }

        /*
         * 错误提示
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }
}