package de.hwr_berlin.quizapp.activities.fragments;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hwr_berlin.quizapp.R;
import de.hwr_berlin.quizapp.models.OnFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link de.hwr_berlin.quizapp.models.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainLegalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainLegalFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static final String TAG = MainLegalFragment.class.getSimpleName();

    private TextView legal_version;

    public MainLegalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainLegalFragment.
     */
    public static MainLegalFragment newInstance() {
        MainLegalFragment fragment = new MainLegalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_legal, container, false);
        legal_version = (TextView) view.findViewById(R.id.legal_version);

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            legal_version.setText("App-Version: " + version);
        } catch (Exception e) {
            Log.e(TAG, "Could not fetch the version name.", e);
        }

        return view;
    }

    public void onButtonPressed(View view) {
        if (mListener != null) {
            mListener.onFragmentViewInteraction(view);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
