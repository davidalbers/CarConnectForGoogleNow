package edu.dalbers.carnowcontrol.wizard.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import edu.dalbers.carnowcontrol.R;
import edu.dalbers.carnowcontrol.wizard.model.InformationPage;

/**
 * Created by davidalbers on 9/12/15.
 */
public class InformationFragment extends Fragment {

    private static final String ARG_KEY = "information_key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private InformationPage mPage;
    private TextView infoView;

    public static InformationFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        InformationFragment fragment = new InformationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public InformationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (InformationPage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.information_page, container, false);
        //TextView title = ((TextView) rootView.findViewById(R.id.title));
        //title.setText(mPage.getTitle());
        //infoView = ((TextView) rootView.findViewById(R.id.info));
        //infoView.setText(mPage.getInfoToDisplay());

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (infoView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }
}
