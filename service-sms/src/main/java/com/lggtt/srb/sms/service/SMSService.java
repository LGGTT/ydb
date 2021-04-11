package com.lggtt.srb.sms.service;

import java.util.Map;

public interface SMSService {
    void send(String phone, String template, Map<String,Object> param);
}
