package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.contexthandler.ContextConnection;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.dbhandler.DBHandle;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.dbhandler.DBSupport;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by Darshana Priyasad on 12/4/2015.
 */
public class PersistentAccountDAO implements AccountDAO{

    private DBSupport dbSupport;
    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;
    private DBHandle dbHandle;

    public PersistentAccountDAO(){

        dbHandle = new DBHandle();
        dbSupport = new DBSupport();
        writableDatabase = dbSupport.getWritableDatabaseConnection();
        readableDatabase = dbSupport.getReadableDatabaseConnection();
    }

    /***
     * Get a list of account numbers.
     *
     * @return - list of account numbers as String
     */
    @Override
    public List<String> getAccountNumbersList() {

        String sql = "SELECT accountNo FROM Account;";
        Cursor data = dbHandle.getData(readableDatabase, sql);
        List<String> accountList = new ArrayList<>();
        if(data.moveToFirst()){
            do{
                String number = data.getString(0);
                accountList.add(number);
            }while(data.moveToNext());
        }
        return accountList;
    }

    /***
     * Get a list of accounts.
     *
     * @return - list of Account objects.
     */
    @Override
    public List<Account> getAccountsList() {

        String sql = "SELECT * FROM Account;";
        Cursor data = dbHandle.getData(readableDatabase, sql);
        List<Account> accountList = new ArrayList<>();
        if(data.moveToFirst()){
            do{
                String accountNo = data.getString(0);
                String bankName = data.getString(1);
                String holderName = data.getString(2);
                double balance = Double.parseDouble(data.getString(3));
                Account account = new Account(accountNo,bankName,holderName,balance);
                accountList.add(account);
            }while(data.moveToNext());
        }
        return accountList;
    }

    /***
     * Get the account given the account number.
     *
     * @param accountNo as String
     * @return - the corresponding Account
     * @throws InvalidAccountException - if the account number is invalid
     */
    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String sql = "SELECT * FROM Account WHERE accountNo=?;";
        String[] values = {accountNo};
        Cursor data=dbHandle.getData(readableDatabase,sql,values);

        if(data.moveToFirst()){
            String accountNos = data.getString(0);
            String bankName = data.getString(1);
            String holderName = data.getString(2);
            double balance = Double.parseDouble(data.getString(3));
            Account account = new Account(accountNos,bankName,holderName,balance);
            return account;

        }

        String message = "Invalid Account Number : '"+ accountNo+"'";
        throw new InvalidAccountException(message);
    }

    /***
     * Add an account to the accounts collection.
     *
     * @param account - the account to be added.
     */
    @Override
    public void addAccount(Account account) {

        String temp_sql = "SELECT * FROM Account WHERE accountNo=?;";
        String[] values = {account.getAccountNo()};
        Cursor exists=dbHandle.getData(readableDatabase, temp_sql, values);
        if (!exists.moveToFirst()){
            String sql = "INSERT INTO Account VALUES(?,?,?,?)";
            String[] data = {account.getAccountNo(),account.getBankName(),account.getAccountHolderName(), String.valueOf(account.getBalance())};
            boolean added = dbHandle.setData(writableDatabase, sql,data,1);
            Context context = ContextConnection.getCustomAppContext();
            CharSequence text = "Sucessfully Addded";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
            Context context = ContextConnection.getCustomAppContext();
            CharSequence text = "Duplicate Entry, Please check again !!!!!!!!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    /***
     * Remove an account from the accounts collection.
     *
     * @param accountNo - of the account to be removed.
     * @throws InvalidAccountException - if the account number is invalid
     */
    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String sql = "DELETE FROM Account WHERE accountNo=?";
        String data[] = {accountNo};
        boolean deleted = dbHandle.setData(writableDatabase, sql, data,2);
        if(!deleted){
            String message = "Invalid Account Number : '"+ accountNo+"'";
            throw new InvalidAccountException(message);
        }else{
            Context context = ContextConnection.getCustomAppContext();
            CharSequence text = "Successfully Deleted";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    /***
     * Update the balance of the given account. The type of the expense is specified in order to determine which
     * action to be performed.
     * <p/>
     * The implementation has the flexibility to figure out how the updating operation is committed based on the type
     * of the transaction.
     *
     * @param accountNo   - account number of the respective account
     * @param expenseType - the type of the transaction
     * @param amount      - amount involved
     * @throws InvalidAccountException - if the account number is invalid
     */
    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        String sql = "UPDATE Account SET balance = balance + ? WHERE accountNo = ?";
        String data[] = null;
        if(expenseType.equals(ExpenseType.EXPENSE)){
            data= new String[]{String.valueOf(amount * -1), accountNo};
        }else{
            data= new String[]{String.valueOf(amount), accountNo};
        }
        boolean updated = dbHandle.setData(writableDatabase, sql, data,2);
        if(!updated){
            String message = "Invalid Account Number : '"+ accountNo+"'";
            throw new InvalidAccountException(message);
        }else{
            Context context = ContextConnection.getCustomAppContext();
            CharSequence text = "Successfully Updated Balance";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}