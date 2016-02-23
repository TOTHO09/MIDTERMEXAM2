package com.example.martino.midtermexam;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://joseniandroid.herokuapp.com/api/books";

    // JSON Node names
    private static final String TAG_BOOKTITLE = "title";
    private static final String TAG_ISREAD = "isRead";

    // contacts JSONArray
    JSONArray title = null;
    TextView tv;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> bookList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView)findViewById(R.id.bookname);
        bookList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();


        // Calling async task to get json
        new GetBooks().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
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
            // Creating service handler class instance
            HttpUtils sh = new HttpUtils();

            // Making a request to url and getting response
            String jsonStr = sh.getResponse(url,"GET");

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    title = jsonObj.getJSONArray(TAG_BOOKTITLE);

                    for (int i = 0; i < title.length(); i++) {
                        JSONObject c = title.getJSONObject(i);
                        String bookname = c.getString(TAG_BOOKTITLE);
                        String isRead = c.getString(TAG_ISREAD);
                        // tmp hashmap for single contact
                        HashMap<String, String> book = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        book.put(TAG_BOOKTITLE, bookname);
                        book.put(TAG_ISREAD,isRead );
                        // adding contact to contact list
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
                    R.layout.list_item, new String[] { TAG_BOOKTITLE, TAG_ISREAD,
                     }, new int[] { R.id.bookname,
                    R.id.read});
            setListAdapter(adapter);
        }

    }

}