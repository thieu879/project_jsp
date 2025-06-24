package com.data.project.repository.admin;

import com.data.project.entity.Technology;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TechnologyRepositoryImpl implements TechnologyRepository {
    private SessionFactory sessionFactory;

    public TechnologyRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Technology> findAll(int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        Query<Technology> query = session.createQuery(
                "FROM Technology t WHERE t.status = true ORDER BY t.name", Technology.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public Technology findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "FROM Technology t WHERE t.id = :id AND t.status = true", Technology.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    @Override
    public Technology save(Technology technology) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(technology);
        return technology;
    }

    @Override
    public void updateStatus(Long id, boolean status) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("UPDATE Technology t SET t.status = :status WHERE t.id = :id")
                .setParameter("status", status)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void updateTechnology(Technology technology) {
        Session session = sessionFactory.getCurrentSession();
        session.update(technology);
    }

    @Override
    public List<Technology> searchByName(String name, int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        Query<Technology> query = session.createQuery(
                "FROM Technology t WHERE t.name LIKE :name AND t.status = true ORDER BY t.name",
                Technology.class);
        query.setParameter("name", "%" + name + "%");
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long countAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT COUNT(t) FROM Technology t WHERE t.status = true", Long.class)
                .uniqueResult();
    }

    @Override
    public long countByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "SELECT COUNT(t) FROM Technology t WHERE t.name LIKE :name AND t.status = true", Long.class)
                .setParameter("name", "%" + name + "%")
                .uniqueResult();
    }

    @Override
    public boolean existsByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Long count = session.createQuery(
                        "SELECT COUNT(t) FROM Technology t WHERE LOWER(TRIM(t.name)) = LOWER(TRIM(:name)) AND t.status = true",
                        Long.class)
                .setParameter("name", name)
                .uniqueResult();
        return count > 0;
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        Session session = sessionFactory.getCurrentSession();
        Long count = session.createQuery(
                        "SELECT COUNT(t) FROM Technology t WHERE LOWER(TRIM(t.name)) = LOWER(TRIM(:name)) AND t.id != :id AND t.status = true",
                        Long.class)
                .setParameter("name", name)
                .setParameter("id", id)
                .uniqueResult();
        return count > 0;
    }
}
