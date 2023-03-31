package model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.activity.calendarschedulerwithfirebase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Event_Recycle_Adapter extends RecyclerView.Adapter<Event_Recycle_Adapter.ViewHolder> {

    Context context;
    ArrayList<Calendar_Event> eventItemsArrayList;
    DatabaseReference databaseReference;

    public Event_Recycle_Adapter(Context context, ArrayList<Calendar_Event> eventItemsArrayList) {
        this.context = context;
        this.eventItemsArrayList = eventItemsArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.calendar_event, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Calendar_Event events = eventItemsArrayList.get(position);

        holder.textEventName.setText("Event Name : " + events.getTitle());
        holder.textEventDescp.setText("Event Description : " + events.getDescription());

        //Update button operation
        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dialog window to help update our event
                ViewDialogUpdate viewDialogUpdate = new ViewDialogUpdate();
                viewDialogUpdate.showdialog(context,events.getId(),events.getTitle(),events.getDescription());
            }
        });

        //Delete button operation
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize dialog window to confirm weither or not we want to delete an event
                ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
                viewDialogConfirmDelete.showdialog(context, events.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventItemsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        //Declaring our variable
        TextView textEventName;
        TextView textEventDescp;

        Button deleteBtn;
        Button updateBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Variable initialization
            textEventDescp = itemView.findViewById(R.id.text_event_desc);
            textEventName = itemView.findViewById(R.id.text_event_Name);

            deleteBtn = itemView.findViewById(R.id.buttondelete);
            updateBtn = itemView.findViewById(R.id.updateEvent);
        }
    }

    //Dialog update event
    public class ViewDialogUpdate{
        public void showdialog(Context context, String id, String name, String desc){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_event);

            EditText txt_name_event = dialog.findViewById(R.id.text_add_event_Name);
            EditText txt_desc_event = dialog.findViewById(R.id.text_add_event_desc);

            txt_name_event.setText(name);
            txt_desc_event.setText(desc);

            Button buttonUpdate = dialog.findViewById(R.id.buttonAdd);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonUpdate.setText("UPDATE");

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String new_name = txt_name_event.getText().toString();
                    String new_desc = txt_desc_event.getText().toString();

                    if (new_name.isEmpty() || new_desc.isEmpty()){
                        Toast.makeText(context, "Please Fill All Data", Toast.LENGTH_LONG).show();
                    }else {

                        if(new_name.equals(name) && new_desc.equals(desc)){
                            Toast.makeText(context, "No change were made !", Toast.LENGTH_LONG).show();
                        } else {
                            databaseReference.child("EVENTS").child(id).setValue(new Calendar_Event(id,new_name,new_desc));
                            Toast.makeText(context, "Event Updated ...", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }


                    }
                }
            });

            //set transparent bg when dialog shows
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    //Dialogue delete option
    public class ViewDialogConfirmDelete{
        public void showdialog(Context context, String id){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.view_dialog_delete_confirm);

            Button buttonDelete = dialog.findViewById(R.id.buttonDeleteConfirm);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancelConfirm);

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    databaseReference.child("EVENTS").child(id).removeValue();
                    Toast.makeText(context, "Event Deleted ...", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });

            //set transparent bg when dialog shows
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}
