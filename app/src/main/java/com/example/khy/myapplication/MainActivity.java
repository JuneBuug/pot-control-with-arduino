package com.example.khy.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.os.Build;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    PopupWindow popup;
    View popupview;
    LinearLayout linear;

    PopupWindow popup_humidity;
    View popupview_humidity;
    LinearLayout linear_humidity;

    PopupWindow popup_light;
    View popupview_light;
    LinearLayout linear_light;

    PopupWindow popup_soil;
    View popupview_soil;
    LinearLayout linear_soil;

    ImageView image;

    JSONObject order;

    private static final String TAG = "MyFirebaseMsgService";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        linear = (LinearLayout)findViewById(R.id.temperature);
        popupview = View.inflate(this, R.layout.temperature, null);
        popup = new PopupWindow(popupview,width,height/3, true);
        linear.setOnClickListener(new LinearLayout.OnClickListener(){
            public void onClick(View v){
                // 가운데 아래 놓기
                TextView pop_temperature_val = (TextView)popupview.findViewById(R.id.temperature_val);
                TextView pop_temperature_txt = (TextView)popupview.findViewById(R.id.temperature_txt);
                TextView temperature_val = (TextView)findViewById(R.id.temperature_val);
                TextView temperature_txt = (TextView)findViewById(R.id.temperature_txt);
                pop_temperature_val.setText((String)temperature_val.getText());
                pop_temperature_txt.setText((String)temperature_txt.getText());
                popup.showAtLocation(linear, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 50,50);
            }
        });
        ImageButton btnclose = (ImageButton)popupview.findViewById(R.id.btnclose);
        btnclose.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v){
                popup.dismiss();
            }
        });

        linear_humidity = (LinearLayout)findViewById(R.id.humidity);
        popupview_humidity = View.inflate(this, R.layout.humidity, null);
        popup_humidity = new PopupWindow(popupview_humidity,width,height/3, true);
        linear_humidity.setOnClickListener(new LinearLayout.OnClickListener(){
            public void onClick(View v){
                // 가운데 아래 놓기
                TextView pop_humidity_val = (TextView)popupview_humidity.findViewById(R.id.humidity_val);
                TextView pop_humidity_txt = (TextView)popupview_humidity.findViewById(R.id.humidity_txt);
                TextView humidity_val = (TextView)findViewById(R.id.humidity_val);
                TextView humidity_txt = (TextView)findViewById(R.id.humidity_txt);
                pop_humidity_val.setText((String)humidity_val.getText());
                pop_humidity_txt.setText((String)humidity_txt.getText());
                popup_humidity.showAtLocation(linear_humidity, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 50,50);
            }
        });
        ImageButton btnclose2 = (ImageButton)popupview_humidity.findViewById(R.id.btnclose);
        btnclose2.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v){
                popup_humidity.dismiss();
            }
        });

        linear_light = (LinearLayout)findViewById(R.id.light);
        popupview_light = View.inflate(this, R.layout.light, null);
        popup_light = new PopupWindow(popupview_light,width,height/3, true);
        linear_light.setOnClickListener(new LinearLayout.OnClickListener(){
            public void onClick(View v){
                // 가운데 아래 놓기
                TextView pop_light_val = (TextView)popupview_light.findViewById(R.id.light_val);
                Switch pop_led_val = (Switch)popupview_light.findViewById(R.id.led_val);
                TextView pop_light_txt = (TextView)popupview_light.findViewById(R.id.light_txt);
                TextView light_val = (TextView)findViewById(R.id.light_val);
                TextView led_val = (TextView)findViewById(R.id.led_val);
                TextView light_txt = (TextView)findViewById(R.id.light_txt);
                pop_light_val.setText((String)light_val.getText());
                pop_led_val.setChecked(Boolean.valueOf((String)led_val.getText()));
                pop_light_txt.setText((String)light_txt.getText());

                popup_light.showAtLocation(linear_light, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 50,50);
            }
        });
        Switch led_val = (Switch)popupview_light.findViewById(R.id.led_val);
        led_val.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sendPost();
            }
        });
        ImageButton btnclose3 = (ImageButton)popupview_light.findViewById(R.id.btnclose);
        btnclose3.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v){
                //sendPost();
                popup_light.dismiss();
            }
        });

        linear_soil = (LinearLayout)findViewById(R.id.soilwater);
        popupview_soil = View.inflate(this, R.layout.soilwater, null);
        popup_soil = new PopupWindow(popupview_soil,width,height/3, true);
        linear_soil.setOnClickListener(new LinearLayout.OnClickListener(){
            public void onClick(View v){
                // 가운데 아래 놓기
                TextView pop_soil_val = (TextView)popupview_soil.findViewById(R.id.soilwater_val);
                TextView pop_soil_txt = (TextView)popupview_soil.findViewById(R.id.soilwater_txt);
                TextView soil_val = (TextView)findViewById(R.id.soilwater_val);
                TextView soil_txt = (TextView)findViewById(R.id.soilwater_txt);
                pop_soil_val.setText((String)soil_val.getText());
                pop_soil_txt.setText((String)soil_txt.getText());
                popup_soil.showAtLocation(linear_soil, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 50,50);
            }
        });
        ImageButton btnclose4 = (ImageButton)popupview_soil.findViewById(R.id.btnclose);
        btnclose4.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v){
                popup_soil.dismiss();
            }
        });

        image = (ImageView)findViewById(R.id.image);
        image.setOnClickListener(new ImageView.OnClickListener(){
            public void onClick(View v){
                DownThread thread = new DownThread("http://13.125.92.56/api/log");
                thread.start();
            }
        });
    }

    class DownThread extends Thread {
        String mAddr;

        DownThread(String addr){
            mAddr = addr;
        }

        public void run() {
            String result = DownloadHtml(mAddr);
            Message message = mAfterDown.obtainMessage();
            message.obj = result;
            mAfterDown.sendMessage(message);
        }

        String DownloadHtml(String addr){
            StringBuilder html = new StringBuilder();
            try {
                URL url = new URL(addr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if(conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        for(;;) {
                            String line = br.readLine();
                            if(line == null) break;
                            html.append(line + '\n');
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (NetworkOnMainThreadException e) {
                return "Error : 메인 스레드 네트워크 작업 에러 - " + e.getMessage();
            } catch (Exception e) {
                return "Error : " + e.getMessage();
            }
            return html.toString();
        }

        Handler mAfterDown = new Handler() {
            public void handleMessage(Message msg) {
                //TextView result = (TextView)findViewById(R.id.result);
                //result.setText((String)msg.obj);
                String Json = (String)msg.obj;
                try {

                    JSONArray ja = new JSONArray(Json);
                    order = ja.getJSONObject(ja.length()-1);
                    String temperature = order.getString("temperature");
                    String humidity = order.getString("humidity");
                    String brightness = order.getString("brightness");
                    String soil_water = order.getString("soil_water");
                    String is_led_active = order.getJSONObject("plant_info").getString("is_led_active");

                    TextView t = (TextView)findViewById(R.id.temperature_val);
                    TextView t2 = (TextView)findViewById(R.id.temperature_txt);
                    ImageView it1 = (ImageView)findViewById(R.id.temperature_status1);
                    ImageView it2 = (ImageView)findViewById(R.id.temperature_status2);
                    ImageView it3 = (ImageView)findViewById(R.id.temperature_status3);
                    t.setText(temperature);
                    if(Integer.parseInt(temperature) < 15){
                        t2.setText("추워요");
                        it1.setImageResource(R.drawable.status_red);
                        it2.setImageResource(R.drawable.status_grey);
                        it3.setImageResource(R.drawable.status_grey);
                    } else if(Integer.parseInt(temperature) >= 15 && Integer.parseInt(temperature) < 25){
                        t2.setText("적당해요");
                        it1.setImageResource(R.drawable.status_grey);
                        it2.setImageResource(R.drawable.status_green);
                        it3.setImageResource(R.drawable.status_grey);;
                    } else {
                        t2.setText("더워요");
                        it1.setImageResource(R.drawable.status_grey);
                        it2.setImageResource(R.drawable.status_grey);
                        it3.setImageResource(R.drawable.status_red);
                    }
                    TextView h = (TextView)findViewById(R.id.humidity_val);
                    TextView h2 = (TextView)findViewById(R.id.humidity_txt);
                    ImageView ih1 = (ImageView)findViewById(R.id.humidity_status1);
                    ImageView ih2 = (ImageView)findViewById(R.id.humidity_status2);
                    ImageView ih3 = (ImageView)findViewById(R.id.humidity_status3);
                    h.setText(humidity);
                    if(Integer.parseInt(humidity) < 40){
                        h2.setText("건조해요");
                        ih1.setImageResource(R.drawable.status_red);
                        ih2.setImageResource(R.drawable.status_grey);
                        ih3.setImageResource(R.drawable.status_grey);
                    } else if(Integer.parseInt(humidity) >= 40 && Integer.parseInt(humidity) < 65){
                        h2.setText("적당해요");
                        ih1.setImageResource(R.drawable.status_grey);
                        ih2.setImageResource(R.drawable.status_green);
                        ih3.setImageResource(R.drawable.status_grey);
                    } else {
                        h2.setText("습해요");
                        ih1.setImageResource(R.drawable.status_grey);
                        ih2.setImageResource(R.drawable.status_grey);
                        ih3.setImageResource(R.drawable.status_red);
                    }
                    TextView b = (TextView)findViewById(R.id.light_val);
                    TextView b2 = (TextView)findViewById(R.id.light_txt);
                    TextView b3 = (TextView)findViewById(R.id.led_val);
                    ImageView ib1 = (ImageView)findViewById(R.id.light_status1);
                    ImageView ib2 = (ImageView)findViewById(R.id.light_status2);
                    ImageView ib3 = (ImageView)findViewById(R.id.light_status3);
                    b.setText(brightness);
                    b3.setText(is_led_active);
                    if(Integer.parseInt(brightness) < 500){
                        b2.setText("어두워요");
                        ib1.setImageResource(R.drawable.status_red);
                        ib2.setImageResource(R.drawable.status_grey);
                        ib3.setImageResource(R.drawable.status_grey);
                    } else if(Integer.parseInt(brightness) >= 500 && Integer.parseInt(brightness) < 5000){
                        b2.setText("적당해요");
                        ib1.setImageResource(R.drawable.status_grey);
                        ib2.setImageResource(R.drawable.status_green);
                        ib3.setImageResource(R.drawable.status_grey);
                    } else {
                        b2.setText("눈부셔요");
                        ib1.setImageResource(R.drawable.status_grey);
                        ib2.setImageResource(R.drawable.status_grey);
                        ib3.setImageResource(R.drawable.status_red);
                    }
                    TextView s = (TextView)findViewById(R.id.soilwater_val);
                    TextView s2 = (TextView)findViewById(R.id.soilwater_txt);
                    ImageView i1 = (ImageView)findViewById(R.id.soilwater_status1);
                    ImageView i2 = (ImageView)findViewById(R.id.soilwater_status2);
                    ImageView i3 =  (ImageView)findViewById(R.id.soilwater_status3);
                    s.setText(soil_water);
                    if(Integer.parseInt(brightness) < 500){
                        s2.setText("목말라요");
                        i1.setImageResource(R.drawable.status_red);
                        i2.setImageResource(R.drawable.status_grey);
                        i3.setImageResource(R.drawable.status_grey);
                    } else if(Integer.parseInt(brightness) >= 500 && Integer.parseInt(brightness) < 5000){
                        s2.setText("적당해요");
                        i1.setImageResource(R.drawable.status_grey);
                        i2.setImageResource(R.drawable.status_green);
                        i3.setImageResource(R.drawable.status_grey);
                    } else {
                        s2.setText("배불러요");
                        i1.setImageResource(R.drawable.status_grey);
                        i2.setImageResource(R.drawable.status_grey);
                        i3.setImageResource(R.drawable.status_red);
                    }

                } catch (JSONException e){
                    //Toast.makeText(e.getMessage(), 0).show();
                }

            }

        };

    }

    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Boolean led_status;
                    String s = ""; //status
                    Switch ischecked = (Switch)popupview_light.findViewById(R.id.led_val);
                    led_status = ischecked.isChecked();
                    if(led_status){
                        s = "H";
                    } else {
                        s = "L";
                    }
                    URL url = new URL("http://172.20.10.10/" + s);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    //add request header
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                    int responseCode = conn.getResponseCode();
                    System.out.println("\nSending 'GET' request to URL : " + url);
                    System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //print result
                    System.out.println(response.toString());
                    TextView led_val = (TextView)findViewById(R.id.led_val);
                    led_val.setText(Boolean.toString(led_status));

//                    Switch sth = (Switch)popupview_light.findViewById(R.id.led_val);
//
//                    JSONObject jsonParam = order;
//                    jsonParam.getJSONObject("plant_info").remove("is_led_active");
//                    jsonParam.getJSONObject("plant_info").remove("device_token");
//                    jsonParam.getJSONObject("plant_info").put("is_led_active", sth.isChecked());
//                    jsonParam.getJSONObject("plant_info").put("device_token", "none");
////                    jsonParam.put("timestamp", 1488873360);
////                    jsonParam.put("uname", message.getUser());
////                    jsonParam.put("message", message.getMessage());
////                    jsonParam.put("latitude", 0D);
////                    jsonParam.put("longitude", 0D);

//                    Log.i("JSON", jsonParam.toString());
//                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
//                    os.writeBytes(jsonParam.toString());
//
//                    os.flush();
//                    os.close();
//
//                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
//                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


}
