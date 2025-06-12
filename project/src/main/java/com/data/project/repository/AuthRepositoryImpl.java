package com.data.project.repository;

import com.data.project.entity.Auth;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthRepositoryImpl implements AuthRepository {

    private final SessionFactory sessionFactory;

    public AuthRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void register(Auth auth) {
        Session session = sessionFactory.getCurrentSession();

        // Mã hóa mật khẩu trước khi lưu
        String hashedPassword = BCrypt.hashpw(auth.getPassword(), BCrypt.gensalt());
        auth.setPassword(hashedPassword);

        session.save(auth);
    }

    @Override
    public void login(String username, String password) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "FROM Auth WHERE username = :username AND status = true";
        Query<Auth> query = session.createQuery(hql, Auth.class);
        query.setParameter("username", username);
        Auth auth = query.uniqueResult();

        if (auth == null) {
            throw new RuntimeException("Tên đăng nhập không tồn tại hoặc tài khoản bị khóa");
        }

        // Kiểm tra mật khẩu mã hóa
        if (!BCrypt.checkpw(password, auth.getPassword())) {
            throw new RuntimeException("Sai mật khẩu");
        }
    }

    @Override
    public void updateAuth(Auth auth) {
        Session session = sessionFactory.getCurrentSession();

        // Nếu có cập nhật mật khẩu thì mã hóa lại
        if (auth.getPassword() != null && !auth.getPassword().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(auth.getPassword(), BCrypt.gensalt());
            auth.setPassword(hashedPassword);
        }

        session.update(auth);
    }

    @Override
    public void updateStatus(Long id, boolean status) {
        Session session = sessionFactory.getCurrentSession();
        Auth auth = session.get(Auth.class, id);
        if (auth != null) {
            auth.setStatus(status);
            session.update(auth);
        }
    }

    @Override
    public List<Auth> getAllAuths(int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Auth";
        Query<Auth> query = session.createQuery(hql, Auth.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }
    @Override
    public Auth getAuthByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Auth WHERE username = :username";
        Query<Auth> query = session.createQuery(hql, Auth.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }

}
