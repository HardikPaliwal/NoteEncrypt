package com.studios.hsoni.noteencrypt.Fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.studios.hsoni.noteencrypt.Other.NoteEntry;
import com.studios.hsoni.noteencrypt.Other.Phone;
import com.studios.hsoni.noteencrypt.R;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.IOException;
import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;

public class CreateNewNote extends Fragment {


    public CreateNewNote() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewDataBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_create_new_note, container, false);
        final EditText content = binding.getRoot().findViewById(R.id.createNotesContent);
        final EditText password = binding.getRoot().findViewById(R.id.createNotesPassword);
        Button submit = binding.getRoot().findViewById(R.id.createNotesSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(content.getText())){
                    content.setError("Invalid Note");
                }
                if(TextUtils.isEmpty(password.getText())){
                    content.setError("Invalid Password");
                }
                else{
                    Phone phone = null;
                    try {
                        phone = new Phone(getActivity());
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }
                    try {
                        phone.storeNote(content.getText().toString(), password.getText().toString());
                    } catch (DigestException e) {
                        e.printStackTrace();
                    }
                    MDToast.makeText(getActivity(),"Successfully stored your note", MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();

                }
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        void onFragmentInteraction(Uri uri);
    }
}
