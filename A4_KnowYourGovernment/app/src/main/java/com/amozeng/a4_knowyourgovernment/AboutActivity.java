package com.amozeng.a4_knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private String API_URL = "https://developers.google.com/civic-information/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView textView =(TextView)findViewById(R.id.GoogleAPI);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://developers.google.com/civic-information/'> Google Civic Information API </a>";
        textView.setText(Html.fromHtml(text));
    }

    public void clickAPI(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(API_URL));;
        startActivity(intent);
    }
}