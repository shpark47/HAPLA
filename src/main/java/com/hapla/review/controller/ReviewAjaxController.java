package com.hapla.review.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.hapla.comm.model.service.CommService;
import com.hapla.comm.model.vo.Reply;
import com.hapla.review.model.service.ReviewService;
import com.hapla.review.model.vo.Review;
import com.hapla.users.model.vo.Users;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@RequestMapping({"/review", "/comm"})
@RequiredArgsConstructor
@SessionAttributes("loginUser")
public class ReviewAjaxController {
	
	private final ReviewService reviewService;
	private final CommService commService;
	
	@Value("${r2.bucket-name}")
    private String r2BucketName;
	
	// R2 엔드포인트와 공개 URL 설정
    private static final String R2_ENDPOINT = "https://33a78afb4d99dee34672fc2e9b3a305d.r2.cloudflarestorage.com/";
    private static final String R2_PUBLIC_URL = "https://pub-04c6fc4d78f440d78896dc3d2f55f689.r2.dev/";
	
	private S3Client s3Client;
	
//	@PostMapping("insert")
//    public Map<String, Object> insertReview(@RequestBody HashMap<String, String> map, 
//                                            @ModelAttribute Review r,
//                                            @RequestParam("imageUrls") MultipartFile imageUrls,
//                                            HttpSession session) {  
//		
//		
//        
//        try {
//        	
//        	String originalFileName = imageUrls.getOriginalFilename();
//    		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
//    		String renameFileName = new SimpleDateFormat("yyyyMMddHHmmssSSS")
//    								.format(new Date()) + (int)(Math.random() * 100000) + extension;
//    		
//    		// R2에 업로드
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(r2BucketName)
//                    .key(renameFileName)
//                    .build();
//            
//			s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody
//					.fromInputStream(imageUrls.getInputStream(), imageUrls.getSize()));
//			
//			String imagePath = R2_PUBLIC_URL + renameFileName;
//			
//			Map<String, Object> response = new HashMap<>();
//			
//	        Users loginUser = (Users) session.getAttribute("loginUser");
//
//	        if (loginUser == null) {
//	            response.put("status", "error");
//	            response.put("message", "로그인이 필요합니다.");
//	            response.put("imagePath", imagePath);
//	            System.out.println("파일 업로드 성공: " + imagePath);
//	            
//	            return response;
//	        }
//
//	        // ⭐ @ModelAttribute Review r로 이미 일부 값 자동 매핑됨
//	        // @RequestBody HashMap<String, String>을 통해 나머지 값 덮어쓰기 (우선순위 적용)
//	        r.setUserNo(loginUser.getUserNo());  // 세션에서 유저 정보 추가
//	        r.setTitle(map.getOrDefault("title", r.getTitle() != null ? r.getTitle() : "제목 없음"));
//	        r.setContent(map.getOrDefault("content", r.getContent() != null ? r.getContent() : "내용 없음"));
//
//	        // ⭐ rating은 Integer로 변환 필요
//	        int rating;
//	        try {
//	            rating = Integer.parseInt(map.getOrDefault("rating", String.valueOf(r.getRating() > 0 ? r.getRating() : 3)));  
//	        } catch (NumberFormatException e) {
//	            rating = 3;
//	        }
//	        r.setRating(rating);
//
//	        r.setWhen(map.getOrDefault("when", r.getWhen() != null ? r.getWhen() : "최근 1개월 이내"));
//	        r.setWithWhom(map.getOrDefault("withWhom", r.getWithWhom() != null ? r.getWithWhom() : "혼자"));
//
//	        // 데이터 저장
//	        int result = reviewService.insertReview(r);
//
//	        if (result > 0) {
//	            response.put("status", "success");
//	            response.put("message", "리뷰가 성공적으로 저장되었습니다.");
//	        } else {
//	            response.put("status", "error");
//	            response.put("message", "게시글 작성을 실패하였습니다.");
//	        }
//
//	        return response;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//        
//    }
	
	@PostMapping("insert")
    public Map<String, Object> insertReview(@RequestBody HashMap<String, String> map, 
                                            @ModelAttribute Review r, HttpSession session) {

		Map<String, Object> response = new HashMap<>();
        Users loginUser = (Users) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.put("status", "error");
            response.put("message", "로그인이 필요합니다.");
            return response;  // ✅ JSON 응답 반환
        }

        // ⭐ @ModelAttribute Review r로 이미 일부 값 자동 매핑됨
        // @RequestBody HashMap<String, String>을 통해 나머지 값 덮어쓰기 (우선순위 적용)
        r.setUserNo(loginUser.getUserNo());  // 세션에서 유저 정보 추가
        r.setTitle(map.getOrDefault("title", r.getTitle() != null ? r.getTitle() : "제목 없음"));
        r.setContent(map.getOrDefault("content", r.getContent() != null ? r.getContent() : "내용 없음"));

        // ⭐ rating은 Integer로 변환 필요
        int rating;
        try {
            rating = Integer.parseInt(map.getOrDefault("rating", String.valueOf(r.getRating() > 0 ? r.getRating() : 3)));  
        } catch (NumberFormatException e) {
            rating = 3;
        }
        r.setRating(rating);

        r.setWhen(map.getOrDefault("when", r.getWhen() != null ? r.getWhen() : "최근 1개월 이내"));
        r.setWithWhom(map.getOrDefault("withWhom", r.getWithWhom() != null ? r.getWithWhom() : "혼자"));

        // 데이터 저장
        int result = reviewService.insertReview(r);

        if (result > 0) {
            response.put("status", "success");
            response.put("message", "리뷰가 성공적으로 저장되었습니다.");
        } else {
            response.put("status", "error");
            response.put("message", "게시글 작성을 실패하였습니다.");
        }

        return response;
	}
	
	@PostMapping("reply")
	public ArrayList<Reply> insertReply(@ModelAttribute Reply r, HttpServletResponse response){
		System.out.println(r);
		int result = commService.insertReply(r);
		ArrayList<Reply> list = commService.selectReplyList(r.getCommNo());
		return list;
	}
	
	@DeleteMapping("reply")
	public int deleteReply(@RequestParam("replyNo") int replyNo) {
		return commService.deleteReply(replyNo);

	}

	@PutMapping("reply")
	public int updateReply(@ModelAttribute Reply r) {
		int result = commService.updateReply(r);
		return result;
	}
	
	
	
	
	@PostMapping("/like")
	public ResponseEntity<Map<String, Object>> toggleLike(@RequestParam("commNo") int commNo,
	                                                      @RequestParam("userNo") int userNo) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        System.out.println("좋아요 요청 - userNo: " + userNo + ", commNo: " + commNo);

	        boolean isLiked = commService.checkUserLike(userNo, commNo) > 0;
	        System.out.println("좋아요 상태 확인 - isLiked: " + isLiked);

	        if (isLiked) {
	            commService.removeLike(userNo, commNo);
	        } else {
	            commService.addLike(userNo, commNo);
	        }

	        int updatedLikeCount = commService.getLikeCount(commNo);
	        
	        System.out.println(updatedLikeCount);
	        System.out.println("좋아요 개수 업데이트 완료 - updatedLikeCount: " + updatedLikeCount);

	        response.put("status", "success");
	        response.put("likes", updatedLikeCount);
	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", "서버 오류 발생: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}


	
	
	
	
}