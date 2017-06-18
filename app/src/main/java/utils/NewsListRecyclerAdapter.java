package utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import editor.allinone.app.ads.craftystudio.allinonenewsappeditor.R;

/**
 * Created by bunny on 19/06/17.
 */

public class NewsListRecyclerAdapter extends RecyclerView.Adapter<NewsListRecyclerAdapter.MyViewHolder> {


    private ArrayList<NewsMetaInfo> newsMetaInfoArrayList;
    Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView newsHeadingTextView, newsDateTextView, newsCategoryTextView,newsSourceShort;
        public ImageView newsImageView ,newsSourceImageView;

        public MyViewHolder(View view) {
            super(view);
            newsHeadingTextView = (TextView) view.findViewById(R.id.newslist_row_heading_textView);
            newsDateTextView = (TextView) view.findViewById(R.id.newslist_row_date_textView);
            newsCategoryTextView = (TextView) view.findViewById(R.id.newslist_row_category_textView);
            newsSourceShort =(TextView)view.findViewById(R.id.newsList_row_newsSourceshort_textView) ;
            newsImageView = (ImageView)view.findViewById(R.id.newslist_row_imageView);
            newsSourceImageView =(ImageView)view.findViewById(R.id.newslist_row_newsSource_imageView);

        }
    }


    public NewsListRecyclerAdapter(ArrayList<NewsMetaInfo> newsMetaInfoArrayList , Context context) {
        this.newsMetaInfoArrayList = newsMetaInfoArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newslist_recycler_row, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NewsMetaInfo newsMetaInfo = newsMetaInfoArrayList.get(position);
        holder.newsHeadingTextView.setText(newsMetaInfo.getNewsHeading());
        holder.newsDateTextView.setText(newsMetaInfo.resolveDateString(newsMetaInfo.getNewsTime()));
        holder.newsCategoryTextView.setText(newsMetaInfo.getNewsSource());
        holder.newsImageView.setImageBitmap(newsMetaInfo.getNewsImage());

        holder.newsSourceShort.setText(newsMetaInfo.getNewsSourceShort());
        //holder.newsSourceImageView.setImageDrawable(NewsSourceList.resolveIconImage(context ,newsMetaInfo.getNewsSourceimageIndex()));

    }

    @Override
    public int getItemCount() {
        return newsMetaInfoArrayList.size();
    }


}

