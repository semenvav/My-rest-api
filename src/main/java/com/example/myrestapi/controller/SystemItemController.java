package com.example.myrestapi.controller;

import com.example.myrestapi.model.Error;
import com.example.myrestapi.model.SystemItem;
import com.example.myrestapi.service.SystemItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;



@RestController
public class SystemItemController {

    private final SystemItemService systemItemService;

    @Autowired
    public SystemItemController(SystemItemService systemItemService){
        this.systemItemService =systemItemService;
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, @RequestParam String date){
        try{
            timeFormatCheck(date);
            if(!Objects.equals(id, "") && id.length() > 255){
                return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
            }
            systemItemService.delete(id, date);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (DateTimeParseException e){
            e.printStackTrace();
            return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new Error(404, "Item not found"),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/nodes/{id}")
    public ResponseEntity<?> get(@PathVariable String id){
        try{
            if(!Objects.equals(id, "") && id.length() > 255){
                return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
            }
            SystemItem item = systemItemService.get(id);
            return new ResponseEntity<>(item, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new Error(404, "Item not found"),HttpStatus.NOT_FOUND);
        }
    }

    private void timeFormatCheck(String date) {
        ZonedDateTime.parse(date);
    }
}
