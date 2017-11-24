package com.example.admin.materialdesignrecyclerview;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "https://api.androidhive.info/json/contacts.json";
    private RecyclerView mRecyclerView;
    private ArrayList<Contact> mArrayList;
    private ContactAdapter mAdapter;

    /*private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Contact> contactList;
    private ContactAdapter mAdapter;
    private SearchView searchView;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        initViews();
        new loadJson().execute();

    }
    private void initViews() {
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

    }
    private class loadJson extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProgressDialog pd = new ProgressDialog(MainActivity.this);
            pd.setTitle("Json Data");
            pd.setMessage("Please wait");
            pd.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            OkHttpClient client = new OkHttpClient();
            Request req = new Request.Builder().url(BASE_URL).build();

            try {
                Response response = client.newCall(req).execute();
                mArrayList = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(response.body().string());
                    for (int i = 0 ; i<array.length() ; i++)
                    {
                        Contact androidVersion = new Contact();

                        JSONObject obj  = array.getJSONObject(i);
                        String name = obj.getString("name");
                        String image = obj.getString("image");
                        String phone = obj.getString("phone");

                        androidVersion.setName(name);
                        androidVersion.setImage(image);
                        androidVersion.setPhone(phone);
                        Log.i("tag", name+"");

                        mArrayList.add(androidVersion);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return mArrayList;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Failed";
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            mArrayList = (ArrayList<Contact>) o;
            mAdapter = new ContactAdapter(mArrayList,MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}
