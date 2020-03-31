package com.example.order_with.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.order_with.Data.Menu
import com.example.order_with.R
import kotlinx.android.synthetic.main.menu_item.view.*

class MenuAdapter(var items : ArrayList<Menu>) : RecyclerView.Adapter<MenuAdapter.ViewHolder>(){
    internal var mListener: MyClickListener? = null

    interface MyClickListener {
        fun onItemClicked(menu: Menu, position: Int)
    }

    fun setOnItemClickListener(listener: MyClickListener) {
        mListener = listener
    }

    class ViewHolder : RecyclerView.ViewHolder{
        constructor(itemView: View) : super(itemView)
        var menu : TextView = itemView.tvMenu
        var price : TextView = itemView.tvPrice
        var imageView: ImageView = itemView.ivMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items[position]
        viewHolder.menu.text = item.name
        viewHolder.price.text = item.price

        if (item.name.contains("볶이"))
            viewHolder.imageView.setImageResource(R.drawable.dduckboggi)
        else if (item.name.equals("갈비탕"))
            viewHolder.imageView.setImageResource(R.drawable.galbittang)
        else if (item.name.equals("햄버거"))
            viewHolder.imageView.setImageResource(R.drawable.burger)
        else if (item.name.contains("김치찌개"))
            viewHolder.imageView.setImageResource(R.drawable.kimchizzigae)
        else if (item.name.equals("쌀밥"))
            viewHolder.imageView.setImageResource(R.drawable.ssalbab)
        else if (item.name.equals("쌀국수"))
            viewHolder.imageView.setImageResource(R.drawable.ssalguksu)
        else if (item.name.contains("김밥"))
            viewHolder.imageView.setImageResource(R.drawable.kimbab)
        else if (item.name.contains("라면"))
            viewHolder.imageView.setImageResource(R.drawable.rameon)
        else if (item.name.equals("김치볶음밥"))
            viewHolder.imageView.setImageResource(R.drawable.kimchibboggumbab)
        else if (item.name.equals("순두부찌개"))
            viewHolder.imageView.setImageResource(R.drawable.soondubozzigae)
        else if (item.name.equals("된장찌개"))
            viewHolder.imageView.setImageResource(R.drawable.dunjangzzigae)
        else if (item.name.contains("돈가스"))
            viewHolder.imageView.setImageResource(R.drawable.wangdongass)
        else if (item.name.equals("오징어덮밥"))
            viewHolder.imageView.setImageResource(R.drawable.ozzingoddupbab)
        else if (item.name.equals("새우볶음밥")) viewHolder.imageView.setImageResource(R.drawable.sawoobbogenbab)
        else viewHolder.imageView.setImageResource(R.drawable.logo)

        if (mListener != null) {
            viewHolder.itemView.setOnClickListener(View.OnClickListener { mListener!!.onItemClicked(item, itemCount) })
        }
    }
}