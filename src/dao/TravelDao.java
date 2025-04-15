package dao;

import model.TravelVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class TravelDao {
    private final Connection conn;

    private static final Logger logger = LoggerFactory.getLogger(TravelDao.class);


    public TravelDao(Connection conn) {
        this.conn = conn;
    }


    // 전체 관광지 목록
    public List<TravelVO> selectAll() {
        return selectByQuery("SELECT * FROM travel");
    }

    // 지역명으로 검색
    public List<TravelVO> selectByDistrict(String district) {
        return selectByQuery("SELECT * FROM travel WHERE district LIKE ?", "%" + district + "%");
    }

    // 제목 키워드로 검색
    public List<TravelVO> selectByKeyword(String keyword) {
        return selectByQuery("SELECT * FROM travel WHERE title LIKE ?", "%" + keyword + "%");
    }

    // 제목 + 지역으로 검색
    public List<TravelVO> selectByKeyword(String keyword1, String keyword2) {
        return selectByQuery(
                "SELECT * FROM travel WHERE (title LIKE ? OR district LIKE ?) AND (title LIKE ? OR district LIKE ?)",
                "%" + keyword1 + "%", "%" + keyword1 + "%",
                "%" + keyword2 + "%", "%" + keyword2 + "%"
        );
    }

    // 제목 또는 설명으로 검색
    public List<TravelVO> selectByCategoryKeyword(String keyword) {
        return selectByQuery(
                "SELECT * FROM travel WHERE title LIKE ? OR description LIKE ?",
                "%" + keyword + "%", "%" + keyword + "%"
        );
    }

    // 번호로 관광지 조회
    public TravelVO selectByNo(int no) {
        String sql = "SELECT * FROM travel WHERE no = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, no);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVO(rs);
                }
            }

        } catch (SQLException e) {
            logger.error("❌ 데이터베이스 조회중 오류 발생", e);
        }
        return null;
    }


    // 공통 쿼리 실행 메서드
    private List<TravelVO> selectByQuery(String sql, Object... params) {
        List<TravelVO> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToVO(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("❌ 데이터베이스 조회중 오류 발생", e);
        }
        return list;
    }

    // DB에서 상세 데이터 읽어 객체 반환
    private TravelVO mapResultSetToVO(ResultSet rs) throws SQLException {
        return new TravelVO(
                rs.getInt("no"),
                rs.getString("district"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("address"),
                rs.getString("phone")
        );
    }
}
