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

public class CurrencyItemAdapter extends BaseAdapter {

    ExchangeRateDatabase rateDb;

    public CurrencyItemAdapter(ExchangeRateDatabase db) {
        rateDb = db;
    }

    @Override
    public int getCount() {
        return rateDb.getCurrencies().length;
    }

    @Override
    public Object getItem(int position) {
        return rateDb.getCurrencies()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        String name = rateDb.getCurrencies()[position];
        View view;

        int imageId = context.getResources().getIdentifier("flag_"+name.toLowerCase(),
        "drawable", context.getPackageName());

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_row_view, null, false);
        } else {
            view = convertView;
        }

        TextView text = view.findViewById(R.id.spinner_text);
        text.setText(name);

        ImageView flagView = view.findViewById(R.id.spinner_img);

        Resources res = context.getResources();
        String drawableName = "flag_"+name.toLowerCase();
        int resId = res.getIdentifier(drawableName, "drawable", context.getPackageName());
        Drawable drawable = res.getDrawable(resId);

        flagView.setImageDrawable(drawable);
      //  flagView.setImageResource(R.drawable.flag_aud);
        return view;
    }
}