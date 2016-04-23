package id.web.go_cak.sewa;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    View view;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private TextView titleToolbar;
    private Toolbar sendToolbar;


    /*public MainActivityFragment(TextView titleToolbar,Toolbar toolbar) {
        this.titleToolbar = titleToolbar;
        this.sendToolbar = toolbar;
    }

    public static Fragment newInstance(TextView titleToolbar,Toolbar toolbar) {
        return new MainActivityFragment(titleToolbar,toolbar);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        //titleToolbar.setText(getString(R.string.app_name));
        //sendToolbar.setNavigationIcon(R.drawable.icon);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Calling the RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        // mGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.setLayoutManager(mGridLayoutManager);
        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new GridAdapter(getActivity(),fragmentTransaction,titleToolbar,sendToolbar);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}
