package gonglue.zhuayou.com.myapplication;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public WebView web;
    public String dimain="http://news.h545.com";
    public String dimain1="http://news.zhangdianwan.com:85";
    public String url="/index.php/home/index/index/cid/";
    public int uid=21;
    public String realurl2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        setContentView(R.layout.activity_main);
        web = findViewById(R.id.webview1);
        StringBuffer weburl = new StringBuffer();

        weburl.append(dimain);
        weburl.append(url);
        weburl.append(uid);
        weburl.append(".html");
        String realurl = weburl.toString();
        StringBuffer weburl2 = new StringBuffer();
        weburl2.append(dimain1);
        weburl2.append(url);
        weburl2.append(uid);
        weburl2.append(".html");
        realurl2 = weburl2.toString();


        web.loadUrl(realurl);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);


        web.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                if (url.startsWith(dimain))
                {
                    view.loadUrl(url);
                    return true;
                }else
                    {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                        return true;
                    }


            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                view.loadUrl(realurl2);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (url.contains("该网站暂时无法访问"))
                {
                    view.loadUrl(realurl2);
                }
                return super.shouldInterceptRequest(view, request);
            }
        });


        web.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                if (Build.VERSION.SDK_INT>=24)
//                {
//                    downloadByBrowser(url);
//                }
                String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                String destPath = ToolsUtil.getApkDir()+ fileName;
                new DownloadTask().execute(url,destPath);


            }
        });

    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&web.canGoBack()){
            web.goBack();
            return true;
        }else
            {
                onBackPressed();
            }

        return super.onKeyDown(keyCode, event);
    }

    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
        // 设置通知栏的描述
//        request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName  = URLUtil.guessFileName(url, contentDisposition, mimeType);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);

    }

    private void downloadByBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private class DownloadTask extends AsyncTask<String, Void, Void> {
        // 传递两个参数：URL 和 目标路径
        private String url;
        private String destPath;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {

            url = params[0];
            destPath = params[1];
            OutputStream out = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(15000);
                InputStream in = urlConnection.getInputStream();
                out = new FileOutputStream(params[1]);
                byte[] buffer = new byte[10 * 1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                in.close();
            } catch (IOException e) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {

                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24)
            {

                String mimeType = getMIMEType(url);
                FileProvider.getUriForFile(MainActivity.this,"gonglue.zhuayou.com",new File(destPath));
                Uri uri = Uri.fromFile(new File(destPath));
                handlerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                handlerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                handlerIntent.setDataAndType(uri, mimeType);
            }else
                {
                    String mimeType = getMIMEType(url);
                    Uri uri = Uri.fromFile(new File(destPath));
                    handlerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    handlerIntent.setDataAndType(uri, mimeType);
                }

            startActivity(handlerIntent);
        }
    }

    private String getMIMEType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }




}
