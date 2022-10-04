package com.example.alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class TestActivity extends AppCompatActivity {
    TextView question;
    Button answerA, answerB, answerC, answerD;
    Question q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        question = (TextView) findViewById(R.id.question);
        answerA = (Button) findViewById(R.id.answerA);
        answerB = (Button) findViewById(R.id.answerB);
        answerC = (Button) findViewById(R.id.answerC);
        answerD = (Button) findViewById(R.id.answerD);

        getQuestions();
    }

    private void getQuestions()
    {
        FBref.refQuestions.child("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                q = dS.getValue(Question.class);
                initUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initUI()
    {
        question.setText(q.getQuestion());
        answerA.setText(q.getAnswerA());
        answerB.setText(q.getAnswerB());
        answerC.setText(q.getAnswerC());
        answerD.setText(q.getAnswerD());
    }
}