package com.thegavners.axile;



//Created By: Carlos Mbendera
// Copyright Carlos Mbenderea

import android.content.Intent;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Discover extends AppCompatActivity {

 // This class the calls the API to get a list of the Top Games being played right now.
//TODO add your client ID to all API Calls
    private final List<DiscoverRow> listDiscoverRows = new ArrayList<>();
    private DiscoverListViewAdapter listViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

         new GetAPIData().execute("https://api.twitch.tv/kraken/games/top");

        setTitle("Discover");

        ListView DiscoverListView = findViewById(R.id.dicoveryList);
        listViewAdapter = new DiscoverListViewAdapter(getApplication(), listDiscoverRows);
        DiscoverListView.setAdapter(listViewAdapter);

        DiscoverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showGameDetails = new Intent(getApplicationContext(), GameFeed.class);
                showGameDetails.putExtra("Game Title", listDiscoverRows.get(position).getGameTitle());
                showGameDetails.putExtra("Game Id", listDiscoverRows.get(position).getGameId());
                showGameDetails.putExtra("Game Cover", listDiscoverRows.get(position).getCoverImageURL());

                startActivity(showGameDetails);
            }
        });

      }

      class GetAPIData extends AsyncTask<String, Void,String>{

          @Override
          protected String doInBackground(String... urls) {

              Log.i("Discover API", " Async has started");

              String result ;
              URL url;
              HttpURLConnection connection ;

              try{

                  url = new URL( urls[0]);

                  connection = (HttpURLConnection) url.openConnection();

                  //Set your Client ID here
                  connection.setRequestProperty("Client-ID:","Your Client ID ");

                 connection.connect();

                  BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                  StringBuilder sb = new StringBuilder();
                  String line;
                  while ((line = br.readLine()) != null) {
                      sb.append(line).append("\n");
                  }
                  br.close();

                  result = sb.toString();

                  Log.i("Discover API", "Success. \n The result is \n" + result);

                  return result;


              }
              catch (Exception e){
                  Log.i("Discover API", "FAILED \n" + e.getMessage());
                  return null;
              }


          }

          @Override
          protected void onPostExecute(String result) {
              super.onPostExecute(result);

              if (result == null){
                  Toast.makeText(Discover.this, "Can't get feed.", Toast.LENGTH_SHORT).show();

              }

              ProgressBar progressBar = findViewById(R.id.discoverProgressBar);
              progressBar.setVisibility(View.INVISIBLE);

              Log.i("Discover API ", "Post execution has began");

              try {

                Log.i("Discover API", "JSON OBJECT Interaction");

                  JSONObject object = new JSONObject(result);

                  JSONArray jsonArray = object.getJSONArray("top");

                  for(int i = 0 ; i < jsonArray.length(); i++){

                      Log.i("Discover API", "List Items are being called.");

                      JSONObject jsonPart = jsonArray.getJSONObject(i).getJSONObject("game");

                      listDiscoverRows.add(new DiscoverRow(
                              jsonPart.getString("name"),
                              jsonPart.getString("_id"),
                              jsonPart.getJSONObject("box").getString("large")

                      ));

                  }
                    listViewAdapter.notifyDataSetChanged();
              }
              catch (Exception e){
                  e.printStackTrace();
              }
          }
      }

}


// The two class below allow a custom List VIew to be used.

class DiscoverRow {

    private String gameTitle;
    private String gameId;
    private String coverImageURL;




    DiscoverRow( String rowGameTitle, String rowGameId ,String rowCoverImageURL) {

        gameTitle = rowGameTitle;
        gameId = rowGameId;
        coverImageURL = rowCoverImageURL;

    }

    String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    String getCoverImageURL() {
        return coverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        this.coverImageURL = coverImageURL;
    }




}

class DiscoverListViewAdapter extends BaseAdapter {

    private final Context context;
    private final List<DiscoverRow> discoverRowList;

    DiscoverListViewAdapter(Context context, List<DiscoverRow> discoverRowList) {

        this.context = context;
        this.discoverRowList = discoverRowList;
    }

    @Override
    public int getCount() {
        return discoverRowList.size();
    }

    @Override
    public Object getItem(int position) {
        return discoverRowList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        DiscoverListViewAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.dicover_list_item, null);

            viewHolder = new DiscoverListViewAdapter.ViewHolder();

            viewHolder.gameCover = convertView.findViewById(R.id.cover);

            viewHolder.gameTitle = convertView.findViewById(R.id.gameTitle);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (DiscoverListViewAdapter.ViewHolder) convertView.getTag();

        }
        DiscoverRow discoverRow = discoverRowList.get(position);

          Picasso.get()
                .load(discoverRow.getCoverImageURL())
              .placeholder(R.color.colorAccent)
              .into(viewHolder.gameCover);


        viewHolder.gameTitle.setText(discoverRow.getGameTitle());



        return convertView;
    }

    class ViewHolder {

        ImageView gameCover;
        TextView gameTitle;

    }
}

