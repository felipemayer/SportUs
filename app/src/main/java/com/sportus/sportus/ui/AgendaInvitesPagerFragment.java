package com.sportus.sportus.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sportus.sportus.R;

public class AgendaInvitesPagerFragment extends Fragment {
    public static final String KEY_EVENT_INDEX = "event_index";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.agendainvitepager_fragment, container, false);

        final AgendaFragment agendaFragment = new AgendaFragment();
        final InvitesFragment invitesFragment = new InvitesFragment();

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return position == 0 ? agendaFragment : invitesFragment;
                /*if  (position == 0){
                    return agendaFragment;
                } else {
                    return invitesFragment;
                }*/
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "Agenda" : "Convites";
            }
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabAgendaInvite);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

}
