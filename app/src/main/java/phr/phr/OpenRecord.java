package phr.phr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

import phr.lib.Auth_Access;
import phr.lib.HealthProfessional;
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.User;

/**
 * Created by Anupam on 29-Apr-18.
 *
 * This view shows the user the attachment that may have been added to the record
 *
 */

public class OpenRecord extends AppCompatActivity {
    WebView webView;

    //These are used when the user needs to go back
    String GOTO, url;
    int position;
    Record record;
    Patient patient;
    HealthProfessional healthProfessional;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_record);
        webView = findViewById(R.id.webView);

        // this is used to desicide which page the user arrived here from, and will send them back to that page. (there are 2 - 3 pages that can come here from)
        GOTO = (String)getIntent().getExtras().get("GOTO");


        //get the nessasry data from the previous activity
        ArrayList<Record> list = (ArrayList<Record>)getIntent().getExtras().get("RECORD");
        ArrayList<User> list2 = (ArrayList<User>)getIntent().getExtras().get("USER");
        position = (int)getIntent().getExtras().get("POS");
        record = list.get(0);
        patient = (Patient)list2.get(0);
        healthProfessional = (HealthProfessional)list2.get(1);

        //used to check what type of file is being recieved
        boolean isImage=false, isFile=false;


        String fileName = record.getFilename();
        //generate the url for the file the "phr_auth/uploads" is gotten caus thats how the path the files are uploaded to
        url = Auth_Access.IP+"/PHR_AUTH/uploads/"+patient.getEmail()+"/"+fileName;

        //get the extension type of the file
        String[] split = fileName.split("\\.");
        String extension = split[split.length-1];

        //check if the file is a image or file type
        for(String e:Lib.IMAGE_EXT){
            if(extension.equals(e))
                isImage=true;
        }
        for(String e:Lib.FILE_EXT){
            if(extension.equals(e))
                isFile=true;
        }

        //the methods used below prevent the need for the file to be downloaded to the physicians phone.

        //using the google docs, display the file to the user
        if(isFile) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.loadUrl("http://docs.google.com/gview?embedded=true&url="+url);

        }
        //open the image as a webimage for the user
        else if(isImage) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.loadUrl(url);
        }
        else{
            Toast toast = Toast.makeText(OpenRecord.this, "File Type Error", Toast.LENGTH_SHORT);
            toast.show();
        }


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //control the flow of the app
        Intent intent = new Intent(getApplicationContext(), HealthProfessionalRecordView.class);
        ArrayList<Record> list = new ArrayList<Record>();
        list.add(record);
        ArrayList<User> list2 = new ArrayList<User>();
        list2.add(patient);
        list2.add(healthProfessional);
        intent.putExtra("RECORD",list);
        intent.putExtra("USER",list2);
        intent.putExtra("GOTO", GOTO);
        intent.putExtra("POS",position);
        startActivity(intent);
    }
}
