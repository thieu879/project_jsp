package com.data.project.repository.admin;

import com.data.project.entity.Candidate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CandidateRepositoryImpl implements CandidateRepository {
    public SessionFactory sessionFactory;

    public CandidateRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Candidate> findAll(int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Candidate ORDER BY id DESC";
        Query<Candidate> query = session.createQuery(hql, Candidate.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public Candidate findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT c FROM Candidate c LEFT JOIN FETCH c.technologies WHERE c.id = :id";
        Query<Candidate> query = session.createQuery(hql, Candidate.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }


    @Override
    public Candidate save(Candidate candidate) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(candidate);
        return candidate;
    }

    @Override
    public void updateStatus(Long id, boolean status) {
        Session session = sessionFactory.getCurrentSession();
        Candidate candidate = session.get(Candidate.class, id);
        if (candidate != null) {
            candidate.setStatus(status);
            session.update(candidate);
        }
    }

    @Override
    public List<Candidate> searchByName(String name, int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Candidate WHERE name LIKE :name ORDER BY id DESC";
        Query<Candidate> query = session.createQuery(hql, Candidate.class);
        query.setParameter("name", "%" + name + "%");
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public long countAll() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Candidate";
        Query<Long> query = session.createQuery(hql, Long.class);
        return query.uniqueResult();
    }

    @Override
    public long countSearchByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Candidate WHERE name LIKE :name";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("name", "%" + name + "%");
        return query.uniqueResult();
    }

    @Override
    public boolean existsByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Candidate WHERE email = :email";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("email", email);
        return query.uniqueResult() > 0;
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Candidate WHERE email = :email AND id != :id";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("email", email);
        query.setParameter("id", id);
        return query.uniqueResult() > 0;
    }

    @Override
    public List<Candidate> findWithFilters(Map<String, Object> filters, int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("FROM Candidate c WHERE 1=1");

        // Xử lý filter technology
        if (filters.containsKey("technologys")) {
            hql.append(" AND EXISTS (SELECT 1 FROM c.technologies t WHERE t.id = :technologyId)");
        }

        // Xây dựng câu query động
        if (filters.containsKey("search")) {
            hql.append(" AND c.name LIKE :search");
        }
        if (filters.containsKey("gender")) {
            hql.append(" AND c.gender = :gender");
        }
        if (filters.containsKey("experience")) {
            String expRange = (String) filters.get("experience");
            if (expRange.equals("0-1")) {
                hql.append(" AND c.experience BETWEEN 0 AND 1");
            } else if (expRange.equals("2-3")) {
                hql.append(" AND c.experience BETWEEN 2 AND 3");
            } else if (expRange.equals("4-5")) {
                hql.append(" AND c.experience BETWEEN 4 AND 5");
            } else if (expRange.equals("5+")) {
                hql.append(" AND c.experience > 5");
            }
        }
        if (filters.containsKey("status")) {
            hql.append(" AND c.status = :status");
        }

        hql.append(" ORDER BY c.id DESC");

        Query<Candidate> query = session.createQuery(hql.toString(), Candidate.class);

        // Set parameters
        if (filters.containsKey("technologys")) {
            query.setParameter("technologyId", filters.get("technologys"));
        }
        if (filters.containsKey("search")) {
            query.setParameter("search", "%" + filters.get("search") + "%");
        }
        if (filters.containsKey("gender")) {
            query.setParameter("gender", filters.get("gender"));
        }
        if (filters.containsKey("status")) {
            query.setParameter("status", filters.get("status"));
        }

        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public long countWithFilters(Map<String, Object> filters) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("SELECT COUNT(*) FROM Candidate c WHERE 1=1");

        // Xử lý filter technology
        if (filters.containsKey("technologys")) {
            hql.append(" AND EXISTS (SELECT 1 FROM c.technologies t WHERE t.id = :technologyId)");
        }

        // Xây dựng câu query động
        if (filters.containsKey("search")) {
            hql.append(" AND c.name LIKE :search");
        }
        if (filters.containsKey("gender")) {
            hql.append(" AND c.gender = :gender");
        }
        if (filters.containsKey("experience")) {
            String expRange = (String) filters.get("experience");
            if (expRange.equals("0-1")) {
                hql.append(" AND c.experience BETWEEN 0 AND 1");
            } else if (expRange.equals("2-3")) {
                hql.append(" AND c.experience BETWEEN 2 AND 3");
            } else if (expRange.equals("4-5")) {
                hql.append(" AND c.experience BETWEEN 4 AND 5");
            } else if (expRange.equals("5+")) {
                hql.append(" AND c.experience > 5");
            }
        }
        if (filters.containsKey("status")) {
            hql.append(" AND c.status = :status");
        }

        Query<Long> query = session.createQuery(hql.toString(), Long.class);

        // Set parameters
        if (filters.containsKey("technologys")) {
            query.setParameter("technologyId", filters.get("technologys"));
        }
        if (filters.containsKey("search")) {
            query.setParameter("search", "%" + filters.get("search") + "%");
        }
        if (filters.containsKey("gender")) {
            query.setParameter("gender", filters.get("gender"));
        }
        if (filters.containsKey("status")) {
            query.setParameter("status", filters.get("status"));
        }

        return query.uniqueResult();
    }
}
