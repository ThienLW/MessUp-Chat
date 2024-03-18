package server;

import bean.ServerUser;
import dao.UserDaoImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;

public class MasterServer {

    /**
     * Danh sách người dùng
     */
    private ArrayList<ServerUser> users;

    public ServerSocket masterServer;
    public WorkerServer workServer;

    private int port = 23402;

    public void start() {
        users = new ArrayList<ServerUser>();
        try {
            masterServer = new ServerSocket(port);
            try {
                users = (ArrayList<ServerUser>) UserDaoImpl.getInstance().findAll();
                for (ServerUser u:users) {
                    u.setStatus("offline");
                }
                System.out.println("get user: "+users.size());
            } catch (SQLException e) {
                System.out.println("userList init failed");
                e.printStackTrace();
            }
            System.out.println("server loading");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                workServer = new WorkerServer(masterServer.accept(), users);
                workServer.start();
                System.out.println("--- workServer product ---");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
