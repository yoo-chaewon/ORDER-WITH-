package com.example.order_with.menuItem;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.order_with.R;

import java.util.ArrayList;

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
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menu = (TextView)itemView.findViewById(R.id.tvMenu);
            price = (TextView)itemView.findViewById(R.id.tvPrice);
            imageView = (ImageView)itemView.findViewById(R.id.ivMenu);
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
        if (item.getTitle().equals("라볶이")) viewHolder.imageView.setImageResource(R.drawable.llabboggi);
        else if (item.getTitle().equals("갈비탕")) viewHolder.imageView.setImageResource(R.drawable.galbittang);
        else if (item.getTitle().equals("햄버거")) viewHolder.imageView.setImageResource(R.drawable.burger);
        else if (item.getTitle().equals("김치찌개")) viewHolder.imageView.setImageResource(R.drawable.kimchizzigae);
        else if (item.getTitle().equals("쌀밥")) viewHolder.imageView.setImageResource(R.drawable.ssalbab);
        else if (item.getTitle().equals("치즈떡볶이")) viewHolder.imageView.setImageResource(R.drawable.cheezedduckboki);
        else if (item.getTitle().equals("치즈라면")) viewHolder.imageView.setImageResource(R.drawable.cheezerameon);
        else if (item.getTitle().equals("떡라면")) viewHolder.imageView.setImageResource(R.drawable.dduckrameon);
        else if (item.getTitle().equals("쌀국수")) viewHolder.imageView.setImageResource(R.drawable.ssalguksu);
        else if (item.getTitle().equals("김밥")) viewHolder.imageView.setImageResource(R.drawable.kimbab);
        else if (item.getTitle().equals("야채김밥")) viewHolder.imageView.setImageResource(R.drawable.yachaekimbab);
        else if (item.getTitle().equals("치킨카스김밥")) viewHolder.imageView.setImageResource(R.drawable.chickenkimbab);
        else if (item.getTitle().equals("치즈김밥")) viewHolder.imageView.setImageResource(R.drawable.cheezekimbab);
        else if (item.getTitle().equals("라면")) viewHolder.imageView.setImageResource(R.drawable.rameon);
        else if (item.getTitle().equals("떡볶이")) viewHolder.imageView.setImageResource(R.drawable.dduckboggi);
        else if (item.getTitle().equals("김치볶음밥")) viewHolder.imageView.setImageResource(R.drawable.kimchibboggumbab);
        else if (item.getTitle().equals("콩나물김치찌개")) viewHolder.imageView.setImageResource(R.drawable.kongnamoolkimchizzigae);
        else if (item.getTitle().equals("순두부찌개")) viewHolder.imageView.setImageResource(R.drawable.soondubozzigae);
        else if (item.getTitle().equals("된장찌개")) viewHolder.imageView.setImageResource(R.drawable.dunjangzzigae);
        else if (item.getTitle().equals("왕돈가스")) viewHolder.imageView.setImageResource(R.drawable.wangdongass);
        else if (item.getTitle().equals("치즈돈가스")) viewHolder.imageView.setImageResource(R.drawable.cheezedungass);
        else if (item.getTitle().equals("샐러드돈가스")) viewHolder.imageView.setImageResource(R.drawable.saladedungass);
        else if (item.getTitle().equals("오징어덮밥")) viewHolder.imageView.setImageResource(R.drawable.ozzingoddupbab);
        else if (item.getTitle().equals("불갈비스테이크덮밥")) viewHolder.imageView.setImageResource(R.drawable.bulgalbisteakdubbab);
        else if (item.getTitle().equals("원조김밥")) viewHolder.imageView.setImageResource(R.drawable.onezokimbab);
        else if (item.getTitle().equals("치즈김밥")) viewHolder.imageView.setImageResource(R.drawable.cheezekimbab);
        else if (item.getTitle().equals("일반라면")) viewHolder.imageView.setImageResource(R.drawable.originrameon);
        else if (item.getTitle().equals("김치라면")) viewHolder.imageView.setImageResource(R.drawable.kimchirameon);
        else if (item.getTitle().equals("새우볶음밥")) viewHolder.imageView.setImageResource(R.drawable.sawoobbogenbab);

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
