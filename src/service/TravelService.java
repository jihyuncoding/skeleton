package service;

import dao.TravelDao;
import model.TravelVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ViewUtils;

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

    private static final Logger logger = LoggerFactory.getLogger(TravelService.class);

    public TravelService(TravelDao dao) {
        this.dao = dao;
    }

    // 키워드 검색
    public List<TravelVO> getTravelByKeyword(String keyword) {
        return dao.selectByKeyword(keyword);
    }

    // 전체 관광지 목록 보기
    public void showAllTravelInfoPaged(Scanner sc) {
        List<TravelVO> list = dao.selectAll();
        ViewUtils.showTravelList(list, sc, this);
    }

    // 지역으로 검색 결과 출력
    public void showTravelByDistrict(String district, Scanner sc) {
        List<TravelVO> list = dao.selectByDistrict(district);
        ViewUtils.showTravelList(list, sc, this);
    }

    // 제목 + 지역으로 검색 결과 출력
    public void showTravelByTitleAndDistrict(String title, String district, Scanner sc) {
        List<TravelVO> list = dao.selectByKeyword(title, district);
        ViewUtils.showTravelList(list, sc, this);
    }

    // 설명 키워드로 검색
    public void showTravelByDescriptionKeyword(String keyword, Scanner sc) {
        List<TravelVO> list = dao.selectByCategoryKeyword(keyword);
        ViewUtils.showTravelList(list, sc, this);
    }

    // 상세보기 및 브라우저 열기 옵션
    public void showTravelByNo(int no, Scanner sc) {
        TravelVO vo = dao.selectByNo(no);
        if (vo != null) {
            System.out.println(vo);
            System.out.println("설명: " + vo.getDescription());
            System.out.println("전화번호: " + (vo.getPhone() == null || vo.getPhone().isEmpty() ? "없음" : vo.getPhone()));

            while (true) {
                System.out.print("\n[1] 브라우저로 열기  [0] 돌아가기 ▶ ");
                String input = sc.nextLine();

                switch (input) {
                    case "1" -> openInBrowser(vo.getTitle());
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


    // 외부 사이트 검색 열기
    private void openInBrowser(String title) {
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String url = "https://korean.visitkorea.or.kr/search/search_list.do?keyword=" + encodedTitle;

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
                System.out.println("🌐 브라우저에서 '" + title + "' 검색 결과를 엽니다.");
            } else {
                System.out.println("⚠️ 현재 환경에서는 브라우저 열기를 지원하지 않습니다.");
            }
        } catch (IOException e) {
            logger.error("❌ 브라우저 열기 중 오류 발생", e);
        }
    }

    // 즐겨찾기 추가
    public void addToFavorites(int no) {
        if (dao.selectByNo(no) != null) {
            favoriteNos.add(no);
            System.out.println("⭐ 즐겨찾기에 추가되었습니다.");
        } else {
            System.out.println("⚠️ 해당 번호의 관광지가 없습니다.");
        }
    }

    // 즐겨찾기 삭제
    public void removeFromFavorites(int no) {
        if (favoriteNos.contains(no)) {
            favoriteNos.remove(no);
            System.out.println("🗑️ 즐겨찾기에서 삭제되었습니다.");
        } else {
            System.out.println("⚠️ 해당 번호는 즐겨찾기 목록에 없습니다.");
        }
    }

    // 즐겨찾기 목록 보기
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

        ViewUtils.showTravelList(list, sc, this);
    }

    // 랜덤 관광지 추천
    public List<TravelVO> getRandomPlaces(int count) {
        List<TravelVO> all = dao.selectAll();
        List<TravelVO> result = new ArrayList<>();

        if (all.isEmpty()) return result;

        Random rand = new Random();
        Set<Integer> selected = new HashSet<>();

        while (selected.size() < Math.min(count, all.size())) {
            int index = rand.nextInt(all.size());
            if (selected.add(index)) {
                result.add(all.get(index));
            }
        }

        return result;
    }
}
