package com.youngwu.volleydemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView tv_text;
    private ImageView img_pic1;
    private NetworkImageView img_pic2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_test = (Button) findViewById(R.id.btn_test);
        tv_text = (TextView) findViewById(R.id.tv_text);
        img_pic1 = (ImageView) findViewById(R.id.img_pic1);
        img_pic2 = (NetworkImageView) findViewById(R.id.img_pic2);
        btn_test.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        click0();
    }

    private void click0() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest("http://www.jcodecraeer.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                tv_text.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tv_text.setText("响应出错");
            }
        });
        requestQueue.add(request);
    }

    private void click1() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest("http://www.jcodecraeer.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                tv_text.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tv_text.setText("响应出错");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("name", "小明");
                map.put("password", "123456");
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void click2() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://m.weather.com.cn/data/101010100.html", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                tv_text.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tv_text.setText("响应出错");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void click3() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ImageRequest imageRequest = new ImageRequest("http://files.jb51.net/file_images/article/201609/201609220904442.png", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                img_pic1.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tv_text.setText("响应出错");
            }
        });
        requestQueue.add(imageRequest);
    }

    private void click4() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ImageLoader imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {

            }
        });
        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(img_pic1, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        imageLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg", imageListener);
    }

    public static class BitmapCache implements ImageLoader.ImageCache {
        private LruCache<String, Bitmap> lruCache;

        public BitmapCache() {
            int cacheSize = 10 * 1024 * 1024;
            lruCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return lruCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            lruCache.put(url, bitmap);
        }
    }

    private void click5() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ImageLoader imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(img_pic1, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        imageLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg", imageListener);
    }

    private void click6() {
        img_pic2.setDefaultImageResId(R.mipmap.ic_launcher);
        img_pic2.setErrorImageResId(R.mipmap.ic_launcher);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ImageLoader imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        img_pic2.setImageUrl("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg", imageLoader);
    }

    public static class XMLRequest extends Request<XmlPullParser> {
        private Response.Listener<XmlPullParser> listener;

        public XMLRequest(int method, String url, Response.Listener<XmlPullParser> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.listener = listener;
        }

        public XMLRequest(String url, Response.Listener<XmlPullParser> listener, Response.ErrorListener errorListener) {
            this(Method.GET, url, listener, errorListener);
        }

        @Override
        protected Response<XmlPullParser> parseNetworkResponse(NetworkResponse response) {
            try {
                String xmlString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(xmlString));
                return Response.success(parser, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (XmlPullParserException e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(XmlPullParser response) {
            listener.onResponse(response);
        }
    }

    private void click7() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        XMLRequest request = new XMLRequest("http://flash.weather.com.cn/wmaps/xml/china.xml", new Response.Listener<XmlPullParser>() {
            @Override
            public void onResponse(XmlPullParser response) {
                try {
                    int eventType = response.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                String nodeName = response.getName();
                                if ("city".equals(nodeName)) {
                                    String pName = response.getAttributeValue(0);
                                    Log.d("TAG", "pName is " + pName);
                                }
                                break;
                        }
                        eventType = response.next();
                    }
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        requestQueue.add(request);
    }

    public static class GsonRequest<T> extends Request<T> {
        private Response.Listener<T> listener;
        private Class<T> clazz;
        private Gson gson;

        public GsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.listener = listener;
            this.clazz = clazz;
            gson = new Gson();
        }

        public GsonRequest(String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
            this(Method.GET, url, clazz, listener, errorListener);
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                return Response.success(gson.fromJson(jsonString, clazz), HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(T response) {
            listener.onResponse(response);
        }
    }

    private void click8() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        GsonRequest<TestBean> request = new GsonRequest<>("", TestBean.class, new Response.Listener<TestBean>() {
            @Override
            public void onResponse(TestBean response) {
                Log.e("TAG", response.getName());
                Log.e("TAG", response.getPassword());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        requestQueue.add(request);
    }

    private class TestBean {
        private String name;
        private String password;

        private String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        private String getPassword() {
            return password;
        }

        private void setPassword(String password) {
            this.password = password;
        }

    }
}
