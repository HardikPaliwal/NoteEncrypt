package com.studios.hsoni.noteencrypt;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.florent37.androidnosql.AndroidNoSql;
import com.github.florent37.androidnosql.NoSql;
import com.studios.hsoni.noteencrypt.Fragments.CreateNewNote;
import com.studios.hsoni.noteencrypt.Other.NoteEntry;
import com.studios.hsoni.noteencrypt.Other.NoteEntryAdapter;
import com.studios.hsoni.noteencrypt.Other.Phone;
import com.studios.hsoni.noteencrypt.databinding.ActivityMainBinding;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.security.KeyStoreException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ArrayList<NoteEntry> notes = new ArrayList<>();
    ActivityMainBinding binding;
    NoteEntryAdapter adapter;
    Phone phone;
    private static final String newNote = "newNote", enterPassword = "Enter Password", viewNote = "viewNote", home ="home";
    private String currentFragment = home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidNoSql.initWithDefault(getApplicationContext());


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.includedLayout.currentListings.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(binding.toolbar);
        try {
            phone = new Phone(this);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        notes = phone.loadData();
        loadOnClick();
        loadAdapter();
    }

    private void loadOnClick() {
        binding.includedLayout.deleteAllListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone.nukeNotes();
                notes.clear();
                adapter.notifyDataSetChanged();
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new CreateNewNote(), newNote);
            }
        });
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Back Button Clicked");
                backToActivity();
            }
        });
    }

    private void loadAdapter() {
        adapter = new NoteEntryAdapter(notes, this, new NoteEntryAdapter.OnClick() {
            @Override
            public void onClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = View.inflate(getApplicationContext(), R.layout.enter_password_alert, null);
                builder.setView(view);
                final EditText password = view.findViewById(R.id.alertPassword);
                builder.setPositiveButton("Submit Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(TextUtils.isEmpty(password.getText())){
                            MDToast.makeText(getApplicationContext(), "Invalid Input", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                        }
                        else if(!phone.isCorrectPassword(password.getText().toString(),notes.get(position).getEntryPassword())){
                            MDToast.makeText(getApplicationContext(), "Incorrect Password", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                        }
                        else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            Toast.makeText(getApplicationContext(), phone.decryptyt(password.getText().toString(), notes.get(position).getEntryData()), Toast.LENGTH_LONG).show();

                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            }

            @Override
            public void delete(int position) {
                phone.deleteNote(notes.get(position));
                notes.remove(position);
                adapter.notifyItemRemoved(position);
                MDToast.makeText(getApplicationContext(), "Your Listing Was Removed", MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
            }
        });
        binding.includedLayout.currentListings.setAdapter(adapter);
    }

    public void replaceFragment(Fragment fragment, String TAG) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        updateToolbar(TAG);
    }

    private void updateToolbar(String tag) {
        switch(tag){
            case newNote:
                currentFragment = newNote;
                binding.fab.hide();
                binding.toolbar.setTitle("Create A New Note");
                this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            case enterPassword:
                currentFragment = enterPassword;
                break;
            case viewNote:
                currentFragment = viewNote;
                break;

        }
    }
    @Override
    public void onBackPressed(){
       backToActivity();
    }
    private void backToActivity(){
        if(currentFragment.equals(newNote) || currentFragment.equals(enterPassword)) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            notes.clear();
            phone.updateData(notes);
            adapter.notifyDataSetChanged();
            getSupportFragmentManager().popBackStack();
            binding.fab.show();
            binding.toolbar.setTitle("Your Current Notes");
        }
        else if(currentFragment.equals(viewNote)){
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            binding.fab.show();
            binding.toolbar.setTitle("Your Current Notes");
        }
        else{
            finish();
        }
    }
}
