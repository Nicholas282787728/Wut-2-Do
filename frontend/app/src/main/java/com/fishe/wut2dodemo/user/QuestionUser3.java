package com.fishe.wut2dodemo.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fishe.wut2dodemo.wut2do.MainActivity;
import com.fishe.wut2dodemo.R;

import java.util.ArrayList;

public class QuestionUser3 extends AppCompatActivity {
    public ArrayList<String> questionList;
    public ArrayList<String> ansList;


    public String qn1 = "Please choose 2 more category";

    public String ans5 = "Swim";
    public String ans6 = "Bowling";
    public String ans7 = "Laser Tag";
    public String ans8 = "Escape Room";
    public String ans9 = "Cinema";
    public String ans1 = "Bowling";
    public String ans2 = "Museum";
    public String ans3 = "Badminton";
    public String ans4 = "Arcade";
    public String ans10 = "Park";
    TextView txtQuestion;
    RadioButton rda,rdb,rdc,rdd,rde,rdf,rdg,rdh,rdi,rdj;
    Button butNext;

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_user3);

        questionList = new ArrayList<String>();
        questionList.add(qn1);

        ansList = new ArrayList<String>();
        ansList.add(ans10);
        ansList.add(ans1);
        ansList.add(ans2);
        ansList.add(ans3);
        ansList.add(ans4);
        ansList.add(ans5);
        ansList.add(ans6);
        ansList.add(ans7);
        ansList.add(ans8);
        ansList.add(ans9);
        txtQuestion=(TextView)findViewById(R.id.textView1);
        rda=(RadioButton)findViewById(R.id.radio0);
        rdb=(RadioButton)findViewById(R.id.radio1);
        rdc=(RadioButton)findViewById(R.id.radio2);
        rdd=(RadioButton)findViewById(R.id.radio3);
        rde=(RadioButton)findViewById(R.id.radio4);
        rdf=(RadioButton)findViewById(R.id.radio5);
        rdg=(RadioButton)findViewById(R.id.radio6);
        rdh=(RadioButton)findViewById(R.id.radio7);
        rdi=(RadioButton)findViewById(R.id.radio8);
        rdj=(RadioButton)findViewById(R.id.radio9);

        butNext=(Button)findViewById(R.id.button1);
        setQuestionView();

        butNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup grp=(RadioGroup)findViewById(R.id.radioGroup1);
                RadioButton answer=(RadioButton)findViewById(grp.getCheckedRadioButtonId());
                //      Toast.makeText(getApplicationContext(), answer.getText(),Toast.LENGTH_SHORT).show();
                QuestionSharedPreference.setFavourite(getApplicationContext(), (String) answer.getText());

                RadioGroup grp2=(RadioGroup)findViewById(R.id.radioGroup2);
                RadioButton answer2=(RadioButton)findViewById(grp2.getCheckedRadioButtonId());

                QuestionSharedPreference.setFavourite(getApplicationContext(), (String) answer2.getText());



                finish();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);

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
        rdi.setText(ansList.get(8));
        rdj.setText(ansList.get(9));

    }
}
