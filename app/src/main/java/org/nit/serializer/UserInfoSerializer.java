package org.nit.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.nit.eventproducer.UserInfoEvent;
import org.nit.model.UserInfoDto;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, UserInfoEvent userInfoEvent) {
        byte[] retval =null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
           retval = objectMapper.writeValueAsString (userInfoEvent).getBytes();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return retval;
    }

    @Override
    public void close() {

    }
}
