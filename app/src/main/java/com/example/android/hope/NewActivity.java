package com.example.android.hope;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//import android.net.Uri;
//import android.view.View;
//import android.widget.Button;

public class NewActivity extends AppCompatActivity {
    private WebView myWebView;
    String website;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i=getIntent();
        website=i.getStringExtra("website");
        if(website==null)
            website="https://www.google.com";
        setPhoneNumber();

LoadPage();
    }
void LoadPage()
{
    myWebView = findViewById(R.id.webview);
    WebSettings webSettings = myWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAppCacheEnabled(true);

    myWebView.loadUrl(website);

    myWebView.setWebViewClient(new WebViewClient());

}
    @Override//just a comment for git

    public void onBackPressed() {
        if (myWebView.canGoBack())
            myWebView.goBack();
        else
            {//sign out from app even if back is pressed
            FirebaseAuth.getInstance().signOut();
            super.onBackPressed();
        }
    }

    private void setPhoneNumber(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        try {
            Toast.makeText(this,user.getPhoneNumber().toString()+" is logged in",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this,"Phone number not found",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main,menu);
                return true;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if(id==R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(NewActivity.this,LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

