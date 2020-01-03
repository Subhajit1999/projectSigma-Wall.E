package com.subhajitkar.projectsigma.hydra.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.activities.GeneralActivity;
import com.subhajitkar.projectsigma.hydra.utils.StaticUtils;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {
    private static final String TAG = "AboutFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: creating about page view");

        //app version element
        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0.1");
        versionElement.setIconDrawable(R.drawable.ic_verion);

        //app t&c element
        Element termsElement = new Element();
        termsElement.setTitle("Terms & conditions");
        termsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GeneralActivity.class);
                i.putExtra(StaticUtils.KEY_INTENT_GENERAL,"webView");
                i.putExtra(StaticUtils.KEY_WEBURL,"file:///android_asset/terms_and_conditions.html");
                startActivity(i);
            }
        });

        //app privacy element
        Element privacyElement = new Element();
        privacyElement.setTitle("Privacy policy");
        privacyElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GeneralActivity.class);
                i.putExtra(StaticUtils.KEY_INTENT_GENERAL,"webView");
                i.putExtra(StaticUtils.KEY_WEBURL,"file:///android_asset/privacy_policy.html");
                startActivity(i);
            }
        });

        //github element
        Element githubElement = new Element();
        githubElement.setIconDrawable(R.drawable.ic_github);
        githubElement.setTitle("Fork us on GitHub");
        githubElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GeneralActivity.class);
                i.putExtra(StaticUtils.KEY_INTENT_GENERAL,"webView");
                i.putExtra(StaticUtils.KEY_WEBURL,"http://www.github.com/Subhajit1999");
                startActivity(i);
            }
        });

        //Copyright
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setGravity(Gravity.CENTER);

        View aboutPage = new AboutPage(getContext())
                .isRTL(false)
                .setImage(R.drawable.about_circle_icon)
                .setDescription(getResources().getString(R.string.app_name)+getResources().getString(R.string.about_app_desc))
//                .addFacebook("the.medy")
//                .addTwitter("medyo80")
//                .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addGroup("Connect:")
                .addPlayStore(getContext().getPackageName())
                .addItem(githubElement)
//                .addInstagram("medyo80")
                .addGroup("Feedback & Support:")
                .addEmail("developercontact.subhajitkar@gmail.com")
                .addGroup("Legal Information:")
                .addItem(termsElement)
                .addItem(privacyElement)
                .addItem(versionElement)
                .addItem(copyRightsElement)
                .create();
        return aboutPage;
    }
}
