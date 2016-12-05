package com.fishe.wut2dodemo.logic.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fishe.wut2dodemo.R;

import java.util.ArrayList;

public class QuestionUser extends AppCompatActivity {

    public ArrayList<String> questionList;
    public ArrayList<String> ansList;

    public String qn1 = "What type of activities do you prefer";
//    public String qn2 = "Please choose your top 3 categories";

    public String ans1 = "Indoor";
    public String ans2 = "Outdoor";


    TextView txtQuestion;
    RadioButton rda, rdb;
    Button butNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_user);

        txtQuestion=(TextView)findViewById(R.id.textView1);
        rda=(RadioButton)findViewById(R.id.radio0);
        rdb=(RadioButton)findViewById(R.id.radio1);
        butNext=(Button)findViewById(R.id.button1);

        questionList = new ArrayList<String>();
        questionList.add(qn1);

        ansList = new ArrayList<String>();

        ansList.add(ans1);
        ansList.add(ans2);
        setQuestionView();

        butNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup grp=(RadioGroup)findViewById(R.id.radioGroup1);
                RadioButton answer=(RadioButton)findViewById(grp.getCheckedRadioButtonId());
                Toast.makeText(getApplicationContext(), answer.getText(),Toast.LENGTH_SHORT).show();
                QuestionSharedPreference.setPreference(getApplicationContext(), (String) answer.getText());

                finish();
                Intent i = new Intent(getApplicationContext(), QuestionUser2.class);
                i.putExtra("result", answer.getText());

                startActivity(i);
            }
        });
    }

    private void setQuestionView(){
        txtQuestion.setText(questionList.get(0));
        rda.setText(ansList.get(0));
        rdb.setText(ansList.get(1));
    }



}
