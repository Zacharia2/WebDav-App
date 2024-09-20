package com.xinglan.webdavserver.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xinglan.webdavserver.R;
import com.xinglan.webdavserver.widget.viewflow.TitleProvider;

public class WebdavAdapter extends BaseAdapter implements TitleProvider {
    private static final int VIEW1 = 0;
    private static final int VIEW2 = 1;
    private static final int VIEW_MAX_COUNT = 2;
    private Context context;
    private LayoutInflater mInflater;
    private final int[] names = {R.string.server, R.string.about};
    private int res1;
    private int res2;

    public WebdavAdapter(Context context, int inflateRes1, int inflateRes2) {
        this.res1 = 0;
        this.res2 = 2;
        this.res1 = inflateRes1;
        this.res2 = inflateRes2;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int position) {
        return position;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 2;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return 2;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return Integer.valueOf(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        int view = getItemViewType(position);
        if (convertView == null) {
            switch (view) {
                case 0:
                    return this.mInflater.inflate(this.res1, (ViewGroup) null);
                case 1:
                    return this.mInflater.inflate(this.res2, (ViewGroup) null);
                default:
                    return convertView;
            }
        }
        return convertView;
    }

    @Override // org.taptwo.android.widget.TitleProvider
    public String getTitle(int position) {
        return this.context.getString(this.names[position]);
    }
}
