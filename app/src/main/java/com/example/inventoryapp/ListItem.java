package com.example.inventoryapp;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListItem {
    Connection connect;

    public List<Map<String, String>> getList(){
        List<Map<String, String>> data = null;
        data = new ArrayList<Map<String, String>>();
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            StrictMode.ThreadPolicy a = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(a);
            Connection c = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.103;databaseName=Inventory;user=;password=");
            Statement s = c.createStatement();
            String sqlStatement = "SELECT p_name, instock, p_id from product ORDER BY p_name ASC;";
            ResultSet r = s.executeQuery(sqlStatement);
            while(r.next()){
                Map<String, String> dataValues = new HashMap<String, String>();
                dataValues.put("Product Name", r.getString(1));
                dataValues.put("In Stock", "In stock: " + r.getString(2) + "\t");
                dataValues.put("Product ID", r.getString(3));
                data.add(dataValues);
            }
            c.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }

}
