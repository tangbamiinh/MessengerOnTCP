package hanu.npr.messengerserver.repositories;

import hanu.npr.messengerserver.config.Db;
import hanu.npr.messengerserver.models.Conversation;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ConversationRepository implements CRUDRepository<Long, Conversation> {

    protected Session getSession() {
        return Db.getInstance().openSession();
    }

    @Override
    public void addOne(Conversation conversation) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            // do some work
            session.persist(conversation);
            session.flush();
            tx.commit();
            session.close();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public List<Conversation> getAll() {
        Session session = getSession();

        return session.createQuery("FROM Conversation", Conversation.class).list();
    }

    @Override
    public Conversation getById(Long id) {
        Session session = getSession();
        return session.get(Conversation.class, id);
    }

    @Override
    public void updateById(Long id, Conversation newConversation) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            // do some work
            session.merge(newConversation);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void deleteById(Long id) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            // do some work
            Conversation target = session.get(Conversation.class, id);
            session.delete(target);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
