package dao;

import model.UserTravelVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserTravelDao {
    private final Connection conn;

    public UserTravelDao(Connection conn) {
        this.conn = conn;
    }

    // 사용자 관광지 등록
    public void insert(UserTravelVO vo) throws SQLException {
        String sql = "INSERT INTO user_travel (district, title, description, address, phone) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vo.getDistrict());
            pstmt.setString(2, vo.getTitle());
            pstmt.setString(3, vo.getDescription());
            pstmt.setString(4, vo.getAddress());
            pstmt.setString(5, vo.getPhone());
            pstmt.executeUpdate();
        }
    }

    // 전체 사용자 등록 관광지
    public List<UserTravelVO> selectAll() throws SQLException {
        List<UserTravelVO> list = new ArrayList<>();
        String sql = "SELECT * FROM user_travel";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new UserTravelVO(
                        rs.getInt("no"),
                        rs.getString("district"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("address"),
                        rs.getString("phone")
                ));
            }
        }

        return list;
    }

    // 사용자 등록 관광지 삭제
    public boolean delete(int no) throws SQLException {
        String sql = "DELETE FROM user_travel WHERE no = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, no);
            return stmt.executeUpdate() > 0;
        }
    }
}

