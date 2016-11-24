package com.fishe.wut2dodemo;

import android.content.Context;
import android.util.Log;

import com.fishe.wut2dodemo.user.QuestionSharedPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by fishe on 18/7/2016.
 */
public class RandomChoose {

    String category;
    ArrayList<String> categoryList;
    public String ans1 = "Karaoke";
    public String ans2 = "Game Cafe";
    public String ans3 = "Board Games";
    public String ans4 = "Gym";
    public String ans5 = "Swim";
    public String ans6 = "Bowling";
    public String ans7 = "Laser Tag";
    public String ans8 = "Escape Room";
    public String ans9 = "Cinema";
    public String ans10 = "Heritage";
    public String ans11 = "Arcade";
    public String ans12 = "Theme Park";
    public String ans13 = "Badminton";
    public String ans14 = "Zoo";
    public String ans15 = "Museum";
    public String ans16 = "Beach";
    public String ans17 = "Park";
    public String ans18 = "Aquarium";
    public String ans19 = "Paintball";


    private Context context;

    HashMap<String,Integer> map= new HashMap<String,Integer>();

    public RandomChoose(Context context){
        categoryList = new ArrayList<>();
        categoryList.add(ans1);
        categoryList.add(ans2);
        categoryList.add(ans3);
        categoryList.add(ans4);
        categoryList.add(ans5);
        categoryList.add(ans6);
        categoryList.add(ans7);
        categoryList.add(ans8);
        categoryList.add(ans9);
        /*
        categoryList.add(ans10);
        categoryList.add(ans11);
        categoryList.add(ans12);
        categoryList.add(ans14);
        categoryList.add(ans15);
        categoryList.add(ans16);
        categoryList.add(ans17);
        categoryList.add(ans18);
        categoryList.add(ans19);
        categoryList.add(ans20);
        categoryList.add(ans21);
        categoryList.add(ans22);
        */
        this.context=context;

        for(int i=0;i<9;i++){
            map.put(categoryList.get(i), QuestionSharedPreference.getPref(context,categoryList.get(i)));
            Log.i("Testing ", String.valueOf(map.get(categoryList.get(i))));
        }
    }

    public String getCategory(){
        Random r = new Random();
        int cat = r.nextInt(10);
        category = categoryList.get(cat);
        return category;
    }
    public String getRandomCategory(){
        //QuestionSharedPreference.setDefault(context);
        //shuffle the arraylist so not always constant deduction
        Collections.shuffle(categoryList);
        double totalWeight = (double)QuestionSharedPreference.getSumWeight(context);
        Log.i("Weight ", String.valueOf(totalWeight));
        int randomIdx = -1;
        double random = totalWeight * Math.random();
        Log.i("Random Max ", String.valueOf(random));
        for(int i = 0;i<categoryList.size();i++){
            random -= map.get(categoryList.get(i));
            Log.i("Random value ", String.valueOf(random));
            Log.i("Deducted ", String.valueOf(map.get(categoryList.get(i))));
            Log.i("Name ", categoryList.get(i));
            if(random < 0.0){
                randomIdx = i;
                break;
            }
        }
        return categoryList.get(randomIdx);
    }

}
