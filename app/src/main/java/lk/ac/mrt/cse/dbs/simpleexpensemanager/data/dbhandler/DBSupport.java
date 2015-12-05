package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.dbhandler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.contexthandler.ContextConnection;

/**
 * Created by Darshana Priyasad on 12/4/2015.
 */
public class DBSupport extends SQLiteOpenHelper {

    private static final String dbName = "130472B";
    private static final SQLiteDatabase.CursorFactory factory = null;
    private static final int version = 1 ;
    private static final Context context = ContextConnection.getCustomAppContext();


    public DBSupport() {
        super(context, dbName, factory, version);

    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String accountSQL = "CREATE TABLE Account( accountNo TEXT NOT NULL, bankName TEXT NOT NULL, holderName TEXT NOT NULL, balance REAL NOT NULL,PRIMARY KEY(accountNo));";
        String transactionSQL = "CREATE TABLE Transactions(date DATE NOT NULL,accountNo TEXT NOT NULL,expenseType TEXT not null,amount REAL NOT NULL,PRIMARY KEY(date,accountNo),FOREIGN KEY(accountNo) references Transactions(accountNo));";

        db.execSQL(accountSQL);
        db.execSQL(transactionSQL);


    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String accountSQL = "CREATE TABLE Account( accountNo TEXT NOT NULL, bankName TEXT NOT NULL, holderName TEXT NOT NULL, balance REAL NOT NULL,PRIMARY KEY(accountNo));";
        String transactionSQL = "CREATE TABLE Transactions(date DATE NOT NULL,accountNo TEXT NOT NULL,expenseType TEXT NOT NULL,amount REAL NOT NULL,PRIMARY KEY(date,accountNo));";
        db.execSQL(accountSQL);
        db.execSQL(transactionSQL);

    }


    public SQLiteDatabase getReadableDatabaseConnection(){
        return getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabaseConnection(){
        return getWritableDatabase();
    }
}
