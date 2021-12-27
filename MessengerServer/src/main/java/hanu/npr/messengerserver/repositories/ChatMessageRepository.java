package hanu.npr.messengerserver.repositories;

import hanu.npr.messengerserver.config.Db;
import hanu.npr.messengerserver.models.ChatMessage;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.util.List;

public class ChatMessageRepository implements CRUDRepository<Long, ChatMessage> {

    private Session getSession() {
        return Db.getInstance().openSession();
    }

    @Override
    public void addOne(ChatMessage chatMessage) {
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
    public List<ChatMessage> getAll() {
        Session session = getSession();

        return session.createQuery("FROM ChatMessage", ChatMessage.class).list();
    }

    @Override
    public ChatMessage getById(Long id) {
        Session session = getSession();
        return session.get(ChatMessage.class, id);
    }

    @Override
    public void updateById(Long id, ChatMessage updateData) {
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
            ChatMessage target = session.get(ChatMessage.class, id);
            session.delete(target);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public List<ChatMessage> getAllByConversationId(Long conversationId) {
        Session session = getSession();

        Query query = session.createQuery("FROM ChatMessage WHERE id = :conversationId", ChatMessage.class);
        query.setParameter("conversationId", conversationId);
        return query.getResultList();
    }
}
