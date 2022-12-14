package com.example.alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
    }

    public void openTimePicker(View view) {
        // instance of our calendar.
        final Calendar c = Calendar.getInstance();

        // getting our hour, minute.
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // initializing our Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(TimerActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(TimerActivity.this, MyReciever.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(TimerActivity.this, 0, intent, 0);
                        Calendar time = Calendar.getInstance();
                        time.setTimeInMillis(System.currentTimeMillis());
                        time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        time.set(Calendar.MINUTE, minute);
                        time.set(Calendar.SECOND, 0);
                        alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
                    }
                }, hour, minute, true);
        // display our time picker dialog.
        timePickerDialog.show();
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
        else if (id == R.id.login)
        {
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
        }

        return true;
    }
}