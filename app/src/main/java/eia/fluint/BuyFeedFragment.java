package eia.fluint;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import java.util.ArrayList;
import java.util.List;




/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuyFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuyFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuyFeedFragment extends Fragment implements FeedAdapter.ClickListener {

    private static final String TAG = "BuyFeedFragment";

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private FeedAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout feedSwipeRefresh;
    private ProgressBar mProgressBar;
    private FloatingActionButton fabBuyFeed;

    private String buyPreference = "None";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BuyFeedFragment.
     */
    public static BuyFeedFragment newInstance() {
        BuyFeedFragment fragment = new BuyFeedFragment();
        return fragment;
    }

    public BuyFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy_feed, container, false);
        view.setTag(TAG);

        // TODO: Get references to the layout views

        feedSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.feedSwipeRefresh);
        feedSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: refresh content and update location
                getUserLocation();
                getLatestPosts(buyPreference);
            }
        });

        // TODO: set color scheme of the SwipeRefreshLayout
        feedSwipeRefresh.setColorSchemeResources(R.color.primaryColor);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        // TODO: get reference to ProgressBar
        mProgressBar = (ProgressBar) view.findViewById(R.id.buyFeedProgressBar);

        fabBuyFeed = (FloatingActionButton) view.findViewById(R.id.fabBuyFeed);
        fabBuyFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: launch add new Buy post Activity
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
            }
        });

        // TODO: XML: wrap FAB in a CoordinatorLayout

        // TODO: Change color of fab with setBackgroundTintList(ColorStateList)

        // TODO: Display icon in fab with setImageDrawable(Drawable)

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        buyPreference = sp.getString(getResources().getString(R.string.buy_preference), "None");

        Toast.makeText(getActivity(), buyPreference, Toast.LENGTH_SHORT).show();

        getUserLocation();
        getLatestPosts(buyPreference);

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void getLatestPosts(String buy) {
        // TODO: get latest posts
        mProgressBar.setVisibility(View.VISIBLE);

        if (buy.equals("None")) {
            // TODO: query all buy posts within X miles of user

        } else {

        }

        ParseQuery query = new ParseQuery(Transaction.TAG);
        query.setLimit(100);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    ArrayList<Transaction> latestData = new ArrayList<>();
                    for (Object o : list) {
                        Transaction trans = new Transaction();
                        ParseObject po = (ParseObject) o;
                        latestData.add(trans);

                    }
                    FeedAdapter adapter = new FeedAdapter(getActivity(), latestData);
                    adapter.setClickListener(BuyFeedFragment.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    // Exception caught
                }
            }

            @Override
            public void done(Object o, Throwable t) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });


    }

    private void getUserLocation() {
        // TODO: get user's location

    }


    @Override
    public void itemClicked(View view, int position) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
