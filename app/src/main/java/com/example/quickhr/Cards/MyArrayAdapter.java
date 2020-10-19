package com.example.quickhr.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quickhr.Cards.Cards;
import com.example.quickhr.R;

import java.util.List;

// cards
public class MyArrayAdapter extends android.widget.ArrayAdapter {

    Context context;

    public MyArrayAdapter(Context context, int resourceId, List<Cards> items) {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards cardsItem = (Cards) getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);

        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        name.setText(cardsItem.getName());
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
