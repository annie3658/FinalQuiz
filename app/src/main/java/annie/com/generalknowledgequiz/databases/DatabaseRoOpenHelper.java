package annie.com.generalknowledgequiz.databases;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Annie on 12/05/2017.
 */

public class DatabaseRoOpenHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "roQuestions.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseRoOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }
}
