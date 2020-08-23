package com.erezbiox1.paytimer.Room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ShiftRepository {

    private ShiftDao dao;
    private LiveData<List<Shift>> allShifts;

    public ShiftRepository(Application application) {
        ShiftDatabase shiftDatabase = ShiftDatabase.getDatabase(application);
        dao = shiftDatabase.shiftDao();
        allShifts = dao.getAllShifts();
    }

    public LiveData<List<Shift>> getAllShifts() {
        return allShifts;
    }

    public void insert(Shift shift){
        new asyncShiftChanger(dao).insert(shift);
    }

    public void update(Shift shift){
        new asyncShiftChanger(dao).update(shift);
    }

    public void delete(Shift shift){
        new asyncShiftChanger(dao).delete(shift);
    }

    private static class asyncShiftChanger extends AsyncTask<Shift, Void, Void>{
        private ShiftDao dao;
        private ShiftChangeType type = null;

        private enum ShiftChangeType{ INSERT, UPDATE, DELETE }

        asyncShiftChanger(ShiftDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final Shift... params) {
            switch (type){
                case INSERT:
                    dao.insert(params[0]);
                    break;
                case UPDATE:
                    dao.update(params[0]);
                    break;
                case DELETE:
                    dao.delete(params[0]);
                    break;
            }

            return null;
        }

        void insert(Shift shift){
            type = ShiftChangeType.INSERT;
            execute(shift);
        }

        void update(Shift shift){
            type = ShiftChangeType.UPDATE;
            execute(shift);
        }

        void delete(Shift shift){
            type = ShiftChangeType.DELETE;
            execute(shift);
        }
    }

}
