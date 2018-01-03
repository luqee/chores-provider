package com.luqi.choresprovider;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by luqi on 8/29/17.
 */

public class FragmentRegister extends Fragment implements AdapterView.OnItemSelectedListener{
    public static String TAG = "FragmentRegister";

    private FragmentRegisterListener fragmentRegisterListener;
    EditText editTextName, editTextNumber;
    Spinner spinner;
    Button buttonRegister;
    Utils mUtils;

    public interface FragmentRegisterListener{
        void registrationComplete();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentRegisterListener =(FragmentRegisterListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentRegisterListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils = new Utils(getContext());
//        fetchCategories();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        editTextName = (EditText) root.findViewById(R.id.editTextName);
        editTextNumber = (EditText) root.findViewById(R.id.editTextNumber);
        spinner = (Spinner) root.findViewById(R.id.spinnerCategories);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        buttonRegister = (Button) root.findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = editTextName.getText().toString();
                String uNumber = editTextNumber.getText().toString();
                registerProvider(uName, uNumber);
            }
        });
        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection = (String) parent.getItemAtPosition(position);
        mUtils.savePreferences(Utils.REGISTRED_AS, selection);
        Log.d(TAG, "Selevtion is :: "+selection);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void registerProvider(final String username, final String userNumber){
        new AsyncTask<Void, Void, JSONObject>(){

            @Override
            protected JSONObject doInBackground(Void... params) {
                Log.d(TAG, "Starting registration");
                HashMap<String, String> nameValuePairs = new HashMap<>();
                JSONParser parser = new JSONParser();
//                nameValuePairs.put("name", mUtils.getFromPreferences(Utils.USER_NAME));
                nameValuePairs.put("username", username);
                nameValuePairs.put("number", userNumber);
//                nameValuePairs.put("number", mUtils.getFromPreferences(Utils.USER_NUMBER));
                nameValuePairs.put("latitude", mUtils.getFromPreferences(Utils.USER_LATITUDE));
                nameValuePairs.put("longitude", mUtils.getFromPreferences(Utils.USER_LONGITUDE));
                nameValuePairs.put("token", mUtils.getFromPreferences(Utils.PROPERTY_REG_ID));

                nameValuePairs.put("registered_as", mUtils.getFromPreferences(Utils.REGISTRED_AS));
                Log.d(TAG, "create namevalue pairs");
                JSONObject jsonObject = parser.makeHttpRequest(mUtils.getCurrentIPAddress() +"tatua/api/v1.0/auth/provider/register", "POST",nameValuePairs);
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                try {
                    String response = jsonObject.getString("result");

                    if (response.equals("success")){
                        Log.d(TAG, "Successfull registration::"+ jsonObject.toString());
                        //show progress bar
                        mUtils.savePreferences(Utils.REGISTRED, "True");
                        mUtils.savePreferences(Utils.USER_ID, jsonObject.getString("provider_id"));
                        fragmentRegisterListener.registrationComplete();

                    }else if (response.equals("error")){
                        Log.d(TAG, "Error in registration");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(null, null, null);
    }

//    public void fetchCategories(){
//        new AsyncTask<Void, Void, JSONObject>(){
//
//            @Override
//            protected JSONObject doInBackground(Void... args) {
//                HashMap<String, String> params = new HashMap<>();
//                Log.d(TAG, "Fetching Categories to display");
//                JSONParser parser = new JSONParser();
//                JSONObject jsonObject = parser.makeHttpRequest(utils.getCurrentIPAddress() +"tatua/api/v1.0/categories","GET", params);
//                return jsonObject;
//            }
//
//            @Override
//            protected void onPostExecute(JSONObject jsonObject) {
//                try {
//                    String response = jsonObject.getString("result");
//
//                    if (response.equals("success")){
//                        Log.d(TAG, "Successfully fetched categiries : ");
//
//                        for (int i = 0 ; i<jsonObject.getJSONArray("categories").length(); i++) {
//                            JSONObject categoryJSON = jsonObject.getJSONArray("categories").getJSONObject(i);
//                            Category category = new Category();
//                            category.setId(Integer.parseInt(categoryJSON.getString("id")));
//                            category.setName(categoryJSON.getString("name"));
//                            categories.add(category);
//                        }
//                        adapter.notifyDataSetChanged();
//                    }else if (response.equals("error")){
//                        Log.d(TAG, "Error in registration");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.execute(null, null, null);
//    }
}
