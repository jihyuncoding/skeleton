package util;

import model.TravelVO;
import model.UserTravelVO;
import service.TravelService;

import java.util.List;
import java.util.Scanner;

public class ViewUtils {


    // 관광지 목록 페이지 나눔 및 상세보기 템플릿
    public static void showTravelList(List<TravelVO> list, Scanner sc, TravelService service) {
        if (list == null || list.isEmpty()) {
            System.out.println("⚠️ 목록이 비어 있습니다.");
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
                    else System.out.println("📌 첫 페이지입니다.");
                }
                case "3" -> showTravelDetail(list, sc, service);
                case "0" -> {
                    System.out.println("✅ 이전 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("⚠️ 잘못된 입력입니다.");
            }
        }
    }


    // 상세보기 처리
    private static void showTravelDetail(List<TravelVO> list, Scanner sc, TravelService service) {
        System.out.print("상세보기할 번호 입력 ▶ ");
        try {
            int no = Integer.parseInt(sc.nextLine());
            boolean exists = list.stream().anyMatch(vo -> vo.getNo() == no);

            if (exists) {
                service.showTravelByNo(no, sc); // ✅ Scanner 전달
            } else {
                System.out.println("⚠️ 해당 번호는 목록에 없습니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요.");
        }
    }


    // 사용자 관광지 페이지 나눔 및 상세보기 템플릿
    public static void showUserTravelList(List<UserTravelVO> list, Scanner sc) {
        if (list == null || list.isEmpty()) {
            System.out.println("⚠️ 목록이 비어 있습니다.");
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
                    else System.out.println("📌 첫 페이지입니다.");
                }
                case "3" -> showUserTravelDetail(list, sc);
                case "0" -> {
                    System.out.println("✅ 이전 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("⚠️ 잘못된 입력입니다.");
            }
        }
    }


    // 사용자 관광지 상세보기
    private static void showUserTravelDetail(List<UserTravelVO> list, Scanner sc) {
        System.out.print("상세보기할 번호 입력 ▶ ");
        try {
            int no = Integer.parseInt(sc.nextLine());
            for (UserTravelVO vo : list) {
                if (vo.getNo() == no) {
                    System.out.println("\n[" + vo.getNo() + "] " + vo.getTitle() + " (" + vo.getDistrict() + ")");
                    System.out.println("설명: " + vo.getDescription());
                    System.out.println("주소: " + vo.getAddress());
                    System.out.println("전화번호: " + (vo.getPhone() == null || vo.getPhone().isEmpty() ? "없음" : vo.getPhone()));
                    return;
                }
            }
            System.out.println("⚠️ 해당 번호는 목록에 없습니다.");
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요.");
        }
    }
}
