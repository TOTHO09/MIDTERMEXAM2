package com.example.martino.midtermexam;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import static com.example.martino.midtermexam.HttpUtils.getResponse;

public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://joseniandroid.herokuapp.com/api/books";

    // JSON Node names
    private static final String TAG_BOOKTITLE = "title";
    private static final String TAG_ISREAD = "isRead";

    // contacts JSONArray
    JSONArray title = null;
    TextView tvBooktitle;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> bookList;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvBooktitle = (TextView) findViewById(R.id.bookname);
        bookList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();


        // Calling async task to get json
        new GetBooks().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetBooks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String bookname;
            String isRead ;

            String jsonStr = getResponse(url, "GET");
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    for (int n = 0; n < jsonArray.length(); n++) {
                        JSONObject obj = jsonArray.getJSONObject(n);
                         bookname = obj.getString(TAG_BOOKTITLE);
                         isRead = obj.getString(TAG_ISREAD);
                        // adding each child node to HashMap key => value
                        HashMap<String, String> book = new HashMap<String, String>();
                        book.put(TAG_BOOKTITLE, bookname);
                        book.put(TAG_ISREAD, isRead);
                        bookList.add(book);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);



            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, bookList,
                    R.layout.list_item, new String[]{TAG_BOOKTITLE,
            }, new int[]{R.id.bookname,
                   });
            setListAdapter(adapter);
        }

    }

}