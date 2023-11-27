//Currency Converter Project for Mobile Application Development SoSe 23
//By Yani Angelov

package com.thu.mad_currencyconverter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ExchangeRateDatabase database = new ExchangeRateDatabase();
    Spinner listViewFrom;
    Spinner listViewTo;
    EditText inputText;
    TextView resultText, textFrom, textTo;
    Button button;
    Toolbar toolbar;
    ShareActionProvider shareActionProvider;
    String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputText = findViewById(R.id.editText);
        textFrom = findViewById(R.id.textFrom);
        textTo = findViewById(R.id.textTo);
        resultText = findViewById(R.id.textResult);
        button = findViewById(R.id.button);

        listViewFrom = findViewById(R.id.currencyFrom);
        CurrencyItemAdapter customAdapter = new CurrencyItemAdapter(database);
        listViewFrom.setAdapter(customAdapter);

        listViewTo = findViewById(R.id.currencyTo);
        listViewTo.setAdapter(customAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        int spinnerValueOne = prefs.getInt("spinnerChoiceOne",-1);
        int spinnerValueTwo = prefs.getInt("spinnerChoiceTwo",-1);
        String st1 = prefs.getString("str","");
        listViewFrom.setSelection(spinnerValueOne);
        listViewTo.setSelection(spinnerValueTwo);
        inputText.setText(st1);

        WorkRequest myWorkRequest = OneTimeWorkRequest.from(ExchangeRateUpdateWorker.class);
        WorkManager.getInstance(this).enqueue(myWorkRequest);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency();
                str = inputText.getText().toString();
                int spinnerChoiceOne = listViewFrom.getSelectedItemPosition();
                int spinnerChoiceTwo = listViewTo.getSelectedItemPosition();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs .edit();
                editor.putString("str", str);
                editor.putInt("spinnerChoiceOne",spinnerChoiceOne);
                editor.putInt("spinnerChoiceTwo",spinnerChoiceTwo);
                editor.apply();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        StrictMode.ThreadPolicy police = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(police);
        getMenuInflater().inflate(R.menu.my_menu, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareText("Result is: " + resultText.getText().toString());
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intentOne = new Intent(MainActivity.this, CurrencyListActivity.class);
        if(item.getItemId() == R.id.action_dropdown1) {
            startActivity(intentOne);
            return true;
        }
        else if (item.getItemId() == R.id.action_dropdown2) {
            WorkRequest myWorkRequest = OneTimeWorkRequest.from(ExchangeRateUpdateWorker.class);
            WorkManager.getInstance(this).enqueue(myWorkRequest);
            Toast toast = Toast.makeText(this, "Currencies Refreshed", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        else if (item.getItemId() == R.id.action_dropdown3) {
            schedulePeriodicCounting();
            return true;
        }
        else if (item.getItemId() == R.id.action_dropdown4){
            cancelPeriodicCounter();
            return true;
        }

        else
            return super.onOptionsItemSelected(item);
    }
    private void setShareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        shareActionProvider.setShareIntent(shareIntent);
    }
    public void convertCurrency() {
        String inputValue = inputText.getText().toString();

        double givenValue = Double.parseDouble(inputValue);

        resultText.setText("" + round((database.convert(givenValue, listViewFrom.getSelectedItem().toString(), listViewTo.getSelectedItem().toString())), 2));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void schedulePeriodicCounting() {
        WorkManager workManager = WorkManager.getInstance(this);
        Constraints constraints = new Constraints.Builder().setRequiresCharging(false).build();

        PeriodicWorkRequest periodicCounterRequest =
                new PeriodicWorkRequest.Builder(ExchangeRateUpdateWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag("periodicRefreshRates")
                        .build();
        workManager.enqueueUniquePeriodicWork("periodicRefresh",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicCounterRequest);
    }

    public void cancelPeriodicCounter() {
        WorkManager.getInstance(this).cancelAllWorkByTag("periodicRefreshRates");
    }
}