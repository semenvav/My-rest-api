package com.example.myrestapi.service;

import com.example.myrestapi.model.SystemItem;

public interface SystemItemService {

    void delete(String id, String date);

    SystemItem get(String id);

}
