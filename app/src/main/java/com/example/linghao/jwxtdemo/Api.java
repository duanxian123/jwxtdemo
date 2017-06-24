package com.example.linghao.jwxtdemo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by linghao on 2017/6/17.
 */

public interface Api {
    @Headers({
            "Referer: http://jwxt.zjyc.edu.cn/default2.aspx",
            "Host: 115.231.0.43",
            "Content-Type: application/x-www-form-urlencoded",
            "charset: gb2312"
    })
    @FormUrlEncoded
    @POST("default2.aspx")
    Call<ResponseBody> login(
            @Header("Cookie") String cookie,
            @Field("__VIEWSTATE") String viewstate,
            @Field("txtUserName") String user,
            @Field("TextBox2") String password,
            @Field("txtSecretCode") String code,
            @Field("RadioButtonList1") String studORTheacher,
            @Field("Button1") String button1,
            @Field("lbLanguage") String lbLanguage
    );

    @GET("CheckCode.aspx")
    Call<ResponseBody> getCodeImage();

    @GET
    Call<ResponseBody> getContent(@Url String url);
}

