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

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 *
 * Tyler Strembitsky
 * CCID: strembit
 * Student ID: 1390996
 *
 * @Author Tyler Strembitsky
 * @version 1.0 - 05/02/2018
 */
public class NewSubscriptionActivity extends AppCompatActivity {

    private static final String ADD_SUB = "com.example.ADD_SUB";
    private EditText subName;
    private EditText subDate;
    private EditText subPrice;
    private EditText subComment;
    private int day, month, year;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subscription);

        subName = (EditText) findViewById(R.id.subName);
        subDate = (EditText) findViewById(R.id.subDate);
        subPrice = (EditText) findViewById(R.id.subPrice);
        subComment = (EditText) findViewById(R.id.subComment);
        Button addButton = findViewById(R.id.addButton);

        subDate.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                Calendar mCurrentDate = Calendar.getInstance();
                year = mCurrentDate.get(Calendar.YEAR);
                month = mCurrentDate.get(Calendar.MONTH);
                day = mCurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(NewSubscriptionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    /**
                     *
                     * Date format ensures that the date is provided in a YYYY-MM-DD format,
                     * complete with 0s to ensure that the default of YYYY-M-D is avoided.
                     *
                     * Implemented from the following code:
                     * URL: https://stackoverflow.com/questions/24273935/add-0-to-month-i-e-1-01/
                     * Date accessed: 02/02/2018
                     * Author: Subash Poudel
                     *
                     * @param datePicker
                     * @param i Year
                     * @param i1 Month
                     * @param i2 Day
                     */
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        i1 = i1 + 1;
                        String fixedMonth = "" + i1;
                        String fixedDay = "" + i2;
                        if (i1 < 10) {
                            fixedMonth = "0" + i1;
                        }
                        if (i2 < 10) {
                            fixedDay = "0" + i2;
                        }

                        subDate.setText(i + "-" + fixedMonth + "-" + fixedDay);
                    }
                }, year, month, day);
                mDatePicker.show();
            }
        });

        /**
         *
         * This code sets up an alert dialog to ensure that continuation is not possible
         * unless the specified fields are completed.
         */
        final AlertDialog.Builder error = new AlertDialog.Builder(NewSubscriptionActivity.this);
        error.setCancelable(true);
        error.setTitle("Error!");
        error.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            /**
             *
             * @param dialogInterface
             * @param i
             */
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        /**
         *
         * This code determines the function of the "add" button
         */
        addButton.setOnClickListener(new View.OnClickListener(){
            /**
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                String name = subName.getText().toString();
                String date = subDate.getText().toString();
                String price = subPrice.getText().toString();
                String comment = subComment.getText().toString();

                if (TextUtils.isEmpty(subName.getText()) || (TextUtils.isEmpty(subDate.getText()) || (TextUtils.isEmpty(subPrice.getText())))){
                    error.setMessage("You must include a name, date, and price. Comment is optional.");
                    error.show();
                }
                else if (name.length() > 20){
                    error.setMessage("The name of the subscription cannot be longer than 20 characters. Please shorten it.");
                    error.show();
                }
                else if (comment.length() > 30){
                    error.setMessage("A subscription's comment cannot be longer than 30 characters. Please shorten it.");
                    error.show();
                }
                else{
                    Subscription newSubscription = new Subscription(name, date, formatDecimal(price), comment);
                    System.out.println("HELLO!");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    setResult(1, intent);
                    intent.putExtra(ADD_SUB, newSubscription);
                    finish();
                }

            }
        });

    }

    /**
     *
     * @param value
     * @return returns a properly formatted currency string
     */
    public static String formatDecimal(String value) {
        DecimalFormat currency = new DecimalFormat("###,##0.00");
        String number = String.valueOf(currency.format(Double.valueOf(value)));
        return number;
    }
}
