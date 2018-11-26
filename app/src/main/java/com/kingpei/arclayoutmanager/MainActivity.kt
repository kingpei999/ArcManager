package com.kingpei.arclayoutmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kingpei.viewmodel.layoutManager.ArcCalculator
import com.kingpei.viewmodel.layoutManager.ArcLayoutManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var rv = findViewById<RecyclerView>(R.id.main_rv)
        var screenWidth = resources.displayMetrics.widthPixels
        var calculator = ArcCalculator(screenWidth, screenWidth)
        var arcLayoutManager = ArcLayoutManager(calculator)
        rv.layoutManager = arcLayoutManager

        var data = arrayListOf<String>()
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534408345353&di=897df53ecddae10a9261a689b3aa4b55&imgtype=0&src=http%3A%2F%2Fimages.liqucn.com%2Fimg%2Fh1%2Fh971%2Fimg201709220953360_info300X300.jpg")
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534408393864&di=5ba3e38ce9657348dbb90d54abb5a9ae&imgtype=0&src=http%3A%2F%2Fimages.liqucn.com%2Fimg%2Fh1%2Fh971%2Fimg201709221119020_info300X300.jpg")
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534408424581&di=b458ceccb7adf1e2d6c9ae5fc6e3b7d2&imgtype=0&src=http%3A%2F%2Fimages.liqucn.com%2Fimg%2Fh1%2Fh971%2Fimg201709211633300_info300X300.jpg")
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534408424581&di=a549a7f937019409290051fd9382e476&imgtype=0&src=http%3A%2F%2Fwww.qqzhi.com%2Fuploadpic%2F2014-10-03%2F050247475.jpg")
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534408424580&di=e02f43d837de54162a25231aa45a26c1&imgtype=0&src=http%3A%2F%2Fimages.liqucn.com%2Fimg%2Fh1%2Fh969%2Fimg201709201603430_info300X300.jpg")
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534408587324&di=a57534b50877ce3815201332fd4f3559&imgtype=0&src=http%3A%2F%2Fimg.myvacation.cn%2Fattchment%2Fuploadimg%2F20151103%2F144653421694824600.jpg")
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534408587314&di=38b2fbdd8e498cd827f56c417c5b30d0&imgtype=0&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Fface%2F00200d65b0961c5272474d51fa794908add04b1c.jpg")
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535004319&di=490a654157e28c551ce644933a9f63b6&imgtype=jpg&er=1&src=http%3A%2F%2Fwnzk-img.zuyushop.com%2Fr_img%2Fimg%2F20170529%2F20%2F20170529202238025_Ylk.jpg")
        rv.adapter = MyAdapter(data)

//        Handler().postDelayed(Runnable {
//            rv.adapter.notifyDataSetChanged()
//        }, 3000)

    }

    private class MyHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(text: String) {
            var imageView = itemView.findViewById<ImageView>(R.id.item_iv)
            Glide.with(itemView).load(text).into(imageView)
        }

    }

    private class MyAdapter(data:List<String>) : RecyclerView.Adapter<MyHolder>() {
        private var data = data


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_simple, parent, false)
            return MyHolder(view)
        }

        override fun getItemCount(): Int {
            return if(data == null)  0 else data.size
        }


        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.bind(data[position])
        }

    }
}
