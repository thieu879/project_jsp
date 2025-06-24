package com.data.project.repository.admin;

import com.data.project.entity.Application;
import com.data.project.entity.Progress;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ApplicationRepositoryImpl implements ApplicationRepository {
    private SessionFactory sessionFactory;

    public ApplicationRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Application> findAll(int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Application a ORDER BY a.createdAt DESC";
        Query<Application> query = session.createQuery(hql, Application.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public List<Application> findAllApplications(int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Application a ORDER BY a.createdAt DESC";
        Query<Application> query = session.createQuery(hql, Application.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public Application findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Application.class, id.intValue());
    }

    @Override
    public Application save(Application application) {
        Session session = sessionFactory.getCurrentSession();
        if (application.getId() == 0) {
            application.setCreatedAt(LocalDateTime.now());
        }
        application.setUpdatedAt(LocalDateTime.now());
        session.saveOrUpdate(application);
        return application;
    }

    @Override
    public void updateStatus(Long id, Progress status) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "UPDATE Application a SET a.progress = :status, a.updatedAt = :updatedAt WHERE a.id = :id";
        session.createQuery(hql)
                .setParameter("status", status)
                .setParameter("updatedAt", LocalDateTime.now())
                .setParameter("id", id.intValue())
                .executeUpdate();
    }

    @Override
    public List<Application> searchByName(String name, int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT a FROM Application a " +
                "JOIN Candidate c ON a.candidateId = c.id " +
                "WHERE c.name LIKE :name ORDER BY a.createdAt DESC";
        Query<Application> query = session.createQuery(hql, Application.class);
        query.setParameter("name", "%" + name + "%");
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public long countAll() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Application";
        Query<Long> query = session.createQuery(hql, Long.class);
        return query.uniqueResult();
    }

    @Override
    public long countByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(a) FROM Application a " +
                "JOIN Candidate c ON a.candidateId = c.id " +
                "WHERE c.name LIKE :name";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("name", "%" + name + "%");
        return query.uniqueResult();
    }

    // Thêm các phương thức mới
    @Override
    public List<Application> findByStatus(Progress status, int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Application a WHERE a.progress = :status ORDER BY a.createdAt DESC";
        Query<Application> query = session.createQuery(hql, Application.class);
        query.setParameter("status", status);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public long countByStatus(Progress status) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Application a WHERE a.progress = :status";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("status", status);
        return query.uniqueResult();
    }

    @Override
    public List<Application> searchByNameAndStatus(String name, Progress status, int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT a FROM Application a " +
                "JOIN Candidate c ON a.candidateId = c.id " +
                "WHERE c.name LIKE :name AND a.progress = :status ORDER BY a.createdAt DESC";
        Query<Application> query = session.createQuery(hql, Application.class);
        query.setParameter("name", "%" + name + "%");
        query.setParameter("status", status);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public long countByNameAndStatus(String name, Progress status) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(a) FROM Application a " +
                "JOIN Candidate c ON a.candidateId = c.id " +
                "WHERE c.name LIKE :name AND a.progress = :status";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("name", "%" + name + "%");
        query.setParameter("status", status);
        return query.uniqueResult();
    }

    @Override
    public void updateApplication(Application application) {
        Session session = sessionFactory.getCurrentSession();
        application.setUpdatedAt(LocalDateTime.now());
        session.merge(application);
    }
    @Override
    public List<Application> findByCandidateId(Long candidateId, int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Application a WHERE a.candidateId = :candidateId ORDER BY a.createdAt DESC";
        Query<Application> query = session.createQuery(hql, Application.class);
        query.setParameter("candidateId", candidateId);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public long countByCandidateId(Long candidateId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Application a WHERE a.candidateId = :candidateId";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("candidateId", candidateId);
        return query.uniqueResult();
    }

}
