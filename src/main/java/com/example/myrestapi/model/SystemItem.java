package com.example.myrestapi.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(appliesTo = "system_item")
@javax.persistence.Table(name = "system_item")
public class SystemItem {

    @NonNull
    @Id
    @Column(unique = true, nullable = false)
    private String id;
    private String url;
    @NonNull
    @Column(nullable = false)
    private String date;
    private String parentId;
    private int size;
    @NonNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SystemItemType type;
    @OneToMany(fetch = FetchType.EAGER, targetEntity = SystemItem.class)
    private List<SystemItem> children;

    public void deleteChild(SystemItem child){
        children.remove(child);
    }

    public void addChildren(SystemItem child){
        children.add(child);
    }

}
