package com.thegavners.axile;

//Created By: Carlos Mbendera
// Copyright Carlos Mbenderea

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Hub extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final List<FeedRow> listFeedRows = new ArrayList<>();

    //This activity shows the top ongoing streams at the moment
    //TODO add your client ID to all API Calls

    // These Array Lists are used to get the details of the item clicked in the list view.

    List channelArray = new ArrayList();
    List gameArray = new ArrayList();
    List idArray = new ArrayList();
    List logoArray = new ArrayList();
    List viewerArray = new ArrayList();

    private FeedListViewAdapter feedListViewAdapter;
    private ProgressBar progressBar;

    private Button retry;
    private TextView tv ;

    public void Retry (View view){
        progressBar.setVisibility(View.VISIBLE);

        retry.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.INVISIBLE);

        new GetStreams().execute("https://api.twitch.tv/kraken/streams");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        progressBar = findViewById(R.id.progressBarHub);
        tv = findViewById(R.id.textView2);
        retry = findViewById(R.id.hubRetry);


        tv.setVisibility(View.INVISIBLE);
        retry.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        //An API call, executed on a different thread, to get a list of streams
        new GetStreams().execute("https://api.twitch.tv/kraken/streams");

        ListView feedListView = findViewById(R.id.hubListView);
        feedListViewAdapter = new FeedListViewAdapter(getApplication(), listFeedRows);
        feedListView.setAdapter(feedListViewAdapter);


        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showGameDetails = new Intent(getApplicationContext(), Stream.class);
                showGameDetails.putExtra("Channel", channelArray.get(position).toString());
                showGameDetails.putExtra("Game", gameArray.get(position).toString());
                showGameDetails.putExtra("ID", idArray.get(position).toString());
                showGameDetails.putExtra("Logo", logoArray.get(position).toString());
                showGameDetails.putExtra("Views", viewerArray.get(position).toString());

                startActivity(showGameDetails);

            }
        });

        Toolbar toolbar = findViewById(R.id.hubToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("My Feed");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    class GetStreams extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection;
            String result;

            try{
                //Establishing a connection with the URL executed in the onCreate Method
                url = new URL(urls[0]);

                connection = (HttpURLConnection) url.openConnection();

                //Setting the client ID for API call
                connection.setRequestProperty("Client-ID:","Your Client ID ");

                connection.connect();

                //Reads the result from connection
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();

                result = sb.toString();

                Log.i("Feed API", "Success. \n The result is \n" + result);

                return result;
            }

            catch(Exception e){

                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            progressBar.setVisibility(View.INVISIBLE);


            if (result == null){

                retry.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);

            }


            Log.i("Feed Api", "Post Execution started");

            if(result == null){
                Toast.makeText(getApplicationContext(), "Can't get feed.", Toast.LENGTH_SHORT).show();
            }

            try {

                // Since the result is a JSON object. It is appropriate to work it as a JSON rather than a String.
                JSONObject object = new JSONObject(result);

                //The result contains multiple JSON Object.Thus, a JSON array
                JSONArray array = object.getJSONArray("streams");

                //A loop that goes through the JSON array and adds the objects to the custom ListView Accordingly
                for (int i = 0; i < array.length(); i++){

                    Log.i("Feed API", "List Items are being called.");

                    JSONObject jsonPart = array.getJSONObject(i);

                    listFeedRows.add(new FeedRow(
                            jsonPart.getString("_id"),
                            jsonPart.getJSONObject("channel").getString("display_name"),
                            jsonPart.getJSONObject("channel").getString("logo"),
                            jsonPart.getJSONObject("preview").getString("large"),
                            jsonPart.getString("viewers")

                    ));

                    idArray.add(i, jsonPart.getString("_id"));
                    channelArray.add(i,  jsonPart.getJSONObject("channel").getString("name") );
                    gameArray.add(i, jsonPart.getJSONObject("channel").getString("game"));
                    logoArray.add(i,jsonPart.getJSONObject("channel").getString("logo") );
                    viewerArray.add(i,  jsonPart.getString("viewers"));

                }
                feedListViewAdapter.notifyDataSetChanged();
            }

            catch (Exception e){

                e.printStackTrace();
                Log.i("Feed APi", "Failed"+ e.getMessage());

            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Listener for Navigation Drawer clicks
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_discover) {
            Intent startDiscover = new Intent(getApplicationContext(), Discover.class);
            Hub.this.startActivity(startDiscover);


        } else if (id == R.id.nav_feedback) {


        } else if (id == R.id.nav_sign_out) {

        } 
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
