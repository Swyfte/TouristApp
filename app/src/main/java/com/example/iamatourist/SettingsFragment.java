package com.example.iamatourist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String[] languages = new String[] {
            "Select language", //0
            "English",  //1
            "Français", //2//French
            "Español",  //3//Spanish
            "Deutsch",  //4//German
            "Norsk",    //5//Norwegian
            "Svenska",  //6//Swedish
            "Cymreig"   //7//Welsh
    };

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = super.getContext();
        // Inflate the layout for this fragment
        final View settingView = inflater.inflate(R.layout.fragment_settings, container, false);
        final Button clear_button = settingView.findViewById(R.id.delete_button);
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });

        final Spinner languageSpinner = settingView.findViewById(R.id.language_spinner);
        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, languages);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(langAdapter);

        //languageSpinner.setSelection(0);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String languageChoice = languageSpinner.getItemAtPosition(i).toString();
                switch (i) {
                    case 1: { //English
                        Toast.makeText(settingView.getContext(), languageChoice, Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).setAppLanguage("en");
                        break;
                    }
                    case 2: { //French
                        Toast.makeText(settingView.getContext(), languageChoice, Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).setAppLanguage("fr");
                        break;
                    }
                    case 3: { //Spanish
                        Toast.makeText(settingView.getContext(), languageChoice, Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).setAppLanguage("es");
                        break;
                    }
                    case 4: { //German
                        Toast.makeText(settingView.getContext(), languageChoice, Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).setAppLanguage("de");
                        break;
                    }
                    case 5: { //Norwegian
                        Toast.makeText(settingView.getContext(), languageChoice, Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).setAppLanguage("no");
                        break;
                    }
                    case 6: { //Swedish
                        Toast.makeText(settingView.getContext(), languageChoice, Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).setAppLanguage("sv");
                        break;
                    }
                    case 7: { //Welsh
                        Toast.makeText(settingView.getContext(), languageChoice, Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).setAppLanguage("cy");
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //TODO Make permissions revoke on switch off
        final Switch camera_perm = settingView.findViewById(R.id.camera_perm_switch);
        camera_perm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                } else {
                    Toast.makeText(context,"Permission revoked", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final Switch location_perm = settingView.findViewById(R.id.location_perm_switch);
        location_perm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                } else {
                    Toast.makeText(context,"Permission revoked", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final Switch gallery_perm = settingView.findViewById(R.id.gallery_perm_switch);
        gallery_perm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                } else {
                    Toast.makeText(context,"Permission revoked", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return settingView;
    }

    /**
     * A method that displays a confirmation dialog.
     * This method is only called when the user presses the "Delete Galleries" button
     */
    //TODO - Actually delete links to galleries.
    private void confirmDelete() {
        final Context context = super.getContext();
        Dialog confirm = new AlertDialog.Builder(context)
                .setTitle("Empty App Galleries?")
                .setMessage("This will not delete the files from your device, and this action cannot be undone")
                .setIcon(R.drawable.ic_warning_red_24dp)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Gallery deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
