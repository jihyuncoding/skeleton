import dao.TravelDao;
import model.TravelVO;
import service.TravelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class TravelApp {

    private static final String URL = "jdbc:mysql://localhost:3306/travel_db";
    private static final String USER = "root";
    private static final String PASSWORD = "!12345";

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

                logger.info("✅ DB 연결 성공");

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
            showMainMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1 -> service.showAllTravelInfoPaged(sc);
                case 2 -> showSearchMenu();
                case 3 -> showFavoritesMenu();
                case 0 -> {
                    System.out.println("✅ 종료합니다.");
                    return;
                }
                default -> System.out.println("⚠️ 올바른 번호를 입력해주세요.");
            }
        }
    }


    private void showMainMenu() {
        System.out.println("""
            
            === 관광지 검색 시스템 ===
            1. 전체 목록 보기
            2. 검색
            3. 즐겨찾기
            0. 종료하기
            """);
        System.out.print("선택: ");
    }

    private void showSearchMenu() {
        while (true) {
            System.out.println("""
            
                === 🔍 검색 메뉴 ===
                1. 지역으로 검색
                2. 제목 키워드로 검색
                3. 제목 + 지역으로 검색
                4. 카테고리(제목 또는 설명)로 검색
                5. 랜덤 관광지 추천
                0. 메인으로 돌아가기
                """);
            System.out.print("선택: ");

            int choice = getUserChoice();
            switch (choice) {
                case 1 -> searchByDistrict();
                case 2 -> searchByKeyword();
                case 3 -> searchByTitleAndDistrict();
                case 4 -> searchByDescriptionKeyword();
                case 5 -> showRandomRecommendation();
                case 0 -> {
                    return;
                }
                default -> System.out.println("⚠️ 올바른 번호를 입력해주세요.");
            }
        }
    }


    private int getUserChoice() {
        String input = sc.nextLine();
        try {
            return Integer.parseInt(input);

        } catch (NumberFormatException e) {

            return -1;
        }
    }

    private void searchByDistrict() {
        System.out.print("지역 이름 입력: ");
        String district = sc.nextLine();

        service.showTravelByDistrict(district, sc);
    }

    private void searchByKeyword() {
        System.out.print("제목 키워드 입력: ");
        String keyword = sc.nextLine();

        List<TravelVO> list = service.getTravelByKeyword(keyword);
        if (list.isEmpty()) {
            System.out.println("⚠️ 검색 결과가 없습니다.");
            return;
        }

        service.showTravel(list, sc); // 페이징 출력

        askForDetailOrBack(list);
    }

    private void askForDetailOrBack(List<TravelVO> list) {
        System.out.print("\n상세보기할 번호를 입력하거나 [0]을 입력해 검색 메뉴로 돌아갑니다.\n>> ");
        String input = sc.nextLine();
        try {
            int no = Integer.parseInt(input);
            if (no == 0) return;

            boolean exists = list.stream().anyMatch(vo -> vo.getNo() == no);
            if (exists) {
                service.showTravelByNo(no);
            } else {
                System.out.println("⚠️ 해당 번호는 검색 결과에 없습니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요.");
        }
    }

    private void searchByTitleAndDistrict() {
        System.out.println("제목과 지역 키워드를 입력하세요 (예: 경복궁, 서울)");

        while (true) {
            System.out.print(">> ");
            String input = sc.nextLine();

            String[] parts = input.split(",");

            if (parts.length == 2) {
                String title = parts[0].trim();
                String district = parts[1].trim();

                service.showTravelByTitleAndDistrict(title, district, sc);
                return;
            }

            System.out.println("⚠️ 입력 형식이 올바르지 않습니다. 예: 경복궁, 서울");

        }

    }

    private void showFavoritesMenu() {
        while (true) {
            System.out.println("""
        
                === ⭐ 즐겨찾기 메뉴 ===
                1. 즐겨찾기 추가
                2. 즐겨찾기 목록 보기
                3. 즐겨찾기 삭제
                0. 메인으로 돌아가기
                """);
            System.out.print("선택: ");
            int choice = getUserChoice();

            switch (choice) {
                case 1 -> addToFavorites();
                case 2 -> service.showFavorites(sc);
                case 3 -> removeFromFavorites();
                case 0 -> {
                    return;
                }
                default -> System.out.println("⚠️ 올바른 번호를 입력해주세요.");
            }
        }
    }


    private void searchByDescriptionKeyword() {
        System.out.print("설명 키워드 입력: ");
        String keyword = sc.nextLine();

        service.showTravelByDescriptionKeyword(keyword, sc);
    }

    private void addToFavorites() {
        System.out.print("추가할 관광지 번호 입력: ");
        try {
            int no = Integer.parseInt(sc.nextLine());
            service.addToFavorites(no);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요.");
        }
    }

    private void removeFromFavorites() {
        System.out.print("삭제할 관광지 번호 입력: ");
        try {
            int no = Integer.parseInt(sc.nextLine());
            service.removeFromFavorites(no);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요.");
        }
    }

    private void showRandomRecommendation() {
        List<TravelVO> list = service.getRandomPlaces(3);

        if (list.isEmpty()) {
            System.out.println("⚠️ 추천할 관광지가 없습니다.");
            return;
        }

        System.out.println("\n🎲 오늘의 랜덤 추천 관광지 🎲");
        for (TravelVO vo : list) {
            System.out.println(vo);
        }

        askForDetailOrBack(list); // 상세보기 연결
    }

}
