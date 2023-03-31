package com.activity.calendarschedulerwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import model.Calendar_Event;
import model.Event_Recycle_Adapter;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private EditText editText;
    private String selectedDate;
    private DatabaseReference databaseReference;

    RecyclerView recyclerView;
    ArrayList<Calendar_Event> calendar_eventArrayList;
    Event_Recycle_Adapter adapter;

    Button add_event;

    //Import our event
    Calendar_Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Objects.requireNonNull(getSupportActionBar()).hide();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        calendar_eventArrayList = new ArrayList<>();

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
                adapter = new Event_Recycle_Adapter(MainActivity.this, calendar_eventArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Dialog widow to help add new event to the database
    public class ViewDialogAdd{
        public void showdialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_event);

            //Retrieve value from editext to supply our database
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
                        databaseReference.child("EVENTS").child(id).setValue(new Calendar_Event(id,name,desc));
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

}