package com.csbgroup.myphr;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.ContactsEntity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ContactDetails extends Fragment {

    private ContactsEntity thiscontact; // the contact we're viewing now

    private Menu editMenu;
    private String mode = "view";
    private View rootView;

    // key listeners and backgrounds for toggling field editability
    private KeyListener titleKL, emailKL, phoneKL, notesKL;
    private Drawable titleBG, emailBG, phoneBG, notesBG;

    public ContactDetails() {
        // Required empty public constructor
    }

    public static ContactDetails newInstance() {
        ContactDetails fragment = new ContactDetails();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contact_details, container, false);
        this.rootView = rootView;

        Bundle args = getArguments();
        ContactsEntity contact = getContact(Integer.valueOf(args.getString("uid")));
        thiscontact = contact;

        EditText contactTitle = rootView.findViewById(R.id.contact_title);
        EditText email = rootView.findViewById(R.id.email);
        EditText phone = rootView.findViewById(R.id.phone);
        EditText notes = rootView.findViewById(R.id.notes);

        // fill in the values
        contactTitle.setText(contact.getName());
        email.setText(contact.getEmail());
        phone.setText(contact.getPhone());
        notes.setText(contact.getNotes());

        // save listeners and backgrounds
        titleKL = contactTitle.getKeyListener();
        titleBG = contactTitle.getBackground();
        emailKL = email.getKeyListener();
        emailBG = email.getBackground();
        phoneKL = phone.getKeyListener();
        phoneBG = phone.getBackground();
        notesKL = notes.getKeyListener();
        notesBG = notes.getBackground();

        //disable editability
        disableEditing(contactTitle);
        disableEditing(email);
        disableEditing(phone);
        disableEditing(notes);

        // back button
        ((MainActivity) getActivity()).setToolbar("My Contacts", true);
        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Fetches a single contact entity from the database, found by name
     * @param uid is the primary key of the contact to be retrieved
     * @return the contact entity
     */
    private ContactsEntity getContact(final int uid) {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).contactsDao().getContact(uid);
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<ContactsEntity> result = service.submit(callable);

        ContactsEntity contact = null;
        try {
            contact = result.get();
        } catch (Exception e) {
        }

        return contact;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);
        editMenu = menu;
    }

    /**
     * Provides navigation/actions for menu items.
     * @param item the clicked menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button - go back
                ((MainActivity) getActivity()).switchFragment(Contacts.newInstance());
                return true;

            case R.id.details_edit: // edit button - edit contact details
                switchMode();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * switchMode toggles between viewing and editing the contact details.
     */
    public void switchMode() {

        final EditText title = rootView.findViewById(R.id.contact_title);
        final EditText email = rootView.findViewById(R.id.email);
        final EditText phone = rootView.findViewById(R.id.phone);
        final EditText notes = rootView.findViewById(R.id.notes);
        final Button delete = rootView.findViewById(R.id.delete);

        if (this.mode.equals("view")) {
            editMenu.getItem(0).setIcon(R.drawable.tick);

            // activate error checking
            errorChecking(title);

            // show the delete button
            delete.setVisibility(View.VISIBLE);

            // restore bg and kl to make editable
            title.setBackground(titleBG);
            title.setKeyListener(titleKL);
            email.setKeyListener(emailKL);
            email.setBackground(emailBG);
            phone.setKeyListener(phoneKL);
            phone.setBackground(phoneBG);
            notes.setKeyListener(notesKL);
            notes.setBackground(notesBG);

            // delete the contact
            delete.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                            db.contactsDao().delete(thiscontact);
                            ((MainActivity) getActivity()).switchFragment(Contacts.newInstance());
                        }
                    }).start();
                }
            });

            this.mode = "edit";
            return;
        }

        if (this.mode.equals("edit")){
            editMenu.getItem(0).setIcon(R.drawable.edit);

            // hide the delete button
            delete.setVisibility(View.GONE);

            // disable editing of all fields
            disableEditing(title);
            disableEditing(email);
            disableEditing(phone);
            disableEditing(notes);

            // update the contact in the database
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                    thiscontact.setName(title.getText().toString());
                    thiscontact.setEmail(email.getText().toString());
                    thiscontact.setPhone(phone.getText().toString());
                    thiscontact.setNotes(notes.getText().toString());
                    db.contactsDao().update(thiscontact);

                    // refresh to get rid of keyboard
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(ContactDetails.this).attach(ContactDetails.this).commit();
                }
            }).start();

            this.mode = "view";
            return;
        }
    }

    /**
     * disableEditing sets background and keylistener to null to stop user editing
     * @param field is the editText field to be disabled
     */
    public void disableEditing(EditText field){
        field.setBackground(null);
        field.setKeyListener(null);
    }

    /**
     * errorChecking live checks the formatting of fields; errors are highlighted to the user
     * and saving is disabled until they are corrected.
     * @param et the contact name, which cannot be empty
     */
    public void errorChecking(EditText et){

        final EditText name = et;

        // name format checking
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (name.getText().length() == 0){ // empty name
                    name.setError("Name cannot be empty"); // show error message
                    editMenu.getItem(0).setEnabled(false); // disable save button
                }
                else { // valid name
                    editMenu.getItem(0).setEnabled(true); // enable save button
                }
            }
            // not needed for our purposes
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
    }

}