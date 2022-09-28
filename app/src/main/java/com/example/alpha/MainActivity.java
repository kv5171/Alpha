package com.example.alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.validator.routines.EmailValidator;

public class MainActivity extends AppCompatActivity {
    TextView moveLoginStateET, currentLoginStateET;
    EditText mailET, passwordET;

    final boolean SIGN_UP_STATE = false;
    boolean currentState = SIGN_UP_STATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moveLoginStateET = (TextView) findViewById(R.id.moveLoginStateET);
        currentLoginStateET = (TextView) findViewById(R.id.currentLoginStateET);
        mailET = (EditText) findViewById(R.id.mailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in already
        FirebaseUser currentUser = FBref.auth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(this, "המשתמש כבר מחובר :)", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeCurrentState(View view) {
        currentState = !currentState;

        if (currentState == SIGN_UP_STATE){
            moveLoginStateET.setText("להתחברות");
            currentLoginStateET.setText("הרשמה");
        }
        else {
            moveLoginStateET.setText("להרשמה");
            currentLoginStateET.setText("התחברות");
        }
    }

    public void finishRegister(View view) {
        String email = mailET.getText().toString();
        String password = passwordET.getText().toString();

        if ((EmailValidator.getInstance().isValid(email)) && (password.length() >= 6)) {
            if (currentState == SIGN_UP_STATE) {
                FBref.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    Toast.makeText(MainActivity.this, "הרשמה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                    if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                        Toast.makeText(MainActivity.this, "כתובת המייל כבר בשימוש", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "הרשמה נכשלה. אנא נסה שנית", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            } else { // login
                FBref.auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    Toast.makeText(MainActivity.this, "התחברות בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                                } else {
                                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                    // If sign in fails, display a message to the user.
                                    if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                                        Toast.makeText(MainActivity.this, "סיסמה שגוייה. אנא נסה שנית", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (errorCode.equals("ERROR_USER_NOT_FOUND"))
                                    {
                                        Toast.makeText(MainActivity.this, "משתמש בעל כתובת מייל זו לא נמצא!", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this, "התחברות נכשלה. אנא נסה שנית", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        }
        else
        {
            Toast.makeText(this, "כתובת המייל או הסיסמה בפורמט לא תקין", Toast.LENGTH_SHORT).show();
        }
    }

    public void signout(View view) {
        FBref.auth.signOut();
        Toast.makeText(this, "התנתק בהצלחה", Toast.LENGTH_SHORT).show();
    }
}