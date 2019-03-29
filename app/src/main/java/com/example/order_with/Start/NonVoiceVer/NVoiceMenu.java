package com.example.order_with.Start.NonVoiceVer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.order_with.R;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;

import java.util.ArrayList;

public class NVoiceMenu extends AppCompatActivity implements MenuAdapter.MyClickListener {

    private String title;
    private String price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nvoicemenu);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rvNVoiceActivity);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Menu> items = new ArrayList<Menu>();
        for(int i = 0 ; i < 15; i++){//get item here
            items.add(new Menu("유채" + i , "바보" + i));
        }

        MenuAdapter adapter = new MenuAdapter(items);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClicked(Menu menu, int position) {
        title = menu.getTitle();
        price = menu.getPrice();

        Intent intent = new Intent(this, NVoiceOrderFinal.class);
        intent.putExtra("clickedItem",menu);

        startActivity(intent);
        //Toast.makeText(this, "ItemName" + menu.getTitle(), Toast.LENGTH_SHORT).show();
    }

}

