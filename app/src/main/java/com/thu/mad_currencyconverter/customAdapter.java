package com.thu.mad_currencyconverter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class customAdapter extends BaseAdapter {

    private String[] names;
    private double[] rates;

    public customAdapter(String[] names, double[] rates) {
        this.names = names;
        this.rates = rates;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        String name = names[position];
        double rate = rates[position];

        Context context = parent.getContext();
        View view;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_custom_list_view, null, false);
        }else {
            view = convertView;
        }

        TextView textViewLeft = view.findViewById(R.id.leftText);
        textViewLeft.setText(name);

        TextView textViewRight = view.findViewById(R.id.rightText);
        textViewRight.setText(Double.toString(rate));

        ImageView flagView = view.findViewById(R.id.flag_image);

        Resources res = context.getResources();
        String drawableName = "flag_"+name.toLowerCase();
        int resId = res.getIdentifier(drawableName, "drawable", context.getPackageName());
        Drawable drawable = res.getDrawable(resId);

        flagView.setImageDrawable(drawable);

        return view;
    }
}
