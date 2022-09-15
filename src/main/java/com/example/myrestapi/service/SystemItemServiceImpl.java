package com.example.myrestapi.service;

import com.example.myrestapi.model.SystemItem;
import com.example.myrestapi.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

@Service
public class SystemItemServiceImpl implements SystemItemService{

    @Override
    public void delete(String id, String date) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            SystemItem itemForDelete = session.get(SystemItem.class, id);
            deleteChilds(session, itemForDelete);
            session.delete(itemForDelete);
            SystemItem parentItem = session.get(SystemItem.class, itemForDelete.getParentId());
            updateParentData(date, session, itemForDelete, parentItem);
            transaction.commit();
        }catch (Exception e){
            if(transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void deleteChilds(Session session, SystemItem itemForDelete) {
        for(SystemItem child : itemForDelete.getChildren()){
            session.delete(child);
        }
    }

    private void updateParentData(String date, Session session, SystemItem itemForDelete, SystemItem parentItem) {
        if(parentItem != null){
            parentItem.deleteChild(itemForDelete);
        }
        while (parentItem != null){
            parentItem.setDate(date);
            parentItem.setSize(parentItem.getSize() - itemForDelete.getSize());
            session.update(parentItem);
            if(parentItem.getParentId() == null){
                break;
            }
            parentItem = session.get(SystemItem.class, parentItem.getParentId());
        }
    }

    @Override
    public SystemItem get(String id) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            SystemItem ReturningItem = session.get(SystemItem.class, id);
            transaction.commit();
            return ReturningItem;
        }catch (Exception e){
            if(transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
