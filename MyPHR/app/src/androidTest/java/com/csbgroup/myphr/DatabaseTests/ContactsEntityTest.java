package com.csbgroup.myphr.DatabaseTests;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.ContactsDao;
import com.csbgroup.myphr.database.ContactsEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ContactsEntityTest {

    private ContactsDao contactsDao;
    private AppDatabase appDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        contactsDao = appDatabase.contactsDao();
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    @Test
    public void createContactTest() throws Exception {
        ContactsEntity contactsEntity = new ContactsEntity("Contact Name",
                "email@email.com", "12345678910", "Notes");
        contactsDao.insert(contactsEntity);

        ContactsEntity ce = contactsDao.getContactByName("Contact Name");
        assertEquals(contactsEntity.getName(), ce.getName());
    }

    @Test
    public void deleteContactTest() throws Exception {
        ContactsEntity contactsEntity = new ContactsEntity("Contact Name",
                "email@email.com", "12345678910", "Notes");
        contactsDao.insert(contactsEntity);

        // Ensure the database contains the contact to be deleted
        assertEquals(contactsEntity.getName(), contactsDao.getContactByName("Contact Name").getName());

        // Delete the contact from the database and ensure the getContact query returns null
        contactsDao.delete(contactsDao.getContactByName("Contact Name"));
        assertEquals(null, contactsDao.getContactByName("Contact Name"));
    }

    @Test
    public void insertMultipleContactsTest() throws Exception {
        List<String> titles = Arrays.asList("Contact 1", "Contact 2", "Contact 3", "Contact 4");

        List<ContactsEntity> contacts = new ArrayList<>();
        for (int i = 1; i < 5; i++)
            contacts.add(new ContactsEntity("Contact " + i, null, null, null));
        contactsDao.insertAll(contacts.toArray(new ContactsEntity[contacts.size()]));

        assertEquals(titles, contactsDao.getAllNames());
    }

    @Test
    public void getAllContactsTest() throws Exception {
        for (int i = 1; i < 5; i++)
            contactsDao.insert((new ContactsEntity("Contact " + i, null, null, null)));

        assertEquals(4, contactsDao.getAll().size());
    }

    @Test
    public void deleteAllContactsTest() throws Exception {
        for (int i = 1; i < 5; i++)
            contactsDao.insert(new ContactsEntity("Contact " + i, null, null, null));

        // Ensure there are currently 4 contacts in the database
        assertEquals(4, contactsDao.getAll().size());

        // Delete all contacts and ensure there are 0 left
        contactsDao.deleteAll();
        assertEquals(0, contactsDao.getAll().size());
    }

    @Test
    public void updateContactTest() throws Exception {
        ContactsEntity contactsEntity = new ContactsEntity("Contact Name",
                "email@email.com", "12345678910", "Notes");
        contactsEntity.setUid(112);
        contactsDao.insert(contactsEntity);

        // Ensure the database contains the contact to be updated
        assertEquals(contactsEntity.getName(), contactsDao.getContact(112).getName());

        // Update the contact from the database and ensure the getContact query returns the new version
        ContactsEntity updated = contactsDao.getContact(112);
        updated.setName("Updated Name");
        contactsDao.update(updated);

        assertEquals("Updated Name", contactsDao.getContact(112).getName());
    }
}
