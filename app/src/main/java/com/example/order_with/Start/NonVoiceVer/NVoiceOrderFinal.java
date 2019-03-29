package com.example.order_with.Start.NonVoiceVer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.order_with.R;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;
import java.util.ArrayList;


public class NVoiceOrderFinal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nvoicefinal);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvNVoicefinal);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        Menu menu = intent.getParcelableExtra("clickedItem");

        ArrayList<Menu> items = new ArrayList<Menu>();

        items.add(menu);
        MenuAdapter adapter = new MenuAdapter(items);
        recyclerView.setAdapter(adapter);

        //Toast.makeText(this, menu.getTitle(), Toast.LENGTH_SHORT).show();
    }

}
