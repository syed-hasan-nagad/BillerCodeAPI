package com.billercode.api.billercodeapi.services;

import java.util.UUID;

public class sessionIdGenerator {
    public static String getNewSessionId(){
        UUID sessionID = UUID.randomUUID();
        return sessionID.toString();
    }
}
