package com.csbgroup.myphr.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ContactsDao {

    @Query("SELECT * FROM contactsentity")
    List<ContactsEntity> getAll();

    @Query("SELECT name FROM contactsentity")
    List<String> getAllNames();

    @Query("SELECT * from contactsentity WHERE uid LIKE :uid")
    ContactsEntity getContact(int uid);

    @Query("SELECT * FROM contactsentity WHERE name LIKE :name")
    ContactsEntity getContactByName(String name);

    @Insert
    void insertAll(ContactsEntity... contactsEntities);

    @Update
    void update(ContactsEntity contactsEntity);

    @Delete
    void delete(ContactsEntity contactsEntity);

    @Query("DELETE FROM contactsentity")
    void deleteAll();
}
