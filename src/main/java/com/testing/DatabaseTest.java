package com.testing;

import com.db.Database;
import com.routeHelpers.dataTypes.EventData;

/**
 * Created by a623557 on 25-5-2016.
 */
public class DatabaseTest {
    public static void main(String[] arg) {
        Database database = Database.getInstance();
        database.insertEvent(new EventData().set(3, 3, 1));
        System.out.println("Amount of spaces: " + database.getSpaceStats(3));
        database.insertEvent(new EventData().set(3,3,4));
        System.out.println("Amount of likes" + database.getMessageStats(3));
    }




}
