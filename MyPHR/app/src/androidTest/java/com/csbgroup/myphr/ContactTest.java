package com.csbgroup.myphr;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.test.InstrumentationRegistry;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.ContactsDao;

public class ContactTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    private ListView contactsList;
    private FloatingActionButton fab;

    public ContactTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();

        Contacts contacts = Contacts.newInstance();

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, contacts);
        transaction.commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        contactsList = getActivity().findViewById(R.id.contacts_list);
        fab = getActivity().findViewById(R.id.contact_fab);
    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        assertNotNull(contactsList);
        assertNotNull(fab);
    }

    public void testContactList() {
        Context context = InstrumentationRegistry.getTargetContext();
        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        ContactsDao dao = appDatabase.contactsDao();

        assertEquals(contactsList.getChildCount(), dao.getAll().size());
    }
}
