package com.thu.mad_currencyconverter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class CurrencyListActivity extends AppCompatActivity {

    ExchangeRateDatabase database = new ExchangeRateDatabase();
    ListView listView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        toolbar = findViewById(R.id.toolbar_activity_2);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.list_view);

        customAdapter adapter = new customAdapter(database.getCurrencies(), database.getExchange());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=" +database.getCapital(database.getCurrencies()[i])));
                startActivity(detailsIntent);
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu_activity_2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.button) {
            finish();
            startActivity(getIntent());
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

}