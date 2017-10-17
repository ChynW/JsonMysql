package com.example.asustest.jsonmysql;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private String TAG = MainActivity.class.getSimpleName();
    TextView tv1;
    TextView tv2;
    TextView tv3;
    private ProgressDialog pdialog;
   // private ListView lv;

    private static String Url = "https://api.myjson.com/bins/13ubuv";
    ArrayList<HashMap<String,String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1=(TextView) findViewById(R.id.textView2);
        tv2=(TextView) findViewById(R.id.textView3);
        tv3=(TextView) findViewById(R.id.textView4);
        contactList = new ArrayList<>();

        //lv = (ListView) findViewById(R.id.list);

        new GetContacts().execute();
    }



    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HTTPHandler sh = new HTTPHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr); //ERROR

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String train = c.getString("train_no");
                        String duration = c.getString("Duraion");
                        String name = c.getString("name");
                        String deptime = c.getString("departure_station");
                        String arrtime = c.getString("arrival_station");


                        // Phone node is JSON Object
                        //JSONObject phone = c.getJSONObject("phone");
                        //String mobile = phone.getString("mobile");
                        //String home = phone.getString("home");
                        //String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("train_no", train);
                        contact.put("Duraion", duration);
                        contact.put("name", name);
                        contact.put("departure_station", deptime);
                        contact.put("arrival_station", arrtime);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pdialog = new ProgressDialog(MainActivity.this);
            pdialog.setMessage("Please wait...");
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pdialog.isShowing())
                pdialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            tv1.setText(contactList.get(0).get("Duraion"));
            tv2.setText(contactList.get(0).get("arrival_station"));
            tv3.setText(contactList.get(0).get("departure_station"));

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"name", "departure_station",
                    "arrival_station", "duration"}, new int[]{R.id.textView2,
                    R.id.textView3, R.id.textView4});

            //lv.setAdapter(adapter);
        }
    }
}
