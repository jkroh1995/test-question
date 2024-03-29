package project.server.domain.recommend.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.server.domain.recommend.entity.Recommend;
import project.server.domain.recommend.repository.RecommendRepository;
import project.server.domain.user.User;

import java.util.List;

@Service
public class RecommendReadService {

    public static final int DEFAULT_ELEMENTS_COUNT = 5;

    private final RecommendRepository recommendRepository;

    public RecommendReadService(RecommendRepository recommendRepository) {
        this.recommendRepository = recommendRepository;
    }

    @Transactional(readOnly = true)
    public List<Recommend> readBestCocktails() {
        Pageable pageable = PageRequest.ofSize(DEFAULT_ELEMENTS_COUNT).withPage(0);
        return recommendRepository.findBestCocktails(pageable);
    }

    @Transactional(readOnly = true)
    public List<Recommend> readRecommendCocktails(User user) {
        Pageable pageable = PageRequest.ofSize(DEFAULT_ELEMENTS_COUNT).withPage(0);
        return recommendRepository.findRecommendCocktails(getUserAgeGroup(user),
                user.getGender(),
                pageable);
    }

    private int getUserAgeGroup(User user) {
        return user.getAge() / 10 * 10;
    }
}
