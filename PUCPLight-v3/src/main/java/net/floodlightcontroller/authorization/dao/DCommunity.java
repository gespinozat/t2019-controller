package net.floodlightcontroller.authorization.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.floodlightcontroller.authorization.bean.Community;

public class DCommunity extends DBase {
	
	private ResultSet rs;
    private PreparedStatement pstmt;

    public ArrayList<Community> german() {
        ArrayList<Community> l = new ArrayList<Community>();

        try {
            String sql = "select * from community order by name";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            Community c;
            while (rs.next()) {
                c = new Community();
                c.setId(rs.getString(1));                
                c.setName(rs.getString(2));
                l.add(c);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DCommunity.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DCommunity.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return l;
    }


}
