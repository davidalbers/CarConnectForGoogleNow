package edu.dalbers.carnowcontrol.wizard.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import edu.dalbers.carnowcontrol.InputListenerActivity;
import edu.dalbers.carnowcontrol.R;
import edu.dalbers.carnowcontrol.wizard.model.SetupServicePage;

/**
 * Created by davidalbers on 9/12/15.
 * Shows whether or not the service is enabled and offers a button to jump to the enabling button
 */
public class SetupServiceFragment extends Fragment {
    private static final String ARG_KEY = "setup_key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private SetupServicePage mPage;
    private TextView aboutAccessibilityView;
    private boolean serviceEnabled;
    public static SetupServiceFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        SetupServiceFragment fragment = new SetupServiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SetupServiceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceEnabled = ((InputListenerActivity)getActivity()).isAccessibilityEnabled();
        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (SetupServicePage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setup_service_layout, container, false);

        aboutAccessibilityView = ((TextView) rootView.findViewById(R.id.about_accessibility));
        TextView alreadyEnabledService = ((TextView) rootView.findViewById(R.id.already_enabled_service));
        Button enableAccessibility = ((Button) rootView.findViewById(R.id.enable_service));
        enableAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });

        if(!serviceEnabled) {
            enableAccessibility.setEnabled(false);
            alreadyEnabledService.setVisibility(View.VISIBLE);
        }

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
        if (aboutAccessibilityView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }


}
