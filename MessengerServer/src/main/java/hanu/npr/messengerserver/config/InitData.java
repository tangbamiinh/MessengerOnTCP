package hanu.npr.messengerserver.config;

import hanu.npr.messengerserver.models.ChatMessage;
import hanu.npr.messengerserver.models.GroupConversation;
import hanu.npr.messengerserver.models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


public class InitData {

    public static final String ADMIN_USERNAME = "admin";

    private User admin;

    private User minhtb;

    private User duongnt;

    private GroupConversation groupConversation;

    public GroupConversation getGroupConversation() {
        return groupConversation;
    }

    public InitData() {
        addAdminUser();
        addGlobalConversation();
        addSampleUsers();
        addSampleUsersToGlobalConversation();
    }

    private void addAdminUser() {

        Session session = Db.getInstance().openSession();

        admin = session.get(User.class, ADMIN_USERNAME);

        if (admin == null) {
            admin = new User();
            admin.setFullName("#ADMIN");
            admin.setUsername(ADMIN_USERNAME);
            admin.setPassword("12345678");

            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(admin);
                session.flush();
                tx.commit();
                session.close();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
    }

    private void addGlobalConversation() {
        Session session = Db.getInstance().openSession();

        groupConversation = session.createQuery("FROM GroupConversation", GroupConversation.class).uniqueResult();

        if (groupConversation == null) {

            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                groupConversation = new GroupConversation();

                groupConversation.setName("Global");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent("Hello everyone!");

                Query<User> userQuery = session.createQuery("From User WHERE username = :username", User.class);
                userQuery.setParameter("username", ADMIN_USERNAME);

                User sender = userQuery.uniqueResult();
                chatMessage.setSender(sender);

                groupConversation.addMessage(chatMessage);

                session.save(groupConversation);

                session.flush();
                tx.commit();
                session.close();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
    }

    private void addSampleUsers() {
        Session session = Db.getInstance().openSession();

        minhtb = session.get(User.class, "minhtb");

        if (minhtb == null) {
            minhtb = new User();
            minhtb.setFullName("Ba Minh");
            minhtb.setUsername("minhtb");
            minhtb.setPassword("minhtb");
        }

        duongnt = session.get(User.class, "duongnt");

        if (duongnt == null) {
            duongnt = new User();
            duongnt.setFullName("Thuy Duong");
            duongnt.setUsername("duongnt");
            duongnt.setPassword("duongnt");
        }

        if (groupConversation != null) {

            Transaction tx = null;
            session = Db.getInstance().openSession();
            try {
                tx = session.beginTransaction();
                session.save(minhtb);
                session.save(duongnt);

                session.flush();

                tx.commit();
                session.close();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
    }

    private void addSampleUsersToGlobalConversation() {
        Session session = Db.getInstance().openSession();

        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            groupConversation.addMember(duongnt);
            groupConversation.addMember(minhtb);

            session.update(groupConversation);

            session.flush();

            tx.commit();
            session.close();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
