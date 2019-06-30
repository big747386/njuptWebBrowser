package com.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.entity.BookMark;
import com.example.app.entity.History;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.webkit.URLUtil.isHttpUrl;

public class MainActivity extends Activity implements View.OnClickListener{

    private WebView mWebView;
    private ProgressBar progressBar;
    private EditText textUrl;
    private WebSettings mwebSettings;
    private TextView  mtitle;
    private Context mContext;
    /*private TextView  mMenuTv;*/

    private DBOperator dbOperator;
    private SQLiteDatabase sqLiteDatabase;
    private ImageView webIcon, goBack, goForward, navSet, goHome, btnStart, navTest,save,full_screen_exit;
    private InputMethodManager manager;
    private PopupWindow mPopWindow;
    private LinearLayout footer,header;

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    private int flag = 0;
    private String flagUrl;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        initDataBase();
        mContext = MainActivity.this;
        manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //mToolbar = findViewById(R.id.activity_main_toolbar);

        // Force links and redirects to open in the WebView instead of in a browser
        //mWebView.setWebViewClient(new MyWebViewClient());
        //mWebView.setWebChromeClient(new MyWebChromeClient());

        // Enable Javascript
        mwebSettings = mWebView.getSettings();
        mwebSettings.setJavaScriptEnabled(true);
        //见图片调整到适合webview的大小
        mwebSettings.setUseWideViewPort(true);
        //缩放至屏幕的大小
        mwebSettings.setLoadWithOverviewMode(true);

        mwebSettings.setSupportZoom(true);
        mwebSettings.setBuiltInZoomControls(true);
        mwebSettings.setDisplayZoomControls(false);

        //开启http和https混用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mwebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        // REMOTE RESOURCE
        mWebView.loadUrl("http://www.oneplusbbs.com/?adid=qqbrowser_103\n");

        // LOCAL RESOURCE

        //View view = this.getLayoutInflater().inflate(R.layout.activity_main,null);


        mWebView.setWebChromeClient(new mWebChromeClient());
        mWebView.setWebViewClient(new mWebViewClient());

    }


    // Prevent the back-button from closing the app
    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        if (flag == 1) {
            mWebView.loadUrl(flagUrl);
            flag = 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            //System.out.println(mWebView.toString());
            mWebView.loadDataWithBaseURL(null,"",
                    "text/html","utf-8", null);

            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*mToolbar.inflateMenu(R.menu.menus);
        getMenuInflater().inflate(R.menu.menus,menu);
        menu.add(Menu.NONE, Menu.FIRST, 0, "设置");
        return super.onCreateOptionsMenu(menu);*/
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menus, menu);
        return true;
    }

    private void initView() {
        mWebView = findViewById(R.id.activity_main_webView);
        progressBar = findViewById(R.id.progressBar);
        textUrl = findViewById(R.id.textUrl);
        webIcon = findViewById(R.id.webIcon);
        btnStart = findViewById(R.id.btnStart);
        goBack = findViewById(R.id.goBack);
        goForward = findViewById(R.id.goForward);
        navSet = findViewById(R.id.navSet);
        goHome = findViewById(R.id.goHome);

        navTest = findViewById(R.id.full_screen);

        save = findViewById(R.id.save);
        header = findViewById(R.id.urlWindow);
        footer = findViewById(R.id.nevbar);

        full_screen_exit = findViewById(R.id.full_screen_exit);

        // 绑定按钮点击事件
        btnStart.setOnClickListener(this);
        goBack.setOnClickListener(this);
        goForward.setOnClickListener(this);
        navSet.setOnClickListener(this);
        goHome.setOnClickListener(this);
        navTest.setOnClickListener(this);
        save.setOnClickListener(this);
        /*noPicSwitch.setOnClickListener(this);*/

        full_screen_exit.setOnClickListener(this);

        full_screen_exit.getBackground().setAlpha(0);
        full_screen_exit.setVisibility(View.INVISIBLE);
        // 地址输入栏获取与失去焦点处理
        textUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // 显示当前网址链接 TODO:搜索页面显示搜索词
                    textUrl.setText(mWebView.getUrl());
                    // 光标置于末尾
                    textUrl.setSelection(textUrl.getText().length());
                    // 显示因特网图标
                    webIcon.setImageResource(R.drawable.internet);
                    // 显示跳转按钮
                    btnStart.setImageResource(R.drawable.go);
                } else {
                    // 显示网站名
                    textUrl.setText(mWebView.getTitle());
                    // 显示网站图标
                    webIcon.setImageBitmap(mWebView.getFavicon());
                    // 显示刷新按钮
                    btnStart.setImageResource(R.drawable.refresh);
                }
            }
        });

        // 监听键盘回车搜索
        textUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    // 执行搜索
                    btnStart.callOnClick();
                    textUrl.clearFocus();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 跳转 或 刷新
            case R.id.btnStart:
                if (textUrl.hasFocus()) {
                    // 隐藏软键盘
                    if (manager.isActive()) {
                        manager.hideSoftInputFromWindow(textUrl.getApplicationWindowToken(), 0);
                    }

                    // 地址栏有焦点，是跳转
                    String input = textUrl.getText().toString();
                    if (!isHttpUrl(input)) {
                        // 不是网址，加载搜索引擎处理
                        try {
                            // URL 编码
                            input = URLEncoder.encode(input, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        input = "https://www.baidu.com/s?wd=" + input + "&ie=UTF-8";
                    }
                    mWebView.loadUrl(input);

                    // 取消掉地址栏的焦点
                    textUrl.clearFocus();
                } else {
                    // 地址栏没焦点，是刷新
                    mWebView.reload();
                }
                break;

            // 后退
            case R.id.goBack:
                mWebView.goBack();
                break;

            // 前进
            case R.id.goForward:
                mWebView.goForward();
                break;

            // 设置
            case R.id.navSet:
               //跳出弹框
                showPopupWindow();
                /*Toast.makeText(mContext, "功能开发中", Toast.LENGTH_SHORT).show();*/
                break;

            //保存网页
            case R.id.save:
                Toast.makeText(mContext, "保存网页", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    // Get a PrintManager instance
                    PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

                    // Get a print adapter instance
                    PrintDocumentAdapter printAdapter = mWebView.createPrintDocumentAdapter("保存的网页");

                    // Create a print job with name and adapter instance
                    String jobName = getString(R.string.app_name) + " Document";
                    printManager.print(jobName, printAdapter,
                            new PrintAttributes.Builder().build());
                } else {
                    Toast.makeText(getApplicationContext(), "当前系统不支持该功能", Toast.LENGTH_SHORT).show();
                }
                break;

            // 主页
            case R.id.goHome:
                mWebView.loadUrl(getResources().getString(R.string.home_url));
                break;

            //测试接口
            case R.id.full_screen:
                footer.setVisibility(View.GONE);
                header.setVisibility(View.GONE);
                full_screen_exit.setVisibility(View.VISIBLE);
                break;

            //popWindow里的选项
            case R.id.history:{
                jumpToHis();
                mPopWindow.dismiss();
            }
            break;

            case R.id.favourite:{
                jumpToFav();
                mPopWindow.dismiss();
            }
            break;

            case R.id.pop_exit:{
                Toast.makeText(this,"退出设置菜单",Toast.LENGTH_SHORT).show();
                mPopWindow.dismiss();
            }
            break;

            case R.id.add_to_fav:{
                Toast.makeText(this,"添加到收藏夹",Toast.LENGTH_SHORT).show();
                insertBookMark();
                break;
            }

            case R.id.full_screen_exit:{
                full_screen_exit.bringToFront();
                footer.setVisibility(View.VISIBLE);
                header.setVisibility(View.VISIBLE);
                full_screen_exit.setVisibility(View.INVISIBLE);
            }
                break;

            case R.id.landscape:{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设定为横屏
            }
            break;

            case R.id.vertical_screen:{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设定为竖屏
            }
            break;

            case R.id.no_pic:{
                mwebSettings.setBlockNetworkImage(true); // 进入无图模式
            }
            break;

            case R.id.no_pic_off:{
                mwebSettings.setBlockNetworkImage(false); // 退出无图模式
            }
            break;


            default:
        }
    }

    //跳转到收藏夹
    public void jumpToFav() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, FavoriteActivity.class);
        //收藏夹的返回码是1
        startActivityForResult(intent, 1);
    }

    public void jumpToHis() {
        Intent intent = new Intent();
        //历史纪录的返回码是2
        intent.setClass(MainActivity.this, HistoryActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //data = getIntent();
                    String dataUrl = data.getStringExtra("data");
                    flag = 1;
                    flagUrl = dataUrl;
                    Toast.makeText(mContext, dataUrl + "##" +flag, Toast.LENGTH_SHORT).show();
                    //mWebView.loadUrl(dataUrl);
                }
                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    //data = getIntent();
                    String dataUrl = data.getStringExtra("data");
                    int i = 0;
                    flag = 1;
                    flagUrl = dataUrl;
                    //Toast.makeText(mContext, dataUrl, Toast.LENGTH_SHORT).show();
                    //mWebView.loadUrl(dataUrl);
                }
                break;

            default:
        }
    }

    //插入收藏夹
    public void insertBookMark() {
        BookMark bookMark = new BookMark();
        bookMark.setTitle(mWebView.getTitle());
        bookMark.setUrl(mWebView.getUrl());

        ContentValues contentValues = new ContentValues();
        contentValues.put("URL",bookMark.getUrl());
        contentValues.put("TITLE",bookMark.getTitle());
        //SQLITE的类型需要与JAVA中的Date转换
        Date currentTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(currentTime);
        contentValues.put("TIME", dateString);
        sqLiteDatabase.insert("Bookmark", null, contentValues);
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.pop_up_window, null);
        mPopWindow = new PopupWindow(contentView);
        mPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setFocusable(true);//解决打开多个popwindow 并实现县级其他位置关闭弹窗


        TextView history = contentView.findViewById(R.id.history);
        TextView favourite = contentView.findViewById(R.id.favourite);
        TextView exitPop = contentView.findViewById(R.id.pop_exit);
        TextView add_to_fav = contentView.findViewById(R.id.add_to_fav);
        TextView landscape = contentView.findViewById(R.id.landscape);
        TextView vertical_screen = contentView.findViewById(R.id.vertical_screen);
        TextView no_pic= contentView.findViewById(R.id.no_pic);
        TextView no_pic_off = contentView.findViewById(R.id.no_pic_off);

        history.setOnClickListener(this);
        favourite.setOnClickListener(this);
        add_to_fav.setOnClickListener(this);
        exitPop.setOnClickListener(this);
        landscape.setOnClickListener(this);
        vertical_screen.setOnClickListener(this);
        no_pic.setOnClickListener(this);
        no_pic_off.setOnClickListener(this);

        mPopWindow.showAsDropDown(navSet,-200,-500);
    }


    public static boolean isHttpUrl(String urls) {
        boolean isUrl;
        // 判断是否是网址的正则表达式
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";

        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(urls.trim());
        isUrl = mat.matches();
        return isUrl;
    }

    public void addHistory(String title, String url) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TITLE",title);
        contentValues.put("URL",url);
        sqLiteDatabase.insert("History", null, contentValues);
    }

    private class mWebChromeClient extends WebChromeClient {
        private final static int WEB_PROGRESS_MAX = 100;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            // 加载进度变动，刷新进度条
            progressBar.setProgress(newProgress);
            if (newProgress > 0) {
                if (newProgress == WEB_PROGRESS_MAX) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);

            // 改变图标
            webIcon.setImageBitmap(icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            // 改变标题
            setTitle(title);
            // 显示页面标题
            textUrl.setText(title);
        }
    }

    private class mWebViewClient extends WebViewClient {
        boolean if_load;
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if_load = false;
            // 设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中

            if (url == null) {
                // 返回true自己处理，返回false不处理
                return true;
            }

            // 正常的内容，打开
            if (url.startsWith(HTTP) || url.startsWith(HTTPS)) {
                view.loadUrl(url);
                return true;
            }

            // 调用第三方应用，防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
            try {
                // TODO:弹窗提示用户，允许后再调用
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } catch (Exception e) {
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // 网页开始加载，显示进度条
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);

            // 更新状态文字
            textUrl.setText("加载中...");

            // 切换默认网页图标
            webIcon.setImageResource(R.drawable.internet);

            // 开始加载
            if_load = true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 网页加载完毕，隐藏进度条
            progressBar.setVisibility(View.INVISIBLE);

            // 改变标题
            setTitle(mWebView.getTitle());
            // 显示页面标题
            textUrl.setText(mWebView.getTitle());

            //防止重复加载
            if (if_load) {
                addHistory(mWebView.copyBackForwardList().getCurrentItem().getTitle(),
                        mWebView.copyBackForwardList().getCurrentItem().getUrl());
                if_load = false;
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            switch (errorCode) {
                case 404:
                    view.loadUrl("file:///android_assets/index.html");
                    break;
            }
        }
    }

    public void initDataBase() {
        dbOperator = new DBOperator(this, "browserdatabase.db",
                null, 1);

        sqLiteDatabase = dbOperator.getWritableDatabase();
    }
}