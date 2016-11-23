package com.fishe.wut2dodemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionSharedPreference {

    static final String preference = "prefer";
    static final String favourite = "favourite";

    static final String ans1 = "Karaoke";//
    static final String ans2 = "Game Cafe";//
    static final String ans3 = "Board Games";//
    static final String ans4 = "Gym";//
    static final String ans5 = "Swim";//
    static final String ans6 = "Bowling";//
    static final String ans7 = "Laser Tag";//
    static final String ans8 = "Escape Room";//
    static final String ans9 = "Cinema";//
    static final String ans10 = "Heritage";//
    static final String ans11 = "Arcade";//
    static final String ans12 = "Theme Park";//
    static final String ans13 = "Badminton";//
    static final String ans14 = "Zoo";//
    static final String ans15 = "Museum";//
    static final String ans16 = "Beach";//
    static final String ans17 = "Park";//
    static final String ans18 = "Aquarium";//
    static final String ans19 = "Paintball";//



    static ArrayList<String> ansList = new ArrayList<String>();
    static final int num = 3;

    public static int pref1 = 1;
    public static int pref2 = 1;
    public static int pref3 = 1;
    public static int pref4 = 1;
    public static int pref5 = 1;
    public static int pref6 = 1;
    public static int pref7 = 1;
    public static int pref8 = 1;
    public static int pref9 = 1;
    public static int pref10 = 1;
    public static int pref11 = 1;
    public static int pref12 = 1;
    public static int pref13 = 1;
    public static int pref14 = 1;
    public static int pref15 = 1;
    public static int pref16 = 1;
    public static int pref17 = 1;
    public static int pref18 = 1;
    public static int pref19 = 1;
    public static int pref20 = 1;
    public static int pref21 = 1;
    public static int pref22 = 1;
    public static int pref23 = 1;
    public static int pref24 = 1;


    static ArrayList<Integer> pref = new ArrayList<Integer>();


    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setPreference(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(preference, userName);

        editor.commit();
    }

    public static String getPreference(Context ctx)
    {
        return getSharedPreferences(ctx).getString(preference, "");
    }

    public static void setFavourite(Context ctx, String category)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(favourite, category);
        switch (category){
            case ans1:{
                pref1 += num;
                editor.putInt(ans1,pref1);
                editor.commit();
                Log.i("Item weight ", String.valueOf(getPref(ctx,ans1)));
                Log.i("Pref weight ", String.valueOf(pref1));
                break;
            }
            case ans2:{
                pref2 += num;
                editor.putInt(ans2,pref2);
                editor.commit();
                break;
            }
            case ans3:{
                pref3 += num;
                editor.putInt(ans3,pref3);
                editor.commit();
                break;
            }
            case ans4:{
                pref4 += num;
                editor.putInt(ans4,pref4);
                editor.commit();
                break;
            }
            case ans5:{
                pref5 += num;
                editor.putInt(ans5,pref5);
                editor.commit();
                break;
            }
            case ans6:{
                pref6 += num;
                editor.putInt(ans6,pref6);
                editor.commit();
                break;
            }
            case ans7:{
                pref7 += num;
                editor.putInt(ans7,pref7);
                editor.commit();
                break;
            }
            case ans8: {
                pref8 += num;
                editor.putInt(ans8,pref8);
                editor.commit();
                break;
            }
            case ans9:{
                pref9 += num;
                editor.putInt(ans9,pref9);
                editor.commit();
                break;
            }
            case ans10:{
                pref10 +=num;
                editor.putInt(ans10,pref10);
                editor.commit();
                break;
            }
            case ans11:{
                pref11 +=num;
                editor.putInt(ans11,pref11);
                editor.commit();
                break;
            }
            case ans12:{
                pref12 +=num;
                editor.putInt(ans12,pref12);
                editor.commit();
                break;
            }
            case ans13:{
                pref13 +=num;
                editor.putInt(ans13,pref13);
                editor.commit();
                break;
            }
            case ans14:{
                pref14 +=num;
                editor.putInt(ans14,pref14);
                editor.commit();
                break;
            }
            case ans15:{
                pref15 +=num;
                editor.putInt(ans15,pref15);
                editor.commit();
                break;
            }
            case ans16:{
                pref16 +=num;
                editor.putInt(ans16,pref16);
                editor.commit();
                break;
            }
            case ans17:{
                pref17 +=num;
                editor.putInt(ans17,pref17);
                editor.commit();
                break;
            }
            case ans18:{
                pref18 +=num;
                editor.putInt(ans18,pref18);
                editor.commit();
                break;
            }
            case ans19:{
                pref19 +=num;
                editor.putInt(ans19,pref19);
                editor.commit();
                break;
            }

            default: break;
        }
    }

    public static String getFavourite(Context ctx)
    {
        return getSharedPreferences(ctx).getString(favourite, "");
    }

    public static void setDefault(Context ctx) //reset value to 1
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();

        pref1 = 1;
        pref2 = 1;
        pref3 = 1;
        pref4 = 1;
        pref5 = 1;
        pref6 = 1;
        pref7 = 1;
        pref8 = 1;
        pref9 = 1;
        pref10 = 1;
        pref11 = 1;
        pref12 = 1;
        pref13 = 1;
        pref14 = 1;
        pref15 = 1;
        pref16 = 1;
        pref17 = 1;
        pref18 = 1;
        pref19 = 1;
        pref20 = 1;
        pref21 = 1;
        pref22 = 1;

        editor.putInt(ans1, pref1);
        editor.putInt(ans2, pref2);
        editor.putInt(ans3, pref3);
        editor.putInt(ans4, pref4);
        editor.putInt(ans5, pref5);
        editor.putInt(ans6, pref6);
        editor.putInt(ans7, pref7);
        editor.putInt(ans8, pref8);
        editor.putInt(ans9, pref9);
        editor.putInt(ans10, pref10);
        editor.putInt(ans11, pref11);
        editor.putInt(ans12, pref12);
        editor.putInt(ans13, pref13);
        editor.putInt(ans14, pref14);
        editor.putInt(ans15, pref15);
        editor.putInt(ans16, pref16);
        editor.putInt(ans17, pref17);
        editor.putInt(ans18, pref18);
        editor.putInt(ans19, pref19);


        editor.commit();
    }

    //increment pref of category when user selects it
    public static void increasePref(Context ctx, String pref)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        Log.i("Item increasing ",pref);
        switch (pref) {
            case ans1: {
                pref1 ++;
                editor.putInt(ans1, pref1);
                editor.commit();
                Log.i("Item weight ", String.valueOf(getPref(ctx,ans1)));
                Log.i("Pref weight ", String.valueOf(pref1));
                break;
            }
            case ans2: {
                pref2 ++;
                editor.putInt(ans2, pref2);
                editor.commit();
                Log.i("Item weight ", String.valueOf(pref2));
                break;
            }
            case ans3: {
                pref3 ++;
                editor.putInt(ans3, pref3);
                editor.commit();
                Log.i("Item weight ", String.valueOf(pref3));
                break;
            }
            case ans4: {
                pref4 ++;
                editor.putInt(ans4, pref4);
                editor.commit();
                break;
            }
            case ans5: {
                pref5 ++;
                editor.putInt(ans5, pref5);
                editor.commit();
                break;
            }
            case ans6: {
                pref6 ++;
                editor.putInt(ans6, pref6);
                editor.commit();
                break;
            }
            case ans7: {
                pref7 ++;
                editor.putInt(ans7, pref7);
                editor.commit();
                break;
            }
            case ans8: {
                pref8 ++;
                editor.putInt(ans8, pref8);
                editor.commit();
                break;
            }
            case ans9:{
                pref9 ++;
                editor.putInt(ans9,pref9);
                editor.commit();
                break;
            }
            case ans10:{
                pref10 ++;
                editor.putInt(ans10,pref10);
                editor.commit();
                break;
            }
            case ans11:{
                pref11 ++;
                editor.putInt(ans11,pref11);
                editor.commit();
                break;
            }
            case ans12:{
                pref12 ++;
                editor.putInt(ans12,pref12);
                editor.commit();
                break;
            }
            case ans13:{
                pref13 ++;
                editor.putInt(ans13,pref13);
                editor.commit();
                break;
            }
            case ans14:{
                pref14 ++;
                editor.putInt(ans14,pref14);
                editor.commit();
                break;
            }
            case ans15:{
                pref15 ++;
                editor.putInt(ans15,pref15);
                editor.commit();
                break;
            }
            case ans16:{
                pref16 ++;
                editor.putInt(ans16,pref16);
                editor.commit();
                break;
            }
            case ans17:{
                pref17 ++;
                editor.putInt(ans17,pref17);
                editor.commit();
                break;
            }
            case ans18:{
                pref18 ++;
                editor.putInt(ans18,pref18);
                editor.commit();
                break;
            }
            case ans19:{
                pref19 ++;
                editor.putInt(ans19,pref19);
                editor.commit();
                break;
            }

            default:
                break;
        }
    }

    public static int getPref(Context ctx, String ans)
    {
        return getSharedPreferences(ctx).getInt(ans, 0);
    }
    //return the most visited category
    public static String getMostFreq(Context ctx){
        String most = "";
        int max=0;
        ansList.add(ans1);
        ansList.add(ans2);
        ansList.add(ans3);
        ansList.add(ans4);
        ansList.add(ans5);
        ansList.add(ans6);
        ansList.add(ans7);
        ansList.add(ans8);
        ansList.add(ans9);

        for(int i = 0; i<9;i++){
            if(getPref(ctx,ansList.get(i))>max){
                max = getPref(ctx,ansList.get(i));
                most = ansList.get(i);
            }
        }
        return most;
    }
    //sum the total weight for use in randomsation
    public static int getSumWeight(Context ctx){
        int sum = 0;
        ansList.add(ans1);
        ansList.add(ans2);
        ansList.add(ans3);
        ansList.add(ans4);
        ansList.add(ans5);
        ansList.add(ans6);
        ansList.add(ans7);
        ansList.add(ans8);
        ansList.add(ans9);
        for(int i = 0; i<9;i++){
            sum += getPref(ctx,ansList.get(i));
        }
        return sum;
    }

}
