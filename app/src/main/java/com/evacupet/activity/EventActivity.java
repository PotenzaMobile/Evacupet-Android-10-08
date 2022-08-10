package com.evacupet.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.evacupet.R;
import com.evacupet.adapter.EventAdapter;
import com.evacupet.adapter.NotificationAdapter;
import com.evacupet.model.EventsModel;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends DashboardActivity {
    private ArrayList<EventsModel> eventlist  = new ArrayList<>();
EventAdapter eventAdapter;

    RecyclerView rv_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.fl_content); //Remember this is the FrameLayout area within your activity_main.xml
        titleName.setText(getString(R.string.events));
        getLayoutInflater().inflate(R.layout.activity_event, contentFrameLayout);

     //   setContentView(R.layout.activity_event);
        rv_event = findViewById(R.id.rv_event);

        initializeAdapter();
        getAllevents();


    }

    @Override
    public void onImageSuccess(Intent data) {

    }

    private void initializeAdapter() {
        eventAdapter = new EventAdapter(this);
        rv_event.setLayoutManager(new LinearLayoutManager(EventActivity.this));
        rv_event.setAdapter(eventAdapter);
    }


    private void getAllevents() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object1 : objects) {
                            Log.e("TAG", "done:1 " +new Gson().toJson(object1));

                            eventlist.add(new EventsModel(object1));

                        }
                        //set adapter data
                        Log.e("TAG", "done: " +eventlist.size());
                        eventAdapter.setData(eventlist);
                        final ParseObject capacityModel = eventlist.get(1).getParseObject();
                        Log.e("TAG", "done: " +capacityModel.get("description"));
                        Log.e("TAG", "done: " +eventlist.size());
//                        new Handler().post(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        });
                    } else {

                    }

                } else {

                    Log.e("error = ", e.getMessage());
                }
            }
        });
    }
}