package ru.yandex.practicum.blog_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import ru.yandex.practicum.blog_app.model.Post;
import ru.yandex.practicum.blog_app.repository.PostRepository;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllPosts_shouldReturnPosts() {
        String search = "test";
        int pageNumber = 1;
        int pageSize = 10;
        List<Post> expectedPosts = getExpectedPosts();
        when(postRepository.findPostsByTags(List.of(search), pageNumber, pageSize)).thenReturn(expectedPosts);
        List<Post> actualPosts = postService.findAllPosts(search, pageNumber, pageSize);
        assertEquals(expectedPosts, actualPosts);
        verify(postRepository, times(1)).findPostsByTags(List.of(search), pageNumber, pageSize);
    }

    @Test
    void savePost_shouldSaveNewPost() throws IOException {
        String title = "New Post";
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "image-content".getBytes());
        String tagsAsText = "tag1 tag2";
        List<String> tagsAsList = List.of("tag1",  "tag2");
        String content = "This is a test post";
        postService.savePost(title, image, tagsAsText, content);
        verify(postRepository, times(1)).savePost(eq(title), eq(image.getBytes()), eq(tagsAsList), eq(content));
    }

    @Test
    void findById_shouldReturnPost() {
        Long postId = 1L;
        Post expectedPost = getExpectedPosts().get(0);
        when(postRepository.findPostById(postId)).thenReturn(expectedPost);

        Post actualPost = postService.findById(postId);

        assertEquals(expectedPost, actualPost);
        verify(postRepository, times(1)).findPostById(postId);
    }

    @Test
    void changePostLikesCount_shouldIncreaseLikes() {
        Long postId = 1L;
        Post post = getExpectedPosts().get(0);
        when(postRepository.findPostById(postId)).thenReturn(post);

        postService.changePostLikesCount(postId, true);

        assertEquals(11, post.getLikesCount());
        verify(postRepository, times(1)).updatePost(post);
    }

    @Test
    void changePostLikesCount_shouldDecreaseLikes() {
        Long postId = 1L;
        Post post = getExpectedPosts().get(0);
        when(postRepository.findPostById(postId)).thenReturn(post);

        postService.changePostLikesCount(postId, false);

        assertEquals(9, post.getLikesCount());
        verify(postRepository, times(1)).updatePost(post);
    }

    @Test
    void deletePost_shouldCallRepositoryDelete() {
        Long postId = 1L;
        postService.deletePost(postId);
        verify(postRepository, times(1)).delete(postId);
    }

    @Test
    void updatePost_shouldUpdateExistingPost() throws IOException {
        Long postId = 1L;
        String newTitle = "Updated Title";
        MockMultipartFile newImage = new MockMultipartFile(
                "image", "newImage.jpg", "image/jpeg", "new-image-content".getBytes());
        String newTagsAsText = "newTag1 newTag2";
        List<String> newTagsAsList = List.of("newTag1", "newTag2");
        String newText = "Updated text.";

        Post existingPost = getExpectedPosts().get(0);

        when(postRepository.findPostById(postId)).thenReturn(existingPost);

        postService.updatePost(postId, newTitle, newImage, newTagsAsText, newText);

        assertEquals(newTitle, existingPost.getTitle());
        assertArrayEquals(newImage.getBytes(), existingPost.getImage());
        assertEquals(newTagsAsList, existingPost.getTags());
        assertEquals(newText, existingPost.getContent());

        verify(postRepository, times(1)).updatePost(existingPost);
    }

    private List<Post> getExpectedPosts(){
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Post1");
        post1.setImage("Post Image Content One".getBytes());
        post1.setContent("Post Text1");
        post1.setLikesCount(10);
        post1.setContentPreview("Post Text1");
        post1.setContentParts(List.of("Post Text1"));
        post1.setTags(List.of("Tag1", "Tag2"));
        post1.setTagsAsText("Tag1 Tag2");
        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Post2");
        post2.setImage("Post Image Content Two".getBytes());
        post2.setContent("Post Text2");
        post2.setLikesCount(5);
        post2.setContentPreview("Post Text2");
        post2.setContentParts(List.of("Post Text2"));
        post2.setTags(List.of("Tag2", "Tag3"));
        post2.setTagsAsText("Tag2 Tag3");
        return List.of(post1, post2);
    }
}
