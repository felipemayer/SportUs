package com.sportus.sportus.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sportus.sportus.R;


public class DrawerNavigationAdapter extends RecyclerView.Adapter<DrawerNavigationAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    private String mNavTitles[];
    private int mIcons[];

    private String mName;
    private int mProfile;
    private String mEmail;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         int Holderid;

         TextView textView;
         ImageView imageView;
         ImageView profile;
         TextView Name;
         TextView email;
         Context contxt;

        public ViewHolder(View itemView, int ViewType, Context c) {
            super(itemView);
            contxt = c;
            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);

                Holderid = 1;
                }
            else
            {
                Name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                profile = (ImageView) itemView.findViewById(R.id.circleView);

                Holderid = 0;
                }
            }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
        }
    }


    public DrawerNavigationAdapter(String Titles[], int Icons[], String Name, String Email, int Profile, Context passedContext) {
        mNavTitles = Titles;
        mIcons = Icons;
        mName = Name;
        mEmail = Email;
        mProfile = Profile;
        this.context = passedContext;
       }

    @Override
    public DrawerNavigationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
            ViewHolder vhItem = new ViewHolder(v, viewType, context);

            return vhItem;

            } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);

            ViewHolder vhHeader = new ViewHolder(v, viewType, context);

            return vhHeader;
            }
            return null;
            }

    @Override
    public void onBindViewHolder(DrawerNavigationAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position - 1]);
        }
        else{
            holder.profile.setImageResource(mProfile);
            holder.Name.setText(mName);
            holder.email.setText(mEmail);
            }
        }

    @Override
    public int getItemCount() {
        return mNavTitles.length + 1;
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}