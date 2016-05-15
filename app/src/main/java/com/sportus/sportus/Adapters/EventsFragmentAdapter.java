package com.sportus.sportus.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sportus.sportus.data.Events;
import com.sportus.sportus.ui.EventsFragment;
import com.sportus.sportus.R;

public class EventsFragmentAdapter extends RecyclerView.Adapter {

    private final EventsFragment.OnEventSelectedInterface mListener;

    public EventsFragmentAdapter(EventsFragment.OnEventSelectedInterface listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_events, parent, false);
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
        private ImageView mIcon;
        private int mIndex;
        private ImageView mPaymentIcon;

        public ListViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.itemTextEvent);
            mIcon = (ImageView) itemView.findViewById(R.id.itemImageEventIcon);
            mPaymentIcon = (ImageView) itemView.findViewById(R.id.itemImageEventPayment);
            itemView.setOnClickListener(this);
        }

        public void bindView(int position){
            mIndex = position;
            mTextView.setText(Events.eventNames[position]);
            mIcon.setImageResource(Events.eventIcon[position]);
            if (Events.eventPayMethod[position]) {
                mPaymentIcon.setImageResource(R.drawable.ic_money);
            }
        }

        @Override
        public void onClick(View v) {
            mListener.onListEventSelected(mIndex);
        }
    }
}
