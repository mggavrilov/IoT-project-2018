package com.example.grade.esp8266app;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private ToggleButton button;
    //private TextView text;
    private TextView textTemp;
    private TextView textPres;
    private TextView textHumi;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.button = findViewById(R.id.button);
        //this.text = findViewById(R.id.text);
        this.textTemp = findViewById(R.id.textTemp);
        this.textPres = findViewById(R.id.textPres);
        this.textHumi = findViewById(R.id.textHumi);

        this.handler = new Handler();

        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "Button clicked");

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url ="http://192.168.4.1";

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                //text.setText("Response is: " + response);
                                button.setChecked(response.equals("on"));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("asd", error.getMessage());
                        //text.setText("That didn't work!");
                    }
                });

                queue.add(stringRequest);

            }
        });


        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(":)", "Tick");

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String tempUrl = "http://192.168.4.1/temp";

                StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                final String[] values = response.split(",");
                                Log.d(":)", "Boom! " + Thread.currentThread().getName());

                                runOnUiThread(new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.d(":)", "Data! " + Thread.currentThread().getName());
                                        textTemp.setText(values[0] + "°C");
                                        textPres.setText(values[1] + "hPa");
                                        textHumi.setText(values[2] + "░");
                                    }
                                }));

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("asd", "Error");
//                        text.setText("That didn't work!");
                    }
                });
                queue.add(stringRequest);


                String lampUrl = "http://192.168.4.1/lamp";

                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, lampUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {

                                Log.d(":)", "Boom! " + Thread.currentThread().getName());

                                runOnUiThread(new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.d(":)", "Data! " + Thread.currentThread().getName());
//                                        text.setText("Response is: " + response);
                                        button.setChecked(!response.equals("on"));

                                    }
                                }));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("asd", "error!");
//                        text.setText("That didn't work 2 !");
                    }
                });
                queue.add(stringRequest2);

                handler.postDelayed(this, 1000);
            }
        });

    }
}
