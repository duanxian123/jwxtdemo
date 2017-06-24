package com.example.linghao.jwxtdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

/**
 * Created by cz on 2017/6/16.
 */

public class LoginActivity extends Activity {
    private EditText etUsername;
    private EditText etPassword;
    private EditText etCode;
    private ImageView ivCode;
    private Button btLogin;
    private TextView tvResult;
    private String sessionID;
    private Api api;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initNet();
    }

    private void initNet() {
        okHttpClient = getOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://115.231.0.43/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        getCode();
    }

    private void initView() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etCode = (EditText) findViewById(R.id.et_code);
        ivCode = (ImageView) findViewById(R.id.iv_code);
        btLogin = (Button) findViewById(R.id.bt_login);
        tvResult = (TextView) findViewById(R.id.tv_result);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void onClick(View view) {
        getCode();
    }

    private void getCode() {
        api.getCodeImage().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Headers headers = response.headers();
                sessionID = headers.get("Set-Cookie");

                byte[] bytes = new byte[0];
                try {
                    bytes = response.body().bytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //把byte字节组装成图片
                final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //网络图片请求成功，更新到主线程的ImageView
                        tvResult.setText(sessionID);
                        ivCode.setImageBitmap(bmp);
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void login() {
        api.login(
                sessionID,
                "dDwyODE2NTM0OTg7Oz5kuhxMA6DZ8emQOeuBMUKT45FtEw==",
                etUsername.getText().toString(),
                etPassword.getText().toString(),
                etCode.getText().toString(),
                "学生",
                "",
                ""
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 302) {
                        Headers headers = response.headers();
                        String location = headers.get("Location");
                        api.getContent(location).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String content = new String(response.body().bytes(), "gb2312");
                                    tvResult.setText(content);
                                    Log.e(TAG, "onResponse: " + content);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public static OkHttpClient getOkHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(false)  //禁制OkHttp的重定向操作，我们自己处理重定向
                .followSslRedirects(false)
                .cookieJar(new LocalCookieJar())   //为OkHttp设置自动携带Cookie的功能
                .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .build();
        return client;
    }

    //CookieJar是用于保存Cookie的
    public static class LocalCookieJar implements CookieJar {
        List<Cookie> cookies;

        @Override
        public List<Cookie> loadForRequest(HttpUrl arg0) {
            if (cookies != null)
                return cookies;
            return new ArrayList<Cookie>();
        }

        @Override
        public void saveFromResponse(HttpUrl arg0, List<Cookie> cookies) {
            this.cookies = cookies;
        }

    }
}
