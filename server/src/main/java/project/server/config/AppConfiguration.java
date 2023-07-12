package project.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.server.domain.cocktail.embed.category.Category;
import project.server.domain.cocktail.embed.ingredient.Ingredient;
import project.server.domain.cocktail.embed.ingredient.Ingredients;
import project.server.domain.cocktail.embed.liquor.Liquor;
import project.server.domain.cocktail.embed.rate.Rate;
import project.server.domain.cocktail.embed.recipe.Recipe;
import project.server.domain.cocktail.embed.recipe.RecipeDto;
import project.server.domain.cocktail.embed.tag.Tag;
import project.server.domain.cocktail.embed.tag.TagMapper;
import project.server.domain.cocktail.embed.tag.Tags;
import project.server.domain.cocktail.entity.Cocktail;
import project.server.domain.cocktail.repository.CocktailRepository;
import project.server.domain.user.User;
import project.server.domain.user.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AppConfiguration implements InitializingBean {

    private final UserRepository userRepository;
    private final CocktailRepository cocktailRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void afterPropertiesSet() throws Exception{
        init();
    }

    private void init() {
        createTestUsers();
        createCocktails();
    }

    public void createTestUsers(){
        User test1 = new User();
        test1.setName("test1");
        test1.setEmail("test1@test.com");
        test1.setPassword(passwordEncoder.encode("1234"));
        test1.setGender("male");
        test1.setAge(29);
        userRepository.save(test1);

        User test2 = new User();
        test2.setName("test2");
        test2.setEmail("test2@test.com");
        test2.setPassword(passwordEncoder.encode("1234"));
        test2.setGender("male");
        test2.setAge(29);
        userRepository.save(test2);
    }

    public void createCocktails(){
        Cocktail cocktail1 = Cocktail.builder().
                name("test1")
                .recipe(new Recipe(List.of("1", "2"), 0))
                .category(Category.CATEGORY1)
                .liquor(Liquor.RUM)
                .ingredients(new Ingredients(List.of(Ingredient.BEVERAGE, Ingredient.ICE), 0))
                .tags(new Tags(List.of(Tag.BITTER, Tag.FREQUENCY_MEDIUM), 0))
                .imageUrl("https://cocktail-project.s3.ap-northeast-2.amazonaws.com/2023-07-12T05%3A58%3A15.197658%E1%84%83%E1%85%A1%E1%84%8B%E1%85%AE%E1%86%AB%E1%84%85%E1%85%A9%E1%84%83%E1%85%B3.jpeg")
                .rate(new Rate())
                .build();
        User user1 = userRepository.findById(1L).get();
        cocktail1.assignUser(user1);
        cocktailRepository.save(cocktail1);

        Cocktail cocktail2 = Cocktail.builder().
                name("test2")
                .recipe(new Recipe(List.of("1", "2"), 0))
                .category(Category.CATEGORY2)
                .liquor(Liquor.WHISKEY)
                .imageUrl("https://cocktail-project.s3.ap-northeast-2.amazonaws.com/2023-07-12T05%3A58%3A15.197658%E1%84%83%E1%85%A1%E1%84%8B%E1%85%AE%E1%86%AB%E1%84%85%E1%85%A9%E1%84%83%E1%85%B3.jpeg")
                .ingredients(new Ingredients(List.of(Ingredient.SUGAR, Ingredient.SALT), 0))
                .rate(new Rate())
                .tags(new Tags(List.of(Tag.SWEET, Tag.FREQUENCY_MEDIUM), 0))
                .build();
        User user2 = userRepository.findById(2L).get();
        cocktail2.assignUser(user2);
        cocktailRepository.save(cocktail2);
    }
}
