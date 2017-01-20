package com.example.zorzin.gallendar;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AddActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    Calendar myCalendar = Calendar.getInstance();
    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();
    Switch mSwitch;
    TextView mStartDate, mEndDate, mStartTime, mEndTime;
    boolean switchState, mAdded;
    int Hour,Minute;
    Thread mThread;
    String mException;
    private com.google.api.services.calendar.Calendar mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        Intent intent = getIntent();
        String name = intent.getStringExtra(MenuActivity.NAME);
        mCredential.setSelectedAccountName(name);

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Gallendar")
                .build();


        mSwitch = (Switch)findViewById(R.id.SwitchDay);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                TextView end = (TextView) findViewById(R.id.EndTime);
                TextView start = (TextView) findViewById(R.id.StartTime);
                if (isChecked)
                {
                    start.setVisibility(View.INVISIBLE);
                    end.setVisibility(View.INVISIBLE);
                }
                else
                {
                    start.setVisibility(View.VISIBLE);
                    end.setVisibility(View.VISIBLE);
                }
                switchState = mSwitch.isChecked();
                CheckDates();
            }
        });


        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CheckDates();
            }
        };

        mAdded = true;
        Button addButton = (Button)findViewById(R.id.AddButton);
        addButton.setClickable(false);
        startCalendar.set(Calendar.YEAR, 1000);
        endCalendar.set(Calendar.YEAR, 1000);
        mStartDate = (TextView)findViewById(R.id.StartDate);
        mStartTime = (TextView)findViewById(R.id.StartTime);
        mEndDate = (TextView)findViewById(R.id.EndDate);
        mEndTime = (TextView)findViewById(R.id.EndTime);

        mStartDate.addTextChangedListener(watcher);
        mStartTime.addTextChangedListener(watcher);
        mEndDate.addTextChangedListener(watcher);
        mEndTime.addTextChangedListener(watcher);
    }

    private void updateDateLabel(TextView textView) {

        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textView.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTimeLabel(TextView textView) {

        textView.setText(Hour +":"+ Minute);
    }


    public void SelectStartDate(View view) {

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel((TextView)findViewById(R.id.StartDate));
            }
        };
        new DatePickerDialog(AddActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void SelectEndDate(View view) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel((TextView)findViewById(R.id.EndDate));
            }
        };
        new DatePickerDialog(AddActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }



    public void SelectStartTime(View view) {
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startCalendar.set(Calendar.MINUTE, minute);
                Hour = hourOfDay;
                Minute = minute;
                updateTimeLabel((TextView)findViewById(R.id.StartTime));
            }
        };
        new TimePickerDialog(AddActivity.this,time, GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY),
                GregorianCalendar.getInstance().get(Calendar.MINUTE),true).show();
    }

    public void SelectEndTime(View view) {
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endCalendar.set(Calendar.MINUTE, minute);
                Hour = hourOfDay;
                Minute = minute;
                updateTimeLabel((TextView)findViewById(R.id.EndTime));
            }
        };
        new TimePickerDialog(AddActivity.this,time, GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY),
                GregorianCalendar.getInstance().get(Calendar.MINUTE),true).show();
    }



    public void AddEvent(View view) throws InterruptedException {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        final Context context = this;
        new Thread(new Runnable() {
            public void run() {
                TextView titleView = (TextView) findViewById(R.id.TitleText);
                TextView locationView = (TextView) findViewById(R.id.PlaceText);
                TextView descriptionView = (TextView) findViewById(R.id.DescriptionText);
                String location = locationView.getText().toString();
                String title = titleView.getText().toString();
                String description = descriptionView.getText().toString();
                Event event = new Event()
                        .setSummary(title)
                        .setLocation(location)
                        .setDescription(description);
                DateTime startDateTime = new DateTime(startCalendar.getTime());
                DateTime endDateTime = new DateTime(endCalendar.getTime());
                EventDateTime startdate = new EventDateTime();
                EventDateTime enddate = new EventDateTime();
                if (!mSwitch.isChecked())
                {
                    startdate.setDateTime(startDateTime)
                            .setTimeZone("Poland");
                    enddate .setDateTime(endDateTime)
                            .setTimeZone("Poland");
                }
                else
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String start = dateFormat.format(startCalendar.getTime());
                    String end = dateFormat.format(endCalendar.getTime());
                    startDateTime = new DateTime(start);
                    endDateTime = new DateTime(end);
                    startdate.setDate(startDateTime)
                            .setTimeZone("Poland");
                    enddate .setDate(endDateTime)
                            .setTimeZone("Poland");
                }
                event.setStart(startdate)
                        .setEnd(enddate);
                Event.Reminders reminders = new Event.Reminders()
                        .setUseDefault(true);
                event.setReminders(reminders);
                String calendarId = "primary";
                try {
                    mService.events().insert(calendarId, event).execute();
                    ShowDialog();
                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    ShowToast(e);
                }
                finally {
                    return;
                }
            }
        }).start();
        progressDialog.hide();

    }

    private void ShowDialog()
    {

        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(getBaseContext(), "Event added", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
        });


    }

    private void ShowToast(IOException e)
    {
        mException = e.getMessage();
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(getBaseContext(), mException, Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }
    //check dates

    private void CheckDates()
    {
        Button addButton = (Button)findViewById(R.id.AddButton);

        int startYear = startCalendar.get(Calendar.YEAR);
        int endYear = endCalendar.get(Calendar.YEAR);
        if ( startYear == 1000 || endYear == 1000)
        {
            addButton.setClickable(false);
            return;
        }
        else if (startCalendar.getTime().after(endCalendar.getTime()))
        {
            addButton.setClickable(false);
            return;
        }
        else
        {
            if (!switchState)
            {
                if (mEndTime.getText() == "" || mStartTime.getText() == "")
                {
                    addButton.setClickable(false);
                    return;
                }
            }
            addButton.setClickable(true);
            return;
        }

    }


    //API STUFF

    GoogleAccountCredential mCredential;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.d("log",
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                }
                break;
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }
}
