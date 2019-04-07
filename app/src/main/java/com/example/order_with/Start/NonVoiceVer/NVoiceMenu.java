package com.example.order_with.Start.NonVoiceVer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.order_with.R;
import com.example.order_with.Start.VoiceVer.VoiceSpeakingMenu;
import com.example.order_with.menuItem.Menu;
import com.example.order_with.menuItem.MenuAdapter;

import java.util.ArrayList;

public class NVoiceMenu extends AppCompatActivity implements MenuAdapter.MyClickListener {

    private String title;
    private String price;
    private RecyclerView ListrecyclerView;
    private LinearLayoutManager selectLayoutManager;
    private ArrayList<Menu> menuList;
    private MenuAdapter mAdapter;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nvoicemenu);

        button = (Button) findViewById(R.id.button);
        ArrayList<Menu> items = new ArrayList<>();
        Intent intent = getIntent();
        items = intent.getParcelableArrayListExtra("menutoNonVoice");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_voicespeakingmenu);
        //RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rvNVoiceActivity);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);


        selectLayoutManager = new LinearLayoutManager(this);
        selectLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        ListrecyclerView = (RecyclerView) findViewById(R.id.rv_addmenu);
        ListrecyclerView.setLayoutManager(selectLayoutManager);

        menuList = new ArrayList<Menu>();
        mAdapter = new MenuAdapter(menuList);
        ListrecyclerView.setAdapter(mAdapter);

        MenuAdapter adapter = new MenuAdapter(items);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NVoiceMenu.this, NVoiceOrderFinal.class);
                intent.putExtra("clickedItem",menuList);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClicked(Menu menu, int position) {
        title = menu.getTitle();
        price = menu.getPrice();

        Toast.makeText(this, "ItemName" + menu.getTitle(), Toast.LENGTH_SHORT).show();
        Menu selectMenu = new Menu(title, price);
        menuList.add(selectMenu);
        mAdapter.notifyDataSetChanged();

    }

}

