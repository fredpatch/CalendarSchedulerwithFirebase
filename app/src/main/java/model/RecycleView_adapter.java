package model;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.calendarschedulerwithfirebase.MainActivity;
import com.activity.calendarschedulerwithfirebase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class RecycleView_adapter extends RecyclerView.Adapter<RecycleView_adapter.ViewHolder>{


    ArrayList<Calendar_Event> eventItemsArrayList;
    Context context;
    DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    TextView time_pick;

    public RecycleView_adapter(Context context,ArrayList<Calendar_Event> eventItemsArrayList) {
        this.eventItemsArrayList = eventItemsArrayList;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Method used to add the row to the recycle view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.calendar_event, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Calendar_Event calendar_event = eventItemsArrayList.get(position);

        //get event name and desc
        holder.event_name.setText("Name: "+calendar_event.getTitle());
        holder.event_desc.setText("Desc: "+calendar_event.getDescription());
        //holder.event_time.setText("Desc: "+calendar_event.getTime());
    }

    @Override
    public int getItemCount() {
        return eventItemsArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView event_name;
        TextView event_desc;
        TextView event_time;
        ImageView imageView;

        /*
        This method manages the view and what are inside the rows
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            event_desc = itemView.findViewById(R.id.text_event_desc);
            event_name = itemView.findViewById(R.id.text_event_Name);
            imageView = itemView.findViewById(R.id.imageView);
            
            //click action
            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Display the popup menu
            showPopupMenu(v);

            //Display a toast at the position of the pointer
            //Calendar_Event calendar_event = eventItemsArrayList.get(getAdapterPosition());
            //Toast.makeText(context, calendar_event.getTitle()+"", Toast.LENGTH_SHORT).show();
        }

        private void showPopupMenu(View view){
            PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
            popupMenu.inflate(R.menu.popup);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            Calendar_Event events = eventItemsArrayList.get(getAdapterPosition());

            switch (item.getItemId()){
                case R.id.dropdown_menu_delete_event:
                    ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
                    viewDialogConfirmDelete.showdialog(context,events.getId());
                return true;

                case R.id.dropdown_menu_change_event:
                    ViewDialogUpdate viewDialogUpdate = new ViewDialogUpdate();
                    viewDialogUpdate.showdialog(context,events.getId(),events.getTitle(),events.getDescription());
                    return true;

                default:
                    return false;
            }
        }
    }

    //Dialog update event
    public class ViewDialogUpdate{
        public void showdialog(Context context, String id, String name, String desc){


            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_event);

            //time picker Button
            Button timePicker = dialog.findViewById(R.id.buttonTime);

            recyclerView = dialog.findViewById(R.id.recycleView);

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

            //set time
            /*timePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int mins = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog, new TimePickerDialog.OnTimeSetListener() {
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
                }
            });*/

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
                            databaseReference.child("EVENTS").child(id).setValue(new Calendar_Event(id,new_name,/*String.valueOf(time_pick),*/ new_desc));
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
