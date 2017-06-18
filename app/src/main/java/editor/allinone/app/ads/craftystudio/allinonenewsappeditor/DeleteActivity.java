package editor.allinone.app.ads.craftystudio.allinonenewsappeditor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import utils.ClickListener;
import utils.DatabaseHandlerFirebase;
import utils.NewsInfo;
import utils.NewsListRecyclerAdapter;
import utils.NewsMetaInfo;
import utils.RecyclerTouchListener;

public class DeleteActivity extends AppCompatActivity {

    ArrayList<NewsMetaInfo> newsMetaInfoArrayList = new ArrayList<>();
    NewsListRecyclerAdapter newsListRecyclerAdapter;
    private boolean isLoadingMoreArticle=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        initiaizeNewsInfoArrayList();

    }

    public void initializeRecyclerView(){

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contentDelete_list_recyclerView);
        newsListRecyclerAdapter = new NewsListRecyclerAdapter(newsMetaInfoArrayList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(newsListRecyclerAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Toast.makeText(NewsListActivity.this, "Item clicked "+position, Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(DeleteActivity.this);

                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete the post \n Action is non reversable");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        DatabaseHandlerFirebase databaseHandlerFirebase =new DatabaseHandlerFirebase();
                        databaseHandlerFirebase.deleteNewsMetaInfo(newsMetaInfoArrayList.get(position).getNewsPushKeyId());

                        newsMetaInfoArrayList.remove(position);
                        newsListRecyclerAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });

                builder.show();


            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    onScrolledToBottom();
                    Toast.makeText(DeleteActivity.this, "Refreshing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void initiaizeNewsInfoArrayList(){
        DatabaseHandlerFirebase databaseHandlerFirebase = new DatabaseHandlerFirebase();
        databaseHandlerFirebase.getNewsList(10);
        databaseHandlerFirebase.addNewsListListner(new DatabaseHandlerFirebase.DataBaseHandlerNewsListListner() {
            @Override
            public void onNewsList(ArrayList<NewsMetaInfo> newsMetaInfoArrayList) {



                initializeRecyclerView();

                for (NewsMetaInfo newsMetaInfo : newsMetaInfoArrayList) {


                    DeleteActivity.this.newsMetaInfoArrayList.add(newsMetaInfo);
                }

                //pd.dismiss();

                newsListRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onNoticePost(boolean isSuccessful) {

            }

            @Override
            public void onNewsImageFetched(boolean isFetchedImage) {
                newsListRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNewsInfo(NewsInfo newsInfo) {

            }

            @Override
            public void ongetNewsListMore(ArrayList<NewsMetaInfo> newsMetaInfoArrayListMore) {

            }
        });




    }


    private void onScrolledToBottom() {
        //Toast.makeText(this, "Scrolled to bootom", Toast.LENGTH_SHORT).show();
        if (isLoadingMoreArticle) {


        } else {
            loadMoreArticle();
            isLoadingMoreArticle = true;
        }
    }

    private void loadMoreArticle() {
        DatabaseHandlerFirebase databaseHandlerFirebase = new DatabaseHandlerFirebase();
        databaseHandlerFirebase.addNewsListListner(new DatabaseHandlerFirebase.DataBaseHandlerNewsListListner() {
            @Override
            public void onNewsList(ArrayList<NewsMetaInfo> newsMetaInfoArrayList) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onNoticePost(boolean isSuccessful) {

            }

            @Override
            public void onNewsImageFetched(boolean isFetchedImage) {

            }

            @Override
            public void onNewsInfo(NewsInfo newsInfo) {

            }

            @Override
            public void ongetNewsListMore(ArrayList<NewsMetaInfo> newsMetaInfoArrayListMore) {
                newsMetaInfoArrayListMore.remove(newsMetaInfoArrayListMore.size() - 1);
                for (int i = newsMetaInfoArrayListMore.size() - 1; i >= 0; i--) {
                    DeleteActivity.this.newsMetaInfoArrayList.add(newsMetaInfoArrayListMore.get(i));

                }

                isLoadingMoreArticle = false;
                newsListRecyclerAdapter.notifyDataSetChanged();
                Toast.makeText(DeleteActivity.this, "Done loading", Toast.LENGTH_SHORT).show();

            }
        });
        databaseHandlerFirebase.getNewsListMore(newsMetaInfoArrayList.get(newsMetaInfoArrayList.size() - 1).getNewsPushKeyId(), 10);

    }



}


