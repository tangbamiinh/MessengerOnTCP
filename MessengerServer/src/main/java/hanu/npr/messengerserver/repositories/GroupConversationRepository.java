package hanu.npr.messengerserver.repositories;

import hanu.npr.messengerserver.models.GroupConversation;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class GroupConversationRepository extends ConversationRepository {

    public List<GroupConversation> getAllByMemberUsername(String username) {
        Session session = super.getSession();
        String hql = "SELECT gc FROM GroupConversation gc JOIN gc.members m WHERE m.username = :username";
        Query<GroupConversation> query = session.createQuery(hql, GroupConversation.class);
        query.setParameter("username", username);
        return query.getResultList();
    }
}
