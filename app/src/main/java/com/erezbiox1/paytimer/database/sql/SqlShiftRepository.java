/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.database.sql;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.erezbiox1.paytimer.database.ShiftRepository;
import com.erezbiox1.paytimer.model.Shift;

import java.util.List;

public class SqlShiftRepository extends ShiftRepository {

    // The shift's dao ( data access object that is used to interact with the database )
    private SqlShiftDao dao;

    // A rolling list of all of the shifts in the database, will be update live by the database
    private LiveData<List<Shift>> allShifts;
    private int id;
    private Shift shift;

    // Create the repository with the application context if needed, get the dao and the rolling shifts list.
    public SqlShiftRepository(Context context) {
        SqlShiftDatabase sqlShiftDatabase = SqlShiftDatabase.getDatabase(context);
        dao = sqlShiftDatabase.shiftDao();
        allShifts = dao.getAllShifts();
    }

    /**
     * @return a rolling list of all of the shifts in the database, will be update live by the database
     */
    @Override
    public LiveData<List<Shift>> getAllShifts() {
        return allShifts;
    }

    /**
     * @param id shift's id
     * @return live data of the said shift.
     */
    @Override
    public LiveData<Shift> getShift(int id){
        this.id = id;
        return dao.getShift(id);
    }

    /**
     * Asynchronously will insert a shift into the database
     * @param shift
     */
    @Override
    public void insert(Shift shift){
        this.shift = shift;
        new asyncShiftChanger(dao).insert(shift);
    }

    /**
     * Asynchronously will update specified shift in the database
     * @param shift
     */
    @Override
    public void update(Shift shift){
        this.shift = shift;
        new asyncShiftChanger(dao).update(shift);
    }

    /**
     * Asynchronously will delete specified shift from the database
     * @param shift
     */
    @Override
    public void delete(Shift shift){
        new asyncShiftChanger(dao).delete(shift);
    }

    // Helper class that is used to asynchronously interact with the database
    private static class asyncShiftChanger extends AsyncTask<Shift, Void, Void>{
        // Dao used to interact with the database, passed in the constructor.
        private SqlShiftDao dao;

        // The data change method, either insert, update, or delete.
        private ShiftChangeType type = null;

        private enum ShiftChangeType { INSERT, UPDATE, DELETE }

        // Constructor, passes the dao.
        asyncShiftChanger(SqlShiftDao dao) {
            this.dao = dao;
        }

        /**
         * Preform the action
         * @param params shift that is manipulated.
         */
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

        /**
         * Set the action type, enqueue the async operation.
         * @param shift specified shift
         */
        void insert(Shift shift){
            type = ShiftChangeType.INSERT;
            execute(shift);
        }

        /**
         * Set the action type, enqueue the async operation.
         * @param shift specified shift
         */
        void update(Shift shift){
            type = ShiftChangeType.UPDATE;
            execute(shift);
        }

        /**
         * Set the action type, enqueue the async operation.
         * @param shift specified shift
         */
        void delete(Shift shift){
            type = ShiftChangeType.DELETE;
            execute(shift);
        }
    }

}
