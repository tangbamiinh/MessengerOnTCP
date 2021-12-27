package hanu.npr.messengerserver.repositories;

import hanu.npr.messengerserver.models.PrivateConversation;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class PrivateConversationRepository extends ConversationRepository {

    public PrivateConversation getOneByBothUsername(String username1, String username2) {
        Session session = super.getSession();
        String hql = "FROM PrivateConversation WHERE (user1.username = :username1 AND user2.username = :username2) OR (user1.username = :username2 AND user2.username = :username1)";
        Query<PrivateConversation> query = session.createQuery(hql, PrivateConversation.class);
        query.setParameter("username1", username1);
        query.setParameter("username2", username2);
        return query.getSingleResult();
    }

    public List<PrivateConversation> getAllByUsername(String username) {
        Session session = super.getSession();
        String hql = "FROM PrivateConversation WHERE user1.username = :username OR user2.username = :username";
        Query<PrivateConversation> query = session.createQuery(hql, PrivateConversation.class);
        query.setParameter("username", username);
        return query.list();
    }
}
