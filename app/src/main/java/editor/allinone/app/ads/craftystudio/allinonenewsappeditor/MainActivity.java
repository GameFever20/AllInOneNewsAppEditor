package editor.allinone.app.ads.craftystudio.allinonenewsappeditor;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import utils.DatabaseHandlerFirebase;
import utils.NewsInfo;
import utils.NewsMetaInfo;
import utils.NewsSourceList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Main";
    private int SELECT_PICTURE=4;
    public String imagePath="";

    private FirebaseAuth mAuth;
    private Uri imageUri;

    NewsInfo newsInfo = new NewsInfo();
    NewsMetaInfo newsMetaInfo = new NewsMetaInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        deserializeNewsInfo();

    }

    private void downloadImage( String pushKeyId) {
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
        return true;
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

    public void selectImageFromStorage(){
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
        Toast.makeText(this, "path is "+ uri.getPath(), Toast.LENGTH_SHORT).show();

        imageUri = uri;
    }


    public void uploadImageToFirebase(final Uri imageUri ,String pushKeyId){
        DatabaseHandlerFirebase databaseHandlerFirebase =new DatabaseHandlerFirebase();
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
                Toast.makeText(MainActivity.this, "Image Link "+ImageLink, Toast.LENGTH_SHORT).show();
                imagePath = ImageLink;
            }
        });

    }




    public void UploadToFireBase(View view) {
        NewsMetaInfo newsMetaInfo = new NewsMetaInfo();
        EditText editText = (EditText)findViewById(R.id.content_main_newsHeading_editText);
        newsMetaInfo.setNewsHeading(editText.getText().toString());
        editText = (EditText)findViewById(R.id.content_main_newsdate_editText);
        newsMetaInfo.setNewsDate(editText.getText().toString());
        editText = (EditText)findViewById(R.id.content_main_newscategory_editText);
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

        HashMap<String , NewsSourceList> newsSourceListHashMap =new HashMap<>();
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

        HashMap<String , Long> newsTweetHashMap =new HashMap<>();
        newsTweetHashMap.put("tweet1" ,858655214517141504L);
        newsTweetHashMap.put("tweet2" ,858758633051627520L);
        newsTweetHashMap.put("tweet3" ,858769350437724160L);

        newsInfo.setNewsTweetListHashMap(newsTweetHashMap);


        databaseHandlerFirebase.insertNewsFullArticle(newsMetaInfo ,newsInfo , imageUri);
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
        });

    }

    public void serializeNewsInfo(){

        NewsInfo newsInfo = new NewsInfo();
        newsInfo.setNewsSummary("News Sumahagdsj cb hcmb ybfd vbhre vs  Hello guys, welcome to Firebase Storage Tutorial for Android. In this tutorial we are going to learn how you can upload images to firebase storage. Infact not only images you can use this firebase storage android tutorial to upload any kind of file to firebase storage. So lets start our Firebase Storage Tutorial.\n" +
                "\n");
        newsInfo.setNewsHeadline("News withb Dummy HEading to show ui here");
        newsInfo.setNewsDate("23/04/2017");
        newsInfo.setNewsSource("Zee");
        newsInfo.setNewsCategory("Technology");
        newsInfo.setNewsNotify("yes");
        newsInfo.setNewsTime("1500");

        HashMap<String , NewsSourceList> newsSourceListHashMap =new HashMap<>();
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

        HashMap<String , Long> newsTweetHashMap =new HashMap<>();
        newsTweetHashMap.put("tweet1" ,858655214517141504L);
        newsTweetHashMap.put("tweet2" ,858758633051627520L);
        newsTweetHashMap.put("tweet3" ,858769350437724160L);

        newsInfo.setNewsTweetListHashMap(newsTweetHashMap);


        String filename = "newsinfo1.ser";

        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("/sdcard/save_object.bin"))); //Select where you wish to save the file...
            oos.writeObject(newsInfo); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            oos.close();// close the stream
        }
        catch(Exception ex)
        {
            Log.v("Serialization  Error : ",ex.getMessage());
            ex.printStackTrace();
        }

    }

    public void deserializeNewsInfo(){
        String filename = "newsinfo1.ser";
        NewsInfo newsInfo=null;
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("/sdcard/save_object.bin")));
            newsInfo = (NewsInfo) ois.readObject();

        }
        catch(Exception ex)
        {
            Log.v("Serialization  Error : ",ex.getMessage());
            ex.printStackTrace();
        }
        Log.d(TAG, "deserializeNewsInfo: "+newsInfo);
    }

    public void openAlertDialog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        LayoutInflater inflater = getLayoutInflater();

        final View modifyView = inflater.inflate(R.layout.dialoguebox_edittext_layout, null);

        builder.setView(modifyView);

        final EditText editText = (EditText)modifyView.findViewById(R.id.dialogue_editText);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // String m_Text = input.getText().toString();
               // Toast.makeText(MainActivity.this, "text is "+m_Text, Toast.LENGTH_SHORT).show();

                String text = editText.getText().toString();
                Toast.makeText(MainActivity.this, "text is - "+text, Toast.LENGTH_SHORT).show();
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

    public void onHeadingClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("News Heading");

        LayoutInflater inflater = getLayoutInflater();

        final View modifyView = inflater.inflate(R.layout.dialoguebox_edittext_layout, null);

        builder.setView(modifyView);

        final EditText editText = (EditText)modifyView.findViewById(R.id.dialogue_editText);
        if (newsMetaInfo.getNewsHeading()!=null) {
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

        final EditText editText = (EditText)modifyView.findViewById(R.id.dialogue_editText);
        if (newsInfo.getNewsSummary()!=null) {
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
    }

    public void onNewsImageClick(View view) {
        selectImageFromStorage();
    }

    public void onAddSourceButtonClick(View view) {
    }

    public void onAddTweetClik(View view) {
    }

    public void onPreviewClick(View view) {
    }

    public void onSchduleClick(View view) {
    }


}
