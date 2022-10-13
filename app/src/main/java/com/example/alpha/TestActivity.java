package com.example.alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;

public class TestActivity extends AppCompatActivity {
    TextView question;
    Button answerA, answerB, answerC, answerD, selectedButton;
    ImageView questionImage;
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
        questionImage = (ImageView) findViewById(R.id.questionImage);

        getQuestions();
    }

    private void getQuestions()
    {
        FBref.refQuestions.child("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                q = dS.getValue(Question.class);

                // if this question has an image - download it!
                if (!q.getPicturePath().equals(""))
                {
                    downloadQuestionPicture(q.getPicturePath());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void downloadQuestionPicture(String imagePath)
    {
        File localFile = new File(getCacheDir().getAbsolutePath() + imagePath);

        FBref.storageRef.child("questions/" + imagePath).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                initUI();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
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

        if (q.getPicturePath().equals(""))
        {
            questionImage.setVisibility(View.INVISIBLE);
        }
        else
        {
            questionImage.setVisibility(View.VISIBLE);
            questionImage.setImageURI(Uri.fromFile(new File(getCacheDir() + q.getPicturePath())));
        }
    }

    public void changeSelectedAnswer(View view) {
        // if there was an answer that was clicked before
        if (selectedButton != null)
        {
            selectedButton.setBackgroundColor(Color.BLUE);
        }

        selectedButton = (Button) view;
        view.setBackgroundColor(Color.MAGENTA);
    }

    public void checkAnswer(View view) {
        // if no answer was selected
        if (selectedButton == null)
        {
            Toast.makeText(this, "נא לבחור תשובה קודם", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (Integer.parseInt(selectedButton.getTag().toString()) == q.getCorrectAnswer())
            {
                Toast.makeText(this, "כל הכבוד (נכון)", Toast.LENGTH_SHORT).show();
                selectedButton.setBackgroundColor(Color.GREEN);
            }
            else
            {
                Toast.makeText(this, "איזו טעות :(", Toast.LENGTH_SHORT).show();
                selectedButton.setBackgroundColor(Color.RED);

                // color green the button with the right answer
                switch (q.getCorrectAnswer())
                {
                    case 0:
                        answerA.setBackgroundColor(Color.GREEN);
                        break;
                    case 1:
                        answerB.setBackgroundColor(Color.GREEN);
                        break;
                    case 2:
                        answerC.setBackgroundColor(Color.GREEN);
                        break;
                    case 3:
                        answerD.setBackgroundColor(Color.GREEN);
                        break;
                }
            }
        }
    }
    /**
     * Create the options menu
     *
     * @param menu the menu
     * @return true if success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Go where clicked
     *
     * @param item the item in menu that was clicked
     *  @return true if success
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.login)
        {
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
        else if (id == R.id.timer)
        {
            Intent si = new Intent(this, TimerActivity.class);
            startActivity(si);
        }

        return true;
    }
}