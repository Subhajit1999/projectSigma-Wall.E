package com.subhajitkar.projectsigma.hydra.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.subhajitkar.projectsigma.hydra.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {
    private static final String TAG = "AboutFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //app version element
        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0.1");

        View aboutPage = new AboutPage(getContext())
                .isRTL(false)
                .setImage(R.drawable.about_circle_icon)
                .setDescription(getResources().getString(R.string.app_name)+getResources().getString(R.string.about_app_desc))
                .addItem(versionElement)
//                .addFacebook("the.medy")
//                .addTwitter("medyo80")
//                .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addGroup("Connect:")
                .addPlayStore(getContext().getPackageName())
                .addGitHub("Subhajit1999")
//                .addInstagram("medyo80")
                .addGroup("Feedback & Support:")
                .addEmail("developercontact.subhajitkar@gmail.com")
                .create();
        return aboutPage;
    }
}
