package com.springboot.app.config;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class PaginationHelper {
    public static <T> List<T> getPaginatedResults(Session session, String hql, int page, int size, Class<T> clazz) {
        int start = page * size;

        Query<T> query = session.createQuery(hql, clazz);
        query.setFirstResult(start);
        query.setMaxResults(size);

        return query.list();
    }

}
