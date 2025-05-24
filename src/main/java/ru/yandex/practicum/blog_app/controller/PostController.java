package ru.yandex.practicum.blog_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ru.yandex.practicum.blog_app.model.Paging;
import ru.yandex.practicum.blog_app.service.CommentService;
import ru.yandex.practicum.blog_app.service.PostService;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final CommentService commentService;

    @GetMapping("/")
    public String showPosts(Model model,
                            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                            @RequestParam(value = "search", defaultValue = "") String search) {
        var posts = postService.findAllPosts(search, pageNumber, pageSize);
        var paging = new Paging(posts.size(), pageNumber, pageSize);
        posts.forEach(p -> p.setComments(commentService.findAllCommentsByPostId(p.getId())));
        model.addAttribute("posts", posts);
        model.addAttribute("paging", paging);
        model.addAttribute("search", search);
        return "posts";
    }

    @GetMapping("/add")
    public String addPost() {
        return "add-post";
    }

    @PostMapping("/savePost")
    public String savePost(
            @RequestParam("title") String title,
            @RequestParam(value = "image") MultipartFile image,
            @RequestParam("tags") String tags,
            @RequestParam("text") String text) throws IOException {
        postService.savePost(title, image, tags, text);
        return "redirect:/";
    }

    @GetMapping("/images/{postId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("postId") Long postId) {
        var post = postService.findById(postId);
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .contentLength(post.getImage().length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new ByteArrayResource(post.getImage()));
    }

    @GetMapping("/{postId}")
    public String showPost(Model model, @PathVariable("postId") Long postId) {
        var post = postService.findById(postId);
        post.setComments(commentService.findAllCommentsByPostId(postId));
        model.addAttribute("post", post);
        return "post";
    }

    @PostMapping("/{postId}/like")
    public String changeRating(@PathVariable("postId") Long postId, @RequestParam("like") boolean like) {
        postService.changePostLikesCount(postId, like);
        return "redirect:/" + postId;
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("postId") Long postId) {
        commentService.deleteCommentsByPostId(postId);
        postService.deletePost(postId);
        return "redirect:/";
    }

    @GetMapping("/{postId}/edit")
    public String editPost(Model model, @PathVariable("postId") Long postId) {
        var post = postService.findById(postId);
        model.addAttribute("post", post);
        return "add-post";
    }

    @PostMapping("/{postId}")
    public String updatePost(
            @PathVariable("postId") Long postId,
            @RequestParam("title") String title,
            @RequestParam("image") MultipartFile image,
            @RequestParam("tags") String tags,
            @RequestParam("text") String text
    ) throws IOException {
        postService.updatePost(postId, title, image, tags, text);
        return "redirect:/" + postId;
    }

    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable("postId") Long postId, @RequestParam("text") String text) {
        commentService.addCommentToPost(postId, text);
        return "redirect:/" + postId;
    }

    @PostMapping("/{postId}/comments/{commentId}")
    public String updateComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestParam("text") String text) {
        commentService.updateComment(commentId, text);
        return "redirect:/" + postId;
    }

    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        commentService.deleteCommentFromPost(commentId);
        return "redirect:/" + postId;
    }

}
