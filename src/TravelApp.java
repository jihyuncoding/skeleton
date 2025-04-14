import dao.TravelDao;
import model.TravelVO;
import service.TravelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

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
                case 1 -> showListMenu();
                case 2 -> showSearchMenu();
                case 3 -> showFavoritesMenu();
                case 4 -> runUserTravelMenu(); // ✨ 추가된 나만의 관광지 메뉴
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
            1. 관광지 목록 보기
            2. 검색
            3. 즐겨찾기
            4. 나만의 관광지
            0. 종료하기
            """);
        System.out.print("선택: ");
    }

    private int getUserChoice() {
        String input = sc.nextLine();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
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

        service.showTravel(list, sc);
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

    private void searchByDescriptionKeyword() {
        System.out.print("설명 키워드 입력: ");
        String keyword = sc.nextLine();
        service.showTravelByDescriptionKeyword(keyword, sc);
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
        askForDetailOrBack(list);
    }

    // 4번: 나만의 관광지
    private void runUserTravelMenu() {
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
                case "1" -> insertUserRecommendation();
                case "2" -> showUserRecommendations();
                case "3" -> deleteUserRecommendation();
                case "0" -> {
                    System.out.println("메인 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void insertUserRecommendation() {
        System.out.println("\n추천 관광지를 아래 형식에 맞게 입력해주세요.");
        System.out.println("예시: 서울, 남산타워, 야경이 멋진 서울의 명소, 서울 용산구 남산공원길, 02-123-4567");

        System.out.print("입력 ▶ ");
        String line = sc.nextLine();
        String[] parts = line.split(",", 5);

        if (parts.length < 5) {
            System.out.println("형식이 올바르지 않습니다. 다시 시도해주세요.");
            return;
        }

        String sql = "INSERT INTO user_travel (district, title, description, address, phone) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < 5; i++) {
                pstmt.setString(i + 1, parts[i].trim());
            }
            pstmt.executeUpdate();
            System.out.println("추천 관광지가 등록되었습니다!");
        } catch (SQLException e) {
            System.err.println("저장 실패:");
            e.printStackTrace();
        }
    }

    private void showUserRecommendations() {
        String sql = "SELECT * FROM user_travel";
        List<TravelVO> list = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new TravelVO(
                        rs.getInt("no"),
                        rs.getString("district"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("address"),
                        rs.getString("phone")
                ));
            }

        } catch (SQLException e) {
            System.err.println("추천 관광지 조회 실패:");
            e.printStackTrace();
            return;
        }

        if (list.isEmpty()) {
            System.out.println("\n등록된 추천 관광지가 없습니다.");
            return;
        }

        int pageSize = 10;
        int currentPage = 0;
        int totalPages = (int) Math.ceil(list.size() / (double) pageSize);

        while (true) {
            int start = currentPage * pageSize;
            int end = Math.min(start + pageSize, list.size());

            System.out.printf("\n페이지 %d / %d\n", currentPage + 1, totalPages);
            for (int i = start; i < end; i++) {
                TravelVO t = list.get(i);
                System.out.printf("[%d] %s (%s) - %s\n", t.getNo(), t.getTitle(), t.getDistrict(), t.getAddress());
            }

            System.out.println("\n[1] 다음 페이지  [2] 이전 페이지  [3] 상세보기  [0] 돌아가기");
            System.out.print(">> ");
            String input = sc.nextLine();

            switch (input) {
                case "1" -> {
                    if (currentPage < totalPages - 1) currentPage++;
                    else System.out.println("마지막 페이지입니다.");
                }
                case "2" -> {
                    if (currentPage > 0) currentPage--;
                    else System.out.println("첫 페이지입니다.");
                }
                case "3" -> {
                    System.out.print("상세보기할 번호 입력 (0 입력시 되돌리기): ");
                    try {
                        int no = Integer.parseInt(sc.nextLine());
                        Optional<TravelVO> selected = list.stream().filter(vo -> vo.getNo() == no).findFirst();

                        if (selected.isPresent()) {
                            TravelVO t = selected.get();
                            System.out.println("\n[" + t.getNo() + "] " + t.getTitle() + " (" + t.getDistrict() + ")");
                            System.out.println("설명: " + t.getDescription());
                            System.out.println("주소: " + t.getAddress());
                            System.out.println("전화번호: " + (t.getPhone() == null || t.getPhone().isEmpty() ? "없음" : t.getPhone()));
                        } else {
                            System.out.println("해당 번호는 목록에 없습니다.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("숫자만 입력해주세요.");
                    }
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void deleteUserRecommendation() {
        System.out.print("삭제할 관광지 번호 입력 : ");
        try {
            int no = Integer.parseInt(sc.nextLine());
            String sql = "DELETE FROM user_travel WHERE no = ?";

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, no);
                int result = pstmt.executeUpdate();

                if (result > 0) {
                    System.out.println("관광지가 삭제되었습니다.");
                } else {
                    System.out.println("해당 번호의 관광지를 찾을 수 없습니다.");
                }

            }
        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력해주세요.");
        } catch (SQLException e) {
            System.err.println("삭제 실패:");
            e.printStackTrace();
        }
    }

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
                case 2 -> showRegionList();  // 권역 선택 화면으로 이동
                case 0 -> {
                    return;
                }
                default -> System.out.println("⚠️ 올바른 번호를 입력해주세요.");
            }
        }
    }

    private void showRegionList() {
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
            System.out.println("⚠️ 잘못된 번호입니다.");
            return;
        }

        String selectedRegion = regions[input - 1];
        service.showTravelByDistrict(selectedRegion, sc);  // 해당 권역으로 검색
    }


}
