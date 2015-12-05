package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.contexthandler;

import android.app.Application;
import android.content.Context;

/**
 * Created by Darshana Priyasad on 12/4/2015.
 */
public class ContextConnection extends Application{

    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getCustomAppContext(){
        return context;
    }

}
