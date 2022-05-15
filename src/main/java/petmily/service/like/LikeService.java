package petmily.service.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petmily.controller.dto.LikeSaveRequestDto;
import petmily.controller.dto.UserListResponseDto;
import petmily.domain.like.Like;
import petmily.domain.like.LikeRepository;
import petmily.service.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserService userService;

    public Long createLike(LikeSaveRequestDto requestDto){
        Like like = likeRepository.check(requestDto.getEmail(), requestDto.getPostId())
                .orElse(requestDto.toEntity());
        Long id = likeRepository.save(like).getLikeId();
        return id;
    }

    public void delete(String email, Long postId) {
        try {
            Like like = likeRepository.findByEmailPostId(email, postId);
            likeRepository.delete(like);
        }catch (Exception e){}
    }

    public int findMyLike(String email, Long postId){
         if(likeRepository.check(email, postId).isPresent()){
             return 1;
         }else{
             return 0;
         }
    }

    @Transactional(readOnly = true)
    public int countLike(Long postId){
        return likeRepository.countLike(postId);
    }

    @Transactional(readOnly = true)
    public List<UserListResponseDto> findAllUser(Long postId){
        List<UserListResponseDto> result = likeRepository.findUserByPostId(postId).stream().map(like -> {
            return new UserListResponseDto(like.getEmail(), userService.findImgByEmail(like.getEmail()));
        }).collect(Collectors.toList());

        return result;
    }

    public boolean checkPresent(String email){
        return likeRepository.checkPresent(email).isPresent();
    }
}