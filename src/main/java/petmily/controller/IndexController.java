package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import petmily.config.auth.LoginUser;
import petmily.config.auth.dto.SessionUser;
import petmily.service.place.PlaceService;
import petmily.service.post.PostService;
import petmily.service.walk.WalkService;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostService postsService;
    private final PlaceService placeService;
    private final WalkService walkService;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {

        model.addAttribute("post", postsService.findAllDesc());

        if(user != null){
            model.addAttribute("userName", user.getName());
            model.addAttribute("place", placeService.findByEmail(user.getName()));

        }

        return "index";
    }

    @GetMapping("/post/save")
    public String postsSave() {
        return "posts-save";
    }

    //테스트하러 추가한거
    @GetMapping("/test/walk")
    public String moveToWalk(Model model, @LoginUser SessionUser user) {
        model.addAttribute("walk", walkService.findAllDesc(user.getEmail()));
        return "walk-list";
    }

//    @GetMapping("/posts/update/{id}")
//    public String postsUpdate(@PathVariable Long id, Model model) {
//        PostsResponseDto dto = postsService.findById(id);
//        model.addAttribute("post", dto);
//
//        return "posts-update";
//    }
}
