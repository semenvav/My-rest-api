package com.example.myrestapi.model;

import lombok.Data;
import org.springframework.lang.NonNull;

import javax.validation.Valid;


@Data
public class SystemItemImportRequest {

    @Valid
    @NonNull
    SystemItemImport[] items;
    @NonNull
    String updateDate;


}
