package com.example.aifomo.service.impl;

import com.example.aifomo.dto.NewsArticleVO;
import com.example.aifomo.dto.NewsFeedResponse;
import com.example.aifomo.service.NewsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Service
public class NewsServiceImpl implements NewsService {
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
    private final RestTemplate restTemplate;
    private final String feedUrl;
    private final String sourceName;

    public NewsServiceImpl(RestTemplate restTemplate,
                           @Value("${news.feed-url:https://techcrunch.com/feed/}") String feedUrl,
                           @Value("${news.source-name:TechCrunch}") String sourceName) {
        this.restTemplate = restTemplate;
        this.feedUrl = feedUrl;
        this.sourceName = sourceName;
    }

    @Override
    public NewsFeedResponse getNews(String category, String keyword, int pageSize) {
        String safeCategory = normalizeCategory(category);
        String safeKeyword = keyword == null ? "" : keyword.trim();
        int safePageSize = Math.min(Math.max(pageSize, 4), 12);

        try {
            String xml = fetchFeed();
            List<NewsArticleVO> articles = parseArticles(xml, safeCategory, safeKeyword, safePageSize);
            if (articles.isEmpty()) {
                return mockFeed(safeCategory, safeKeyword.isBlank() ? defaultKeyword(safeCategory) : safeKeyword, safePageSize);
            }
            NewsFeedResponse feed = new NewsFeedResponse();
            feed.setSourceName(sourceName + " RSS");
            feed.setCategory(safeCategory);
            feed.setKeyword(safeKeyword);
            feed.setTotal(articles.size());
            feed.setMock(false);
            feed.setFetchedAt(LocalDateTime.now());
            feed.setArticles(articles);
            return feed;
        } catch (Exception ignored) {
            return mockFeed(safeCategory, safeKeyword.isBlank() ? defaultKeyword(safeCategory) : safeKeyword, safePageSize);
        }
    }

    private String fetchFeed() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.ALL));
        headers.add("User-Agent", "Mozilla/5.0 (GraduationThesis/1.0)");
        return Objects.requireNonNull(restTemplate.exchange(feedUrl, org.springframework.http.HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody());
    }

    private List<NewsArticleVO> parseArticles(String xml, String category, String keyword, int pageSize) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setExpandEntityReferences(false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        NodeList items = document.getElementsByTagName("item");
        List<NewsArticleVO> results = new ArrayList<>();
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            String title = text(item, "title");
            String description = stripHtml(text(item, "description"));
            String link = text(item, "link");
            String pubDate = text(item, "pubDate");
            List<String> categories = extractCategories(item);
            if (!matchCategory(category, title, description, categories)) {
                continue;
            }
            if (!keyword.isBlank() && !matchKeyword(keyword, title, description, categories)) {
                continue;
            }

            NewsArticleVO vo = new NewsArticleVO();
            vo.setTitle(title.isBlank() ? "TechCrunch 新闻" : title);
            vo.setDescription(description.isBlank() ? "暂无摘要" : description);
            vo.setContent(description.isBlank() ? "暂无内容" : description);
            vo.setUrl(link);
            vo.setUrlToImage(fetchImage(link, i));
            vo.setSourceName(sourceName);
            vo.setPublishedAt(normalizeDate(pubDate));
            vo.setCategory(category);
            results.add(vo);
            if (results.size() >= pageSize) {
                break;
            }
        }
        return results;
    }

    private String fetchImage(String articleUrl, int index) {
        if (articleUrl == null || articleUrl.isBlank()) {
            return fallbackImage(index);
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.TEXT_HTML, MediaType.ALL));
            headers.add("User-Agent", "Mozilla/5.0 (GraduationThesis/1.0)");
            String html = restTemplate.exchange(articleUrl, org.springframework.http.HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
            if (html == null || html.isBlank()) {
                return fallbackImage(index);
            }
            String image = extractMeta(html, "property", "og:image");
            if (image.isBlank()) {
                image = extractMeta(html, "name", "twitter:image");
            }
            if (image.isBlank()) {
                image = extractMeta(html, "property", "twitter:image:src");
            }
            return image.isBlank() ? fallbackImage(index) : image;
        } catch (Exception ignored) {
            return fallbackImage(index);
        }
    }

    private String extractMeta(String html, String attrName, String attrValue) {
        String lower = html.toLowerCase(Locale.ROOT);
        String needle = (attrName + "=\"" + attrValue.toLowerCase(Locale.ROOT) + "\"").toLowerCase(Locale.ROOT);
        int idx = lower.indexOf(needle);
        if (idx < 0) {
            return "";
        }
        int contentIdx = lower.indexOf("content=\"", idx);
        if (contentIdx < 0) {
            return "";
        }
        int start = contentIdx + "content=\"".length();
        int end = lower.indexOf("\"", start);
        if (end < 0) {
            return "";
        }
        return html.substring(start, end).trim();
    }

    private String text(Element item, String tag) {
        NodeList nodes = item.getElementsByTagName(tag);
        if (nodes.getLength() == 0 || nodes.item(0) == null) {
            return "";
        }
        return nodes.item(0).getTextContent().trim();
    }

    private List<String> extractCategories(Element item) {
        NodeList nodes = item.getElementsByTagName("category");
        List<String> categories = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            String value = nodes.item(i).getTextContent();
            if (value != null && !value.isBlank()) {
                categories.add(value.trim().toLowerCase(Locale.ROOT));
            }
        }
        return categories;
    }

    private boolean matchCategory(String category, String title, String description, List<String> categories) {
        String haystack = (title + " " + description + " " + String.join(" ", categories)).toLowerCase(Locale.ROOT);
        return switch (category) {
            case "ai" -> containsAny(haystack, List.of("ai", "artificial intelligence", "machine learning", "llm", "model", "agent", "generative", "openai", "anthropic", "gemini", "gpt"));
            case "research" -> containsAny(haystack, List.of("research", "paper", "benchmark", "study", "open source", "arxiv", "lab", "scientist"));
            case "tech" -> true;
            default -> true;
        };
    }

    private boolean matchKeyword(String keyword, String title, String description, List<String> categories) {
        String haystack = (title + " " + description + " " + String.join(" ", categories)).toLowerCase(Locale.ROOT);
        for (String term : keyword.toLowerCase(Locale.ROOT).split("\\s+or\\s+|\\s+")) {
            String clean = term.trim();
            if (clean.isBlank()) {
                continue;
            }
            if (haystack.contains(clean)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(String haystack, List<String> terms) {
        for (String term : terms) {
            if (haystack.contains(term)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeDate(String pubDate) {
        try {
            return OffsetDateTime.parse(pubDate).toLocalDateTime().toString();
        } catch (Exception ignored) {
            return pubDate;
        }
    }

    private String stripHtml(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        return TAG_PATTERN.matcher(html).replaceAll("").replace("&nbsp;", " ").replace("&amp;", "&").trim();
    }

    private String fallbackImage(int index) {
        return "https://picsum.photos/seed/techcrunch-fallback-" + index + "/900/600";
    }

    private NewsFeedResponse mockFeed(String category, String keyword, int pageSize) {
        List<NewsArticleVO> articles = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            NewsArticleVO vo = new NewsArticleVO();
            vo.setTitle(mockTitle(category, i));
            vo.setDescription("围绕 " + keyword + " 的最新动态，系统在无法抓取 RSS 时使用本地示例数据回退。");
            vo.setContent("当前未能抓取 TechCrunch RSS，展示的是本地 mock 新闻，用于保证新闻栏目始终可运行。");
            vo.setUrl("https://techcrunch.com/");
            vo.setUrlToImage(fallbackImage(i));
            vo.setSourceName("TechCrunch Mock");
            vo.setPublishedAt(LocalDateTime.now().minusHours(i * 3L).toString());
            vo.setCategory(category);
            articles.add(vo);
        }
        NewsFeedResponse feed = new NewsFeedResponse();
        feed.setSourceName("TechCrunch Mock");
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

    private String defaultKeyword(String category) {
        return switch (category) {
            case "ai" -> "ai";
            case "tech" -> "technology";
            case "research" -> "research";
            default -> "technology";
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
