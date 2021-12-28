package hanu.npr.messengerserver;

import hanu.npr.messengerserver.controllers.ServerController;
import hanu.npr.messengerserver.models.User;

import java.io.IOException;

public class MessengerServerCLIApplication implements ServerController.Interface {

    public MessengerServerCLIApplication() throws IOException {
        new ServerController(60000, this);
    }

    public static void main(String[] args) throws IOException {
        new MessengerServerCLIApplication();
    }

    @Override
    public void onPortOpened(int port) {
        System.out.println(port + " has been opened");
    }

    @Override
    public void onNewConnectionCreated() {
        System.out.println("New connection");
    }

    @Override
    public void onNewUserRegistered(User user) {
        System.out.println("New user registered: " + user.getUsername() + ", " + user.getFullName());
    }

    @Override
    public void onUserLogin(User user) {
        System.out.println("User " + user.getUsername() + " has logged in");
    }

    @Override
    public void onUserLogout(User user) {
        System.out.println("User " + user.getUsername() + " has logged out");
    }
}
