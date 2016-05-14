package com.sportus.sportus.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sportus.sportus.R;
import com.sportus.sportus.data.Events;

public class AgendaFragmentAdapter extends RecyclerView.Adapter {


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_agenda, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return Events.eventNames.length;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTextView;
        private ImageView mImageView;
        private int mIndex;

        public ListViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.itemTextAgenda);
            mImageView = (ImageView) itemView.findViewById(R.id.itemImageAgenda);
            itemView.setOnClickListener(this);
        }

        public void bindView(int position){
            mIndex = position;
            mTextView.setText(Events.eventNames[position]);
            mImageView.setImageResource(Events.eventIcon[position]);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
