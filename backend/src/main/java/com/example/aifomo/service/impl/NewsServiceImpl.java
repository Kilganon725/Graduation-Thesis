package com.example.aifomo.service.impl;

import com.example.aifomo.dto.NewsArticleVO;
import com.example.aifomo.dto.NewsFeedResponse;
import com.example.aifomo.service.NewsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;
    private final String baseUrl;

    public NewsServiceImpl(RestTemplate restTemplate,
                           @Value("${news.api-key:}") String apiKey,
                           @Value("${news.base-url:https://newsapi.org}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @Override
    public NewsFeedResponse getNews(String category, String keyword, int pageSize) {
        String safeCategory = normalizeCategory(category);
        String safeKeyword = (keyword == null || keyword.isBlank()) ? defaultKeyword(safeCategory) : keyword.trim();
        int safePageSize = Math.min(Math.max(pageSize, 4), 12);

        if (apiKey == null || apiKey.isBlank()) {
            return mockFeed(safeCategory, safeKeyword, safePageSize);
        }

        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v2/everything")
                    .queryParam("q", safeKeyword)
                    .queryParam("language", "zh")
                    .queryParam("sortBy", "publishedAt")
                    .queryParam("pageSize", safePageSize)
                    .queryParam("apiKey", apiKey)
                    .toUriString();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            String response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
            JsonNode root = objectMapper.readTree(response == null ? "{}" : response);
            if (!"ok".equals(root.path("status").asText())) {
                return mockFeed(safeCategory, safeKeyword, safePageSize);
            }
            List<NewsArticleVO> articles = new ArrayList<>();
            for (JsonNode node : root.path("articles")) {
                NewsArticleVO vo = new NewsArticleVO();
                vo.setTitle(text(node, "title", "AI 最新进展"));
                vo.setDescription(text(node, "description", "暂无摘要"));
                vo.setContent(text(node, "content", "暂无内容"));
                vo.setUrl(text(node, "url", ""));
                vo.setUrlToImage(imageOrFallback(node.path("urlToImage").asText(null), safeCategory, articles.size()));
                vo.setSourceName(node.path("source").path("name").asText("未知来源"));
                vo.setPublishedAt(node.path("publishedAt").asText(""));
                vo.setCategory(safeCategory);
                articles.add(vo);
            }

            NewsFeedResponse feed = new NewsFeedResponse();
            feed.setCategory(safeCategory);
            feed.setKeyword(safeKeyword);
            feed.setTotal(root.path("totalResults").asInt(articles.size()));
            feed.setMock(false);
            feed.setFetchedAt(LocalDateTime.now());
            feed.setArticles(articles);
            return feed;
        } catch (Exception ignored) {
            return mockFeed(safeCategory, safeKeyword, safePageSize);
        }
    }

    private NewsFeedResponse mockFeed(String category, String keyword, int pageSize) {
        List<NewsArticleVO> articles = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            NewsArticleVO vo = new NewsArticleVO();
            vo.setTitle(mockTitle(category, i));
            vo.setDescription("围绕 " + keyword + " 的最新动态，系统在无 API Key 时使用本地示例数据回退。");
            vo.setContent("当前未配置 NewsAPI 密钥，展示的是本地 mock 新闻，用于保证新闻栏目始终可运行。");
            vo.setUrl("https://newsapi.org/");
            vo.setUrlToImage("https://picsum.photos/seed/" + category + "-" + i + "/900/600");
            vo.setSourceName("Mock News");
            vo.setPublishedAt(LocalDateTime.now().minusHours(i * 3L).toString());
            vo.setCategory(category);
            articles.add(vo);
        }
        NewsFeedResponse feed = new NewsFeedResponse();
        feed.setCategory(category);
        feed.setKeyword(keyword);
        feed.setTotal(pageSize);
        feed.setMock(true);
        feed.setFetchedAt(LocalDateTime.now());
        feed.setArticles(articles);
        return feed;
    }

    private String mockTitle(String category, int index) {
        if ("ai".equals(category)) return "AI 模型与应用进展更新 " + (index + 1);
        if ("tech".equals(category)) return "科技行业前沿动态 " + (index + 1);
        if ("research".equals(category)) return "AI 研究与学术进展 " + (index + 1);
        return "科技综合新闻 " + (index + 1);
    }

    private String text(JsonNode node, String field, String fallback) {
        String value = node.path(field).asText();
        return value == null || value.isBlank() ? fallback : value;
    }

    private String imageOrFallback(String url, String category, int index) {
        if (url != null && !url.isBlank()) {
            return url;
        }
        return "https://picsum.photos/seed/" + category + "-fallback-" + index + "/900/600";
    }

    private String defaultKeyword(String category) {
        return switch (category) {
            case "ai" -> "artificial intelligence OR AI OR machine learning OR large language model";
            case "tech" -> "technology OR semiconductor OR robotics OR cloud OR quantum";
            case "research" -> "AI research OR machine learning OR robotics OR computer science";
            default -> "technology OR artificial intelligence";
        };
    }

    private String normalizeCategory(String category) {
        if (category == null) {
            return "ai";
        }
        String value = category.trim().toLowerCase();
        if (List.of("ai", "tech", "research").contains(value)) {
            return value;
        }
        return "ai";
    }
}
