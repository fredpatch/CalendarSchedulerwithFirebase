package model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.calendarschedulerwithfirebase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecycleView_adapter extends RecyclerView.Adapter<RecycleView_adapter.ViewHolder>{


    ArrayList<Calendar_Event> eventItemsArrayList;
    Context context;
    DatabaseReference databaseReference;

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
        //Count the event
        holder.count_event.setText(String.valueOf(position));

        Calendar_Event calendar_event = eventItemsArrayList.get(position);

        //get event name and desc
        holder.event_name.setText("Name: "+calendar_event.getTitle());
        holder.event_desc.setText("Desc: "+calendar_event.getDescription());
    }

    @Override
    public int getItemCount() {
        return eventItemsArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView event_name;
        TextView event_desc;
        TextView count_event;

        /*
        This method manages the view and what are inside the rows
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            event_desc = itemView.findViewById(R.id.text_event_desc);
            event_name = itemView.findViewById(R.id.text_event_Name);
            count_event = itemView.findViewById(R.id.count_event);
            
            //click action
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Calendar_Event calendar_event = eventItemsArrayList.get(getAdapterPosition());
            Toast.makeText(context, calendar_event.getTitle()+"", Toast.LENGTH_SHORT).show();
        }
    }
}
