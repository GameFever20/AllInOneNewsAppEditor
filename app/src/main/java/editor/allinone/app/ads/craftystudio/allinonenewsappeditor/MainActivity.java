package editor.allinone.app.ads.craftystudio.allinonenewsappeditor;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import io.fabric.sdk.android.Fabric;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import utils.ClickListener;
import utils.DatabaseHandlerFirebase;
import utils.NewsInfo;
import utils.NewsMetaInfo;
import utils.NewsSourceList;
import utils.NewsSourcesRecyclerAdapter;
import utils.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "XPcyxmVmGSPED5WdoiFBQzhTM";
    private static final String TWITTER_SECRET = "tZElX2L8puW0NFlR52xocz3IAwECFU2L2R1gKTLaM5PynUWUwu";


    private static final String TAG = "Main";
    private int SELECT_PICTURE = 4;
    public String imagePath = "";

    private FirebaseAuth mAuth;
    private Uri imageUri;

    NewsInfo newsInfo = new NewsInfo();
    NewsMetaInfo newsMetaInfo = new NewsMetaInfo();
    HashMap<String, NewsSourceList> newsSourceListHashMap = new HashMap<>();
    HashMap<String, Long> tweetHashMap = new HashMap<>();

    ArrayList<NewsSourceList> newsSourceListArrayList = new ArrayList<>();
    ArrayList<Long> tweetArrayList = new ArrayList<>();

    NewsSourcesRecyclerAdapter newsSourcesRecyclerAdapter;

    boolean isImageUploaded = false, isNewsInfoUploaded = false, isNewsMetaInfoUploaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                selectImageFromStorage();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        signIn();

        //serializeNewsInfo();
        //deserializeNewsInfo();
        initializeRecyclerView();
        newsInfo.setNewsTweetListHashMap(tweetHashMap);


        CheckBox checkBox = (CheckBox) findViewById(R.id.newsFeed_newsNotification_checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (newsMetaInfo != null) {
                    newsMetaInfo.setNewsNotification(isChecked);
                }
            }
        });

    }

    private void downloadImage(String pushKeyId) {
        DatabaseHandlerFirebase databaseHandlerFirebase = new DatabaseHandlerFirebase();

        databaseHandlerFirebase.addNewsListListner(new DatabaseHandlerFirebase.DataBaseHandlerNewsUploadListner() {
            @Override
            public void onNewsList(ArrayList<NewsMetaInfo> newsMetaInfoArrayList) {

            }

            @Override
            public void onNewsImageFetched(File imageFile) {

                Toast.makeText(MainActivity.this, "Downloaded", Toast.LENGTH_SHORT).show();
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());

                ImageView imageView = (ImageView) findViewById(R.id.content_main_imageView);
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onNewsImageLink(String ImageLink) {

            }

            @Override
            public void onNewsImageProgress(int progressComplete) {

            }

            @Override
            public void onNewsFullArticle(int newsIndex) {

            }


        });
        databaseHandlerFirebase.downloadImageFromFireBase(pushKeyId);
    }

    private void signIn() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            downloadImage("");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action

            Intent intent =new Intent(MainActivity.this , DeleteActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void selectImageFromStorage() {
        // select a file
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();


                Toast.makeText(this, "encoded path is " + selectedImageUri.getEncodedPath(), Toast.LENGTH_SHORT).show();
                try {
                    getBitmapFromUri(selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //selectedImagePath = getPath(selectedImageUri);
            }
        }
    }

    private void getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        ImageView imageView = (ImageView) findViewById(R.id.newsFeed_newsImage_ImageView);
        imageView.setImageBitmap(image);
        Toast.makeText(this, "path is " + uri.getPath(), Toast.LENGTH_SHORT).show();

        imageUri = uri;
    }


    public void uploadImageToFirebase(final Uri imageUri, String pushKeyId) {
        DatabaseHandlerFirebase databaseHandlerFirebase = new DatabaseHandlerFirebase();
        databaseHandlerFirebase.addNewsListListner(new DatabaseHandlerFirebase.DataBaseHandlerNewsUploadListner() {
            @Override
            public void onNewsList(ArrayList<NewsMetaInfo> newsMetaInfoArrayList) {

            }

            @Override
            public void onNewsImageFetched(File imageFile) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onNewsImageLink(String ImageLink) {
                Toast.makeText(MainActivity.this, "Image Link " + ImageLink, Toast.LENGTH_SHORT).show();
                imagePath = ImageLink;
            }

            @Override
            public void onNewsImageProgress(int progressComplete) {

            }

            @Override
            public void onNewsFullArticle(int newsIndex) {

            }


        });

    }


    public void UploadToFireBase(View view) {
        NewsMetaInfo newsMetaInfo = new NewsMetaInfo();
        EditText editText = (EditText) findViewById(R.id.content_main_newsHeading_editText);
        newsMetaInfo.setNewsHeading(editText.getText().toString());
        editText = (EditText) findViewById(R.id.content_main_newsdate_editText);
        newsMetaInfo.setNewsDate(editText.getText().toString());
        editText = (EditText) findViewById(R.id.content_main_newscategory_editText);
        newsMetaInfo.setNewsSource(editText.getText().toString());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("NewsMetaInfo");

        myRef.push().setValue(newsMetaInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(MainActivity.this, "Value uploaded", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void uploadDummyData(View view) {


        DatabaseHandlerFirebase databaseHandlerFirebase = new DatabaseHandlerFirebase();

        NewsMetaInfo newsMetaInfo = new NewsMetaInfo();
        newsMetaInfo.setNewsHeading("News withb Dummy Heading to show ui here");
        newsMetaInfo.setNewsDate("23/04/2017");
        newsMetaInfo.setNewsSource("Zee ");
        newsMetaInfo.setNewsSourceimageIndex(0);


        NewsInfo newsInfo = new NewsInfo();
        newsInfo.setNewsSummary("News Sumahagdsj cb hcmb ybfd vbhre vs  Hello guys, welcome to Firebase Storage Tutorial for Android. In this tutorial we are going to learn how you can upload images to firebase storage. Infact not only images you can use this firebase storage android tutorial to upload any kind of file to firebase storage. So lets start our Firebase Storage Tutorial.\n" +
                "\n");
        newsInfo.setNewsHeadline("News withb Dummy HEading to show ui here");
        newsInfo.setNewsDate("23/04/2017");
        newsInfo.setNewsSource("Zee");
        newsInfo.setNewsCategory("Technology");
        newsInfo.setNewsNotify("yes");
        newsInfo.setNewsTime("1500");

        HashMap<String, NewsSourceList> newsSourceListHashMap = new HashMap<>();
        NewsSourceList newsSourceList = new NewsSourceList();
        newsSourceList.setNewsListHeading("Heading for source 1 else nothing toshow w");
        newsSourceList.setNewsListArticle("News Sumahagdsj cb hcmb ybfd vbhre vs  Hello guys, welcome to Firebase Storage Tutorial for Android. In this tutorial we are going to learn how you can upload images to firebase storage. Infact not only images you can use this firebase storage android tutorial to upload any kind of file to firebase storage. So lets start our Firebase Storage Tutorial.\n" +
                "\" +\n"
        );
        newsSourceList.setNewsListLink("link for source one");
        newsSourceList.setNewsListSource("AjjTak");
        newsSourceList.setSourceIndex(0);

        newsSourceListHashMap.put("Source 1", newsSourceList);


        NewsSourceList newsSourceList2 = new NewsSourceList();
        newsSourceList2.setNewsListHeading("Heading for source 2 else nothing toshow w");
        newsSourceList2.setNewsListArticle("News Sumahagdsj cb hcmb ybfd  vs  Hello guys, welcome to Firebase Storage Tutorial for Android. In this tutorial we are going to learn how you can upload images to firebase storage. Infact not only images you can use this firebase storage android tutorial to upload any kind of file to firebase storage. So lets start our Firebase Storage Tutorial.\n" +
                "\" +\n"
        );
        newsSourceList2.setNewsListLink("link for source two");
        newsSourceList2.setNewsListSource("NDTV");
        newsSourceList.setSourceIndex(0);

        newsSourceListHashMap.put("Source 2", newsSourceList2);


        NewsSourceList newsSourceList3 = new NewsSourceList();
        newsSourceList3.setNewsListHeading("Heading for source 3 else nothing toshow w");
        newsSourceList3.setNewsListArticle("News Sumahagdsj  hcmb ybfd vbhre vs  Hello guys, welcome to Firebase Storage Tutorial for Android. In this tutorial we are going to learn how you can upload images to firebase storage. Infact not only images you can use this firebase storage android tutorial to upload any kind of file to firebase storage. So lets start our Firebase Storage Tutorial.\n" +
                "\" +\n"
        );
        newsSourceList3.setNewsListLink("link for source three");
        newsSourceList3.setNewsListSource("Dainik Bhaskar");
        newsSourceList.setSourceIndex(0);

        newsSourceListHashMap.put("Source 3", newsSourceList3);


        newsInfo.setNewsSourceListHashMap(newsSourceListHashMap);

        HashMap<String, Long> newsTweetHashMap = new HashMap<>();
        newsTweetHashMap.put("tweet1", 858655214517141504L);
        newsTweetHashMap.put("tweet2", 858758633051627520L);
        newsTweetHashMap.put("tweet3", 858769350437724160L);

        newsInfo.setNewsTweetListHashMap(newsTweetHashMap);


        databaseHandlerFirebase.insertNewsFullArticle(newsMetaInfo, newsInfo, imageUri);
        databaseHandlerFirebase.addNewsListListner(new DatabaseHandlerFirebase.DataBaseHandlerNewsUploadListner() {
            @Override
            public void onNewsList(ArrayList<NewsMetaInfo> newsMetaInfoArrayList) {

            }

            @Override
            public void onNewsImageFetched(File imageFile) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onNewsImageLink(String ImageLink) {

            }

            @Override
            public void onNewsImageProgress(int progressComplete) {

            }

            @Override
            public void onNewsFullArticle(int newsIndex) {

            }


        });

    }

    public void serializeNewsInfo() {

        NewsInfo newsInfo = new NewsInfo();
        newsInfo.setNewsSummary("News Sumahagdsj cb hcmb ybfd vbhre vs  Hello guys, welcome to Firebase Storage Tutorial for Android. In this tutorial we are going to learn how you can upload images to firebase storage. Infact not only images you can use this firebase storage android tutorial to upload any kind of file to firebase storage. So lets start our Firebase Storage Tutorial.\n" +
                "\n");
        newsInfo.setNewsHeadline("News withb Dummy HEading to show ui here");
        newsInfo.setNewsDate("23/04/2017");
        newsInfo.setNewsSource("Zee");
        newsInfo.setNewsCategory("Technology");
        newsInfo.setNewsNotify("yes");
        newsInfo.setNewsTime("1500");

        HashMap<String, NewsSourceList> newsSourceListHashMap = new HashMap<>();
        NewsSourceList newsSourceList = new NewsSourceList();
        newsSourceList.setNewsListHeading("Heading for source 1 else nothing toshow w");
        newsSourceList.setNewsListArticle("News Sumahagdsj cb hcmb ybfd vbhre vs  Hello guys, welcome to Firebase Storage Tutorial for Android. In this tutorial we are going to learn how you can upload images to firebase storage. Infact not only images you can use this firebase storage android tutorial to upload any kind of file to firebase storage. So lets start our Firebase Storage Tutorial.\n" +
                "\" +\n"
        );
        newsSourceList.setNewsListLink("link for source one");
        newsSourceList.setNewsListSource("AjjTak");
        newsSourceList.setSourceIndex(0);

        newsSourceListHashMap.put("Source 1", newsSourceList);


        NewsSourceList newsSourceList2 = new NewsSourceList();
        newsSourceList2.setNewsListHeading("Heading for source 2 else nothing toshow w");
        newsSourceList2.setNewsListArticle("News Sumahagdsj cb hcmb ybfd  vs  Hello guys, welcome to Firebase Storage Tutorial for Android. In this tutorial we are going to learn how you can upload images to firebase storage. Infact not only images you can use this firebase storage android tutorial to upload any kind of file to firebase storage. So lets start our Firebase Storage Tutorial.\n" +
                "\" +\n"
        );
        newsSourceList2.setNewsListLink("link for source two");
        newsSourceList2.setNewsListSource("NDTV");
        newsSourceList.setSourceIndex(0);

        newsSourceListHashMap.put("Source 2", newsSourceList2);


        NewsSourceList newsSourceList3 = new NewsSourceList();
        newsSourceList3.setNewsListHeading("Heading for source 3 else nothing toshow w");
        newsSourceList3.setNewsListArticle("News Sumahagdsj  hcmb ybfd vbhre vs  Hello guys, welcome to Firebase Storage Tutorial for Android. In this tutorial we are going to learn how you can upload images to firebase storage. Infact not only images you can use this firebase storage android tutorial to upload any kind of file to firebase storage. So lets start our Firebase Storage Tutorial.\n" +
                "\" +\n"
        );
        newsSourceList3.setNewsListLink("link for source three");
        newsSourceList3.setNewsListSource("Dainik Bhaskar");
        newsSourceList.setSourceIndex(0);

        newsSourceListHashMap.put("Source 3", newsSourceList3);


        newsInfo.setNewsSourceListHashMap(newsSourceListHashMap);

        HashMap<String, Long> newsTweetHashMap = new HashMap<>();
        newsTweetHashMap.put("tweet1", 858655214517141504L);
        newsTweetHashMap.put("tweet2", 858758633051627520L);
        newsTweetHashMap.put("tweet3", 858769350437724160L);

        newsInfo.setNewsTweetListHashMap(newsTweetHashMap);


        String filename = "newsinfo1.ser";

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("/sdcard/save_object.bin"))); //Select where you wish to save the file...
            oos.writeObject(newsInfo); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            oos.close();// close the stream
        } catch (Exception ex) {
            Log.v("Serialization  Error : ", ex.getMessage());
            ex.printStackTrace();
        }

    }

    public void deserializeNewsInfo() {
        String filename = "newsinfo1.ser";
        NewsInfo newsInfo = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("/sdcard/save_object.bin")));
            newsInfo = (NewsInfo) ois.readObject();

        } catch (Exception ex) {
            Log.v("Serialization  Error : ", ex.getMessage());
            ex.printStackTrace();
        }
        Log.d(TAG, "deserializeNewsInfo: " + newsInfo);
    }

    public void openAlertDialog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        LayoutInflater inflater = getLayoutInflater();

        final View modifyView = inflater.inflate(R.layout.dialoguebox_edittext_layout, null);

        builder.setView(modifyView);

        final EditText editText = (EditText) modifyView.findViewById(R.id.dialogue_editText);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // String m_Text = input.getText().toString();
                // Toast.makeText(MainActivity.this, "text is "+m_Text, Toast.LENGTH_SHORT).show();

                String text = editText.getText().toString();
                Toast.makeText(MainActivity.this, "text is - " + text, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateActivityData() {
        TextView textView = (TextView) findViewById(R.id.newsFeed_newsHeading_textView);
        textView.setText(newsInfo.getNewsHeadline());
        textView = (TextView) findViewById(R.id.newsFeed_newsSummary_textView);
        textView.setText(newsInfo.getNewsSummary());
        textView = (TextView) findViewById(R.id.newsFeed_newsSource_textView);
        //textView.setText(newsInfo.getNewsSource());

    }

    private void addTweetView() {
        final LinearLayout myLayout
                = (LinearLayout) findViewById(R.id.my_tweet_layout);

        myLayout.removeAllViews();
        // TODO: Use a more specific parent

        // TODO: Base this Tweet ID on some data from elsewhere in your app


        for (int i = 0; i < tweetArrayList.size(); i++) {


            TweetUtils.loadTweet(tweetArrayList.get(i), new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    TweetView tweetView = new TweetView(MainActivity.this, result.data);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(30, 20, 30, 20);


                    myLayout.addView(tweetView, layoutParams);
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.d("TwitterKit", "Load Tweet failure", exception);
                }
            });
        }


    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.newsFeed_newsSourceList_recyclerView);
        newsSourcesRecyclerAdapter = new NewsSourcesRecyclerAdapter(newsSourceListArrayList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(newsSourcesRecyclerAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(NewsFeedActivity.this, "Item clicked = " + position, Toast.LENGTH_SHORT).show();
                //newsSourceListArrayList.remove(position);
                //newsSourcesRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLongClick(View view, int position) {

                openNewsSourceListDialogue(position);
            }
        }));

    }

    private void openNewsSourceListDialogue(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add News Source");

        LayoutInflater inflater = getLayoutInflater();

        final View modifyView = inflater.inflate(R.layout.dialogue_newssource_layout, null);

        builder.setView(modifyView);

        final EditText editText = (EditText) modifyView.findViewById(R.id.dialogue_newssource_heading_editText);
        editText.setText(newsSourceListArrayList.get(position).getNewsListHeading());
        final EditText articleEdittext = (EditText) modifyView.findViewById(R.id.dialogue_newssource_article_editText);
        articleEdittext.setText(newsSourceListArrayList.get(position).getNewsListArticle());
        final Spinner spinner = (Spinner) modifyView.findViewById(R.id.dialogue_newssource_source_spinner);
        spinner.setSelection(newsSourceListArrayList.get(position).getSourceIndex());
        // Set up the buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // String m_Text = input.getText().toString();
                // Toast.makeText(MainActivity.this, "text is "+m_Text, Toast.LENGTH_SHORT).show();

                String headingtext = editText.getText().toString();


                NewsSourceList newsSourceList = new NewsSourceList();
                newsSourceList.setNewsListHeading(headingtext);

                headingtext = articleEdittext.getText().toString();
                newsSourceList.setNewsListArticle(headingtext);

                int itempostion = spinner.getSelectedItemPosition();

                newsSourceList.setNewsListSource(getResources().getStringArray(R.array.source_list)[itempostion]);
                newsSourceList.setNewsSourceShort(getResources().getStringArray(R.array.source_list_short)[itempostion]);

                newsSourceList.setSourceIndex(itempostion);

                newsSourceListArrayList.remove(position);
                newsSourceListArrayList.add(newsSourceList);
                newsSourcesRecyclerAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newsSourceListArrayList.remove(position);
                newsSourcesRecyclerAdapter.notifyDataSetChanged();

            }
        });

        builder.show();
    }

    public void onHeadingClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("News Heading");

        LayoutInflater inflater = getLayoutInflater();

        final View modifyView = inflater.inflate(R.layout.dialoguebox_edittext_layout, null);

        builder.setView(modifyView);

        final EditText editText = (EditText) modifyView.findViewById(R.id.dialogue_editText);
        if (newsMetaInfo.getNewsHeading() != null) {
            editText.setText(newsMetaInfo.getNewsHeading());
        }
// Set up the buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // String m_Text = input.getText().toString();
                // Toast.makeText(MainActivity.this, "text is "+m_Text, Toast.LENGTH_SHORT).show();

                String text = editText.getText().toString();
                newsInfo.setNewsHeadline(text);
                newsMetaInfo.setNewsHeading(text);
                TextView textView = (TextView) findViewById(R.id.newsFeed_newsHeading_textView);
                textView.setText(newsInfo.getNewsHeadline());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public void onNewsSummaryClick(View view) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("News Article");

        LayoutInflater inflater = getLayoutInflater();

        final View modifyView = inflater.inflate(R.layout.dialoguebox_edittext_layout, null);

        builder.setView(modifyView);

        final EditText editText = (EditText) modifyView.findViewById(R.id.dialogue_editText);
        if (newsInfo.getNewsSummary() != null) {
            editText.setText(newsInfo.getNewsSummary());
        }
// Set up the buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // String m_Text = input.getText().toString();
                // Toast.makeText(MainActivity.this, "text is "+m_Text, Toast.LENGTH_SHORT).show();

                String text = editText.getText().toString();
                newsInfo.setNewsSummary(text);
                TextView textView = (TextView) findViewById(R.id.newsFeed_newsSummary_textView);
                textView.setText(newsInfo.getNewsSummary());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void onNewsSourceClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Tweet to delete")
                .setItems(R.array.source_list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        String sourcename = getResources().getStringArray(R.array.source_list)[which];

                        newsInfo.setNewsSource(sourcename);
                        newsMetaInfo.setNewsSource(sourcename);
                        sourcename = getResources().getStringArray(R.array.source_list_short)[which];
                        newsMetaInfo.setNewsSourceShort(sourcename);
                        newsInfo.setNewsSourceShort(sourcename);

                        newsMetaInfo.setNewsSourceimageIndex(which);

                        TextView textView = (TextView) findViewById(R.id.newsFeed_newsSourceshort_textView);
                        textView.setText(newsInfo.getNewsSourceShort());

                        textView = (TextView) findViewById(R.id.newsFeed_newsSource_textView);
                        textView.setText(newsInfo.getNewsSource());


                        Toast.makeText(MainActivity.this, "Source is" + sourcename, Toast.LENGTH_SHORT).show();


                    }
                });
        builder.create();
        builder.show();

    }

    public void onNewsImageClick(View view) {
        selectImageFromStorage();
    }

    public void onAddSourceButtonClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add News Source");

        LayoutInflater inflater = getLayoutInflater();

        final View modifyView = inflater.inflate(R.layout.dialogue_newssource_layout, null);

        builder.setView(modifyView);

        final EditText editText = (EditText) modifyView.findViewById(R.id.dialogue_newssource_heading_editText);
        final EditText articleEdittext = (EditText) modifyView.findViewById(R.id.dialogue_newssource_article_editText);

        final Spinner spinner = (Spinner) modifyView.findViewById(R.id.dialogue_newssource_source_spinner);
// Set up the buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // String m_Text = input.getText().toString();
                // Toast.makeText(MainActivity.this, "text is "+m_Text, Toast.LENGTH_SHORT).show();

                String headingtext = editText.getText().toString();


                NewsSourceList newsSourceList = new NewsSourceList();
                newsSourceList.setNewsListHeading(headingtext);

                headingtext = articleEdittext.getText().toString();
                newsSourceList.setNewsListArticle(headingtext);

                int itempostion = spinner.getSelectedItemPosition();

                newsSourceList.setNewsListSource(getResources().getStringArray(R.array.source_list)[itempostion]);
                newsSourceList.setNewsSourceShort(getResources().getStringArray(R.array.source_list_short)[itempostion]);

                newsSourceList.setSourceIndex(itempostion);

                newsSourceListArrayList.add(newsSourceList);
                newsSourcesRecyclerAdapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void onAddTweetClik(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Tweet id");

        LayoutInflater inflater = getLayoutInflater();

        final View modifyView = inflater.inflate(R.layout.dialoguebox_edittext_layout, null);

        builder.setView(modifyView);

        final EditText editText = (EditText) modifyView.findViewById(R.id.dialogue_editText);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);


// Set up the buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // String m_Text = input.getText().toString();
                // Toast.makeText(MainActivity.this, "text is "+m_Text, Toast.LENGTH_SHORT).show();

                String text = editText.getText().toString();
                Long tweetid = Long.parseLong(text);


                tweetArrayList.add(tweetid);
                addTweetView();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }

    public void onPreviewClick(View view) {
        getObjectPrefrenceListDialogue();
        //reLaunchActivity();
    }

    public void onSchduleClick(View view) {


        //buildNewsPost();

        buildNewsPost();


        Toast.makeText(this, "Added in memory " + putObjectInPrefrence(), Toast.LENGTH_SHORT).show();

        //Toast.makeText(this, "Added in memory "+putObjectInPrefrence(), Toast.LENGTH_SHORT).show();

    }

    public void setPreferenceObject1(NewsInfo newsInfo, NewsMetaInfo newsMetaInfo, Uri ImagePath) {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(newsInfo); // myObject - instance of MyObject
        prefsEditor.putString("MyObject1newsInfo", json);

        json = gson.toJson(newsMetaInfo);
        prefsEditor.putString("MyObject1newsMetaInfo", json);

        //json = gson.toJson(imageUri);

        //prefsEditor.putString("MyObject1newsImagePath", imageUri.getPath());

        prefsEditor.putBoolean("MyObject1isEmpty", false);

        prefsEditor.apply();
    }

    public void setPreferenceObject2(NewsInfo newsInfo, NewsMetaInfo newsMetaInfo, Uri ImagePath) {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(newsInfo); // myObject - instance of MyObject
        prefsEditor.putString("MyObject2newsInfo", json);

        json = gson.toJson(newsMetaInfo);
        prefsEditor.putString("MyObject2newsMetaInfo", json);

        //json = gson.toJson(imageUri);

        //prefsEditor.putString("MyObject2newsImagePath", imageUri.getPath());

        prefsEditor.putBoolean("MyObject2isEmpty", false);

        prefsEditor.apply();
    }

    public void setPreferenceObject3(NewsInfo newsInfo, NewsMetaInfo newsMetaInfo, Uri ImagePath) {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(newsInfo); // myObject - instance of MyObject
        prefsEditor.putString("MyObject3newsInfo", json);

        json = gson.toJson(newsMetaInfo);
        prefsEditor.putString("MyObject3newsMetaInfo", json);

        //json = gson.toJson(imageUri);

        //prefsEditor.putString("MyObject3newsImagePath", imageUri.getPath());

        prefsEditor.putBoolean("MyObject3isEmpty", false);

        prefsEditor.apply();
    }

    public void setPreferenceObject4(NewsInfo newsInfo, NewsMetaInfo newsMetaInfo, Uri ImagePath) {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(newsInfo); // myObject - instance of MyObject
        prefsEditor.putString("MyObject4newsInfo", json);

        json = gson.toJson(newsMetaInfo);
        prefsEditor.putString("MyObject4newsMetaInfo", json);

        //json = gson.toJson(imageUri);

        //prefsEditor.putString("MyObject4newsImagePath", imageUri.getPath());

        prefsEditor.putBoolean("MyObject4isEmpty", false);

        prefsEditor.apply();
    }

    public void setPreferenceObject5(NewsInfo newsInfo, NewsMetaInfo newsMetaInfo, Uri ImagePath) {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(newsInfo); // myObject - instance of MyObject
        prefsEditor.putString("MyObject5newsInfo", json);

        json = gson.toJson(newsMetaInfo);
        prefsEditor.putString("MyObject5newsMetaInfo", json);

        //json = gson.toJson(imageUri);
        //prefsEditor.putString("MyObject5newsImagePath", imageUri.getPath());

        prefsEditor.putBoolean("MyObject5isEmpty", false);

        prefsEditor.apply();
    }

    public boolean putObjectInPrefrence() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        if (mPrefs.getBoolean("MyObject1isEmpty", true)) {
            setPreferenceObject1(newsInfo, newsMetaInfo, imageUri);
            return true;
        } else if (mPrefs.getBoolean("MyObject2isEmpty", true)) {
            setPreferenceObject2(newsInfo, newsMetaInfo, imageUri);
            return true;
        } else if (mPrefs.getBoolean("MyObject3isEmpty", true)) {
            setPreferenceObject3(newsInfo, newsMetaInfo, imageUri);
            return true;
        } else if (mPrefs.getBoolean("MyObject4isEmpty", true)) {
            setPreferenceObject4(newsInfo, newsMetaInfo, imageUri);

            return true;
        } else if (mPrefs.getBoolean("MyObject5isEmpty", true)) {
            setPreferenceObject5(newsInfo, newsMetaInfo, imageUri);
            return true;
        }

        return false;
    }

    public void getObjectPrefrenceListDialogue() {
        String[] stringArray = {"", "", "", "", ""};
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = "";
        NewsMetaInfo newsMetaInfo = new NewsMetaInfo();
        try {
            try {
                if (!mPrefs.getBoolean("MyObject1isEmpty", true)) {
                    json = mPrefs.getString("MyObject1newsMetaInfo", "");

                    if (!json.isEmpty()) {
                        newsMetaInfo = gson.fromJson(json, NewsMetaInfo.class);

                        stringArray[0] = newsMetaInfo.getNewsHeading();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (!mPrefs.getBoolean("MyObject2isEmpty", true)) {
                    json = mPrefs.getString("MyObject2newsMetaInfo", "");

                    if (!json.isEmpty()) {
                        newsMetaInfo = gson.fromJson(json, NewsMetaInfo.class);
                        stringArray[1] = newsMetaInfo.getNewsHeading();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (!mPrefs.getBoolean("MyObject3isEmpty", true)) {
                    json = mPrefs.getString("MyObject3newsMetaInfo", "");


                    if (!json.isEmpty()) {
                        newsMetaInfo = gson.fromJson(json, NewsMetaInfo.class);
                        stringArray[2] = newsMetaInfo.getNewsHeading();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (mPrefs.getBoolean("MyObject4isEmpty", true)) {
                    json = mPrefs.getString("MyObject4newsMetaInfo", "");

                    if (!json.isEmpty()) {
                        newsMetaInfo = gson.fromJson(json, NewsMetaInfo.class);

                        stringArray[3] = newsMetaInfo.getNewsHeading();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (mPrefs.getBoolean("MyObject5isEmpty", true)) {
                    json = mPrefs.getString("MyObject5newsMetaInfo", "");

                    if (!json.isEmpty()) {
                        newsMetaInfo = gson.fromJson(json, NewsMetaInfo.class);
                        stringArray[4] = newsMetaInfo.getNewsHeading();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select post to open")
                .setItems(stringArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        switch (which) {
                            case 0:
                                getPreferenceObject1();
                                break;
                            case 1:
                                getPreferenceObject2();
                                break;
                            case 2:
                                getPreferenceObject3();
                                break;
                            case 3:
                                getPreferenceObject4();
                                break;
                            case 4:
                                getPreferenceObject5();
                                break;
                        }

                    }
                });
        builder.create();
        builder.show();


    }

    public void getPreferenceObject1() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        try {
            boolean isEmpty = mPrefs.getBoolean("MyObject1isEmpty", true);

            Gson gson = new Gson();


            String json = mPrefs.getString("MyObject1newsMetaInfo", "");

            if (!json.isEmpty()) {
                newsMetaInfo = gson.fromJson(json, NewsMetaInfo.class);
            }


            json = mPrefs.getString("MyObject1newsInfo", "");

            if (!json.isEmpty()) {
                newsInfo = gson.fromJson(json, NewsInfo.class);
            }

            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putBoolean("MyObject1isEmpty", true);
            prefsEditor.apply();
            initializeActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //imagePath = mPrefs.getString("MyObject1newsImagePath", "");
        //imagePath = gson.fromJson(json, Uri.class);


    }

    private void initializeActivity() {

        TextView textView = (TextView) findViewById(R.id.newsFeed_newsHeading_textView);
        textView.setText(newsInfo.getNewsHeadline());


        textView = (TextView) findViewById(R.id.newsFeed_newsSummary_textView);
        textView.setText(newsInfo.getNewsSummary());


        newsSourceListArrayList.clear();
        for (NewsSourceList newssourceList : newsInfo.getNewsSourceListHashMap().values()) {
            newsSourceListArrayList.add(newssourceList);
        }
        tweetArrayList.clear();
        initializeRecyclerView();
        for (Long tweetid : newsInfo.getNewsTweetListHashMap().values()) {
            tweetArrayList.add(tweetid);
        }
        addTweetView();


    }

    public void getPreferenceObject2() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        try {
            boolean isEmpty = mPrefs.getBoolean("MyObject2isEmpty", true);

            Gson gson = new Gson();


            String json = mPrefs.getString("MyObject2newsMetaInfo", "");

            if (!json.isEmpty()) {
                newsMetaInfo = gson.fromJson(json, NewsMetaInfo.class);
            }


            json = mPrefs.getString("MyObject2newsInfo", "");

            if (!json.isEmpty()) {
                newsInfo = gson.fromJson(json, NewsInfo.class);
            }

            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putBoolean("MyObject2isEmpty", true);

            prefsEditor.apply();

            //imagePath = mPrefs.getString("MyObject2newsImagePath", "");
            //imageUri = gson.fromJson(json, Uri.class);
            //Toast.makeText(this, "image pathis "+imagePath, Toast.LENGTH_SHORT).show();
            initializeActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getPreferenceObject3() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        try {
            boolean isEmpty = mPrefs.getBoolean("MyObject3isEmpty", true);

            Gson gson = new Gson();


            String json = mPrefs.getString("MyObject3newsMetaInfo", "");

            if (!json.isEmpty()) {
                newsMetaInfo = gson.fromJson(json, NewsMetaInfo.class);
            }

            json = mPrefs.getString("MyObject3newsInfo", "");

            if (!json.isEmpty()) {
                newsInfo = gson.fromJson(json, NewsInfo.class);
            }


            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putBoolean("MyObject3isEmpty", true);

            //imagePath = mPrefs.getString("MyObject3newsImagePath", "");
            // = gson.fromJson(json, Uri.class);
            prefsEditor.apply();
            initializeActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getPreferenceObject4() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        try {
            boolean isEmpty = mPrefs.getBoolean("MyObject4isEmpty", true);

            Gson gson = new Gson();


            String json = mPrefs.getString("MyObject4newsMetaInfo", "");

            if (!json.isEmpty()) {
                newsMetaInfo = gson.fromJson(json, NewsMetaInfo.class);
            }

            json = mPrefs.getString("MyObject4newsInfo", "");

            if (!json.isEmpty()) {
                newsInfo = gson.fromJson(json, NewsInfo.class);
            }


            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putBoolean("MyObject4isEmpty", true);

            //imagePath = mPrefs.getString("MyObject4newsImagePath", "");
            //imageUri = gson.fromJson(json, Uri.class);
            prefsEditor.apply();
            initializeActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getPreferenceObject5() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        try {
            boolean isEmpty = mPrefs.getBoolean("MyObject5isEmpty", true);

            Gson gson = new Gson();


            String json = mPrefs.getString("MyObject5newsMetaInfo", "");

            if (!json.isEmpty()) {
                newsMetaInfo = gson.fromJson(json, NewsMetaInfo.class);
            }

            json = mPrefs.getString("MyObject5newsInfo", "");

            if (!json.isEmpty()) {
                newsInfo = gson.fromJson(json, NewsInfo.class);
            }


            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putBoolean("MyObject5isEmpty", true);

            //imagePath = mPrefs.getString("MyObject5newsImagePath", "");
            //imageUri = gson.fromJson(json, Uri.class);
            prefsEditor.apply();
            initializeActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void onTweetViewClick(View view) {
        String[] tweetarray = new String[tweetArrayList.size()];
        for (int i = 0; i < tweetArrayList.size(); i++) {
            tweetarray[i] = tweetArrayList.get(i) + "";

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Tweet to delete")
                .setItems(tweetarray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        tweetArrayList.remove(which);
                        addTweetView();
                    }
                });
        builder.create();
        builder.show();
    }

    public void onUploadNewsArticleClick(View view) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

        LayoutInflater inflater = getLayoutInflater();

        final View modifyView = inflater.inflate(R.layout.dialoguebox_edittext_layout, null);

        builder.setView(modifyView);

        final EditText editText = (EditText) modifyView.findViewById(R.id.dialogue_editText);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);


// Set up the buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // String m_Text = input.getText().toString();
                // Toast.makeText(MainActivity.this, "text is "+m_Text, Toast.LENGTH_SHORT).show();

                int password = Integer.parseInt(editText.getText().toString().trim());

                if (password == 2018) {
                    Toast.makeText(MainActivity.this, "Password Accepted", Toast.LENGTH_SHORT).show();
                    postNewsFullArticle();
                } else {

                    Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }

    private void postNewsFullArticle() {


        if (!buildNewsPost()) {

            return;
        } else {
            Toast.makeText(this, "Uploading Post Please Wait ...", Toast.LENGTH_SHORT).show();
        }

        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Uploading Post please Wait");
        pd.show();


        DatabaseHandlerFirebase databaseHandlerFirebase = new DatabaseHandlerFirebase();
        databaseHandlerFirebase.addNewsListListner(new DatabaseHandlerFirebase.DataBaseHandlerNewsUploadListner() {
            @Override
            public void onNewsList(ArrayList<NewsMetaInfo> newsMetaInfoArrayList) {

            }

            @Override
            public void onNewsImageFetched(File imageFile) {


            }

            @Override
            public void onCancel() {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Error uploading Post");
                builder.setMessage("Contact Developer for more info");


// Set up the buttons
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // String m_Text = input.getText().toString();
                        // Toast.makeText(MainActivity.this, "text is "+m_Text, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            }

            @Override
            public void onNewsImageLink(String ImageLink) {

                isImageUploaded = true;
                if (isNewsInfoUploaded) {
                    pd.dismiss();

                    reLaunchActivity();

                } else {
                    pd.setMessage("Uploading Post please Wait \n" + "Image Uploaded \n" + "uploading News ");
                }

            }

            @Override
            public void onNewsImageProgress(int progressComplete) {

            }

            @Override
            public void onNewsFullArticle(int newsIndex) {

                isNewsInfoUploaded = true;
                if (isImageUploaded) {
                    pd.dismiss();
                    reLaunchActivity();
                } else {
                    pd.setMessage("Uploading Post please Wait \n" + "News Uploaded \n" + "uploading Image ");

                }

            }


        });

        databaseHandlerFirebase.insertNewsFullArticle(newsMetaInfo, newsInfo, imageUri);


    }

    private void reLaunchActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Post Uploaded ");
        builder.setMessage("Press ok To re launch app \n Exit to exit application ");


// Set up the buttons
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // String m_Text = input.getText().toString();
                // Toast.makeText(MainActivity.this, "text is "+m_Text, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                finish();
            }
        });

        builder.show();

    }

    public boolean buildNewsPost() {


        newsSourceListHashMap = new HashMap<>();
        for (int i = 0; i < newsSourceListArrayList.size(); i++) {


            newsSourceListHashMap.put("source" + i, newsSourceListArrayList.get(i));
            NewsSourceList newsSource = newsSourceListArrayList.get(i);
            if (newsSource.getNewsListHeading().length() < 5) {
                Toast.makeText(this, "News Source heading too small", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (newsSource.getNewsListArticle().length() < 10) {
                Toast.makeText(this, "News source article too small", Toast.LENGTH_SHORT).show();
                return false;

            }
            if (newsSource.getNewsListSource().length() < 1) {
                Toast.makeText(this, "News Source not selecteed ", Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        tweetHashMap = new HashMap<>();
        for (int i = 0; i < tweetArrayList.size(); i++) {
            tweetHashMap.put("tweet" + i, tweetArrayList.get(i));

            if (tweetArrayList.get(i) < 0L) {
                Toast.makeText(this, "Invalid tweet id", Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        newsInfo.setNewsSourceListHashMap(newsSourceListHashMap);
        newsInfo.setNewsTweetListHashMap(tweetHashMap);


        if (newsInfo.getNewsHeadline().length() < 5) {
            Toast.makeText(this, "News HEading too short", Toast.LENGTH_SHORT).show();
            return false;

        }
        newsMetaInfo = new NewsMetaInfo();
        newsMetaInfo.setNewsHeading(newsInfo.getNewsHeadline());
        if (newsInfo.getNewsSource().length() < 1) {
            Toast.makeText(this, "News source not selected ", Toast.LENGTH_SHORT).show();
            return false;
        }
        newsMetaInfo.setNewsSource(newsInfo.getNewsSource());
        newsMetaInfo.setNewsSourceShort(newsInfo.getNewsSourceShort());

        if (newsInfo.getNewsSummary().length() < 10) {
            Toast.makeText(this, "NEws article is too short ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (imageUri == null) {
            Toast.makeText(this, "News Image not selected", Toast.LENGTH_SHORT).show();
            return false;
        }

        CheckBox checkBox = (CheckBox) findViewById(R.id.newsFeed_newsNotification_checkBox);
        newsMetaInfo.setNewsNotification(checkBox.isChecked());


        newsMetaInfo.setNewsTime(System.currentTimeMillis());


        Log.d(TAG, "buildNewsPost: " + newsInfo);
        Log.d(TAG, "buildNewsPost: " + newsMetaInfo);
        Log.d(TAG, "buildNewsPost: " + imageUri);

        return true;

    }

    public void checkBuild(View view) {
        Toast.makeText(this, "Build " + buildNewsPost(), Toast.LENGTH_SHORT).show();
    }
}
