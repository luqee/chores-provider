package com.luqi.choresprovider;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class FragmentWorking extends Fragment {
    public static String TAG = "FragmentWorking";
    EditText description, mCost;
    Button submit;

    private FragmentWorkingListener fragmentWorkingListener;

    public interface FragmentWorkingListener{
        void onReportSubmit(String dsc, String cst);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentWorkingListener = (FragmentWorkingListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentWorkingListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_working, container, false);
        description = (EditText) root.findViewById(R.id.editTextDescription);
        mCost = (EditText) root.findViewById(R.id.editTextCost);
        submit = (Button) root.findViewById(R.id.buttonSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: hit server to submit cost
                String desc = description.getText().toString();
                String cost = mCost.getText().toString();
                fragmentWorkingListener.onReportSubmit(desc, cost);
            }
        });
        return root;
    }
}
