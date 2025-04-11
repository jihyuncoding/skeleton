import dao.TravelDao;
import model.TravelVO;
import service.TravelService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class TravelApp {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/travel_db";
        String user = "root";
        String password = "(각자 비밀번호)"; // 본인 MySQL 비번

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
