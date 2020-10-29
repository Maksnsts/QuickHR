package com.example.quickhr.Cards;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.quickhr.Cards.Cards;
import com.example.quickhr.R;

import java.util.List;

// cards
public class MyArrayAdapter extends android.widget.ArrayAdapter {

    TextView name, phone, lastName, email, country, city, skills, experience;
    boolean switch1;
    Context context;

    public MyArrayAdapter(Context context, int resourceId, List<Cards> items) {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards cardsItem = (Cards) getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);

        }

        name = (TextView) convertView.findViewById(R.id.name);
        phone = (TextView) convertView.findViewById(R.id.phone); // step 2
       // lastName = (TextView) convertView.findViewById(R.id.last_name);
        email = (TextView) convertView.findViewById(R.id.email);
        country = (TextView) convertView.findViewById(R.id.country);
        city = (TextView) convertView.findViewById(R.id.city);
        skills = (TextView) convertView.findViewById(R.id.skills);
        experience = (TextView) convertView.findViewById(R.id.experience);

        ImageView image = (ImageView) convertView.findViewById(R.id.image);


        name.setText(cardsItem.getName() + " " + cardsItem.getLastName());
        phone.setText(cardsItem.getPhone());
        //lastName.setText(cardsItem.getLastName());
        email.setText(cardsItem.getEmail());
        country.setText(cardsItem.getCountry());
        city.setText(cardsItem.getCity());
        skills.setText(cardsItem.getSkills());
        experience.setText(cardsItem.getExperience());
        if(cardsItem.getSwitch1().equals("true")){
            experience.setBackgroundResource(R.drawable.rounded_corner_com_exp);
        }else {
            experience.setBackgroundResource(R.drawable.rounded_corner_exp);
        }


        switch(cardsItem.getProfileImageUrl()){
            case "default":
                //image.setImageResource(R.mipmap.ic_launcher);
                Glide.with(getContext()).load(R.mipmap.ic_launcher).into(image);
                break;
            default:
                //Glide.clear(image);
                Glide.with(getContext()).load(cardsItem.getProfileImageUrl()).into(image);
                break;
        }
       // Glide.with(getContext()).load(cardsItem.getProfileImageUrl()).into(image);

        return convertView;

    }
}
