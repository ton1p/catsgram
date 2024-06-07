package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();

    private final UserService userService;

    private final Comparator<Post> askComparator = Comparator.comparingInt((Post post) -> (int) post.getPostDate().toEpochMilli());

    private final Comparator<Post> descComparator = (Post post1, Post post2) -> (int) post2.getPostDate().toEpochMilli() - (int) post1.getPostDate().toEpochMilli();

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(SortOrder sort, Integer size, Integer from) {
        List<Post> result = this.posts.values()
                .stream()
                .sorted(sort == SortOrder.ASCENDING ? askComparator : descComparator)
                .limit(size)
                .toList();

        int toIndex = Math.min((from + size), result.size());
        int fromIndex = from > result.size() ? 0 : from;

        return result.subList(fromIndex, toIndex);
    }

    public Post findById(Long id) {
        Post post = posts.get(id);
        if (post == null) {
            throw new NotFoundException(String.format("Пост с id = %s не найден", id));
        }
        return post;
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        User user = userService.findById(post.getAuthorId());

        if (user == null) {
            throw new ConditionsNotMetException(String.format("Автор с id = %s не найден", post.getAuthorId()));
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);

        return post;
    }

    public Post update(Post newPost) {
        // проверяем необходимые условия
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
