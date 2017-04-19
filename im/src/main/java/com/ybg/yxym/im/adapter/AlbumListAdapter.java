package com.ybg.yxym.im.adapter;

import android.app.Activity;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ybg.yxym.im.R;
import com.ybg.yxym.im.entity.ImageBean;
import com.ybg.yxym.im.tools.ViewHolder;
import com.ybg.yxym.im.view.MyImageView;

import java.io.File;
import java.util.List;

public class AlbumListAdapter extends BaseAdapter {

    protected LayoutInflater mInflater;
    private List<ImageBean> list;
    private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
    private Activity mContext;

    public AlbumListAdapter(Activity context, List<ImageBean> list, float density) {
        this.mContext = context;
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageBean mImageBean = list.get(position);
        String path = mImageBean.getTopImagePath();
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_pick_picture_total, null);
        }
        MyImageView imageView = ViewHolder.get(convertView, R.id.group_image);
        TextView title = ViewHolder.get(convertView, R.id.group_title);
        TextView countTv = ViewHolder.get(convertView, R.id.group_count);
        title.setText(mImageBean.getFolderName());
        String count = "(" + Integer.toString(mImageBean.getImageCounts()) + ")";
        countTv.setText(count);
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            try {
                Picasso.with(mContext).load(file).into(imageView);
            } catch (Exception e) {
                imageView.setImageResource(R.mipmap.jmui_picture_not_found);
            }
        } else {
            imageView.setImageResource(R.mipmap.jmui_picture_not_found);
        }


        return convertView;
    }
}
