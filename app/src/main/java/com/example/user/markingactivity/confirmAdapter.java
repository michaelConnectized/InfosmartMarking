package com.example.user.markingactivity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class confirmAdapter extends bleAdapter {

    public confirmAdapter(Context ctx, ArrayList<mDevice> mDevs) {
        super(ctx, mDevs);
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        mDevice currentDevice = (mDevice)getItem(position);
        View confirmView = super.getView(position, view, viewGroup);
        confirmView.findViewById(R.id.chk_check).setVisibility(View.GONE);
        ((TextView)confirmView.findViewById(R.id.tv_uuid)).setText(currentDevice.getmUUID());
        return confirmView;
    }
}