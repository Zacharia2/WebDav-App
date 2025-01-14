package xyz.realms.mws.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import xyz.realms.mws.R;
import xyz.realms.mws.ui.widget.viewflow.TitleProvider;

public class WebdavAdapter extends BaseAdapter implements TitleProvider {
    private static final int VIEW1 = 0;
    private static final int VIEW2 = 1;
    private static final int VIEW_MAX_COUNT = 2;
    private final int[] names = {R.string.server, R.string.about};
    private final Context context;
    private final LayoutInflater mInflater;
    private int res1;
    private int res2;

    public WebdavAdapter(Context context, int inflateRes1, int inflateRes2) {
        this.res1 = inflateRes1;
        this.res2 = inflateRes2;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return VIEW_MAX_COUNT;
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
        int view = getItemViewType(position);
        if (convertView == null) {
            switch (view) {
                case VIEW1:
                    return this.mInflater.inflate(this.res1, null);
                case VIEW2:
                    return this.mInflater.inflate(this.res2, null);
                default:
                    return convertView;
            }
        }
        return convertView;
    }

    @Override
    public String getTitle(int position) {
        return this.context.getString(this.names[position]);
    }
}
