package project.server.domain.cocktail.service;

import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.server.domain.cocktail.embed.category.Category;
import project.server.domain.cocktail.embed.category.CategoryMapper;
import project.server.domain.cocktail.embed.ingredient.Ingredients;
import project.server.domain.cocktail.embed.liquor.LiquorMapper;
import project.server.domain.cocktail.embed.rate.Rate;
import project.server.domain.cocktail.embed.rate.RateDto;
import project.server.domain.cocktail.embed.recipe.Recipe;
import project.server.domain.cocktail.embed.tag.Tag;
import project.server.domain.cocktail.embed.tag.TagMapper;
import project.server.domain.cocktail.embed.tag.Tags;
import project.server.domain.cocktail.repository.CocktailRepository;
import project.server.domain.cocktail.dto.CocktailDto;
import project.server.domain.cocktail.entity.Cocktail;
import project.server.domain.comment.entity.Comment;
import project.server.domain.recommendcocktail.service.RecommendCocktailService;
import project.server.domain.user.User;
import project.server.domain.user.UserService;
import project.server.dto.MultiResponseDto;
import project.server.exception.BusinessLogicException;
import project.server.exception.ExceptionCode;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CocktailService {

    public static final int DEFAULT_SIZE = 16;
    public static final String SEPARATOR = ",";
    public static final int MAX_RATE_VALUE = 5;
    public static final int MIN_RATE_VALUE = 1;
    public static final boolean UNSIGNED_USER = false;

    private final CocktailRepository cocktailRepository;
    private final RecommendCocktailService recommendCocktailService;
    private final UserService userService;

    public CocktailService(CocktailRepository cocktailRepository, RecommendCocktailService recommendCocktailService, UserService userService) {
        this.cocktailRepository = cocktailRepository;
        this.recommendCocktailService = recommendCocktailService;
        this.userService = userService;
    }

    public CocktailDto.Response createCocktail(Authentication authentication, CocktailDto.Post post) {
        User user = userService.findUserByAuthentication(authentication);
        Cocktail cocktail = cocktailPostDtoToEntity(post);
        cocktail.assignUser(user);
        Cocktail savedCocktail = cocktailRepository.save(cocktail);
        recommendCocktailService.createRecommendCocktail(cocktail, user);
        savedCocktail.assignRecommends(createRecommendCocktails(savedCocktail.getTags(), savedCocktail.getCocktailId()));
        return entityToResponse(savedCocktail, false);
    }

    public CocktailDto.Response readCocktail(Authentication authentication, long cocktailId) {
        Cocktail cocktail = findCocktailById(cocktailId);
        cocktail.assignRecommends(createRecommendCocktails(cocktail.getTags(), cocktail.getCocktailId()));
        cocktail.incrementViewCount();
        if(authentication == null){
            return entityToResponse(cocktail, UNSIGNED_USER);
        }
        User user = userService.findUserByAuthentication(authentication);
        return entityToResponse(cocktail, user.isBookmarked(cocktailId));
    }

    public MultiResponseDto readFilteredCocktails(Authentication authentication, String category, String tag, int page, String sortValue) {
        Sort sort = setSort(sortValue);
        Pageable pageable = PageRequest.ofSize(DEFAULT_SIZE).withPage(page - 1).withSort(sort);
        if (isNotSelectCategoryAndTag(category, tag)) {
            return readEveryCocktails(authentication, pageable);
        }
        if (isNotSelectCategory(category)) {
            return readFilteringByTagsCocktails(authentication, tag, pageable);
        }
        if (isNotSelectTag(tag)) {
            return readFilteringByCategoryCocktails(authentication, category, pageable);
        }
        return readFilteringByTagsAndCategoryCocktails(authentication, category, tag, pageable);
    }

    public CocktailDto.Response updateCocktail(Authentication authentication, long cocktailId, CocktailDto.Patch patch) {
        User user = userService.findUserByAuthentication(authentication);
        Cocktail cocktail = findCocktailById(cocktailId);
        verifyUser(user, cocktail);
        cocktail.modify(patch);
        cocktail.assignRecommends(createRecommendCocktails(cocktail.getTags(), cocktailId));
        return entityToResponse(cocktail, false);
    }

    public void removeCocktail(Authentication authentication, long cocktailId) {
        User user = userService.findUserByAuthentication(authentication);
        Cocktail cocktail = findCocktailById(cocktailId);
        verifyUser(user, cocktail);
        cocktailRepository.delete(cocktail);
    }

    public Cocktail findCocktailById(long cocktailId) {
        return cocktailRepository.findById(cocktailId).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.COCKTAIL_NOT_FOUND));
    }

    public RateDto.Response rateCocktail(Authentication authentication, long cocktailId, int value) {
        verifyRateValue(value);
        User user = userService.findUserByAuthentication(authentication);
        Cocktail cocktail = findCocktailById(cocktailId);
        if (user.isAlreadyRated(cocktailId)) {
            return reCalculateCocktailsRate(cocktailId, value, user, cocktail);
        }
        return calculateCocktailsRate(cocktailId, value, user, cocktail);
    }

    public void bookmarkCocktail(Authentication authentication, long cocktailId) {
       User user = userService.findUserByAuthentication(authentication);
       Cocktail cocktail = findCocktailById(cocktailId);
       if(user.isBookmarked(cocktailId)){
           recommendCocktailService.subtractBookmarkCount(user, cocktail);
           user.cancelBookmark(cocktailId);
           return;
       }
       recommendCocktailService.addBookmarkCount(user, cocktail);
       user.bookmark(cocktailId);
    }

    private RateDto.Response calculateCocktailsRate(long cocktailId, int value, User user, Cocktail cocktail) {
        cocktail.rate(value);
        user.putRatedCocktail(cocktailId, value);
        return new RateDto.Response(cocktail.getRatedScore());
    }

    private RateDto.Response reCalculateCocktailsRate(long cocktailId, int value, User user, Cocktail cocktail) {
        int oldValue = user.getOldRate(cocktailId);
        cocktail.reRate(oldValue, value);
        user.putRatedCocktail(cocktailId, value);
        return new RateDto.Response(cocktail.getRatedScore());
    }

    private void verifyRateValue(int value) {
        if (value > MAX_RATE_VALUE || value < MIN_RATE_VALUE) {
            throw new BusinessLogicException(ExceptionCode.INVALID_RATE_VALUE);
        }
    }

    private MultiResponseDto<CocktailDto.SimpleResponse> readEveryCocktails(Authentication authentication, Pageable pageable) {
        Page<Cocktail> cocktailPage = cocktailRepository.findAll(pageable);
        List<CocktailDto.SimpleResponse> responses = createSimpleResponses(authentication, cocktailPage.getContent());
        return new MultiResponseDto<>(responses, cocktailPage);
    }

    private MultiResponseDto<CocktailDto.SimpleResponse> readFilteringByCategoryCocktails(Authentication authentication, String category, Pageable pageable) {
        Category selectedCategory = CategoryMapper.map(category);
        Page<Cocktail> cocktailPage = cocktailRepository.findByCategory(selectedCategory, pageable);
        List<CocktailDto.SimpleResponse> responses = createSimpleResponses(authentication, cocktailPage.getContent());
        return new MultiResponseDto<>(responses, cocktailPage);
    }

    private MultiResponseDto<CocktailDto.SimpleResponse> readFilteringByTagsCocktails(Authentication authentication, String tag, Pageable pageable) {
        List<Tag> tags = createTagList(tag);
        Page<Cocktail> cocktailPage = cocktailRepository.findDistinctByTagsTagsIn(tags, pageable);
        return createFilteredByTagCockatilsMultiResponseDto(authentication, tags, cocktailPage);
    }

    private MultiResponseDto<CocktailDto.SimpleResponse> readFilteringByTagsAndCategoryCocktails(Authentication authentication, String category, String tag, Pageable pageable) {
        List<Tag> tags = createTagList(tag);
        Category selectedCategory = CategoryMapper.map(category);
        Page<Cocktail> cocktailPage = cocktailRepository.findDistinctByCategoryAndTagsTagsIn(selectedCategory, tags, pageable);
        return createFilteredByTagCockatilsMultiResponseDto(authentication, tags, cocktailPage);
    }

    private MultiResponseDto<CocktailDto.SimpleResponse> createFilteredByTagCockatilsMultiResponseDto(Authentication authentication, List<Tag> tags, Page<Cocktail> cocktailPage) {
        List<Cocktail> filteredCocktails = cocktailPage.get()
                .filter(cocktail -> cocktail.containsAll(tags))
                .collect(Collectors.toList());
        List<CocktailDto.SimpleResponse> responses = createSimpleResponses(authentication, filteredCocktails);
        return new MultiResponseDto<>(responses, cocktailPage);
    }

    private List<Tag> createTagList(String tag) {
        return Arrays.stream(tag.split(SEPARATOR))
                .map(TagMapper::map)
                .collect(Collectors.toList());
    }

    private Sort setSort(String sortValue) {
        if (sortValue.equals("most_viewed")) {
            return Sort.by(Sort.Order.desc("viewCount"));
        }
        if (sortValue.equals("least_viewed")) {
            return Sort.by(Sort.Order.asc("viewCount"));
        }
        if (sortValue.equals("highest_rate")) {
            return Sort.by(Sort.Order.desc("rate.rate"));
        }
        return Sort.by(Sort.Order.asc("rate.rate"));
    }

    private List<CocktailDto.SimpleResponse> createSimpleResponses(Authentication authentication, List<Cocktail> cocktails) {
        if(authentication == null){
            return cocktails.stream()
                    .map(cocktail -> entityToSimpleResponse(UNSIGNED_USER, cocktail))
                    .collect(Collectors.toList());
        }
        User user = userService.findUserByAuthentication(authentication);
        return cocktails.stream()
                .map(cocktail ->entityToSimpleResponse(user.isBookmarked(cocktail.getCocktailId()), cocktail))
                .collect(Collectors.toList());
    }

    private List<Cocktail> createRecommendCocktails(Tags tags, long cocktailId) {
        return cocktailRepository.findDistinctTop3ByTagsTagsContainingAndCocktailIdNotOrderByRateRateDesc(tags.getRandomTag(), cocktailId);
    }

    private Cocktail cocktailPostDtoToEntity(CocktailDto.Post post) {
        return Cocktail.builder()
                .name(post.getName())
                .imageUrl(post.getImageUrl())
                .recipe(new Recipe(post.getRecipe()))
                .tags(new Tags(post.getTags()))
                .category(CategoryMapper.map(post.getLiquor()))
                .rate(new Rate())
                .liquor(LiquorMapper.map(post.getLiquor()))
                .ingredients(new Ingredients(post.getIngredients()))
                .build();
    }

    private CocktailDto.SimpleResponse entityToSimpleResponse(boolean isBookmarked, Cocktail cocktail) {
        return CocktailDto.SimpleResponse.builder()
                .cocktailId(cocktail.getCocktailId())
                .name(cocktail.getName())
                .imageUrl(cocktail.getImageUrl())
                .isBookmarked(isBookmarked)
                .build();
    }

    private CocktailDto.Response entityToResponse(Cocktail cocktail, boolean isBookmarked) {
        User user = cocktail.getUser();
        return CocktailDto.Response.builder()
                .cocktailId(cocktail.getCocktailId())
                .isAdminWritten(user.isAdmin())
                .userId(user.getUserId())
                .userName(user.getName())
                .name(cocktail.getName())
                .imageUrl(cocktail.getImageUrl())
                .liquor(cocktail.getLiquor().getLiquor())
                .ingredients(cocktail.getIngredients().createResponseDtoList())
                .recipe(cocktail.getRecipe().createResponseDtoList())
                .tags(cocktail.getTags().createResponseDtoList())
                .rating(cocktail.getRate().getRate())
                .viewCount(cocktail.getViewCount())
                .createdAt(cocktail.getCreatedAt())
                .modifiedAt(cocktail.getModifiedAt())
                .comments(cocktail.getComments().stream()
                        .map(Comment::entityToResponse)
                        .collect(Collectors.toList()))
                .isBookmarked(isBookmarked)
                .recommends(cocktail.getRecommends().stream()
                        .map(recommendedCocktail
                                -> entityToSimpleResponse(user.isBookmarked(recommendedCocktail.getCocktailId()), recommendedCocktail))
                        .collect(Collectors.toList()))
                .build();
    }

    private void verifyUser(User user, Cocktail cocktail) {
        if(!user.hasAuthority(cocktail)){
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_USER);
        }
    }

    private boolean isNotSelectTag(String tag) {
        return tag == null;
    }

    private boolean isNotSelectCategory(String category) {
        return category == null;
    }

    private boolean isNotSelectCategoryAndTag(String category, String tag) {
        return category == null && tag == null;
    }
}