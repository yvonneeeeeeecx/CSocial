package com.social.csocial.service;

import com.social.csocial.entity.Post;
import com.social.csocial.entity.PostComment;
import com.social.csocial.entity.PostLike;
import com.social.csocial.entity.User;
import com.social.csocial.repository.PostCommentRepository;
import com.social.csocial.repository.PostLikeRepository;
import com.social.csocial.repository.PostRepository;
import com.social.csocial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private UserRepository userRepository;

    public Post createPost(Long userId, String title, String content, String imageUrl) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setImageUrl(imageUrl);
        return postRepository.save(post);
    }

    public List<Post> listPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<PostView> listPostViews() {
        return buildPostViews(listPosts());
    }


    public List<Post> listPostsForAdmin(Long userId, String keyword) {
        String keywordText = keyword == null ? "" : keyword.trim().toLowerCase();

        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> userId == null || userId.equals(item.getUserId()))
                .filter(item -> {
                    if (keywordText.isEmpty()) {
                        return true;
                    }
                    String title = item.getTitle() == null ? "" : item.getTitle().toLowerCase();
                    String content = item.getContent() == null ? "" : item.getContent().toLowerCase();
                    return title.contains(keywordText) || content.contains(keywordText);
                })
                .collect(Collectors.toList());
    }

    public Page<Post> pagePosts(int page, int size) {
        int pageNo = Math.max(page - 1, 0);
        int pageSize = Math.max(size, 1);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public List<Post> listPostsByUser(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<PostView> listPostViewsByUser(Long userId) {
        return buildPostViews(listPostsByUser(userId));
    }

    public List<PostView> buildPostViews(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = posts.stream().map(Post::getUserId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));

        return posts.stream().map(post -> {
            User author = userMap.get(post.getUserId());
            PostView view = new PostView();
            view.setId(post.getId());
            view.setUserId(post.getUserId());
            view.setTitle(post.getTitle());
            view.setContent(post.getContent());
            view.setImageUrl(post.getImageUrl());
            view.setLikeCount(post.getLikeCount());
            view.setCommentCount(post.getCommentCount());
            view.setCreatedAt(post.getCreatedAt());
            if (author != null) {
                view.setUsername(author.getUsername());
                view.setNickname(author.getNickname());
                view.setAvatar(author.getAvatar());
                view.setProfile(author.getProfile());
                view.setCollege(author.getCollege());
                view.setMajor(author.getMajor());
                view.setGrade(author.getGrade());
            }
            return view;
        }).toList();
    }


    @Transactional
    public Post likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("动态不存在"));

        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new RuntimeException("已点赞该动态");
        }

        postLikeRepository.save(new PostLike(postId, userId));
        Integer likeCount = post.getLikeCount() == null ? 0 : post.getLikeCount();
        post.setLikeCount(likeCount + 1);
        return postRepository.save(post);
    }

    public List<LikeUserView> listLikeUsers(Long postId) {
        List<PostLike> likes = postLikeRepository.findByPostIdOrderByCreatedAtDesc(postId);
        List<Long> userIds = likes.stream().map(PostLike::getUserId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));

        return likes.stream().map(item -> {
            User user = userMap.get(item.getUserId());
            LikeUserView view = new LikeUserView();
            view.setUserId(item.getUserId());
            view.setUsername(user == null ? null : user.getUsername());
            view.setNickname(user == null ? null : user.getNickname());
            view.setLikedAt(item.getCreatedAt());
            return view;
        }).toList();
    }

    @Transactional
    public PostComment addComment(Long postId, Long userId, String content) {
        if (content == null || content.isBlank()) {
            throw new RuntimeException("评论内容不能为空");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("动态不存在"));

        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content.trim());
        PostComment savedComment = postCommentRepository.save(comment);

        Integer commentCount = post.getCommentCount() == null ? 0 : post.getCommentCount();
        post.setCommentCount(commentCount + 1);
        postRepository.save(post);

        return savedComment;
    }

    public List<CommentView> listComments(Long postId) {
        List<PostComment> comments = postCommentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        List<Long> userIds = comments.stream().map(PostComment::getUserId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));

        return comments.stream().map(item -> {
            User user = userMap.get(item.getUserId());
            CommentView view = new CommentView();
            view.setId(item.getId());
            view.setUserId(item.getUserId());
            view.setUsername(user == null ? null : user.getUsername());
            view.setNickname(user == null ? null : user.getNickname());
            view.setContent(item.getContent());
            view.setCreatedAt(item.getCreatedAt());
            return view;
        }).toList();
    }

    @Transactional
    public void deleteComment(Long commentId, Long operatorUserId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        if (!comment.getUserId().equals(operatorUserId)) {
            throw new RuntimeException("无权限删除该评论");
        }

        postCommentRepository.deleteById(commentId);

        postRepository.findById(comment.getPostId()).ifPresent(post -> {
            Integer commentCount = post.getCommentCount() == null ? 0 : post.getCommentCount();
            post.setCommentCount(Math.max(0, commentCount - 1));
            postRepository.save(post);
        });
    }

    public void deletePost(Long postId, Long operatorUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("动态不存在"));

        if (!post.getUserId().equals(operatorUserId)) {
            throw new RuntimeException("无权限删除该动态");
        }

        postRepository.deleteById(postId);
    }


    public void forceDeletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("动态不存在");
        }

        postRepository.deleteById(postId);
    }

    public static class CommentView {
        private Long id;
        private Long userId;
        private String username;
        private String nickname;
        private String content;
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class LikeUserView {
        private Long userId;
        private String username;
        private String nickname;
        private LocalDateTime likedAt;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public LocalDateTime getLikedAt() { return likedAt; }
        public void setLikedAt(LocalDateTime likedAt) { this.likedAt = likedAt; }
    }

    public static class PostView {
        private Long id;
        private Long userId;
        private String title;
        private String content;
        private String imageUrl;
        private Integer likeCount;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private String username;
        private String nickname;
        private String avatar;
        private String profile;
        private String college;
        private String major;
        private Integer grade;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public Integer getLikeCount() { return likeCount; }
        public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
        public Integer getCommentCount() { return commentCount; }
        public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getProfile() { return profile; }
        public void setProfile(String profile) { this.profile = profile; }
        public String getCollege() { return college; }
        public void setCollege(String college) { this.college = college; }
        public String getMajor() { return major; }
        public void setMajor(String major) { this.major = major; }
        public Integer getGrade() { return grade; }
        public void setGrade(Integer grade) { this.grade = grade; }
    }
}

