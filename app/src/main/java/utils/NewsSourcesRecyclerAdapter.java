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
 * Created by bunny on 01/05/17.
 */

public class NewsSourcesRecyclerAdapter extends RecyclerView.Adapter<NewsSourcesRecyclerAdapter.MyViewHolder> {


    private ArrayList<NewsSourceList> newsSourceListArrayList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView newsHeadingTextView, newsSourceTextView, newsSummaryTextView ,newsSourceShort;
        public ImageView newsSourceImageView;

        public MyViewHolder(View view) {
            super(view);
            newsHeadingTextView = (TextView) view.findViewById(R.id.newsSource_row_heading_textView);
            newsSourceTextView = (TextView) view.findViewById(R.id.newsSource_row_source_textView);
            newsSourceImageView = (ImageView) view.findViewById(R.id.newsSource_row_sourceImage_imageView);
            newsSummaryTextView = (TextView) view.findViewById(R.id.newsSource_row_newsSummary_textView);
            newsSourceShort = (TextView)view.findViewById(R.id.newsSource_row_sourceshort_textView);

        }
    }


    public NewsSourcesRecyclerAdapter(ArrayList<NewsSourceList> newsSourceListArrayList ,Context context) {
        this.newsSourceListArrayList = newsSourceListArrayList;
        this.context = context;
    }

    @Override
    public NewsSourcesRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newssource_recycler_row, parent, false);


        return new NewsSourcesRecyclerAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(NewsSourcesRecyclerAdapter.MyViewHolder holder, int position) {
        NewsSourceList newsSourceList = newsSourceListArrayList.get(position);
        holder.newsHeadingTextView.setText(newsSourceList.getNewsListHeading());
        holder.newsSourceTextView.setText(newsSourceList.getNewsListSource());
        holder.newsSourceShort.setText(newsSourceList.getNewsSourceShort());



        //holder.newsSourceImageView.setImageDrawable(NewsSourceList.resolveIconImage(context ,newsSourceList.getSourceIndex()));




    }

    @Override
    public int getItemCount() {
        return newsSourceListArrayList.size();
    }


}
