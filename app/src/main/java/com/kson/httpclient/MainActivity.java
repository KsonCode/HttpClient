package com.kson.httpclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.InvalidMarkException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String IMG_URL = "http://img4.imgtn.bdimg.com/it/u=1968856895,1816029947&fm=26&gp=0.jpg";

    private final String POST_URL = "http://v.juhe.cn/toutiao/index";
    private final String GET_URL = "http://v.juhe.cn/toutiao/index?type=top&key=22a108244dbb8d1f49967cd74a0c144d";
    public int SUCCESS = 0;
    public int fail = -1;

    private ImageView mImgIv;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    String result = (String) msg.obj;
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                    parseData(result);
                    break;
                case -1:
                    Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };


    /**
     * 解析返回的json字符串
     *
     * @param result
     */
    private void parseData(String result) {
        // TODO: 2017/8/7


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mImgIv = (ImageView) findViewById(R.id.iv_img);
    }

    /**
     * get请求方法
     *
     * @param v
     */
    public void getClick(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getInfo();
            }
        }).start();

    }

    /**
     * post请求方法
     *
     * @param v
     */
    public void postClick(View v) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                postInfo();
            }
        }).start();

    }

    /**
     * 图片下载
     *
     * @param v
     */
    public void imgClick(View v) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadImg();
            }
        }).start();

    }

    /**
     * 图片下载
     */
    private void downloadImg() {
        try {
            URL u = new URL(IMG_URL);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.connect();

            if (200 == connection.getResponseCode()) {
                InputStream in = connection.getInputStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(in);
                mImgIv.post(new Runnable() {
                    @Override
                    public void run() {
                        mImgIv.setImageBitmap(bitmap);
                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


//        HttpClient client = new DefaultHttpClient();
//        HttpGet post = new HttpGet(IMG_URL);
//
//        try {
//            HttpResponse response =  client.execute(post);
//            if (200==response.getStatusLine().getStatusCode()){
//                InputStream in = response.getEntity().getContent();
//                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//                int length = 0;
//                byte[] buffer = new byte[1024];
//                while ((length = in.read(buffer)) != -1) {
//                    byteOut.write(buffer, 0, length);
//                }
//
//                final Bitmap bitmap = BitmapFactory.decodeByteArray(byteOut.toByteArray(),0,byteOut.size());
//
//                mImgIv.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mImgIv.setImageBitmap(bitmap);
//                    }
//                });
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        mImgIv.setImageBitmap(bitmap);
////                    }
////                });
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    /**
     * get方式请求数据
     */
    private void getInfo() {
        String result = null;
        Message msg = new Message();
        //创建HttpClient对象，用于传输数据
        HttpClient httpClient = new DefaultHttpClient();
        //创建HttpGet对象，负责处理get请求
        HttpGet httpGet = new HttpGet(GET_URL);

        try {
            //创建HttpResponse对象，用于接收服务器返回的数据
            HttpResponse response = httpClient.execute(httpGet);
            //响应码
            int code = response.getStatusLine().getStatusCode();
            //响应信息
            String responseMsg = response.getStatusLine().getReasonPhrase();
            //如果状态码是200，则请求成功，否则失败
            if (200 == code) {
                //通过响应对象的实体对象，拿到输入流
                HttpEntity entity = response.getEntity();
                InputStream in = entity.getContent();
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                int length = 0;
                byte[] buffer = new byte[1024];
                while ((length = in.read(buffer)) != -1) {
                    byteOut.write(buffer, 0, length);
                }
                result = byteOut.toString("utf-8");
                System.out.println("result=====" + result);
                msg.what = SUCCESS;
                msg.obj = result;
                mHandler.sendMessage(msg);

            } else {//请求失败
                msg.what = fail;
                mHandler.sendMessage(msg);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * post请求方法
     */
    private void postInfo() {
        StringBuffer sb = new StringBuffer();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(POST_URL);


        try {
            //封装请求参数
            List<NameValuePair> paramslist = new ArrayList<>();
            paramslist.add(new BasicNameValuePair("key", "22a108244dbb8d1f49967cd74a0c144d"));
            paramslist.add(new BasicNameValuePair("type", "top"));
            UrlEncodedFormEntity urlencodeEntity = new UrlEncodedFormEntity(paramslist);
            httpPost.setEntity(urlencodeEntity);

            HttpResponse response = client.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            String responseMsg = response.getStatusLine().getReasonPhrase();

            if (200 == code) {//请求成功

                InputStream in = response.getEntity().getContent();

                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = bufferReader.readLine()) != null) {

                    sb.append(line);

                }
                Message msg = new Message();
                msg.obj = sb.toString();
                msg.what = 0;
                mHandler.sendMessage(msg);


            } else {//请求失败
                Message msg = new Message();
                msg.obj = sb.toString();
                msg.what = -1;
                mHandler.sendMessage(msg);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    
    public void  test(){
        // TODO: 2017/8/27
    }

    public void hello(){

    }

    public void h(){

    }
}
