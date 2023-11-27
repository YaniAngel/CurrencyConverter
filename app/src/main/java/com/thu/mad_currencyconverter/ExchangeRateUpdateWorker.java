package com.thu.mad_currencyconverter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class ExchangeRateUpdateWorker extends Worker {
    MainActivity mn;
    ExchangeRateDatabase database = new ExchangeRateDatabase();

    public ExchangeRateUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("ExchangeRateUpdateWorker", "Start work");
        updateCurrencies();
/*        mn.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mn, "Refreshed", Toast.LENGTH_SHORT);
                toast.show();
            }
        });*/
        Log.d("ExchangeRateUpdateWorker", "End work");
        return null;
    }

    public void updateCurrencies() {
        List<Entry> update;
        update = querySite();
        for(int i=2; i<update.size(); i++)
        {
            database.setExchangeRate(update.get(i).capital, Double.parseDouble(update.get(i).rates));
        }
    }

    public List<Entry> querySite() {
        List<Entry> ret = new ArrayList<>();
        try {
            URL url = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
            URLConnection connection = url.openConnection();

            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(connection.getInputStream(), connection.getContentEncoding());

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("Cube".equals(parser.getName())) {
                        String temp = parser.getAttributeValue(null, "rate");
                        Entry xd = new Entry(parser.getAttributeValue(null, "currency"), temp);
                        ret.add(xd);
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e("xd", "cant query xd");
            e.printStackTrace();
        }
        return ret;
    }


}
