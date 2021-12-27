package hanu.npr.messengerserver.repositories;

import hanu.npr.messengerserver.config.Db;
import hanu.npr.messengerserver.models.Attachment;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.util.List;

public class AttachmentRepository implements CRUDRepository<Long, Attachment> {

    private Session getSession() {
        return Db.getInstance().openSession();
    }

    @Override
    public void addOne(Attachment chatMessage) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            session.persist(chatMessage);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public List<Attachment> getAll() {
        Session session = getSession();

        return session.createQuery("FROM Attachment", Attachment.class).list();
    }

    @Override
    public Attachment getById(Long id) {
        Session session = getSession();
        return session.get(Attachment.class, id);
    }

    @Override
    public void updateById(Long id, Attachment updateData) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            // do some work
            session.merge(updateData);
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
            Attachment target = session.get(Attachment.class, id);
            session.delete(target);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Attachment> getAllByConversationId(Long conversationId) {
        Session session = getSession();

        Query query = session.createQuery("FROM Attachment WHERE id = :conversationId", Attachment.class);
        query.setParameter("conversationId", conversationId);
        return query.getResultList();
    }
}
