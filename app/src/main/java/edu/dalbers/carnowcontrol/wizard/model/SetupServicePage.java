/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.dalbers.carnowcontrol.wizard.model;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

import edu.dalbers.carnowcontrol.wizard.ui.SetupServiceFragment;

/**
 * Created by davidalbers on 9/12/15.
 */
public class SetupServicePage extends Page {
    public SetupServicePage(ModelCallbacks callbacks) {
        super(callbacks);
    }

    public SetupServicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
    }

    @Override
    public Fragment createFragment() {
        return SetupServiceFragment.create(getKey());
    }
}
