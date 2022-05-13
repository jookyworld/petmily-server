package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petmily.controller.dto.PostListResponseDto;
import petmily.controller.dto.PostResponseDto;
import petmily.controller.dto.PostSaveRequestDto;
import petmily.controller.dto.UserSaveRequestDto;
import petmily.service.post.PostService;
import petmily.service.user.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RequestMapping("/api/post")
@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final UserService userService;
    private final PostService postService;

    private String localPath = "/Users/jookwonyoung/Documents/petmily/testImg";
    private String ec2Path = "/home/ec2-user/petmilyServer/step1/imgDB";
    String postRootPath;    //post 폴더

    @PostMapping("/save")
    public Long save(@RequestHeader(value = "email") String email, @RequestParam("userImg") String userImg, @RequestParam("postImg") MultipartFile files, @RequestParam("postContent") String content) {

        if (new File(ec2Path + "/user").exists()) {
            postRootPath = ec2Path + "/post";        //ec2-server
        } else {
            postRootPath = localPath + "/post";     //localhost
        }

        UserSaveRequestDto saveRequestDto = new UserSaveRequestDto();
        saveRequestDto.setEmail(email);
        saveRequestDto.setUserImg(userImg);
        Long userId = userService.save(saveRequestDto);

        PostSaveRequestDto requestDto = new PostSaveRequestDto();
        requestDto.setEmail(email);
        requestDto.setPostContent(content);
        Long postId = postService.save(requestDto);    //저장할 postImg(filename)

        //String userConType = userImg.getContentType();
        String conType = files.getContentType();
        if (!(conType.equals("image/png") || conType.equals("image/jpeg"))) {
            Long error = null;
            return error;
        }

        String filePath = postRootPath + "/" + postId;
        try {
            files.transferTo(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return postId;
    }




//    @GetMapping(value = "/findById/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
//    public PostResponseDto findById (@PathVariable Long id) throws IOException {
//        PostResponseDto tmpDto = postService.findById(id);
//
//        InputStream in;
//        in = new FileInputStream(localPath+"/post/"+id);
//        byte[] imgByteArray = in.readAllBytes();
//        in.close();
//
//        String strByte = new String(imgByteArray, StandardCharsets.UTF_8);
//
//        System.out.println("strByte"+strByte);
//
//        tmpDto.setPostImg(strByte);
//        //PostEndListResponseDto responseEntity= new PostEndListResponseDto(tmpDto.getPostId(), tmpDto.getEmail(), tmpDto.);
//        return tmpDto;
//    }


    @GetMapping(value = "/getImg/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        InputStream in;
        try {
            try {
                in = new FileInputStream(ec2Path + "/post/" + id);   //파일 읽어오기
            } catch (Exception e) {
                in = new FileInputStream(localPath + "/post/" + id);   //파일 읽어오기
            }
            byte[] imgByteArray = in.readAllBytes();                    //byte로 변환
            in.close();
            return new ResponseEntity<byte[]>(imgByteArray, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/findAll")
    public List<PostListResponseDto> findAll() {
        List<PostListResponseDto> responseDtoList = postService.findAllDesc();
        return responseDtoList;
    }
}
