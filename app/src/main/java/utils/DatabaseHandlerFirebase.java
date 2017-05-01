package utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import editor.allinone.app.ads.craftystudio.allinonenewsappeditor.MainActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by bunny on 23/04/17.
 */

public class DatabaseHandlerFirebase {

    private FirebaseDatabase mDatabase;
    private DataBaseHandlerNewsUploadListner dataBaseHandlerNewsUpload;



    public DatabaseHandlerFirebase() {
        mDatabase = FirebaseDatabase.getInstance();


    }

    public void insertNewsFullArticle(NewsMetaInfo newsMetaInfo , NewsInfo newsInfo ,Uri imageUri){

        // Write a message to the database

        DatabaseReference myRef = mDatabase.getReference("NewsMetaInfo");
        String pushKey= myRef.push().getKey();

        newsMetaInfo.setNewsPushKeyId(pushKey);
        myRef = mDatabase.getReference("NewsMetaInfo/"+pushKey);
        myRef.setValue(newsMetaInfo);


        newsInfo.setNewsImageLink(pushKey);
        DatabaseReference myRef2 = mDatabase.getReference("NewsInfo/"+pushKey);
        myRef2.setValue(newsInfo);


        uploadImagetoStorage(imageUri,pushKey);


    }

    public void uploadImagetoStorage(Uri imageUri  , String pushKeyId){
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child("NewsMetaInfo/newsImage"+pushKeyId+".jpg");

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        dataBaseHandlerNewsUpload.onNewsImageLink("Sucess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    public void downloadImageFromFireBase(String pushKeyId){
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child("NewsMetaInfo/newsImage"+pushKeyId+".jpg");


        File localFile = null;
        try {
            localFile = File.createTempFile("images", ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalLocalFile = localFile;
        riversRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                        dataBaseHandlerNewsUpload.onNewsImageFetched(finalLocalFile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });




    }

    public void addNewsListListner(DataBaseHandlerNewsUploadListner dataBaseHandlerNewsListListner) {

        this.dataBaseHandlerNewsUpload = dataBaseHandlerNewsListListner;

    }



    public interface DataBaseHandlerNewsUploadListner {
        public void onNewsList(ArrayList<NewsMetaInfo> newsMetaInfoArrayList);

        public void onNewsImageFetched(File imageFile);

        void onCancel();

        public void onNewsImageLink(String ImageLink);
    }


}
