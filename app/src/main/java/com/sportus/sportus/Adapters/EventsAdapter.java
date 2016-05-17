package com.sportus.sportus.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sportus.sportus.R;
import com.sportus.sportus.data.EventData;
import com.sportus.sportus.ui.EventsFragment;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter {

    private static final String TAG = EventsAdapter.class.getSimpleName();;
    private EventsFragment.OnEventSelectedInterface mListener;

    List<EventData> mDataList = new ArrayList<>();
    Context mContext;
    LayoutInflater inflater;

    public EventsAdapter(Context context, List<EventData> dataList1, EventsFragment.OnEventSelectedInterface listener) {
        mContext = context;
        mDataList = dataList1;
        inflater = LayoutInflater.from(context);
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
        return mDataList.size();
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
            mTextView.setText(mDataList.get(position).title);
            mIcon.setImageResource(mDataList.get(position).icon);
            if (mDataList.get(position).payMethod) {
                mPaymentIcon.setImageResource(R.drawable.ic_money);
            }
        }

        @Override
        public void onClick(View v) {
            mListener.onListEventSelected(mDataList.get(mIndex).id);
        }
    }
}


