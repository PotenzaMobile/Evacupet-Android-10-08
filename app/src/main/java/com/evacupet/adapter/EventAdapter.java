package com.evacupet.adapter;

import static com.evacupet.utility.SessionManager.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.evacupet.R;
import com.evacupet.activity.AnimalEvacutionActivity;
import com.evacupet.activity.EventActivity;
import com.evacupet.activity.EventDetialActivity;
import com.evacupet.model.EventsModel;
import com.evacupet.model.NotificationModel;
import com.evacupet.utility.Constant;
import com.evacupet.utility.DialogUtility;
import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kushal Prajapati on 30/06/2022.
 * MNTechnologies
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private Context context;
    private ArrayList<EventsModel> eventsModelArrayList = new ArrayList<>();

    public EventAdapter(Context context) {
        this.context = context;

    }

    public void setData(ArrayList<EventsModel> eventsModelArrayList) {
        this.eventsModelArrayList = eventsModelArrayList;
        notifyDataSetChanged();

    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_event, viewGroup, false );
        //View view = LayoutInflater.from(context).inflate(R.layout.item_event, null, false);
        return new EventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder viewHolder, int i) {
        viewHolder.llmain.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventDetialActivity.class);
            intent.putExtra(Constant.NOTIFICATION_OBJECT,eventsModelArrayList.get(i).getParseObject());
            context.startActivity(intent);
        });




        final ParseObject capacityModel = eventsModelArrayList.get(i).getParseObject();
      String name = null;

        try {
            JSONObject obj = new JSONObject(new Gson().toJson(capacityModel.get("image")));
            Log.e(TAG, "onBindViewHolder: image"+ obj.getString("state"));
            JSONObject obj2 = new JSONObject(obj.getString("state"));
            Log.e(TAG, "onBindViewHolder: image1"+ obj2.getString("url"));
            try {
                Picasso.get().load(obj2.getString("url")).placeholder(R.drawable.elogo).into(viewHolder.iv_image);
            }catch (Exception e){

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "onBindViewHolder: image"+ e.getMessage());
        }

        viewHolder.tv_dateevent.setText(capacityModel.get("date").toString());

        Log.e(TAG, "onBindViewHolder: "+ capacityModel.get("date"));
        Log.e(TAG, "onBindViewHolder: "+ new Gson().toJson(capacityModel.get("image")));
        Log.e(TAG, "onBindViewHolder: "+ capacityModel.get("title"));
        Log.e(TAG, "onBindViewHolder: "+ capacityModel.get("description"));


                if(capacityModel.has("date")){

                    viewHolder.tv_dateevent.setText(capacityModel.get("date").toString());
                }

        if(capacityModel.has("title")){

            viewHolder.tv_event.setText(capacityModel.get("title").toString());
        }
        if(capacityModel.has("description")){

            viewHolder.tv_event_des.setText(capacityModel.get("description").toString());
        }


        viewHolder.llmain.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventDetialActivity.class);
            if(capacityModel.get("title").toString() != null) {
                intent.putExtra(Constant.title,capacityModel.get("title").toString());
            }
            if(capacityModel.get("description").toString() != null) {
                intent.putExtra(Constant.description,capacityModel.get("description").toString());
            }
            if(capacityModel.get("date").toString() != null) {
                intent.putExtra(Constant.date,capacityModel.get("date").toString());
            }

            try {
                JSONObject obj = new JSONObject(new Gson().toJson(capacityModel.get("image")));
                Log.e(TAG, "onBindViewHolder: image"+ obj.getString("state"));
                JSONObject obj2 = new JSONObject(obj.getString("state"));
                Log.e(TAG, "onBindViewHolder: image1"+ obj2.getString("url"));
                try {
                    if(obj2.getString("url").toString() != null) {
                        intent.putExtra(Constant.url, obj2.getString("url"));
                    }
                }catch (Exception e){
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onBindViewHolder: image"+ e.getMessage());
            }
            context.startActivity(intent);
        });








    }

    @Override
    public int getItemCount() {

        return eventsModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_event)
        TextView tv_event;
        @BindView(R.id.tv_description)
        TextView tv_event_des;
        @BindView(R.id.tv_dateevent)
        TextView tv_dateevent;
        @BindView(R.id.tv_type)
        TextView tv_type;
        @BindView(R.id.llmain)
        LinearLayout llmain;

        @BindView(R.id.iv_image)
        ImageView iv_image;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}