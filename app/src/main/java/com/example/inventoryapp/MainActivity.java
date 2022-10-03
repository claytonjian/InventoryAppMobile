package com.example.inventoryapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    SimpleAdapter listAdapter;
    SearchManager searchManager;
    SearchView searchView;
    View v;
    Intent goToItemInfo;
    int lastItemSelected;
    ArrayList<String> itemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CREATE", "Create");
        setContentView(R.layout.activity_main);
        goToItemInfo = new Intent(MainActivity.this, ItemInfo.class);
        getItemsList(this.findViewById(android.R.id.content));
        listView.setSelector(R.color.teal_200);
        listView.setTextFilterEnabled(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> obj = (HashMap<String, String>) listAdapter.getItem(position);
                String pid = (String) obj.get("Product ID");
                goToItemInfo.putExtra("PRODUCT_ID", pid);
                lastItemSelected = position;
                startActivity(goToItemInfo);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.options_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                listAdapter.getFilter().filter(s.toString());
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        getItemsList(this.findViewById(android.R.id.content));
        listView.setSelection(lastItemSelected);
    }

    public void getItemsList(View v){
        listView = (ListView) findViewById(R.id.listView);
        List<Map<String, String>> dataList = null;
        ListItem data = new ListItem();
        dataList = data.getList();

        String[] Fromw = {"Product Name", "In Stock", "Product ID"};
        int[] Tow = {R.id.p_name, R.id.instock, R.id.p_id};

        listAdapter = new SimpleAdapter(MainActivity.this, dataList, R.layout.listlayouttemplate, Fromw, Tow);
        listView.setAdapter(listAdapter);
    }
}