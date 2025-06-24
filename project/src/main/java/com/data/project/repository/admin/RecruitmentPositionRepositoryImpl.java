package com.data.project.repository.admin;

import com.data.project.entity.RecruitmentPosition;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RecruitmentPositionRepositoryImpl implements RecruitmentPositionRepository {
    private SessionFactory sessionFactory;

    public RecruitmentPositionRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<RecruitmentPosition> findAll(int page, int size) {
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<RecruitmentPosition> query = session.createQuery(
                    "SELECT DISTINCT r FROM RecruitmentPosition r " +
                            "LEFT JOIN FETCH r.technologies " +
                            "WHERE r.status = true ORDER BY r.createdDate DESC",
                    RecruitmentPosition.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in findAll: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public RecruitmentPosition findById(Long id) {
        try {
            Session session = sessionFactory.getCurrentSession();
            return session.createQuery(
                            "SELECT r FROM RecruitmentPosition r " +
                                    "LEFT JOIN FETCH r.technologies " +
                                    "WHERE r.id = :id AND r.status = true",
                            RecruitmentPosition.class)
                    .setParameter("id", id)
                    .uniqueResult();
        } catch (Exception e) {
            System.err.println("Error in findById: " + e.getMessage());
            return null;
        }
    }

    @Override
    public RecruitmentPosition save(RecruitmentPosition recruitmentPosition) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.saveOrUpdate(recruitmentPosition);
            return recruitmentPosition;
        } catch (Exception e) {
            System.err.println("Error in save: " + e.getMessage());
            throw new RuntimeException("Cannot save recruitment position", e);
        }
    }

    @Override
    public void updateStatus(Long id, boolean status) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.createQuery("UPDATE RecruitmentPosition r SET r.status = :status WHERE r.id = :id")
                    .setParameter("status", status)
                    .setParameter("id", id)
                    .executeUpdate();
        } catch (Exception e) {
            System.err.println("Error in updateStatus: " + e.getMessage());
            throw new RuntimeException("Cannot update status", e);
        }
    }

    @Override
    public void updateRecruitmentPosition(RecruitmentPosition recruitmentPosition) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.update(recruitmentPosition);
        } catch (Exception e) {
            System.err.println("Error in updateRecruitmentPosition: " + e.getMessage());
            throw new RuntimeException("Cannot update recruitment position", e);
        }
    }

    @Override
    public List<RecruitmentPosition> searchByName(String name, int page, int size) {
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<RecruitmentPosition> query = session.createQuery(
                    "SELECT DISTINCT r FROM RecruitmentPosition r " +
                            "LEFT JOIN FETCH r.technologies " +
                            "WHERE r.name LIKE :name AND r.status = true ORDER BY r.createdDate DESC",
                    RecruitmentPosition.class);
            query.setParameter("name", "%" + name + "%");
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in searchByName: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public long countAll() {
        try {
            Session session = sessionFactory.getCurrentSession();
            return session.createQuery("SELECT COUNT(r) FROM RecruitmentPosition r WHERE r.status = true", Long.class)
                    .uniqueResult();
        } catch (Exception e) {
            System.err.println("Error in countAll: " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public long countByName(String name) {
        try {
            Session session = sessionFactory.getCurrentSession();
            return session.createQuery(
                            "SELECT COUNT(r) FROM RecruitmentPosition r WHERE r.name LIKE :name AND r.status = true", Long.class)
                    .setParameter("name", "%" + name + "%")
                    .uniqueResult();
        } catch (Exception e) {
            System.err.println("Error in countByName: " + e.getMessage());
            return 0L;
        }
    }
}
