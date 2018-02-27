package com.csbgroup.myphr;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.test.InstrumentationRegistry;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineDao;

public class MedicineTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    private ListView medicineList;
    private FloatingActionButton fab;

    public MedicineTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();

        Medicine medicine  = Medicine.newInstance();

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, medicine);
        transaction.commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        medicineList = getActivity().findViewById(R.id.medicine_list);
        fab = getActivity().findViewById(R.id.med_fab);
    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        assertNotNull(medicineList);
        assertNotNull(fab);
    }

    public void testMedicineList() {
        Context context = InstrumentationRegistry.getTargetContext();
        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        MedicineDao dao = appDatabase.medicineDao();

        assertEquals(medicineList.getChildCount(), dao.getAll().size());
    }
}