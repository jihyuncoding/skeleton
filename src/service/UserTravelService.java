package service;

import dao.UserTravelDao;
import model.UserTravelVO;
import util.ViewUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class UserTravelService {
    private final UserTravelDao dao;

    public UserTravelService(UserTravelDao dao) {
        this.dao = dao;
    }

    // 사용자 입력을 통해 관광지 추가
    public void addUserTravelFromInput(Scanner sc) {
        System.out.println("예시: 서울, 남산타워, 야경이 멋진 서울의 명소, 서울 용산구 남산공원길, 02-123-4567");
        System.out.print("입력 ▶ ");
        String[] parts = sc.nextLine().split(",", 5);

        if (parts.length < 5) {
            System.out.println("⚠️ 형식이 올바르지 않습니다.");
            return;
        }

        UserTravelVO vo = new UserTravelVO(0,
                parts[0].trim(), parts[1].trim(), parts[2].trim(),
                parts[3].trim(), parts[4].trim());

        try {
            dao.insert(vo);
            System.out.println("✅ 추천 관광지가 등록되었습니다!");
        } catch (SQLException e) {
            System.out.println("❌ 저장 실패: " + e.getMessage());
        }
    }

    // 사용자 관광지 목록 표시 (ViewUtils로 위임)
    public void showUserTravelList(Scanner sc) {
        try {
            List<UserTravelVO> list = dao.selectAll();
            ViewUtils.showUserTravelList(list, sc);
        } catch (SQLException e) {
            System.out.println("❌ 조회 실패: " + e.getMessage());
        }
    }

    // 사용자 관광지 삭제
    public void deleteUserTravelByNo(Scanner sc) {
        System.out.print("삭제할 관광지 번호 입력 ▶ ");
        try {
            int no = Integer.parseInt(sc.nextLine());
            boolean deleted = dao.delete(no);
            if (deleted) {
                System.out.println("✅ 관광지가 삭제되었습니다.");
            } else {
                System.out.println("⚠️ 해당 번호를 찾을 수 없습니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 숫자만 입력해주세요.");
        } catch (SQLException e) {
            System.out.println("❌ 삭제 실패: " + e.getMessage());
        }
    }
}
