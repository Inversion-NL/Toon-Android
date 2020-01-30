package com.toonapps.toon.view;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.toonapps.toon.R;
import com.toonapps.toon.view.fragments.LoginFragment;

public class ConnectionWizardActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSkipEnabled(false);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.connectionWizard_intro_title)
                .description(getString(R.string.connectionWizard_intro_text) + "\n" +
                        getString(R.string.connectionWizard_intro_text2) + "\n" +
                        getString(R.string.connectionWizard_intro_text3))
                .image(R.drawable.eneco_toon)
                .background(android.R.color.holo_blue_light)
                .backgroundDark(android.R.color.holo_blue_dark)
                .scrollable(false)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_light)
                .backgroundDark(android.R.color.holo_blue_dark)
                .fragment(LoginFragment.newInstance())
                .build());
    }
}