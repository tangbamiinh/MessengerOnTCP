package hanu.npr.messengerserver.config;

import hanu.npr.messengerserver.models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;

public class Db {

    private static Db instance;

    private final SessionFactory sessionFactory;


    private Db() {
        Properties properties = new Properties();

        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/messenger?allowPublicKeyRetrieval=true&useSSL=false&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "MessengerOnTCP");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "create-drop");

        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(ChatMessage.class)
                .addAnnotatedClass(Conversation.class)
                .addAnnotatedClass(PrivateConversation.class)
                .addAnnotatedClass(GroupConversation.class)
                .addAnnotatedClass(Attachment.class)
                .buildSessionFactory();
    }

    public static Db getInstance() {
        if (instance == null) {
            instance = new Db();
        }
        return instance;
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }
}
