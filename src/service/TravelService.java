package service;

import dao.TravelDao;
import model.TravelVO;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class TravelService {
    private final TravelDao dao;

    private final Set<Integer> favoriteNos = new LinkedHashSet<>();

    public TravelService(TravelDao dao) {
        this.dao = dao;
    }

    public List<TravelVO> getTravelByKeyword(String keyword) {
        return dao.selectByKeyword(keyword);
    }


    public void showAllTravelInfoPaged(Scanner sc) {
        List<TravelVO> list = dao.selectAll();

        showTravel(list, sc);
    }



    public void showTravelByDistrict(String district, Scanner sc) {
        List<TravelVO> list = dao.selectByDistrict(district);

        showTravel(list, sc);
    }

    public void showTravelByTitleAndDistrict(String title, String district, Scanner sc) {
        List<TravelVO> list = dao.selectByKeyword(title, district);

        showTravel(list, sc);
    }

    public void showTravelByDescriptionKeyword(String keyword, Scanner sc) {
        List<TravelVO> list = dao.selectByCategoryKeyword(keyword);

        showTravel(list, sc);
    }


    public void showTravelByNo(int no) {
        TravelVO vo = dao.selectByNo(no);
        if (vo != null) {
            System.out.println(vo);
            System.out.println("설명: " + vo.getDescription());
            System.out.println("전화번호: " + (vo.getPhone() == null || vo.getPhone().isEmpty() ? "없음" : vo.getPhone()));

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("\n[1] 브라우저로 열기  [0] 돌아가기 ▶ ");
                String input = sc.nextLine();

                switch (input) {
                    case "1" -> {
                        try {
                            String encodedTitle = URLEncoder.encode(vo.getTitle(), StandardCharsets.UTF_8);
                            String url = "https://korean.visitkorea.or.kr/search/search_list.do?keyword=" + encodedTitle;

                            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                Desktop.getDesktop().browse(URI.create(url));
                                System.out.println("🌐 브라우저에서 '" + vo.getTitle() + "' 검색 결과를 엽니다.");
                            } else {
                                System.out.println("⚠️ 현재 환경에서는 브라우저 열기를 지원하지 않습니다.");
                            }
                        } catch (IOException e) {
                            System.out.println("❌ 브라우저 열기 중 오류 발생: " + e.getMessage());
                        }
                        return;
                    }

                    case "0" -> {
                        System.out.println("✅ 메인 화면으로 돌아갑니다.");
                        return;
                    }

                    default -> System.out.println("⚠️ 잘못된 입력입니다. 다시 입력해주세요.");
                }

            }

        } else {
            System.out.println("⚠️ 해당 번호의 관광지를 찾을 수 없습니다.");
        }
    }

    private void showDetailFromList(List<TravelVO> list, Scanner sc) {
        System.out.print("상세보기할 번호 입력 (0 입력시 되돌리기): ");
        String input = sc.nextLine();

        try {
            int no = Integer.parseInt(input);
            if (no == 0) return;

            boolean exists = list.stream().anyMatch(vo -> vo.getNo() == no);
            if (exists) {
                showTravelByNo(no);
            } else {
                System.out.println("⚠️ 해당 번호는 현재 목록에 없습니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요.");
        }
    }



    public void addToFavorites(int no) {
        if (dao.selectByNo(no) != null) {
            favoriteNos.add(no);
            System.out.println("⭐ 즐겨찾기에 추가되었습니다.");
        } else {
            System.out.println("⚠️ 해당 번호의 관광지가 없습니다.");
        }
    }

    public void removeFromFavorites(int no) {
        if (favoriteNos.contains(no)) {
            favoriteNos.remove(no);
            System.out.println("🗑️ 즐겨찾기에서 삭제되었습니다.");
        } else {
            System.out.println("⚠️ 해당 번호는 즐겨찾기 목록에 없습니다.");
        }
    }


    public void showFavorites(Scanner sc) {
        if (favoriteNos.isEmpty()) {
            System.out.println("⭐ 즐겨찾기 목록이 비어 있습니다.");
            return;
        }

        List<TravelVO> list = new ArrayList<>();
        for (int no : favoriteNos) {
            TravelVO vo = dao.selectByNo(no);
            if (vo != null) list.add(vo);
        }
        showTravel(list, sc);
    }


    public void showTravel(List<TravelVO> list, Scanner sc) {
        if (list.isEmpty()) {
            System.out.println("⚠️ 검색 결과가 없습니다.");
            return;
        }

        int pageSize = 10;
        int currentPage = 0;
        int totalPages = (int) Math.ceil(list.size() / (double) pageSize);

        while (true) {
            int start = currentPage * pageSize;
            int end = Math.min(start + pageSize, list.size());

            System.out.printf("\n📄 페이지 %d / %d\n", currentPage + 1, totalPages);
            for (int i = start; i < end; i++) {
                System.out.println(list.get(i));
            }

            System.out.println("\n[1] 다음 페이지  [2] 이전 페이지  [3] 상세보기  [0] 돌아가기");
            System.out.print(">> ");
            String input = sc.nextLine();

            switch (input) {
                case "1" -> {
                    if (currentPage < totalPages - 1) currentPage++;
                    else System.out.println("📌 마지막 페이지입니다.");
                }
                case "2" -> {
                    if (currentPage > 0) currentPage--;
                    else System.out.println("📌 첫 번째 페이지입니다.");
                }
                case "3" -> showDetailFromList(list, sc); // ✅ 상세보기 로직 추가
                case "0" -> {
                    System.out.println("✅ 이전 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("⚠️ 잘못된 입력입니다.");
            }
        }
    }

}