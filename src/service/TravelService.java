package service;

import dao.TravelDao;
import model.TravelVO;

import java.util.List;
import java.util.Scanner;

public class TravelService {
    private final TravelDao dao;

    public TravelService(TravelDao dao) {
        this.dao = dao;
    }

    public void showAllTravelInfoPaged(Scanner sc) {
        List<TravelVO> list = dao.selectAll();

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

            System.out.print("\n[1] 다음 페이지  [2] 이전 페이지  [0] 돌아가기 ▶ ");
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

                case "0" -> {
                    System.out.println("✅ 메인 화면으로 돌아갑니다.");
                    return;
                }

                default -> System.out.println("⚠️ 잘못된 입력입니다.");
            }
        }
    }


    public void showTravelByDistrict(String district) {
        List<TravelVO> list = dao.selectByDistrict(district);
        for (TravelVO vo : list) {
            System.out.println(vo);
        }
    }

    public void showTravelByKeyword(String keyword) {
        List<TravelVO> list = dao.selectByKeyword(keyword);
        for (TravelVO vo : list) {
            System.out.println(vo);
        }
    }

    public void showTravelByNo(int no) {
        TravelVO vo = dao.selectByNo(no);
        if (vo != null) {
            System.out.println(vo);
            System.out.println("설명: " + vo.getDescription());
            System.out.println("전화번호: " + (vo.getPhone() == null || vo.getPhone().isEmpty() ? "없음" : vo.getPhone()));

        } else {
            System.out.println("⚠️ 해당 번호의 관광지를 찾을 수 없습니다.");
        }
    }

    public void showTravelByTitleAndDistrict(String title, String district) {
        List<TravelVO> list = dao.selectByTitleAndDistrict(title, district);

        for (TravelVO vo : list) {
            System.out.println(vo);
        }
    }

}