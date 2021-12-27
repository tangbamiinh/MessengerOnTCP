package hanu.npr.messengerserver.repositories;

import hanu.npr.messengerserver.config.Db;
import hanu.npr.messengerserver.models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserRepository implements CRUDRepository<String, User> {

    private Session getSession() {
        return Db.getInstance().openSession();
    }

    @Override
    public void addOne(User user) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            // do some work
            session.persist(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public List<User> getAll() {
        Session session = getSession();
        List<User> ret = session.createQuery("FROM User", User.class).list();
        session.close();
        return ret;
    }

    @Override
    public User getById(String id) {
        Session session = getSession();
        return session.get(User.class, id);
    }

    @Override
    public void updateById(String username, User newUser) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            // do some work
            session.merge(newUser);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void deleteById(String username) {

        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            // do some work
            User user = session.get(User.class, username);
            session.delete(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
