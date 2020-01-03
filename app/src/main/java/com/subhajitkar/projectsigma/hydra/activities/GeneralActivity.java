package com.subhajitkar.projectsigma.hydra.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.fragments.WebViewFragment;
import com.subhajitkar.projectsigma.hydra.utils.StaticUtils;

public class GeneralActivity extends AppCompatActivity {
    private static final String TAG = "GeneralActivity";

    private String fragment_id,webViewUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: creating activity view with base initialization");
        //getting intent data
        fragment_id = getIntent().getStringExtra(StaticUtils.KEY_INTENT_GENERAL);
        if (fragment_id.equals("webView")) {  //if fullscreen intent callback
            webViewUrl = getIntent().getStringExtra(StaticUtils.KEY_WEBURL);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.BLACK);
            }
            setContentView(R.layout.activity_general);
        }

        //fragment callback
        if (fragment_id.equals("webView")){   //webView fragment
            WebViewFragment fragment = new WebViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString(StaticUtils.KEY_WEBURL,webViewUrl);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                    fragment).commit();
        }
    }
}
