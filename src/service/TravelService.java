package service;

import dao.TravelDao;
import model.TravelVO;

import java.util.List;

public class TravelService {
    private TravelDao dao;

    public TravelService(TravelDao dao) {
        this.dao = dao;
    }

    public void showAllTravelInfo() {
        List<TravelVO> list = dao.selectAll();
        for (TravelVO vo : list) {
            System.out.println(vo);
        }
    }

    // 앞으로 지역 필터, 키워드 검색 같은 메서드 추가 예정
}