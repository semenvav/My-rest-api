package com.example.myrestapi.service;

import com.example.myrestapi.model.SystemItem;
import com.example.myrestapi.model.SystemItemImport;
import com.example.myrestapi.model.SystemItemImportRequest;
import com.example.myrestapi.model.SystemItemType;
import com.example.myrestapi.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;


@Service
public class SystemItemImportRequestServiceImpl implements SystemItemImportRequestService{

    @Override
    public void imports(SystemItemImportRequest request) throws RuntimeException {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Set<String> idSet = new HashSet<>();
            transaction = session.beginTransaction();
            for (SystemItemImport itemImport : request.getItems()){
                checkFolderSize(itemImport);
                checkFileSize(itemImport);
                timeFormatCheck(request);
                checkUniqId(idSet, itemImport);
                checkFolderUrl(itemImport);
                SystemItem itemForChange = session.get(SystemItem.class, itemImport.getId());
                SystemItem childItem;
                if (itemForChange == null) {
                    SystemItem item = buildNewItem(request, itemImport);
                    session.save(item);
                    childItem = item;
                }
                else {
                    checkTypeUpdate(itemImport, itemForChange);
                    if(itemForChange.getParentId() != null && itemImport.getParentId() == null){
                        deleteParent(request, session, itemImport, itemForChange);
                    }
                    updateItem(request, itemImport, itemForChange);
                    session.update(itemForChange);
                    childItem = itemForChange;
                }
                if(itemImport.getParentId() != null){
                    checkParentType(session, childItem);
                    addChild(request, session, childItem);
                }
            }
            transaction.commit();
        }
        catch (Exception e){
            if(transaction != null){
                try {
                    transaction.rollback();
                }
                catch (Exception ignored){}
            }
            throw new RuntimeException();
        }
    }

    private void checkUniqId(Set<String> idSet, SystemItemImport itemImport) {
        if(idSet.contains(itemImport.getId())){
            throw new RuntimeException();
        }
        idSet.add(itemImport.getId());
    }

    private void checkTypeUpdate(SystemItemImport itemImport, SystemItem itemForChange) {
        if(itemImport.getType() != itemForChange.getType()){
            throw new RuntimeException();
        }
    }

    private void checkFolderSize(SystemItemImport itemImport) {
        if (itemImport.getType() == SystemItemType.FOLDER && itemImport.getSize() != 0){
            throw new RuntimeException();
        }
    }

    private void checkFileSize(SystemItemImport itemImport) {
        if (itemImport.getType() == SystemItemType.FILE && itemImport.getSize() <= 0){
            throw new RuntimeException();
        }
    }

    private void updateItem(SystemItemImportRequest request, SystemItemImport itemImport, SystemItem itemForChange) {
        itemForChange.setDate(request.getUpdateDate())
                .setUrl(itemImport.getUrl())
                .setParentId(itemImport.getParentId())
                .setSize(itemImport.getSize());
    }

    private SystemItem buildNewItem(SystemItemImportRequest request, SystemItemImport itemImport) {
        SystemItem item = new SystemItem();
        item.setId(itemImport.getId())
                .setSize(itemImport.getSize())
                .setType(itemImport.getType())
                .setUrl(itemImport.getUrl())
                .setParentId(itemImport.getParentId())
                .setDate(request.getUpdateDate());
        if(itemImport.getType() == SystemItemType.FILE){
            item.setChildren(null);
        }
        return item;
    }

    private void timeFormatCheck(SystemItemImportRequest request) {
        ZonedDateTime d = ZonedDateTime.parse(request.getUpdateDate());

    }

    private void checkParentType(Session session, SystemItem childItem) {
        SystemItem parentItem = session.get(SystemItem.class, childItem.getParentId());
        if(parentItem.getType() == SystemItemType.FILE){
            throw new RuntimeException();
        }
    }

    private void checkFolderUrl(SystemItemImport itemImport) {
        if(itemImport.getType() == SystemItemType.FOLDER && itemImport.getUrl() != null && !itemImport.getUrl().equals("")){
            throw new RuntimeException();
        }
    }

    private void deleteParent(SystemItemImportRequest request, Session session, SystemItemImport itemImport, SystemItem itemForChange) {
        SystemItem parentItem = session.get(SystemItem.class, itemForChange.getParentId());
        parentItem.deleteChild(itemForChange);
        while(parentItem != null) {
            parentItem.setDate(request.getUpdateDate());
            parentItem.setSize(parentItem.getSize() - itemImport.getSize());
            session.update(parentItem);
            if(parentItem.getParentId() == null){
                break;
            }
            parentItem = session.get(SystemItem.class, parentItem.getParentId());
        }
    }

    private void addChild(SystemItemImportRequest request, Session session , SystemItem childItem) {
        SystemItem parentItem = session.get(SystemItem.class, childItem.getParentId());
        parentItem.addChildren(childItem);
        while(parentItem != null) {
            parentItem.setDate(request.getUpdateDate());
            parentItem.setSize(parentItem.getSize() + childItem.getSize());
            session.update(parentItem);
            if(parentItem.getParentId() == null){
                break;
            }
            parentItem = session.get(SystemItem.class, parentItem.getParentId());
        }
    }
}
