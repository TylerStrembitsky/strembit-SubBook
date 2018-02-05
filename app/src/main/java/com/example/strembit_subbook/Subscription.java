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

import java.io.Serializable;

/**
 * Subscription class creates a Subscription object. These objects are
 * passed to JSON to be stored internally.
 *
 * Tyler Strembitsky
 * CCID: strembit
 * Student ID: 1390996
 *
 * @Author Tyler Strembitsky
 * @version 1.0 - 05/02/2018
 */
public class Subscription implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String date;
    private String price;
    private String comment;

    /**
     *
     * @param name
     * @param date
     * @param price
     * @param comment
     */
    public Subscription(String name, String date, String price, String comment) {
        this.name = name;
        this.date = date;
        this.price = price;
        this.comment = comment;

    }

    /**
     *
     * @return name
     */
    public String getName() {
        if (name == null) return null;
        return name;
    }

    /**
     *
     * @return date
     */
    public String getDate() {
        if (date == null) return null;
        return date;
    }

    /**
     *
     * @return price
     */
    public String getPrice() {
        if (price == null) return null;
        return price;
    }

    /**
     *
     * @return comment
     */
    public String getComment() {
        if (comment == null) return null;
        return comment;
    }

    /**
     *
     * @param name name to set
     */
    public void setName(String name) {

        this.name = name;
    }

    /**
     *
     * @param date to set
     */
    public void setDate(String date) {

        this.date = date;
    }

    /**
     *
     * @param price to set
     */
    public void setPrice(String price) {

        this.price = price;
    }

    public void setComment(String comment) {

        this.comment = comment;
    }

    /**
     *
     * @return formatted string for display in MainActivity
     */
    @Override
    public String toString() {

        return ("Subscription: " + this.name + "\n" +
                "Date: " + this.date + "\n" +
                "Price: $" + this.price + "\n" +
                "Comment: " + this.comment);
    }

}
