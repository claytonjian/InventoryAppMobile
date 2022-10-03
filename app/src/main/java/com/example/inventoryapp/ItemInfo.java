package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ItemInfo extends AppCompatActivity {
    String p_id;
    int instock, restock, kit;
    NumberPicker instockNP;
    NumberPicker restockNP;
    Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        p_id = getIntent().getStringExtra("PRODUCT_ID");
        instockNP = findViewById(R.id.instockNP);
        restockNP = findViewById(R.id.restockNP);
        getItemInfo();
        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // remove "Barcode: " from p_id
                p_id = p_id.substring(9);
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    StrictMode.ThreadPolicy a = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(a);
                    Connection c = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.103;databaseName=Inventory;user=;password=");
                    Statement s = c.createStatement();
                    String sqlStatement = "UPDATE product SET instock = " + instockNP.getValue() + ", restock= " + restockNP.getValue() + " WHERE p_id = '" + p_id + "'";
                    s.execute(sqlStatement);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                // add to edited table
                int difference = (instockNP.getValue() - instock);

                if(difference != 0) {
                    try {
                        Class.forName("net.sourceforge.jtds.jdbc.Driver");
                        StrictMode.ThreadPolicy a = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(a);
                        Connection c = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.103;databaseName=Inventory;user=;password=");
                        Statement s = c.createStatement();
                        String sqlStatement = "INSERT INTO edited VALUES('" + p_id + "', " + difference +  ", GETDATE());";
                        s.execute(sqlStatement);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast t = Toast.makeText(getApplicationContext(), "Updated quantities!", Toast.LENGTH_SHORT);
                t.show();
                finish();
            }
        });
    }

    public void getItemInfo() {
        String p_name = "";
        instock = 0; restock = 0;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            StrictMode.ThreadPolicy a = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(a);
            Connection c = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.103;databaseName=Inventory;user=;password=");
            Statement s = c.createStatement();
            String sqlStatement = "SELECT p_id, p_name, instock, restock, kit from product WHERE p_id = '" + p_id + "';";
            ResultSet r = s.executeQuery(sqlStatement);
            while (r.next()) {
                p_id = "Barcode: " + r.getString(1);
                p_name = r.getString(2);
                instock = r.getInt(3);
                restock = r.getInt(4);
                kit = r.getInt(5);
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView pidTV = (TextView) this.findViewById(R.id.item_p_id);
        TextView pnameTV = (TextView) this.findViewById(R.id.item_p_name);
        pidTV.setText(p_id);
        pnameTV.setText(p_name);
        if(kit == 1){
            updateButton.setVisibility(View.INVISIBLE);
            instockNP.setVisibility(View.INVISIBLE);
            restockNP.setVisibility(View.INVISIBLE);
        }
        else{
            instockNP.setMinValue(0);
            instockNP.setMaxValue(1000);
            instockNP.setWrapSelectorWheel(false);
            instockNP.setValue(instock);
            restockNP.setMinValue(0);
            restockNP.setMaxValue(1000);
            restockNP.setWrapSelectorWheel(false);
            restockNP.setValue(restock);
        }
    }
}