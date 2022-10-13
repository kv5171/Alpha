package com.example.alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Uri photoUri, roundImageUri;
    TextView moveLoginStateET, currentLoginStateET;
    EditText mailET, passwordET;
    LinearLayout addProfileLayout;
    ImageView profile;

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
        addProfileLayout = (LinearLayout) findViewById(R.id.addProfileLayout);
        profile = (ImageView) findViewById(R.id.profile);

        roundImageUri = null;
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
            addProfileLayout.setVisibility(View.VISIBLE);
        }
        else {
            moveLoginStateET.setText("להרשמה");
            currentLoginStateET.setText("התחברות");
            addProfileLayout.setVisibility(View.GONE);
        }
    }

    public void finishRegister(View view) {
        String email = mailET.getText().toString();
        String password = passwordET.getText().toString();

        if ((EmailValidator.getInstance().isValid(email)) && (password.length() >= 6)) {
            if (currentState == SIGN_UP_STATE) {
                // if image was captured
                if (roundImageUri != null) {
                    signup(email, password);
                }
                else
                {
                    Toast.makeText(this, "נא לבחור תמונת פרופיל!", Toast.LENGTH_SHORT).show();
                }
            } else { // login
                login(email, password);
            }
        }
        else
        {
            Toast.makeText(this, "כתובת המייל או הסיסמה בפורמט לא תקין", Toast.LENGTH_SHORT).show();
        }
    }

    private void signup(String email, String password)
    {
        FBref.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Toast.makeText(MainActivity.this, "הרשמה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                            uploadImage();
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
    }

    private void login(String email, String password)
    {
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

    public void signout(View view) {
        FBref.auth.signOut();
        Toast.makeText(this, "התנתק בהצלחה", Toast.LENGTH_SHORT).show();
    }

    public void getImageFromUser(View view) {
        // check if we have permission of saving the captured image
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
        else {
            // if there is a permission already
            openImageChooserIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            // if the permission granted - open the intent of choosing or capturing the file
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooserIntent();
            } else {
                Toast.makeText(this, "נא לאשר הרשאת אחסון לאפליקציה", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openImageChooserIntent()
    {
        // Create new intent and set its type to image
        Intent pickIntent = new Intent();
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        // Intent for camera activity to capture a new picture
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File tmpFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "image.jpg");

        photoUri = Uri.fromFile(tmpFile);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        // Title of the popup intent
        String pickTitle = "בחר תמונה";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[]{takePhotoIntent}
                );

        startActivityForResult(chooserIntent, 345);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 345) {
            if (resultCode == RESULT_OK) {
                // if returned From Camera (there is no returned intent)
                if ((imageReturnedIntent == null) || (imageReturnedIntent.getData() == null))
                {
                    // start cropping activity (circle)
                    CropImage.activity(photoUri)
                            .setCropMenuCropButtonTitle("חתוך")
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setFixAspectRatio(true)
                            .start(this);
                }
                else // from storage
                {
                    Uri selectedFile = imageReturnedIntent.getData();
                    CropImage.activity(selectedFile)
                            .setCropMenuCropButtonTitle("חתוך")
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setFixAspectRatio(true)
                            .start(this);
                }
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
            if (resultCode == RESULT_OK)
            {
                // create a round image for the profile image view
                roundImageUri = result.getUri();
                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), roundImageUri.getPath());
                roundDrawable.setCircular(true);
                profile.setImageDrawable(roundDrawable);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Toast.makeText(this, "התרחשה בעיה. אנא נסה שנית", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage()
    {
        UploadTask uploadTask = FBref.storageRef.child("images/"+ FBref.auth.getUid()).putFile(roundImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainActivity.this, "התרחשה בעיה. נא נסה שנית", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this, "העלאה הסתיימה בהצלחה", Toast.LENGTH_SHORT).show();
            }
        });
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

        if (id == R.id.test)
        {
            Intent si = new Intent(this, TestActivity.class);
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