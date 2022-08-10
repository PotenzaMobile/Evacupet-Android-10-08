package com.evacupet.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.evacupet.R;
import com.evacupet.utility.Constant;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventDetialActivity extends AppCompatActivity {


    @BindView(R.id.tv_description)
    TextView tv_description;
    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.tv_event)
    TextView tv_title;
    @BindView(R.id.iv_image)
    ImageView iv_image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detial);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        String eventname=null,description=null,date=null,image = null;

        if (extras != null) {
            eventname = extras.getString(Constant.title);
            description = extras.getString(Constant.description);
            date = extras.getString(Constant.date);
            image = extras.getString(Constant.url);
            // and get whatever type user account id is
        }

        if(image != null){
            try {
                Picasso.get().load(image).placeholder(R.drawable.elogo).into(iv_image);
            }catch (Exception e){

            }
        }
        if(eventname != null){

               tv_title.setText(eventname);

        }
        if(description != null){
            tv_description.setText(description);
        }
        if(date != null){
            tv_date.setText(date);
        }



    }
}