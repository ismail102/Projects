package com.bestapk.petukvai.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bestapk.petukvai.R;
import com.bestapk.petukvai.helper.AppController;
import com.bestapk.petukvai.model.BandInfo;

import java.util.ArrayList;

/**
 * Created by shree1 on 3/16/2017.
 */

public class AdapterStyle3 extends RecyclerView.Adapter<AdapterStyle3.VideoHolder> {

    public ArrayList<BandInfo> bandLogoList;

    public Activity activity;
    public int itemResource;

    ImageLoader netImageLoader = AppController.getInstance().getImageLoader();

    public AdapterStyle3(Activity activity, ArrayList<BandInfo> bandLogoList, int itemResource) {
        this.activity = activity;
        this.bandLogoList = bandLogoList;
        this.itemResource = itemResource;
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public NetworkImageView bandLogo;
        public TextView name;
        public RelativeLayout relativeLayout;

        public VideoHolder(View itemView) {
            super(itemView);
            bandLogo = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
            name = (TextView) itemView.findViewById(R.id.title);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.play_layout);
        }
    }


    @Override
    public int getItemCount() {
        return bandLogoList.size();
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, final int position) {
        final String bandLogoStr = bandLogoList.get(position).getImageUrl();
        holder.bandLogo.setImageUrl(bandLogoStr, netImageLoader);
        holder.name.setText(bandLogoList.get(position).getName());
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemResource, parent, false);
        return new VideoHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
