package dao;

import model.TravelVO;
import java.sql.*;
import java.util.*;

public class TravelDao {
    private Connection conn;

    public TravelDao(Connection conn) {
        this.conn = conn;
    }

    public List<TravelVO> selectAll() {
        List<TravelVO> list = new ArrayList<>();
        String sql = "SELECT * FROM travel";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TravelVO vo = new TravelVO(
                        rs.getInt("no"),
                        rs.getString("district"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("address"),
                        rs.getString("phone")
                );
                list.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 나중에 지역별 조회, 키워드 검색 등 메서드 추가할 수 있어요
}
