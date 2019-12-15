package com.thegavners.axile;


//Created By: Carlos Mbendera
// Copyright Carlos Mbenderea

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Stream extends AppCompatActivity {


    // You don't need a client ID for this

    private TextView username;
    private ImageView profilephoto;
    private TextView views;
    private TextView title;

    String channel;
    String game;
    String id;
    String logo;
    String viewsString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);


        username = findViewById(R.id.usernameStream);
        profilephoto = findViewById(R.id.profilePhotoStream);
        views = findViewById(R.id.views);
        title = findViewById(R.id.titleStream);

        Intent intent = getIntent();

        channel = intent.getStringExtra("Channel");
        game = intent.getStringExtra("Game");
        id = intent.getStringExtra("ID");
        logo = intent.getStringExtra("Logo");
        viewsString = intent.getStringExtra("Views");


        setData();
    }
    private void setData() {

        //Since the Twitch API does not have a call to get the specific stream.
        // A web view is used with their Embed code to achieve this

        WebView streamWebView = findViewById(R.id.streamWebView);

        streamWebView.setWebViewClient(new WebViewClient());

        //The Twitch Embed uses JavaScript. Hence, we need to enable it inside the Web View.
        streamWebView.getSettings().setJavaScriptEnabled(true);

        channel.replaceAll("\\s+","");

        String twitchVideo = "<html><body><iframe src=\"https://player.twitch.tv/?channel="+channel+ "\" height=\"300\" width=\"400\"  frameborder=\"0\"  scrolling=\"no\"    </iframe></body></html>";

        streamWebView.loadData( twitchVideo, "text/html"  ,"UTF-8"        );

        ProgressBar progressBar = findViewById(R.id.progressBarStream);
        progressBar.setVisibility(View.INVISIBLE);

        title.setText(game);
        views.setText(viewsString);
        username.setText(channel);
        Picasso.get()
                .load(logo)
                .placeholder(R.color.colorAccent)
                .into(profilephoto);

        title.setVisibility(View.VISIBLE);
        views.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);
        profilephoto.setVisibility(View.VISIBLE);

    }

}
