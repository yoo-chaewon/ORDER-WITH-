package com.example.order_with.menuItem;

//import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.order_with.R;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    ArrayList<Menu> myItems;

    public MenuAdapter(ArrayList<Menu> items){
        myItems = items;
    }

    public interface MyClickListener{
        void onItemClicked(Menu menu, int position);
    }
    MyClickListener mListener;

    public void setOnItemClickListener(MyClickListener listener){
        mListener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView menu;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menu = (TextView)itemView.findViewById(R.id.tvMenu);
            price = (TextView)itemView.findViewById(R.id.tvPrice);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.menu_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Menu item = myItems.get(i);
        viewHolder.menu.setText(item.getTitle());
        viewHolder.price.setText(item.getPrice());

        if (mListener != null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(item, getItemCount());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return myItems.size();
    }

}
