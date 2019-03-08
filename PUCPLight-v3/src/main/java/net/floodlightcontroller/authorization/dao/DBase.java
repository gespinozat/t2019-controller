package net.floodlightcontroller.authorization.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBase {

    Connection conn;

    public DBase() {
        try {
            String user = "test";
            String pass = "test";
            String url = "jdbc:mysql://192.168.44.10:3306/radius";

            conn = DriverManager.getConnection(url, user, pass);
        } catch (SQLException ex) {
        	Logger.getLogger(DBase.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
}
