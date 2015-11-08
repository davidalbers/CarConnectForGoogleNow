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

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import edu.dalbers.carnowcontrol.wizard.ui.InformationFragment;

/**
 * Created by davidalbers on 9/12/15.
 */
public class InformationPage extends Page {
    private String infoToDisplay = "info";
    private String mTitle = "information";
    private String key = "info_key";
    public InformationPage(ModelCallbacks callbacks) {
        super(callbacks);
    }
    public InformationPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }
    @Override
    public Bundle getData() {
        return super.getData();
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public boolean isRequired() {
        return super.isRequired();
    }

    @Override
    void setParentKey(String parentKey) {
        super.setParentKey(parentKey);
    }

    @Override
    public Page findByKey(String key) {
        return super.findByKey(key);
    }

    @Override
    public void flattenCurrentPageSequence(ArrayList<Page> dest) {
        super.flattenCurrentPageSequence(dest);
    }

    @Override
    public Fragment createFragment() {
        return InformationFragment.create(getKey());
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {

    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted();
    }

    @Override
    public void resetData(Bundle data) {
        super.resetData(data);
    }

    @Override
    public void notifyDataChanged() {
        super.notifyDataChanged();
    }

    @Override
    public Page setRequired(boolean required) {
        return super.setRequired(required);
    }

    public String getInfoToDisplay() {
        return infoToDisplay;
    }
}
