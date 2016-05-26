package com.db;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneOptions;
import com.routeHelpers.dataTypes.EventData;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by a623557 on 24-5-2016.
 */
public class Database {
    MongoClient mongoClient;
    MongoDatabase db;
    MongoCollection<BasicDBObject> col;

    public Database() {
        try{
            mongoClient = new MongoClient("localhost", 27017);
            db = mongoClient.getDatabase("Bk_Java");
            col = db.getCollection("Events", BasicDBObject.class);

            //Turn of unnecessary logging.
            Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.OFF);
            Logger.getLogger("org.mongodb.driver.management").setLevel(Level.OFF);
            Logger.getLogger("org.mongodb.driver.cluster").setLevel(Level.OFF);
            Logger.getLogger("org.mongodb.driver.protocol.insert").setLevel(Level.OFF);
            Logger.getLogger("org.mongodb.driver.protocol.query").setLevel(Level.OFF);
            Logger.getLogger("org.mongodb.driver.protocol.update").setLevel(Level.OFF);

            System.out.println("Connect to database successfully");
        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public Boolean insertEvent(EventData event) {
        try {
            BasicDBObject eventDocument = new BasicDBObject("spaceId", event.spaceId)
                    .append("messageId", event.messageId)
                    .append("eventType", event.eventType);
            col.insertOne(eventDocument);
            return true;
        } catch(Exception e){
            System.err.println("Error insert event: " + e);
            return false;
        }
    }

    public Long getSpaceStats(int spaceId) {
        try {
            BasicDBObject getCreatedEvents = new BasicDBObject("spaceId", spaceId).append("eventType", 1);
            BasicDBObject getDeletedEvents = new BasicDBObject("spaceId", spaceId).append("eventType", 2);

            return col.count(getCreatedEvents) - col.count(getDeletedEvents);
        } catch(Exception e) {
            System.err.println("getSpaceStats error: " + e);
            return null;
        }
    }

    public Long getMessageStats(int messageId) {
        BasicDBObject getLikes = new BasicDBObject("messageId", messageId).append("eventType", 4);
        BasicDBObject getUnlikes = new BasicDBObject("messageId", messageId).append("eventType", 5);

        return col.count(getLikes) - col.count(getUnlikes);
    }
}