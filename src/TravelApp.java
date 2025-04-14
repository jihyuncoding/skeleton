import dao.TravelDao;
import service.TravelService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TravelApp {

    private static final String URL = "jdbc:mysql://localhost:3306/travel_db";
    private static final String USER = "root";
    private static final String PASSWORD = "!123456";

    private static final Logger logger = Logger.getLogger(TravelApp.class.getName());

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            TravelDao dao = new TravelDao(conn);
            TravelService service = new TravelService(dao);

            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n=== 관광지 검색 시스템 ===");
                System.out.println("1. 전체 목록 보기");
                System.out.println("0. 종료");
                System.out.print("선택: ");
                int choice = sc.nextInt();

                if (choice == 1) {
                    service.showAllTravelInfo();
                } else if (choice == 0) {
                    System.out.println("종료합니다.");
                    break;
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ 데이터베이스 연결 실패", e);
        }
    }
}
