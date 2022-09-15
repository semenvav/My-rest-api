package com.example.myrestapi.model;

import lombok.Data;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Size;

@Data
public class SystemItemImport {
    @NonNull
    private String id;
    @Size(max = 255)
    private String url;
    private String parentId;
    private int size = 0;
    @NonNull
    private SystemItemType type;
}
