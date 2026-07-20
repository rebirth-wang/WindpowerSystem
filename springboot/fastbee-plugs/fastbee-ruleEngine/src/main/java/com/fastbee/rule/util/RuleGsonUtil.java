package com.fastbee.rule.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RuleGsonUtil {

    public static Gson getGson(){
        return getGson(false);
    }

    public static Gson getGson(boolean format){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        if(format){
            gsonBuilder.setPrettyPrinting();
        }
        return gsonBuilder.create();
    }

}
