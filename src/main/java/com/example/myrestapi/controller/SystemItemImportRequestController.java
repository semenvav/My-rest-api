package com.example.myrestapi.controller;

import com.example.myrestapi.model.Error;
import com.example.myrestapi.model.SystemItemImportRequest;
import com.example.myrestapi.service.SystemItemImportRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemItemImportRequestController {
    private final SystemItemImportRequestService systemItemImportRequestService;

    @Autowired
    public SystemItemImportRequestController(SystemItemImportRequestService systemItemImportRequestService){
        this.systemItemImportRequestService =systemItemImportRequestService;
    }

    @PostMapping(value = "/imports")
    public ResponseEntity<?> imports( @RequestBody SystemItemImportRequest request){
        try {
            systemItemImportRequestService.imports(request);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception ignored){
            return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        }
    }
}
