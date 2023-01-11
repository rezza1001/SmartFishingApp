package com.vma.smartfishingapp.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.smartfishingapp.R;

import java.util.ArrayList;

/**
 * Created by Mochamad Rezza Gumilang on 15/02/2022
 */
public class DisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final ArrayList<Bundle> mList;
    private Context mContext;
    public DisplayAdapter(Context context, ArrayList<Bundle> list){
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_setting_display, parent, false);
        return new AdapterView(itemView);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder parent, int position) {
        final Bundle data = mList.get(position);
        AdapterView  holder = (AdapterView) parent;

        holder.txvw_name.setText(data.getString("name"));
        holder.txvw_description.setText(data.getString("description"));

        holder.rvly_action.setOnClickListener(view -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class AdapterView extends RecyclerView.ViewHolder{
        RelativeLayout rvly_action;
        TextView txvw_name,txvw_description;

        public AdapterView(@NonNull View itemView) {
            super(itemView);

            rvly_action = itemView.findViewById(R.id.rvly_action);
            txvw_name = itemView.findViewById(R.id.txvw_name);
            txvw_description = itemView.findViewById(R.id.txvw_description);

            CardView card_body = itemView.findViewById(R.id.card_body);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                card_body.setOutlineSpotShadowColor(ContextCompat.getColor(mContext, R.color.primaryLight));
            }
        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle menu);
    }
}
