package com.activity.calendarschedulerwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import model.Calendar_Event;
import model.RecycleView_adapter;

public class MainActivity extends AppCompatActivity {

    // choose date with calendar
    private DatePicker datePicker;
    private CalendarView calendarView;
    RecycleView_adapter recycleView_adapter;

    TextView time_pick;

    // the current selected date: has to be displayed on the top and
    // it will be sent to the model if the event build was successfully
    private LocalDate selectedDate;

    private DatabaseReference databaseReference;

    RecyclerView recyclerView;
    ArrayList<Calendar_Event> calendar_eventArrayList;
    //Event_Recycle_Adapter adapter;
    final List<String> calendarStringDate = new ArrayList<>();

    Button add_event;

    //Import our event
    Calendar_Event event;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initialize our calendar
        calendarView = findViewById(R.id.calendarView);

        //We want to retrieve the calendar value (date)
        //final TextView selectedDay = findViewById(R.id.selectedDay);
        //final TextView selectedMonth = findViewById(R.id.selectedMonth);
        //final TextView selectedYear = findViewById(R.id.selectedYear);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
               // selectedDay.setText(String.valueOf(dayOfMonth));
                //selectedMonth.setText(String.valueOf(month));
                //selectedYear.setText(String.valueOf(year));

                Toast.makeText(MainActivity.this, year+" / "+month+" / "+dayOfMonth, Toast.LENGTH_SHORT).show();
            }


        });


        Objects.requireNonNull(getSupportActionBar()).hide();

        databaseReference = FirebaseDatabase.getInstance().getReference();


        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recycleView_adapter);

        calendar_eventArrayList = new ArrayList<>();
        recycleView_adapter = new RecycleView_adapter(this,calendar_eventArrayList);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        add_event = findViewById(R.id.add_event_btn);
        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
                viewDialogAdd.showdialog(MainActivity.this);
            }
        });

        //call data reader method
        readData();
    }

    //read/ retrieve data from database and display in the recycle view provided
    private void readData() {
        databaseReference.child("EVENTS").orderByChild("eventName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                calendar_eventArrayList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Calendar_Event events = dataSnapshot.getValue(Calendar_Event.class);
                    calendar_eventArrayList.add(events);
                }
                recycleView_adapter = new RecycleView_adapter(MainActivity.this,calendar_eventArrayList);
                recyclerView.setAdapter(recycleView_adapter);
                recycleView_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Dialog window to help add new event to the database
    public class ViewDialogAdd{

        public void showdialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_event);

            //Retrieve value from edittext to supply our database
            EditText txt_name_event = dialog.findViewById(R.id.text_add_event_Name);
            EditText txt_desc_event = dialog.findViewById(R.id.text_add_event_desc);

            //retrieve the time
            //Button timePicker = findViewById(R.id.buttonTime);
            //time_pick = findViewById(R.id.time_picked);

            Button buttonAdd = dialog.findViewById(R.id.buttonAdd);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonAdd.setText("ADD EVENT");

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            //set time
        /*    timePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int mins = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, androidx.appcompat.R.style.Theme_AppCompat_Dialog, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            c.set(Calendar.MINUTE,minute);
                            c.setTimeZone(TimeZone.getDefault());
                            SimpleDateFormat format = new SimpleDateFormat("k:mm a");
                            String time = format.format(c.getTime());
                            time_pick.setText(time);
                        }
                    },hour,mins,false);
                    timePickerDialog.show();
                    dialog.dismiss();
                }
            });*/

            //add event button operation
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String id = "event" + new Date().getTime();
                    String name = txt_name_event.getText().toString();
                    String desc = txt_desc_event.getText().toString();

                    //Test if either value is entered or not
                    //If no value entered, prompt a Toast alerting the user to enter values
                    if (name.isEmpty() || desc.isEmpty()){
                        Toast.makeText(context, "Please Fill All Data", Toast.LENGTH_LONG).show();
                    }else {
                        //Event saved in case the values have been provided
                        databaseReference.child("EVENTS").child(id).setValue(new Calendar_Event(id,name, /*String.valueOf(time_pick),*/ desc));
                        Toast.makeText(context, "Event Saved ...", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            });

            //set transparent bg when dialog shows
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    //Dialog widow to help add new event to the database
    public class ViewDialogPickTime{

        public void showdialog(Context context){

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_event);

            //Retrieve value from edittext to supply our database
            EditText txt_name_event = dialog.findViewById(R.id.text_add_event_Name);
            EditText txt_desc_event = dialog.findViewById(R.id.text_add_event_desc);

            Button buttonAdd = dialog.findViewById(R.id.buttonAdd);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonAdd.setText("ADD EVENT");

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
              public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
            });


        }
    }

}