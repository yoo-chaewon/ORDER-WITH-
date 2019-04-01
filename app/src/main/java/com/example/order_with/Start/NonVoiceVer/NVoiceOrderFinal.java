package com.example.order_with.Start.NonVoiceVer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.example.order_with.R;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;


public class NVoiceOrderFinal extends AppCompatActivity {
    private MenuAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nvoicefinal);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvNVoicefinal);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        ArrayList<Menu> menuList = intent.getParcelableArrayListExtra("clickedItem");

        MenuAdapter adapter = new MenuAdapter(menuList);
        adapter.notifyItemInserted(0);
        recyclerView.setAdapter(adapter);

    }
}
