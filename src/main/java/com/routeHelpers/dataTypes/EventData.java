package com.routeHelpers.dataTypes;

public class EventData {
    public int spaceId, messageId, eventType;

    public int getSpaceId() { return spaceId; }
    public int getMessageId() { return messageId; }
    public int getEventType() { return eventType; }

    public void setSpaceId(int spaceId) { this.spaceId = spaceId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }
    public void setEventType(int eventType) { this.eventType = eventType; }

    public EventData set(int spaceId, int messageId, int eventType) {
        this.spaceId = spaceId;
        this.messageId = messageId;
        this.eventType = eventType;
        return this;
    }
}