package com.fishe.wut2dodemo.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fishe.wut2dodemo.R;

import java.util.ArrayList;

public class QuestionUser2 extends AppCompatActivity {
    public ArrayList<String> questionList;
    public ArrayList<String> ansList;

    public String qn1 = "Please choose your favourite category";
    public String qn2 = "Select another here";
    public String ans1 = "Karaoke";
    public String ans2 = "Game Cafe";
    public String ans3 = "Board Games";
    public String ans4 = "Gym";
    public String ans5 = "Beach";
    public String ans6 = "Zoo";
    public String ans7 = "Theme Park";
    public String ans8 = "Aquarium";

    TextView txtQuestion, txtQuestion2;
    RadioButton rda,rdb,rdc,rdd,rde,rdf,rdg,rdh;
    Button butNext;

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_user2);

        Intent receive = getIntent();
        result = receive.getStringExtra("result");


        questionList = new ArrayList<String>();
        questionList.add(qn1);
        questionList.add(qn2);
        ansList = new ArrayList<String>();
            ansList.add(ans1);
            ansList.add(ans2);
            ansList.add(ans3);
            ansList.add(ans4);
            ansList.add(ans5);
            ansList.add(ans6);
            ansList.add(ans7);
            ansList.add(ans8);


        txtQuestion=(TextView)findViewById(R.id.textView1);
        txtQuestion2=(TextView)findViewById(R.id.textView2);
        rda=(RadioButton)findViewById(R.id.radio0);
        rdb=(RadioButton)findViewById(R.id.radio1);
        rdc=(RadioButton)findViewById(R.id.radio2);
        rdd=(RadioButton)findViewById(R.id.radio3);
        rde=(RadioButton)findViewById(R.id.radio4);
        rdf=(RadioButton)findViewById(R.id.radio5);
        rdg=(RadioButton)findViewById(R.id.radio6);
        rdh=(RadioButton)findViewById(R.id.radio7);

        butNext=(Button)findViewById(R.id.button1);
        setQuestionView();

        butNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup grp1=(RadioGroup)findViewById(R.id.radioGroup1);
                RadioButton answer=(RadioButton)findViewById(grp1.getCheckedRadioButtonId());

                QuestionSharedPreference.setFavourite(getApplicationContext(), (String) answer.getText());

                RadioGroup grp2=(RadioGroup)findViewById(R.id.radioGroup2);
                RadioButton answer2=(RadioButton)findViewById(grp2.getCheckedRadioButtonId());

                QuestionSharedPreference.setFavourite(getApplicationContext(), (String) answer2.getText());


                finish();
                Intent i = new Intent(getApplicationContext(), QuestionUser3.class);

                startActivity(i);
            }
        });
    }
    private void setQuestionView(){
        txtQuestion.setText(questionList.get(0));
        rda.setText(ansList.get(0));
        rdb.setText(ansList.get(1));
        rdc.setText(ansList.get(2));
        rdd.setText(ansList.get(3));
        rde.setText(ansList.get(4));
        rdf.setText(ansList.get(5));
        rdg.setText(ansList.get(6));
        rdh.setText(ansList.get(7));
    }
}

