package com.luqi.choresprovider;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by luqi on 8/29/17.
 */

public class FragmentHome extends Fragment {
    public static String TAG = "FragmentHome";
    Context mContext;
    Utils mUtils;
    TextView textViewInfo;
    Switch aSwitch;
    private FragmentHomeListener fragmentHomeListener;

    public interface FragmentHomeListener{
        void onSetStatus(String status);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentHomeListener = (FragmentHomeListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentHomeListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mUtils = new Utils(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_provider_home, container, false);
        textViewInfo = (TextView) root.findViewById(R.id.txtInfo);
        textViewInfo.setText("Welcome to Chores for Providers");
        aSwitch = (Switch)root.findViewById(R.id.switchAvailability);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    fragmentHomeListener.onSetStatus("Available");

                }else{
                    fragmentHomeListener.onSetStatus("Unavailable");
                }
            }
        });
    }
}
