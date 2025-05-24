package ru.yandex.practicum.blog_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.blog_app.model.Post;
import ru.yandex.practicum.blog_app.repository.PostRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> findAllPosts(String tags, int pageNumber, int pageSize) {
        if (tags.isEmpty()) {
            return postRepository.findAll(pageNumber, pageSize);
        } else {
            return postRepository.findPostsByTags(getTagsFromString(tags), pageNumber, pageSize);
        }
    }

    public void savePost(String title, MultipartFile image, String tags, String text) throws IOException {
        postRepository.savePost(title, image.getBytes(), getTagsFromString(tags), text);
    }

    public Post findById(Long postId) {
        return postRepository.findPostById(postId);
    }

    public void changePostLikesCount(Long postId, boolean like) {
        var post = postRepository.findPostById(postId);
        post.setLikesCount(like ? post.getLikesCount() + 1 : post.getLikesCount() - 1);
        postRepository.updatePost(post);
    }

    public void deletePost(Long postId) {
        postRepository.delete(postId);
    }

    public void updatePost(Long postId, String title, MultipartFile image, String tags, String content) throws IOException {
        var post = postRepository.findPostById(postId);
        post.setTitle(title);
        post.setImage(image.getBytes());
        post.setTags(getTagsFromString(tags));
        post.setContent(content);
        postRepository.updatePost(post);
    }

    private List<String> getTagsFromString(String search) {
        return Arrays.asList(search.split("\\s"));
    }
}
