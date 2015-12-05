package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.contexthandler.ContextConnection;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.dbhandler.DBHandle;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.dbhandler.DBSupport;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Darshana Priyasad on 12/4/2015.
 */
public class PersistentTransactionDAO implements TransactionDAO {

    private DBSupport dbSupport;
    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;
    private DBHandle dbHandle;

    public PersistentTransactionDAO(){

        dbHandle = new DBHandle();
        dbSupport = new DBSupport();
        writableDatabase = dbSupport.getWritableDatabaseConnection();
        readableDatabase = dbSupport.getReadableDatabaseConnection();
    }
    /***
     * Log the transaction requested by the user.
     *
     * @param date        - date of the transaction
     * @param accountNo   - account number involved
     * @param expenseType - type of the expense
     * @param amount      - amount involved
     */
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        String sql = "INSERT INTO Transactions VALUES(?,?,?,?);";
        String values[] = {date.toString(), accountNo, expenseType.toString(), String.valueOf(amount)};
        boolean logged = dbHandle.setData(writableDatabase, sql, values,1);
        if(logged){
            Context context = ContextConnection.getCustomAppContext();
            CharSequence text = "Successfully Logged";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
            Context context = ContextConnection.getCustomAppContext();
            CharSequence text = "Error Occured, Try Again";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
        /***
     * Return all the transactions logged.
     *
     * @return - a list of all the transactions
     */
    @Override
    public List<Transaction> getAllTransactionLogs() {

        String sql = "SELECT * FROM Transactions;";
        Cursor data = dbHandle.getData(readableDatabase, sql);
        List<Transaction> transactionList = new ArrayList<>();
        if(data.moveToFirst()){
            do{
                String temp_date = data.getString(0);
                Date dateVal = new Date(temp_date);
                String accountNo = data.getString(1);
                String type = data.getString(2);
                ExpenseType expType;
                if(type.equals(ExpenseType.EXPENSE.toString())){
                    expType = ExpenseType.EXPENSE;
                }else{
                    expType = ExpenseType.INCOME;
                }
                double balance = Double.parseDouble(data.getString(3));
                Transaction transaction = new Transaction(dateVal,accountNo,expType,balance);
                transactionList.add(transaction);
            }while(data.moveToNext());
        }else{
            Context context = ContextConnection.getCustomAppContext();
            CharSequence text = "No Transactions Found";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        return transactionList;

    }

    /***
     * Return a limited amount of transactions logged.
     *
     * @param limit - number of transactions to be returned
     * @return - a list of requested number of transactions
     */
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        String sql = "SELECT * FROM Transactions LIMIT ?";
        String[] values = {String.valueOf(limit)};
        Cursor data = dbHandle.getData(readableDatabase, sql , values);
        List<Transaction> transactionList = new ArrayList<>();
        if(data.moveToFirst()){
            do{
                String temp_date = data.getString(0);

                Date dateVal = new Date(temp_date);
                String accountNo = data.getString(1);
                String type = data.getString(2);
                ExpenseType expType;
                if(type.equals(ExpenseType.EXPENSE.toString())){
                    expType = ExpenseType.EXPENSE;
                }else{
                    expType = ExpenseType.INCOME;
                }
                double balance = Double.parseDouble(data.getString(3));
                Transaction transaction = new Transaction(dateVal,accountNo,expType,balance);
                transactionList.add(transaction);
            }while(data.moveToNext());
        }
        return transactionList;
    }
}
