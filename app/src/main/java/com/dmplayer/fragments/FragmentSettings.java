/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import com.dmplayer.R;
import com.dmplayer.activities.DMPlayerBaseActivity;
import com.dmplayer.phonemidea.DMPlayerUtility;
import com.dmplayer.utility.ProfileDialog;
import com.dmplayer.utility.ThemeDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;

public class FragmentSettings extends Fragment implements View.OnClickListener {

    public final static int GALLERY_REQUEST = 1;
    public final static int CAMERA_REQUEST = 2;

    public final static String HEADER_BACKGROUND = "HEADER_BACKGROUND";
    public final static String AVATAR = "AVATAR";
    public final static String NAME = "NAME";


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String TAG = "FragmentSettings";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_settings, null);
        setupInitialViews(rootview);
        return rootview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void setupInitialViews(View rootview) {
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        rootview.findViewById(R.id.relativeLayout_choose_theme).setOnClickListener(this);
        rootview.findViewById(R.id.relativeLayout_customize_profile).setOnClickListener(this);
        rootview.findViewById(R.id.relativeLayout_change_header_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativeLayout_choose_theme:
                showColorChooseDialog();
                break;

            case R.id.relativeLayout_customize_profile:
                showColorProfileDialog();
                break;

            case R.id.relativeLayout_change_header_back:
                Intent toGallery = new Intent(Intent.ACTION_PICK);
                toGallery.setType("image/*");
                startActivityForResult(toGallery, GALLERY_REQUEST);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = returnedIntent.getData();

                    String pathToBackground = copyBackgroundToStorage(selectedImage);
                    setHeaderBackground(pathToBackground);

                    startActivity(new Intent(getActivity(), DMPlayerBaseActivity.class));
                    getActivity().finish();
                    getActivity().overridePendingTransition(0, 0);
                }
                break;
        }
    }

    public void setThemeFragment(int theme) {
        editor = sharedPreferences.edit();
        editor.putInt("THEME", theme).apply();
    }

    public void setHeaderBackground(String picture) {
        editor = sharedPreferences.edit();
        editor.putString(HEADER_BACKGROUND, picture.toString()).apply();
    }

    private void showColorChooseDialog() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        ThemeDialog dialog = new ThemeDialog();
        dialog.setOnItemChoose(new ThemeDialog.OnItemChoose() {
            @Override
            public void onClick(int position) {
                setThemeFragment(position);
            }

            @Override
            public void onSaveChange() {
                startActivity(new Intent(getActivity(), DMPlayerBaseActivity.class));
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
            }
        });
        dialog.show(fragmentManager, "fragment_color_chooser");
    }

    private void showColorProfileDialog() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        ProfileDialog dialog = new ProfileDialog();
        dialog.show(fragmentManager, "fragment_profile");
    }

    private String copyBackgroundToStorage(Uri picture) {
        File backgroundSource = new File (DMPlayerUtility.getRealPathFromURI(getActivity(), picture));
        File backgroundDest = new File(ProfileDialog.checkPhotoDirectory() + "/" + "header_background" +
                backgroundSource
                        .getPath()
                        .substring(backgroundSource
                                .getPath()
                                .lastIndexOf(".")));
        try{
            DMPlayerUtility.copyFile(backgroundSource, backgroundDest);
        } catch (IOException ioex) {
            Log.e(TAG, "Error occurred while coping background");
        }
        return backgroundDest.toURI().toString();
    }
}