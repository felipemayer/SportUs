package com.sportus.sportus.Adapters;

public class EventsAdapter {

   /* private static final String TAG = EventsAdapter.class.getSimpleName();;
    private EventsFragment.OnEventSelectedInterface mListener;

    List<Event> mDataList = new ArrayList<>();
    Context mContext;
    LayoutInflater inflater;

    public EventsAdapter(Context context, List<Event> dataList1, EventsFragment.OnEventSelectedInterface listener) {
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
            mIcon.setImageResource(R.drawable.ic_ball);
            if (mDataList.get(position).payMethod) {
                mPaymentIcon.setImageResource(R.drawable.ic_ball);
            }
        }

        @Override
        public void onClick(View v) {
            mListener.onListEventSelected(0);
        }

    }*/
}


