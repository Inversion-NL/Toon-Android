/*
 * Copyright (c) 2020
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements
 * See the NOTICE file distributed with this work for additional information regarding copyright ownership
 * The ASF licenses this file to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.  See the License for the specific language governing permissions and limitations
 * under the License.
 */

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