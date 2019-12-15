
package com.thegavners.axile;



//Created By: Carlos Mbendera
// Copyright Carlos Mbenderea

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//This activity allows Users to view all the active streams for a specific game
//TODO add your client ID to all API Calls

public class GameFeed extends AppCompatActivity {


    static Drawable eye;
    private final List<FeedRow> listFeedRows = new ArrayList<>();
    private FeedListViewAdapter feedListViewAdapter;

    // These Array Lists are used to get the details of the item clicked in the list view.
    List channelArray = new ArrayList();
    List gameArray = new ArrayList();
    List idArray = new ArrayList();
    List logoArray = new ArrayList();
    List viewerArray = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_feed);

        eye = getResources().getDrawable(R.drawable.views);

        // Gets the title of game selected from the previous activity
        Intent intent = getIntent();
        final String gameTitle = intent.getStringExtra("Game Title");

        //An API call, executed on a different thread, to get a list of streams for the game selected
        new GetStreams().execute("https://api.twitch.tv/kraken/streams/?game=" +gameTitle+"&limit=10");

        setTitle(gameTitle);

        ListView feedListView = findViewById(R.id.gameFeedListView);
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

    }

    class GetStreams extends AsyncTask<String, Void, String>{

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
                connection.setRequestProperty("Client-ID:","YOUR CLIENT ID");

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

            ProgressBar progressBar = findViewById(R.id.progressBarGameFeed);
            progressBar.setVisibility(View.INVISIBLE);

            Log.i("Feed Api", "Post Execution started");

            if(result == null){
                Toast.makeText(GameFeed.this, "Can't get feed.", Toast.LENGTH_SHORT).show();
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

}



