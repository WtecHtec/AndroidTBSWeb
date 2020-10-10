package com.example.mywebapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.Date;
import java.util.HashMap;

import utils.DownloadUtil;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;

    private DownloadUtil downloadUtil;


    private    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        final Context ctx = this;

        progressDialog=new ProgressDialog(ctx);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        mWebView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);// 支持js
//        mWebView.setWebViewClient(new WebViewClient());//防止加载网页时调起系统浏览器


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("app", " 网址 is " + url);
                int i = url.indexOf("file");
                if (i != -1) {
                    Log.d("app", " 网址 is " + url);


                    progressDialog.show();

                    /**
                     *  下载文件
                     */
                    DownloadUtil.getInstance().download(url,  ctx.getExternalCacheDir().getPath(), new DownloadUtil.OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess(final String path) {

                            Log.d("app", " 下载成功 " + path);

                            progressDialog.hide();
                            /**
                             *  打开文件
                             */
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("style", "1");
                            params.put("local", "true");
                            QbSdk.openFileReader(ctx,path, params, new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {

                                }
                            });


                        }

                        @Override
                        public void onDownloading(int progress) {
                            Log.d("app","已下载"+progress+"%");
                        }

                        @Override
                        public void onDownloadFailed() {
                            Log.d("app", " 下载失败  ");
                        }
                    });
                    return  true;
                }

                //这里可以对特殊scheme进行拦截处理
                return false;//要返回true否则内核会继续处理
            }
        });
        mWebView.loadUrl("http://192.168.16.87/wechat/login.html?t=" + new Date().getTime());

    }


}