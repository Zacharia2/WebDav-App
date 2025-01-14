/*
 * Copyright (C) 2011 Patrik 乲erfeldt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.realms.mws.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import xyz.realms.mws.R;
import xyz.realms.mws.ui.widget.viewflow.TitleFlowIndicator;
import xyz.realms.mws.ui.widget.viewflow.TitleProvider;
import xyz.realms.mws.ui.widget.viewflow.ViewFlow;

public class TitleViewFlowExample extends Activity {

    private ViewFlow viewFlow;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_title);
        setContentView(R.layout.title_layout);

        viewFlow = findViewById(R.id.view_flow);
        AndroidVersionAdapter adapter = new AndroidVersionAdapter(this);
        viewFlow.setAdapter(adapter, 1);
        TitleFlowIndicator indicator = findViewById(R.id.view_flow_indic);
        indicator.setTitleProvider(adapter);
        viewFlow.setFlowIndicator(indicator);

    }

    /* If your min SDK version is < 8 you need to trigger the onConfigurationChanged in ViewFlow manually, like this */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        viewFlow.onConfigurationChanged(newConfig);
    }

}

class AndroidVersionAdapter extends BaseAdapter implements TitleProvider {

    private static final String[] versions = {"1.5", "1.6", "2.1", "2.2", "2.3", "3.0", "x.y"};
    private static final String[] names = {"Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb", "IceCream Sandwich"};
    private final LayoutInflater mInflater;

    public AndroidVersionAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.flow_item, null);
        }
        ((TextView) convertView.findViewById(R.id.textLabel)).setText(versions[position]);
        return convertView;
    }

    /* (non-Javadoc)
     * @see xyz.realms.mws.widget.viewflow.TitleProvider#getTitle(int)
     */
    @Override
    public String getTitle(int position) {
        return names[position];
    }

}