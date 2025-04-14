import dao.TravelDao;
import service.TravelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class TravelApp {

    private static final String URL = "jdbc:mysql://localhost:3306/travel_db";
    private static final String USER = "root";
    private static final String PASSWORD = "!123456";

    private static final Logger logger = LoggerFactory.getLogger(TravelApp.class);

    private final Scanner sc;
    private final TravelService service;

    public TravelApp(TravelService service, Scanner sc) {
        this.service = service;
        this.sc = sc;
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

                TravelDao dao = new TravelDao(conn);
                TravelService service = new TravelService(dao);
                TravelApp app = new TravelApp(service, sc);

                app.run();
            } catch (SQLException e) {
                logger.error("❌ 데이터베이스 연결 실패", e);
            }
        }
    }

    public void run() {
        while (true) {
            printMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1 -> service.showAllTravelInfoPaged(sc);
                case 2 -> searchByDistrict();
                case 3 -> searchByKeyword();
                case 4 -> showDetailByNo();
                case 5 -> searchByTitleAndDistrict();
                case 0 -> {
                    System.out.println("✅ 종료합니다.");
                    return;
                }
                default -> System.out.println("⚠️ 올바른 번호를 입력해주세요.");
            }
        }
    }

    private void printMenu() {
        System.out.println("""
            
        === 관광지 검색 시스템 ===
        1. 전체 목록 보기
        2. 지역으로 검색
        3. 제목 키워드로 검색
        4. 번호로 관광지 상세 보기
        5. 제목 + 지역으로 검색
        0. 종료하기
        """);
        System.out.print("선택: ");
    }

    private int getUserChoice() {
        String input = sc.nextLine();
        try {
            return Integer.parseInt(input);

        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요!");

            return -1;
        }
    }

    private void searchByDistrict() {
        System.out.print("지역 이름 입력: ");
        String district = sc.nextLine();

        service.showTravelByDistrict(district);
    }

    private void searchByKeyword() {
        System.out.print("제목 키워드 입력: ");
        String keyword = sc.nextLine();

        service.showTravelByKeyword(keyword);
    }

    private void showDetailByNo() {
        System.out.print("관광지 번호 입력: ");
        String input = sc.nextLine();
        try {
            int no = Integer.parseInt(input);

            service.showTravelByNo(no);

        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요!");
        }
    }

    private void searchByTitleAndDistrict() {
        System.out.println("제목과 지역을 입력하세요 (예: 경복궁, 서울)");

        while (true) {
            System.out.print(">> ");
            String input = sc.nextLine();

            String[] parts = input.split(",");

            if (parts.length == 2) {
                String title = parts[0].trim();
                String district = parts[1].trim();

                service.showTravelByTitleAndDistrict(title, district);
                return;
            }

            System.out.println("⚠️ 입력 형식이 올바르지 않습니다. 예: 경복궁, 서울");

        }

    }



}
