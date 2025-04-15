import dao.TravelDao;
import dao.UserTravelDao;
import model.TravelVO;
import service.TravelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserTravelService;
import util.ViewUtils;

import java.sql.*;
import java.util.*;

public class TravelApp {

    private static final String URL = "jdbc:mysql://localhost:3306/travel_db";
    private static final String USER = "root";
    private static final String PASSWORD = "!12345";

    private static final Logger logger = LoggerFactory.getLogger(TravelApp.class);

    private final Scanner sc;
    private final TravelService service;
    private final UserTravelService userTravelService;



    public TravelApp(TravelService service, UserTravelService userTravelService, Scanner sc) {
        this.service = service;
        this.userTravelService = userTravelService;
        this.sc = sc;
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

                logger.info("✅ DB 연결 성공");

                TravelDao dao = new TravelDao(conn);
                TravelService service = new TravelService(dao);

                UserTravelDao userDao = new UserTravelDao(conn);
                UserTravelService userService = new UserTravelService(userDao);

                TravelApp app = new TravelApp(service, userService, sc);

                app.run();

            } catch (SQLException e) {
                logger.error("❌ 데이터베이스 연결 실패", e);
            }
        }
    }

    // 메인 루프. 사용자의 메뉴 선택 처리
    public void run() {
        while (true) {
            showMainMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1 -> showListMenu();
                case 2 -> showSearchMenu();
                case 3 -> showFavoritesMenu();
                case 4 -> showUserTravelMenu();
                case 0 -> {
                    System.out.println("✅ 종료합니다.");
                    return;
                }
                default -> System.out.println("⚠️ 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }

    // 메인 메뉴 출력
    private void showMainMenu() {
        System.out.println("""
            
            === 관광지 검색 시스템 ===
            1. 관광지 목록 보기
            2. 검색
            3. 즐겨찾기
            4. 나만의 관광지
            0. 종료하기
            """);
        System.out.print("선택: ");
    }

    // 검색 관련 메뉴 출력
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
                default -> System.out.println("⚠️ 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }

    // 사용자 입력값 정수로 전환
    private int getUserChoice() {
        String input = sc.nextLine();
        try {
            return Integer.parseInt(input);

        } catch (NumberFormatException e) {

            return -1;
        }
    }

    // 지역으로 검색
    private void searchByDistrict() {
        System.out.print("지역 이름 입력: ");
        String district = sc.nextLine();

        service.showTravelByDistrict(district, sc);
    }

    // 제목 키워드로 검색
    private void searchByKeyword() {
        System.out.print("제목 키워드 입력: ");
        String keyword = sc.nextLine();

        List<TravelVO> list = service.getTravelByKeyword(keyword);
        util.ViewUtils.showTravelList(list, sc, service);
    }


    // 제목 + 지역으로 검색
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


    // 즐겨찾기 메뉴
    private void showFavoritesMenu() {
        while (true) {
            System.out.println("""
        
                === ⭐ 즐겨찾기 메뉴 ===
                1. 즐겨찾기 목록 보기
                2. 즐겨찾기 추가
                3. 즐겨찾기 삭제
                0. 메인으로 돌아가기
                """);
            System.out.print("선택: ");
            int choice = getUserChoice();

            switch (choice) {
                case 1 -> service.showFavorites(sc);
                case 2 -> addToFavorites();
                case 3 -> removeFromFavorites();
                case 0 -> {
                    return;
                }
                default -> System.out.println("⚠️ 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }


    // 설명 키워드로 검색
    private void searchByDescriptionKeyword() {
        System.out.print("설명 키워드 입력: ");
        String keyword = sc.nextLine();

        service.showTravelByDescriptionKeyword(keyword, sc);
    }

    // 즐겨찾기 추가
    private void addToFavorites() {
        System.out.print("추가할 관광지 번호 입력: ");
        try {
            int no = Integer.parseInt(sc.nextLine());
            service.addToFavorites(no);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요.");
        }
    }

    // 즐겨찾기 삭제
    private void removeFromFavorites() {
        System.out.print("삭제할 관광지 번호 입력: ");
        try {
            int no = Integer.parseInt(sc.nextLine());
            service.removeFromFavorites(no);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요.");
        }
    }

    // 랜덤 관광지 추천
    private void showRandomRecommendation() {
        List<TravelVO> list = service.getRandomPlaces(3);
        if (list.isEmpty()) {
            System.out.println("⚠️ 추천할 관광지가 없습니다.");
            return;
        }

        System.out.println("\n🎲 오늘의 랜덤 추천 관광지 🎲");
        ViewUtils.showTravelList(list, sc, service); // 🔁 유틸 직접 호출
    }

    // 사용자 등록 관광지 메뉴
    private void showUserTravelMenu() {

        userTravelService.showUserTravelHelp();

        while (true) {
            System.out.println("""
                === 나만의 관광지 메뉴 ===
                1. 관광지 추가
                2. 내가 등록한 관광지 보기
                3. 관광지 삭제
                0. 메인 메뉴로 돌아가기
                """);
            System.out.print("선택: ");
            String input = sc.nextLine();

            switch (input) {
                case "1" -> userTravelService.addUserTravelFromInput(sc);
                case "2" -> userTravelService.showUserTravelList(sc);
                case "3" -> userTravelService.deleteUserTravelByNo(sc);
                case "0" -> { return; }
                default -> System.out.println("⚠️ 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }

    // 전체 목록 보기 메뉴
    private void showListMenu() {
        while (true) {
            System.out.println("""
            
            === 🗂️ 목록 보기 ===
            1. 전체 관광지 목록
            2. 지역별 목록
            0. 메인으로 돌아가기
            """);
            System.out.print("선택: ");
            int choice = getUserChoice();

            switch (choice) {
                case 1 -> service.showAllTravelInfoPaged(sc);
                case 2 -> showRegionMenu();
                case 0 -> {
                    return;
                }
                default -> System.out.println("⚠️ 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }

    // 권역 선택을 통한 목록 보기
    private void showRegionMenu() {
        String[] regions = {
                "수도권", "충청권", "전라권", "경상권", "강원권", "제주권"
        };

        System.out.println("\n🌍 권역을 선택하세요:");
        for (int i = 0; i < regions.length; i++) {
            System.out.printf("%d. %s\n", i + 1, regions[i]);
        }

        System.out.print("번호 입력 (0: 돌아가기): ");
        int input = getUserChoice();

        if (input == 0) return;

        if (input < 1 || input > regions.length) {
            System.out.println("⚠️ 올바른 메뉴 번호를 입력해주세요.");
            return;
        }

        String selectedRegion = regions[input - 1];
        service.showTravelByDistrict(selectedRegion, sc);  // 해당 권역으로 검색
    }

}