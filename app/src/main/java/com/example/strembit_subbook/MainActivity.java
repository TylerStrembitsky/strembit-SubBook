/**
 * CMPUT 301 Winter 2018
 *
 * Version 1.0
 *
 * 05/02/2018
 *
 * Copyright 2018 Tyler Strembitsky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF AY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.strembit_subbook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This is a subscription "book" app, made for Assignment 1 of CMPUT 301 in
 * Winter 2018 at the University of Alberta. The application is intended to
 * provide a simple interface for users to add, edit, and delete recurring
 * subscriptions (e.g. online subscriptions, bill payments, etc).
 * The app also displays the total monthly cost of the subscriptions.
 *
 * MainActivity.java is the main screen of the app, and also acts as the subscription list.
 *
 * The notion of passing objects back and forth between other classes/activities
 * (rather than calling saveData/loadData in other activities) was provided by
 * CMPUT 301 classmate/teammate Lucas Gauk. Some further discussion, regarding the layout/design
 * of the app, was had with CMPUT 301 classmate/teammate Christopher Wood.
 *
 * Tyler Strembitsky
 * CCID: strembit
 * Student ID: 1390996
 *
 * @Author Tyler Strembitsky
 * @version 1.0 - 05/02/2018
 *
 * @see Subscription
 *
 */
public class MainActivity extends AppCompatActivity {

    private static final String FILENAME = "file.sav";
    private static final String ADD_SUB = "com.example.ADD_SUB";
    private static final String EDIT_SUB = "com.example.EDIT_SUB";
    private static final int ADD_SUB_RESULT = 1;
    private static final int EDIT_SUB_RESULT = 2;
    private static final int DELETE_SUB_RESULT = 3;

    private int currentSub;

    private ListView oldSubsList;
    private ArrayList<Subscription> subbyList = new ArrayList<>();
    private ArrayAdapter<Subscription> adapter;
    private TextView subsTotal;

    private static DecimalFormat dollarAmount = new DecimalFormat("###,##0.00");

    /**
     * General state of MainActivity is determined here.
     *
     * Some educational assistance was received from the following tutorial:
     * URL: https://www.youtube.com/watch?v=dFlPARW5IX8&list=PLp9HFLVct_ZvMa7IVdQyUUyh8t2re9apm
     * Date accessed: 28/01/2018
     * Author: Bill Butterfield
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newSub = (Button) findViewById(R.id.newSub);
        oldSubsList = (ListView) findViewById(R.id.subListView);
        subsTotal = (TextView) findViewById(R.id.totalTextView);

        loadData();
        newSub.setOnClickListener(new View.OnClickListener() {

            /**
             * Button use, sending activity to NewSubscriptionActivity class.
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), NewSubscriptionActivity.class);
                startActivityForResult(startIntent, ADD_SUB_RESULT);
            }
        });

        oldSubsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /**
             * AdapterView position getter. Derived from a code snippet on the following page:
             * URL: https://stackoverflow.com/questions/20541821/get-listview-item-position-on-button-click
             * Date accessed: 01/02/2018
             * Author: Karoly Holczhauser
             */
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = adapterView.getPositionForView(view);
                Intent intent = new Intent(getApplicationContext(), EditSubscriptionActivity.class);
                intent.putExtra(EDIT_SUB, subbyList.get(position));
                currentSub = position;
                startActivityForResult(intent, 3);
            }
        });


    }

    /**
     * Receives the result of the NewSubscriptionActivity and EditSubscriptionActivity
     * classes, and edits the internal file accordingly. Adding, editing, and deleting
     * subscriptions is all handled in this method.
     *
     * @param request
     * @param result
     * @param intent
     */
    @Override
    protected void onActivityResult(int request, int result, Intent intent){
        if (request == ADD_SUB_RESULT) {
            try {
                Subscription newSubscription = (Subscription) intent.getSerializableExtra(ADD_SUB);
                subbyList.add(newSubscription);
                adapter.notifyDataSetChanged();
                saveData();
            }
            catch (Exception e) {
                return;
            }
        }
        if (result == EDIT_SUB_RESULT) {
            try {
                Subscription editableSubscrption = (Subscription) intent.getSerializableExtra(EDIT_SUB);
                subbyList.get(currentSub).setName(editableSubscrption.getName());
                subbyList.get(currentSub).setDate(editableSubscrption.getDate());
                subbyList.get(currentSub).setPrice(editableSubscrption.getPrice());
                subbyList.get(currentSub).setComment(editableSubscrption.getComment());
                adapter.notifyDataSetChanged();
                saveData();
            }
            catch (Exception e) {
                return;
            }
        }
        if (result == DELETE_SUB_RESULT) {
            try {
                subbyList.remove(currentSub);
                adapter.notifyDataSetChanged();
                saveData();
            }
            catch (Exception e) {
                return;
            }
        }
    }

    /**
     * Sets the adapter use, as well as refreshes the total monthly cost.
     */
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        adapter = new ArrayAdapter<>(this, R.layout.text_layout, subbyList);
        oldSubsList.setAdapter(adapter);
        sumOfSubs();
    }

    /**
     * Saves data to internal storage, using JSON. GSON seralizaes our Subscription object into JSON.
     * This method is heavily influenced by the following code:
     * URL: https://github.com/joshua2ua/lonelyTwitter
     * Date accessed: 29/01/2018
     * Author: Joshua Charles Campbell
     */
    private void saveData() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
            Gson gson = new Gson();
            gson.toJson(subbyList, out);
            out.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            subbyList = new ArrayList<Subscription>();
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Loads data from internal storage, from JSON. GSON deserializes this into our Subscription object.
     *
     * This method is heavily influenced by the following code:
     * URL: https://github.com/joshua2ua/lonelyTwitter
     * Date accessed: 29/01/2018
     * Author: Joshua Charles Campbell
     */
    private void loadData() {
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
			/*Taken from https://stackoverflow.com/questions/12384064/gson-convert-from-js
            24-02-2018*/
            Type listType = new TypeToken<ArrayList<Subscription>>(){}.getType();
            subbyList = gson.fromJson(in, listType);
            fis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            subbyList = new ArrayList<Subscription>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * This method is used to parse the price of a subscription to a
     * more readable, common form (e.g. $XXX.XX).
     */
    private void sumOfSubs(){
        double subSum = 0;
        for(Subscription s : subbyList){
            subSum += Double.parseDouble(s.getPrice());
        }
        subsTotal.setText("$" + dollarAmount.format(subSum));
    }
}
