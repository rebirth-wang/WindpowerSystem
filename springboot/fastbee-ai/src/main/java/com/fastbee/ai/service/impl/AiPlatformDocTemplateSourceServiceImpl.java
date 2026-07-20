package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;

import jakarta.annotation.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.fastbee.ai.model.template.AiPlatformDocTemplateRow;
import com.fastbee.ai.service.IAiPlatformDocTemplateSourceService;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.domain.SysMenu;
import com.fastbee.system.mapper.SysMenuMapper;

/**
 * 平台知识企业版模板导出源服务实现。
 */
@Service
public class AiPlatformDocTemplateSourceServiceImpl implements IAiPlatformDocTemplateSourceService {

    private static final String STRATEGY_AUTO = "AUTO";
    private static final String STRATEGY_LOCAL = "LOCAL";
    private static final String STRATEGY_WEB = "WEB";
    private static final String SOURCE_LOCAL = "LOCAL_DOC_SOURCE";
    private static final String SOURCE_WEB = "WEB_DOC_SOURCE";
    private static final String DOC_DIR_NAME = "FastBee-doc";
    private static final String DOC_BASE_URL = "https://fastbee.cn/doc/";
    private static final String DOC_SITEMAP_URL = DOC_BASE_URL + "sitemap.xml";
    private static final String DEFAULT_LANGUAGE = "zh-CN";
    private static final String ENGLISH_LANGUAGE = "en-US";
    private static final String FALLBACK_VERSION = "FASTBEE_DOC_EXPORT";
    private static final Set<String> EXCLUDED_ROOTS = Set.of("internal", ".vuepress");
    private static final Set<String> ACTION_KEYWORDS = Set.of(
            "新增", "创建", "添加", "配置", "设置", "下载", "上传", "导入", "导出", "发布", "启用",
            "停用", "删除", "编辑", "修改", "查看", "查询", "分配", "回收", "绑定", "解绑", "运行", "安装",
            "部署", "接入", "登录", "切换", "保存", "提交", "打开", "选择", "点击", "输入", "勾选"
    );
    private static final Set<String> STEP_HEADING_KEYWORDS = Set.of(
            "新增", "创建", "添加", "复制", "导入", "导出", "上传", "下载", "发布", "启用",
            "停用", "删除", "编辑", "修改", "绑定", "解绑", "安装", "部署", "接入", "登录",
            "保存", "提交", "打开", "选择", "点击", "勾选"
    );
    private static final Set<String> PRECONDITION_KEYWORDS = Set.of(
            "需要", "需先", "必须", "前提", "先把", "先去", "先配置", "先发布", "需支持", "先下载", "先选择", "默认"
    );
    private static final Set<String> NOTICE_KEYWORDS = Set.of(
            "注意", "不支持", "不能", "失败", "限制", "默认", "仅", "异常", "告警", "需先", "必须"
    );
    private static final Set<String> RESULT_KEYWORDS = Set.of(
            "即可", "可以看到", "会", "生成", "展示", "保存到", "支持", "成功", "打开", "显示"
    );
    private static final Set<String> ROLE_KEYWORDS = Set.of(
            "管理员", "租户管理员", "普通用户", "上级机构", "下级机构", "开发者", "运维", "终端用户", "机构"
    );
    private static final Pattern FRONT_MATTER_PATTERN = Pattern.compile("(?s)^---\\s*\\R(.*?)\\R---\\s*\\R?");
    private static final Pattern TITLE_PATTERN = Pattern.compile("(?m)^title:\\s*(.+?)\\s*$");
    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{2,4})\\s+(.+?)\\s*$");
    private static final Pattern TIP_BLOCK_PATTERN = Pattern.compile("(?s)::: ?(?:tip|warning|danger|info|details)?\\s*(.*?):::");
    private static final Pattern MARKDOWN_LINK_PATTERN = Pattern.compile("\\[([^\\]]+)]\\(([^)]+)\\)");
    private static final Pattern HTML_LINK_PATTERN = Pattern.compile("<a[^>]*href=\"([^\"]+)\"[^>]*>(.*?)</a>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern HTML_BREAK_PATTERN = Pattern.compile("(?i)<br\\s*/?>");
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[[^\\]]*]\\([^)]*\\)|<img[^>]*>", Pattern.CASE_INSENSITIVE);
    private static final Pattern CODE_FENCE_PATTERN = Pattern.compile("(?s)```.*?\\R(.*?)```");
    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&quot;|&nbsp;|&amp;|&lt;|&gt;");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Map<String, String> DEFAULT_SECTION_MAPPING = createDefaultSectionMapping();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<AiPlatformDocTemplateRow> loadTemplateRows(String sourceStrategy) {
        String actualStrategy = normalizeSourceStrategy(sourceStrategy);
        MenuPathIndex menuPathIndex = buildMenuPathIndex();
        if (STRATEGY_LOCAL.equals(actualStrategy)) {
            Path localRoot = requireLocalDocRoot();
            return parseLocalRows(localRoot, menuPathIndex);
        }
        if (STRATEGY_WEB.equals(actualStrategy)) {
            return parseWebRows(menuPathIndex);
        }
        Path localRoot = resolveLocalDocRoot();
        if (localRoot != null) {
            return parseLocalRows(localRoot, menuPathIndex);
        }
        return parseWebRows(menuPathIndex);
    }

    private String normalizeSourceStrategy(String sourceStrategy) {
        if (StringUtils.isBlank(sourceStrategy)) {
            return STRATEGY_AUTO;
        }
        String actualStrategy = sourceStrategy.trim().toUpperCase(Locale.ROOT);
        if (STRATEGY_LOCAL.equals(actualStrategy) || STRATEGY_WEB.equals(actualStrategy)) {
            return actualStrategy;
        }
        return STRATEGY_AUTO;
    }

    private Path requireLocalDocRoot() {
        Path localRoot = resolveLocalDocRoot();
        if (localRoot == null) {
            throw new ServiceException(message("ai.platform.doc.template.local.dir.not.found"));
        }
        return localRoot;
    }

    private Path resolveLocalDocRoot() {
        List<Path> candidates = new ArrayList<>();
        Path current = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        candidates.add(current.resolve(DOC_DIR_NAME));
        if (current.getParent() != null) {
            candidates.add(current.getParent().resolve(DOC_DIR_NAME));
        }
        if (current.getParent() != null && current.getParent().getParent() != null) {
            candidates.add(current.getParent().getParent().resolve(DOC_DIR_NAME));
        }
        for (Path candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            Path srcPath = candidate.resolve("src");
            Path sidebarPath = srcPath.resolve(".vuepress").resolve("sidebar").resolve("zh.ts");
            if (Files.isDirectory(candidate) && Files.isDirectory(srcPath) && Files.exists(sidebarPath)) {
                return candidate.normalize();
            }
        }
        return null;
    }

    private List<AiPlatformDocTemplateRow> parseLocalRows(Path localRoot, MenuPathIndex menuPathIndex) {
        Path srcRoot = localRoot.resolve("src");
        Map<String, String> sectionMapping = buildSectionMapping(srcRoot.resolve(".vuepress").resolve("sidebar").resolve("zh.ts"));
        List<Path> markdownFiles;
        try (Stream<Path> stream = Files.walk(srcRoot)) {
            markdownFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith(".md"))
                    .filter(path -> shouldIncludeLocalMarkdown(srcRoot.relativize(path)))
                    .sorted(Comparator.comparing(path -> srcRoot.relativize(path).toString(), String.CASE_INSENSITIVE_ORDER))
                    .toList();
        } catch (Exception ex) {
            throw new ServiceException(message("ai.platform.doc.template.local.read.failed", ex.getMessage()));
        }
        List<AiPlatformDocTemplateRow> rows = new ArrayList<>();
        for (Path markdownFile : markdownFiles) {
            rows.addAll(parseLocalMarkdownFile(srcRoot, markdownFile, sectionMapping, menuPathIndex));
        }
        if (rows.isEmpty()) {
            throw new ServiceException(message("ai.platform.doc.template.local.empty"));
        }
        return rows;
    }

    private boolean shouldIncludeLocalMarkdown(Path relativePath) {
        if (relativePath == null || relativePath.getNameCount() <= 0) {
            return false;
        }
        String firstSegment = normalizePathSegment(relativePath.getName(0).toString());
        if (EXCLUDED_ROOTS.contains(firstSegment)) {
            return false;
        }
        return true;
    }

    private List<AiPlatformDocTemplateRow> parseLocalMarkdownFile(Path srcRoot,
                                                                  Path filePath,
                                                                  Map<String, String> sectionMapping,
                                                                  MenuPathIndex menuPathIndex) {
        try {
            String rawMarkdown = Files.readString(filePath, StandardCharsets.UTF_8);
            String frontMatter = extractFrontMatter(rawMarkdown);
            String markdownBody = stripFrontMatter(rawMarkdown);
            String relativePath = srcRoot.relativize(filePath).toString().replace('\\', '/');
            String pageTitle = resolveLocalPageTitle(frontMatter, markdownBody, filePath);
            String sectionLevel1 = resolveSectionLevel1(relativePath, sectionMapping);
            String sectionPath = buildSectionPath(sectionLevel1, pageTitle);
            String sourceUrl = buildSourceUrl(relativePath);
            String docVersion = resolveLocalDocVersion(filePath);
            return buildMarkdownRows(markdownBody, new PageMeta(
                    sectionLevel1,
                    sectionPath,
                    pageTitle,
                    buildDefaultMenuPath(sectionLevel1, pageTitle),
                    relativePath,
                    sourceUrl,
                    SOURCE_LOCAL,
                    resolveLanguageByRelativePath(relativePath),
                    docVersion
            ), menuPathIndex);
        } catch (Exception ex) {
            throw new ServiceException(message("ai.platform.doc.template.local.parse.failed", filePath.getFileName(), ex.getMessage()));
        }
    }

    private String resolveLocalPageTitle(String frontMatter, String markdownBody, Path filePath) {
        String title = resolveFrontMatterTitle(frontMatter);
        if (StringUtils.isNotBlank(title)) {
            return cleanHeadingText(title);
        }
        Matcher headingMatcher = Pattern.compile("(?m)^#\\s+(.+?)\\s*$").matcher(markdownBody);
        if (headingMatcher.find()) {
            return cleanHeadingText(headingMatcher.group(1));
        }
        String fileName = filePath.getFileName().toString();
        if ("README.md".equalsIgnoreCase(fileName) && filePath.getParent() != null) {
            return humanizeSegment(filePath.getParent().getFileName().toString());
        }
        return humanizeSegment(fileName.replace(".md", ""));
    }

    private List<AiPlatformDocTemplateRow> parseWebRows(MenuPathIndex menuPathIndex) {
        List<String> urls = loadDocUrlsFromSitemap();
        if (urls.isEmpty()) {
            throw new ServiceException(message("ai.platform.doc.template.sitemap.empty"));
        }
        List<AiPlatformDocTemplateRow> rows = new ArrayList<>();
        for (String url : urls) {
            rows.addAll(parseWebPage(url, menuPathIndex));
        }
        if (rows.isEmpty()) {
            throw new ServiceException(message("ai.platform.doc.template.remote.empty"));
        }
        return rows;
    }

    private List<String> loadDocUrlsFromSitemap() {
        String xmlContent = fetchRemoteContent(DOC_SITEMAP_URL);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setExpandEntityReferences(false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            org.w3c.dom.Document xml = factory.newDocumentBuilder().parse(new org.xml.sax.InputSource(new StringReader(xmlContent)));
            org.w3c.dom.NodeList nodeList = xml.getElementsByTagName("loc");
            LinkedHashSet<String> urls = new LinkedHashSet<>();
            for (int index = 0; index < nodeList.getLength(); index++) {
                org.w3c.dom.Node node = nodeList.item(index);
                if (node == null || StringUtils.isBlank(node.getTextContent())) {
                    continue;
                }
                String url = node.getTextContent().trim();
                if (shouldIncludeWebUrl(url)) {
                    urls.add(url);
                }
            }
            return new ArrayList<>(urls);
        } catch (Exception ex) {
            throw new ServiceException(message("ai.platform.doc.template.sitemap.parse.failed", ex.getMessage()));
        }
    }

    private boolean shouldIncludeWebUrl(String url) {
        if (StringUtils.isBlank(url) || !url.startsWith(DOC_BASE_URL)) {
            return false;
        }
        String relative = url.substring(DOC_BASE_URL.length());
        if (relative.startsWith("internal/")) {
            return false;
        }
        return url.endsWith("/") || url.endsWith(".html");
    }

    private List<AiPlatformDocTemplateRow> parseWebPage(String url, MenuPathIndex menuPathIndex) {
        try {
            String html = fetchRemoteContent(url);
            Document document = Jsoup.parse(html, url);
            Element contentRoot = document.selectFirst("main#main-content .theme-hope-content");
            if (contentRoot == null) {
                contentRoot = document.selectFirst(".theme-hope-content");
            }
            if (contentRoot == null) {
                contentRoot = document.selectFirst("main#main-content");
            }
            if (contentRoot == null) {
                return Collections.emptyList();
            }

            String pageTitle = resolveWebPageTitle(document, contentRoot, url);
            String sectionLevel1 = resolveSectionLevel1ByUrl(url);
            String sectionPath = buildSectionPath(sectionLevel1, pageTitle);
            String docVersion = resolveWebDocVersion(document);

            StringBuilder markdownBuilder = new StringBuilder();
            for (Element child : contentRoot.children()) {
                appendWebElementAsMarkdown(markdownBuilder, child);
            }
            return buildMarkdownRows(markdownBuilder.toString(), new PageMeta(
                    sectionLevel1,
                    sectionPath,
                    pageTitle,
                    buildDefaultMenuPath(sectionLevel1, pageTitle),
                    "",
                    url,
                    SOURCE_WEB,
                    resolveLanguageByRelativePath(url.substring(DOC_BASE_URL.length())),
                    docVersion
            ), menuPathIndex);
        } catch (Exception ex) {
            throw new ServiceException(message("ai.platform.doc.template.remote.parse.failed", url, ex.getMessage()));
        }
    }

    private String resolveWebPageTitle(Document document, Element contentRoot, String url) {
        Element titleMeta = document.selectFirst("meta[property=og:title]");
        if (titleMeta != null && StringUtils.isNotBlank(titleMeta.attr("content"))) {
            return cleanHeadingText(titleMeta.attr("content"));
        }
        Element h1 = document.selectFirst("main#main-content .vp-page-title h1");
        if (h1 != null && StringUtils.isNotBlank(h1.text())) {
            return cleanHeadingText(h1.text());
        }
        Element firstHeading = contentRoot.selectFirst("h1,h2");
        if (firstHeading != null && StringUtils.isNotBlank(firstHeading.text())) {
            return cleanHeadingText(firstHeading.text());
        }
        return resolveSectionLevel1ByUrl(url);
    }

    private String resolveWebDocVersion(Document document) {
        Element updatedMeta = document.selectFirst("meta[property=og:updated_time]");
        if (updatedMeta != null && StringUtils.isNotBlank(updatedMeta.attr("content"))) {
            return updatedMeta.attr("content").trim();
        }
        return FALLBACK_VERSION;
    }

    private void appendWebElementAsMarkdown(StringBuilder builder, Element element) {
        if (element == null) {
            return;
        }
        String tagName = element.tagName().toLowerCase(Locale.ROOT);
        if ("h2".equals(tagName)) {
            builder.append("## ").append(element.text()).append('\n');
            return;
        }
        if ("h3".equals(tagName)) {
            builder.append("### ").append(element.text()).append('\n');
            return;
        }
        if ("h4".equals(tagName)) {
            builder.append("#### ").append(element.text()).append('\n');
            return;
        }
        if ("table".equals(tagName)) {
            for (Element row : element.select("tr")) {
                List<String> cells = row.select("th,td").stream()
                        .map(Element::text)
                        .map(this::normalizeWhitespace)
                        .filter(StringUtils::isNotBlank)
                        .toList();
                if (!cells.isEmpty()) {
                    builder.append("| ").append(String.join(" | ", cells)).append(" |").append('\n');
                }
            }
            builder.append('\n');
            return;
        }
        if ("ul".equals(tagName) || "ol".equals(tagName)) {
            for (Element li : element.children()) {
                if (!"li".equalsIgnoreCase(li.tagName())) {
                    continue;
                }
                builder.append("- ").append(li.text()).append('\n');
            }
            builder.append('\n');
            return;
        }
        if (tagName.startsWith("div") && element.classNames().stream().anyMatch(name -> name.contains("hint-container"))) {
            String tipText = element.select("p").eachText().stream()
                    .map(this::normalizeWhitespace)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("\n"));
            if (StringUtils.isNotBlank(tipText)) {
                builder.append("::: tip").append('\n').append(tipText).append('\n').append(":::").append('\n');
            }
            return;
        }
        if ("p".equals(tagName)) {
            String text = normalizeWhitespace(element.text());
            if (StringUtils.isNotBlank(text)) {
                builder.append(text).append('\n');
            }
        }
    }

    private List<AiPlatformDocTemplateRow> buildMarkdownRows(String markdownBody, PageMeta pageMeta, MenuPathIndex menuPathIndex) {
        List<MarkdownSection> sections = splitMarkdownSections(markdownBody);
        if (sections.isEmpty()) {
            sections = Collections.singletonList(new MarkdownSection(Collections.emptyList(), markdownBody));
        }
        List<AiPlatformDocTemplateRow> rows = new ArrayList<>();
        int rowIndex = 0;
        for (MarkdownSection section : sections) {
            String normalizedContent = normalizeMarkdownContent(section.content());
            String tips = extractTipContent(section.content());
            if (StringUtils.isBlank(normalizedContent) && StringUtils.isBlank(tips) && section.headingStack().isEmpty()) {
                continue;
            }
            AiPlatformDocTemplateRow row = buildTemplateRow(pageMeta, section, normalizedContent, tips, rowIndex++, menuPathIndex);
            if (row != null) {
                rows.add(row);
            }
        }
        return rows;
    }

    private List<MarkdownSection> splitMarkdownSections(String markdownBody) {
        if (StringUtils.isBlank(markdownBody)) {
            return Collections.emptyList();
        }
        List<MarkdownSection> sections = new ArrayList<>();
        List<String> headingStack = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (String rawLine : markdownBody.replace("\r\n", "\n").replace('\r', '\n').split("\n")) {
            String line = rawLine == null ? "" : rawLine;
            Matcher matcher = HEADING_PATTERN.matcher(line.trim());
            if (matcher.matches()) {
                flushSection(sections, headingStack, buffer);
                int level = matcher.group(1).length();
                String headingText = cleanHeadingText(matcher.group(2));
                while (headingStack.size() >= level - 1 && !headingStack.isEmpty()) {
                    headingStack.remove(headingStack.size() - 1);
                }
                headingStack.add(headingText);
                continue;
            }
            buffer.append(line).append('\n');
        }
        flushSection(sections, headingStack, buffer);
        return sections;
    }

    private void flushSection(List<MarkdownSection> sections, List<String> headingStack, StringBuilder buffer) {
        String content = buffer.toString().trim();
        if (StringUtils.isNotBlank(content) || !headingStack.isEmpty()) {
            sections.add(new MarkdownSection(new ArrayList<>(headingStack), content));
        }
        buffer.setLength(0);
    }

    private AiPlatformDocTemplateRow buildTemplateRow(PageMeta pageMeta,
                                                      MarkdownSection section,
                                                      String normalizedContent,
                                                      String tipContent,
                                                      int rowIndex,
                                                      MenuPathIndex menuPathIndex) {
        String headingPath = buildHeadingPath(pageMeta.pageTitle(), section.headingStack());
        List<String> relatedDocs = extractRelatedDocs(section.content(), pageMeta.sourceUrl());
        LinkedHashSet<String> cautionLines = new LinkedHashSet<>(extractSentences(tipContent, NOTICE_KEYWORDS, false));
        cautionLines.addAll(extractSentences(normalizedContent, NOTICE_KEYWORDS, false));
        LinkedHashSet<String> preconditions = new LinkedHashSet<>(extractSentences(normalizedContent, PRECONDITION_KEYWORDS, true));
        LinkedHashSet<String> steps = new LinkedHashSet<>(extractActionSteps(normalizedContent));
        LinkedHashSet<String> results = new LinkedHashSet<>(extractSentences(normalizedContent, RESULT_KEYWORDS, true));
        LinkedHashSet<String> targetRoles = new LinkedHashSet<>(extractRoles(normalizedContent + "\n" + tipContent));

        String knowledgeType = resolveKnowledgeType(headingPath, normalizedContent, tipContent);
        String normalizedHeading = stripHeadingNumbering(lastSegment(headingPath));
        String pageSubject = resolvePageSubject(pageMeta.pageTitle());
        LinkedHashSet<String> tags = buildTags(pageMeta, normalizedHeading, normalizedContent, knowledgeType);
        LinkedHashSet<String> aliases = buildAliases(pageMeta, normalizedHeading, pageSubject, knowledgeType);
        String bodySupplement = buildBodySupplement(normalizedContent, preconditions, steps, results, cautionLines);
        String menuPath = resolveMenuPath(pageMeta, normalizedHeading, menuPathIndex);

        AiPlatformDocTemplateRow row = new AiPlatformDocTemplateRow();
        row.setSectionLevel1(pageMeta.sectionLevel1());
        row.setSectionPath(pageMeta.sectionPath());
        row.setPageTitle(pageMeta.pageTitle());
        row.setHeadingPath(headingPath);
        row.setKnowledgeType(knowledgeType);
        row.setMenuPath(menuPath);
        row.setTargetRole(joinValues(targetRoles));
        row.setPreconditions(joinValues(preconditions));
        row.setActionSteps(joinValues(steps));
        row.setResultDesc(joinValues(results));
        row.setCautions(joinValues(cautionLines));
        row.setContent(bodySupplement);
        row.setTags(joinValues(tags));
        row.setAliases(joinValues(aliases));
        row.setRelatedDocs(String.join(";", relatedDocs));
        row.setSourceFile(pageMeta.sourceFile());
        row.setSourceUrl(pageMeta.sourceUrl());
        row.setSourceType(pageMeta.sourceType());
        row.setLanguage(pageMeta.language());
        row.setDocVersion(pageMeta.docVersion());
        row.setRemark(buildRemark(pageMeta, rowIndex));
        if (StringUtils.isBlank(row.getHeadingPath())
                && StringUtils.isBlank(row.getContent())
                && StringUtils.isBlank(row.getActionSteps())
                && StringUtils.isBlank(row.getCautions())) {
            return null;
        }
        return row;
    }

    private String buildHeadingPath(String pageTitle, List<String> headingStack) {
        List<String> pathSegments = new ArrayList<>();
        if (StringUtils.isNotBlank(pageTitle)) {
            pathSegments.add(cleanHeadingText(pageTitle));
        }
        if (headingStack != null) {
            headingStack.stream()
                    .map(this::cleanHeadingText)
                    .filter(StringUtils::isNotBlank)
                    .forEach(pathSegments::add);
        }
        return String.join("/", pathSegments);
    }

    private String extractFrontMatter(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }
        Matcher matcher = FRONT_MATTER_PATTERN.matcher(markdown);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String stripFrontMatter(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }
        return FRONT_MATTER_PATTERN.matcher(markdown).replaceFirst("");
    }

    private String resolveFrontMatterTitle(String frontMatter) {
        if (StringUtils.isBlank(frontMatter)) {
            return "";
        }
        Matcher matcher = TITLE_PATTERN.matcher(frontMatter);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private String normalizeMarkdownContent(String rawContent) {
        if (StringUtils.isBlank(rawContent)) {
            return "";
        }
        String normalized = CODE_FENCE_PATTERN.matcher(rawContent).replaceAll("$1");
        normalized = TIP_BLOCK_PATTERN.matcher(normalized).replaceAll("");
        List<String> lines = new ArrayList<>();
        for (String rawLine : normalized.replace("\r\n", "\n").replace('\r', '\n').split("\n")) {
            String line = rawLine;
            if (StringUtils.isBlank(line)) {
                continue;
            }
            line = IMAGE_PATTERN.matcher(line).replaceAll(" ");
            line = HTML_BREAK_PATTERN.matcher(line).replaceAll("\n");
            line = convertMarkdownLinks(line);
            line = convertHtmlLinks(line);
            line = stripInlineMarkdown(line);
            if (line.contains("|")) {
                String tableLine = convertMarkdownTableLine(line);
                if (StringUtils.isNotBlank(tableLine)) {
                    lines.add(tableLine);
                }
                continue;
            }
            line = decodeHtmlEntities(line);
            line = normalizeWhitespace(line);
            if (StringUtils.isNotBlank(line)) {
                lines.add(line);
            }
        }
        return String.join("\n", lines).trim();
    }

    private String extractTipContent(String rawContent) {
        if (StringUtils.isBlank(rawContent)) {
            return "";
        }
        List<String> tips = new ArrayList<>();
        Matcher matcher = TIP_BLOCK_PATTERN.matcher(rawContent);
        while (matcher.find()) {
            String tip = matcher.group(1);
            String normalized = normalizeMarkdownContent(tip);
            if (StringUtils.isNotBlank(normalized)) {
                tips.add(normalized);
            }
        }
        return String.join("\n", tips);
    }

    private List<String> extractRelatedDocs(String rawContent, String sourceUrl) {
        LinkedHashSet<String> links = new LinkedHashSet<>();
        if (StringUtils.isBlank(rawContent)) {
            return Collections.emptyList();
        }
        Matcher markdownMatcher = MARKDOWN_LINK_PATTERN.matcher(rawContent);
        while (markdownMatcher.find()) {
            String text = normalizeWhitespace(markdownMatcher.group(1));
            String href = normalizeRelatedLink(markdownMatcher.group(2), sourceUrl);
            if (StringUtils.isNotBlank(text) && StringUtils.isNotBlank(href)) {
                links.add(text + "=" + href);
            }
        }
        Matcher htmlMatcher = HTML_LINK_PATTERN.matcher(rawContent);
        while (htmlMatcher.find()) {
            String text = normalizeWhitespace(stripHtml(htmlMatcher.group(2)));
            String href = normalizeRelatedLink(htmlMatcher.group(1), sourceUrl);
            if (StringUtils.isNotBlank(text) && StringUtils.isNotBlank(href)) {
                links.add(text + "=" + href);
            }
        }
        return new ArrayList<>(links);
    }

    private String normalizeRelatedLink(String href, String sourceUrl) {
        if (StringUtils.isBlank(href)) {
            return "";
        }
        String actualHref = href.trim();
        if (actualHref.startsWith("http://") || actualHref.startsWith("https://")) {
            return actualHref;
        }
        if (actualHref.startsWith("/doc/")) {
            return "https://fastbee.cn" + actualHref;
        }
        if (actualHref.startsWith("./") || actualHref.startsWith("../")) {
            try {
                URI sourceUri = URI.create(StringUtils.isBlank(sourceUrl) ? DOC_BASE_URL : sourceUrl);
                return sourceUri.resolve(actualHref).toString();
            } catch (Exception ex) {
                return actualHref;
            }
        }
        return actualHref;
    }

    private LinkedHashSet<String> extractActionSteps(String normalizedContent) {
        LinkedHashSet<String> steps = new LinkedHashSet<>();
        for (String sentence : splitSentences(normalizedContent)) {
            if (containsKeyword(sentence, ACTION_KEYWORDS)) {
                steps.add(sentence);
            }
        }
        return steps;
    }

    private LinkedHashSet<String> extractRoles(String content) {
        LinkedHashSet<String> roles = new LinkedHashSet<>();
        for (String keyword : ROLE_KEYWORDS) {
            if (StringUtils.contains(content, keyword)) {
                roles.add(keyword);
            }
        }
        return roles;
    }

    private List<String> extractSentences(String content, Set<String> keywords, boolean allowPartial) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (String sentence : splitSentences(content)) {
            if (StringUtils.isBlank(sentence)) {
                continue;
            }
            if (containsKeyword(sentence, keywords)) {
                result.add(sentence);
                continue;
            }
            if (allowPartial && sentence.length() <= 32 && containsKeyword(content, Collections.singleton(sentence))) {
                result.add(sentence);
            }
        }
        return result;
    }

    private boolean containsKeyword(String text, Set<String> keywords) {
        if (StringUtils.isBlank(text) || keywords == null || keywords.isEmpty()) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String buildBodySupplement(String normalizedContent,
                                       Set<String> preconditions,
                                       Set<String> steps,
                                       Set<String> results,
                                       Set<String> cautions) {
        List<String> remaining = new ArrayList<>();
        for (String sentence : splitSentences(normalizedContent)) {
            if (preconditions.contains(sentence) || steps.contains(sentence) || results.contains(sentence) || cautions.contains(sentence)) {
                continue;
            }
            remaining.add(sentence);
        }
        if (remaining.isEmpty()) {
            return normalizedContent;
        }
        return String.join("\n", remaining);
    }

    private String resolveKnowledgeType(String headingPath, String normalizedContent, String tipContent) {
        String combined = defaultString(headingPath) + "\n" + defaultString(normalizedContent) + "\n" + defaultString(tipContent);
        if (containsAny(combined, "常见问题", "FAQ", "后续优化")) {
            return "FAQ";
        }
        if (containsAny(combined, "权限", "机构", "角色", "租户")) {
            return "PERMISSION";
        }
        if (containsKeyword(lastSegment(headingPath), STEP_HEADING_KEYWORDS)
                || (containsAny(normalizedContent, "点击") && containsAny(normalizedContent, "按钮"))) {
            return "STEP";
        }
        if (containsAny(combined, "注意", "限制", "失败", "异常") || StringUtils.isNotBlank(tipContent)) {
            return "NOTICE";
        }
        if (containsAny(combined, "概述", "介绍", "简介", "定义", "作用", "特点", "说明")) {
            return "OVERVIEW";
        }
        if (containsAny(combined, "配置项", "配置", "参数", "设置", "规则")) {
            return "CONFIG";
        }
        if (containsKeyword(combined, ACTION_KEYWORDS)) {
            return "STEP";
        }
        return "GUIDE";
    }

    private LinkedHashSet<String> buildTags(PageMeta pageMeta, String normalizedHeading, String normalizedContent, String knowledgeType) {
        LinkedHashSet<String> tags = new LinkedHashSet<>();
        addTag(tags, pageMeta.sectionLevel1());
        addTag(tags, pageMeta.pageTitle());
        addTag(tags, normalizedHeading);
        addTag(tags, knowledgeType);
        String subject = resolvePageSubject(pageMeta.pageTitle());
        addTag(tags, subject);
        for (String keyword : ACTION_KEYWORDS) {
            if (StringUtils.contains(defaultString(normalizedHeading) + defaultString(normalizedContent), keyword)) {
                addTag(tags, keyword);
            }
        }
        return tags;
    }

    private LinkedHashSet<String> buildAliases(PageMeta pageMeta,
                                               String normalizedHeading,
                                               String pageSubject,
                                               String knowledgeType) {
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        String pageTitle = cleanHeadingText(pageMeta.pageTitle());
        if (StringUtils.isNotBlank(pageTitle)) {
            aliases.add(pageTitle + "怎么操作");
        }
        if (StringUtils.isNotBlank(normalizedHeading)) {
            aliases.add(pageTitle + normalizedHeading);
        }
        if ("STEP".equals(knowledgeType)) {
            if (containsAny(normalizedHeading, "新增", "创建", "添加")) {
                aliases.add("怎么新增" + pageSubject);
                aliases.add("如何创建" + pageSubject);
                aliases.add(pageSubject + "怎么创建");
            } else if (containsAny(normalizedHeading, "导入")) {
                aliases.add("怎么导入" + pageSubject);
                aliases.add(pageSubject + "如何导入");
            } else if (containsAny(normalizedHeading, "导出", "下载")) {
                aliases.add("怎么下载" + pageSubject);
                aliases.add(pageSubject + "如何导出");
            } else if (containsAny(normalizedHeading, "发布")) {
                aliases.add("怎么发布" + pageSubject);
                aliases.add(pageSubject + "如何发布");
            } else if (containsAny(normalizedHeading, "分配")) {
                aliases.add("怎么分配" + pageSubject);
                aliases.add(pageSubject + "如何分配");
            } else if (containsAny(normalizedHeading, "回收")) {
                aliases.add("怎么回收" + pageSubject);
            } else if (StringUtils.isNotBlank(normalizedHeading)) {
                aliases.add("怎么" + normalizedHeading);
                aliases.add("如何" + normalizedHeading);
            }
        }
        if ("CONFIG".equals(knowledgeType)) {
            aliases.add(pageSubject + "有哪些配置项");
            aliases.add(pageSubject + "怎么配置");
        }
        if ("NOTICE".equals(knowledgeType)) {
            aliases.add(pageSubject + "有哪些注意事项");
        }
        if ("PERMISSION".equals(knowledgeType)) {
            aliases.add(pageSubject + "权限怎么划分");
        }
        return aliases.stream()
                .map(this::normalizeWhitespace)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String resolvePageSubject(String pageTitle) {
        String actualTitle = cleanHeadingText(pageTitle);
        if (StringUtils.isBlank(actualTitle)) {
            return "该功能";
        }
        String normalized = actualTitle.replace("管理", "")
                .replace("配置", "")
                .replace("模块", "")
                .replace("文档", "")
                .replace("教程", "")
                .trim();
        return StringUtils.isBlank(normalized) ? actualTitle : normalized;
    }

    private MenuPathIndex buildMenuPathIndex() {
        try {
            SysMenu query = new SysMenu();
            query.setVisible("0");
            query.setStatus(0);
            query.setLanguage(DEFAULT_LANGUAGE);
            List<SysMenu> menus = sysMenuMapper.selectMenuList(query);
            if (menus == null || menus.isEmpty()) {
                return MenuPathIndex.empty();
            }
            Map<Long, SysMenu> menuMap = menus.stream()
                    .filter(menu -> menu != null && menu.getMenuId() != null)
                    .collect(Collectors.toMap(SysMenu::getMenuId, menu -> menu, (left, right) -> left, LinkedHashMap::new));
            List<MenuPathCandidate> candidates = new ArrayList<>();
            for (SysMenu menu : menus) {
                if (!isNavigableMenu(menu)) {
                    continue;
                }
                String menuName = cleanHeadingText(menu.getMenuName());
                String menuPath = buildSystemMenuPath(menu, menuMap);
                if (StringUtils.isBlank(menuName) || StringUtils.isBlank(menuPath)) {
                    continue;
                }
                String normalizedName = normalizeMenuMatchValue(menuName);
                String normalizedSubject = normalizeMenuMatchValue(resolvePageSubject(menuName));
                int depth = menuPath.split("/").length;
                candidates.add(new MenuPathCandidate(menuName, menuPath, menu.getMenuType(), normalizedName, normalizedSubject, depth));
            }
            return candidates.isEmpty() ? MenuPathIndex.empty() : new MenuPathIndex(candidates);
        } catch (Exception ex) {
            // 菜单路径只是企业模板增强信息，读取菜单异常时保留文档标题兜底，避免影响模板下载。
            return MenuPathIndex.empty();
        }
    }

    private boolean isNavigableMenu(SysMenu menu) {
        if (menu == null || StringUtils.isBlank(menu.getMenuName())) {
            return false;
        }
        return "M".equals(menu.getMenuType()) || "C".equals(menu.getMenuType());
    }

    private String buildSystemMenuPath(SysMenu menu, Map<Long, SysMenu> menuMap) {
        if (menu == null || menuMap == null || menuMap.isEmpty()) {
            return "";
        }
        LinkedHashSet<Long> visited = new LinkedHashSet<>();
        List<String> segments = new ArrayList<>();
        SysMenu current = menu;
        while (current != null && current.getMenuId() != null && visited.add(current.getMenuId())) {
            String menuName = cleanHeadingText(current.getMenuName());
            if (StringUtils.isNotBlank(menuName)) {
                segments.add(0, menuName);
            }
            Long parentId = current.getParentId();
            if (parentId == null || parentId <= 0) {
                break;
            }
            current = menuMap.get(parentId);
        }
        return String.join("/", segments);
    }

    private String resolveMenuPath(PageMeta pageMeta, String normalizedHeading, MenuPathIndex menuPathIndex) {
        String fallbackMenuPath = containsAny(normalizedHeading, "列表", "详情", "配置", "新增", "导入", "导出", "发布")
                ? pageMeta.pageTitle()
                : pageMeta.defaultMenuPath();
        String systemMenuPath = resolveSystemMenuPath(pageMeta, normalizedHeading, menuPathIndex);
        return StringUtils.isNotBlank(systemMenuPath) ? systemMenuPath : fallbackMenuPath;
    }

    private String resolveSystemMenuPath(PageMeta pageMeta, String normalizedHeading, MenuPathIndex menuPathIndex) {
        if (menuPathIndex == null || menuPathIndex.isEmpty()) {
            return "";
        }
        List<MenuSearchTerm> searchTerms = buildMenuSearchTerms(pageMeta, normalizedHeading);
        if (searchTerms.isEmpty()) {
            return "";
        }
        MenuPathCandidate bestCandidate = null;
        int bestScore = 0;
        for (MenuPathCandidate candidate : menuPathIndex.candidates()) {
            int score = calculateMenuPathMatchScore(candidate, searchTerms);
            if ("C".equals(candidate.menuType())) {
                score += 6;
            }
            score += Math.min(candidate.depth(), 4);
            if (score > bestScore) {
                bestScore = score;
                bestCandidate = candidate;
            }
        }
        return bestCandidate != null && bestScore >= 72 ? bestCandidate.menuPath() : "";
    }

    private List<MenuSearchTerm> buildMenuSearchTerms(PageMeta pageMeta, String normalizedHeading) {
        List<MenuSearchTerm> terms = new ArrayList<>();
        addMenuSearchTerm(terms, pageMeta == null ? "" : pageMeta.pageTitle(), 120, 55);
        addMenuSearchTerm(terms, resolvePageSubject(pageMeta == null ? "" : pageMeta.pageTitle()), 96, 40);
        addMenuSearchTerm(terms, normalizedHeading, 46, 18);
        addMenuSearchTerm(terms, resolvePageSubject(normalizedHeading), 34, 14);
        return terms;
    }

    private void addMenuSearchTerm(List<MenuSearchTerm> terms, String value, int exactScore, int containsScore) {
        String normalizedValue = normalizeMenuMatchValue(value);
        if (StringUtils.isBlank(normalizedValue) || "该功能".equals(value)) {
            return;
        }
        boolean exists = terms.stream().anyMatch(term -> term.value().equals(normalizedValue));
        if (!exists) {
            terms.add(new MenuSearchTerm(normalizedValue, exactScore, containsScore));
        }
    }

    private int calculateMenuPathMatchScore(MenuPathCandidate candidate, List<MenuSearchTerm> searchTerms) {
        if (candidate == null || searchTerms == null || searchTerms.isEmpty()) {
            return 0;
        }
        int score = 0;
        for (MenuSearchTerm term : searchTerms) {
            score += scoreMenuTerm(candidate.normalizedName(), term.value(), term.exactScore(), term.containsScore());
            score += scoreMenuTerm(candidate.normalizedSubject(), term.value(), Math.max(0, term.exactScore() - 12), Math.max(0, term.containsScore() - 8));
        }
        return score;
    }

    private int scoreMenuTerm(String candidate, String term, int exactScore, int containsScore) {
        if (StringUtils.isBlank(candidate) || StringUtils.isBlank(term)) {
            return 0;
        }
        if (candidate.equals(term)) {
            return exactScore;
        }
        if (candidate.contains(term) || term.contains(candidate)) {
            return containsScore;
        }
        return 0;
    }

    private String normalizeMenuMatchValue(String value) {
        return cleanHeadingText(value)
                .replaceAll("[\\s/\\\\|｜>＞\\-—_:：,，.。()（）\\[\\]【】]+", "")
                .toLowerCase(Locale.ROOT);
    }

    private String buildRemark(PageMeta pageMeta, int rowIndex) {
        StringBuilder remark = new StringBuilder();
        remark.append("导出来源=").append(pageMeta.sourceType());
        remark.append("；导出顺序=").append(rowIndex + 1);
        if (StringUtils.isNotBlank(pageMeta.sourceFile())) {
            remark.append("；来源文件=").append(pageMeta.sourceFile());
        }
        if (StringUtils.isNotBlank(pageMeta.sourceUrl())) {
            remark.append("；来源链接=").append(pageMeta.sourceUrl());
        }
        return remark.toString();
    }

    private String resolveSectionLevel1(String relativePath, Map<String, String> sectionMapping) {
        if (StringUtils.isBlank(relativePath)) {
            return "文档首页";
        }
        String[] segments = relativePath.split("/");
        if (segments.length <= 0) {
            return "文档首页";
        }
        String firstSegment = normalizePathSegment(segments[0]);
        if ("README.md".equalsIgnoreCase(firstSegment)) {
            return "文档首页";
        }
        String compoundKey = segments.length >= 2 ? firstSegment + "/" + normalizePathSegment(segments[1]) : firstSegment;
        if (sectionMapping.containsKey(compoundKey)) {
            return sectionMapping.get(compoundKey);
        }
        if (sectionMapping.containsKey(firstSegment)) {
            return sectionMapping.get(firstSegment);
        }
        return DEFAULT_SECTION_MAPPING.getOrDefault(firstSegment, humanizeSegment(firstSegment));
    }

    private String resolveLanguageByRelativePath(String relativePath) {
        if (StringUtils.isBlank(relativePath)) {
            return DEFAULT_LANGUAGE;
        }
        String normalizedPath = relativePath.replace("\\", "/").toLowerCase(Locale.ROOT);
        return normalizedPath.startsWith("en/") ? ENGLISH_LANGUAGE : DEFAULT_LANGUAGE;
    }

    private String resolveSectionLevel1ByUrl(String url) {
        if (StringUtils.isBlank(url) || !url.startsWith(DOC_BASE_URL)) {
            return "文档首页";
        }
        String relative = url.substring(DOC_BASE_URL.length());
        if (StringUtils.isBlank(relative)) {
            return "文档首页";
        }
        String[] segments = relative.split("/");
        String firstSegment = segments.length > 0 ? normalizePathSegment(segments[0]) : "";
        String compoundKey = segments.length >= 2 ? firstSegment + "/" + normalizePathSegment(segments[1]) : firstSegment;
        if (DEFAULT_SECTION_MAPPING.containsKey(compoundKey)) {
            return DEFAULT_SECTION_MAPPING.get(compoundKey);
        }
        return DEFAULT_SECTION_MAPPING.getOrDefault(firstSegment, humanizeSegment(firstSegment));
    }

    private Map<String, String> buildSectionMapping(Path sidebarFile) {
        Map<String, String> mapping = new LinkedHashMap<>(DEFAULT_SECTION_MAPPING);
        if (sidebarFile == null || !Files.exists(sidebarFile)) {
            return mapping;
        }
        try {
            String content = Files.readString(sidebarFile, StandardCharsets.UTF_8);
            String currentRoute = "/";
            String currentText = null;
            for (String rawLine : content.split("\n")) {
                String line = rawLine.trim();
                if (line.startsWith("\"/")) {
                    int endIndex = line.indexOf('"', 1);
                    if (endIndex > 1) {
                        currentRoute = line.substring(1, endIndex);
                    }
                    continue;
                }
                if (line.startsWith("text:")) {
                    currentText = stripQuotes(extractAfterColon(line));
                    continue;
                }
                if (line.startsWith("prefix:")) {
                    String prefix = stripQuotes(extractAfterColon(line)).replace("\"", "").replace("'", "");
                    prefix = prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
                    if (StringUtils.isBlank(prefix) || StringUtils.isBlank(currentText)) {
                        continue;
                    }
                    if ("/".equals(currentRoute)) {
                        mapping.put(normalizePathSegment(prefix), currentText);
                    } else {
                        String routeKey = currentRoute.replace("/", "");
                        mapping.put(routeKey + "/" + normalizePathSegment(prefix), currentText);
                    }
                }
            }
        } catch (Exception ex) {
            return mapping;
        }
        return mapping;
    }

    private String fetchRemoteContent(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .header("User-Agent", "FastBee-AI-Platform-Doc-Exporter")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ServiceException(message("ai.platform.doc.template.remote.request.failed", response.statusCode()));
            }
            return response.body();
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(message("ai.platform.doc.template.remote.read.failed", ex.getMessage()));
        }
    }

    private String resolveLocalDocVersion(Path filePath) {
        try {
            FileTime fileTime = Files.getLastModifiedTime(filePath);
            return DATE_FORMATTER.format(fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } catch (Exception ex) {
            return FALLBACK_VERSION;
        }
    }

    private String buildSourceUrl(String relativePath) {
        if (StringUtils.isBlank(relativePath) || "README.md".equalsIgnoreCase(relativePath)) {
            return DOC_BASE_URL;
        }
        String actualPath = relativePath.replace('\\', '/');
        if (actualPath.endsWith("/README.md")) {
            return DOC_BASE_URL + actualPath.substring(0, actualPath.length() - "README.md".length());
        }
        if (actualPath.endsWith(".md")) {
            return DOC_BASE_URL + actualPath.substring(0, actualPath.length() - 3) + ".html";
        }
        return DOC_BASE_URL + actualPath;
    }

    private String buildSectionPath(String sectionLevel1, String pageTitle) {
        if (StringUtils.isBlank(pageTitle)) {
            return defaultString(sectionLevel1);
        }
        if (StringUtils.isBlank(sectionLevel1) || "文档首页".equals(sectionLevel1)) {
            return pageTitle;
        }
        return sectionLevel1 + "/" + pageTitle;
    }

    private String buildDefaultMenuPath(String sectionLevel1, String pageTitle) {
        if (StringUtils.isBlank(pageTitle)) {
            return defaultString(sectionLevel1);
        }
        return pageTitle;
    }

    private String convertMarkdownLinks(String line) {
        return MARKDOWN_LINK_PATTERN.matcher(line).replaceAll("$1");
    }

    private String convertHtmlLinks(String line) {
        return HTML_LINK_PATTERN.matcher(line).replaceAll("$2");
    }

    private String stripInlineMarkdown(String line) {
        String actualLine = defaultString(line);
        actualLine = actualLine.replace("**", "").replace("__", "").replace("`", "");
        actualLine = actualLine.replace("*", "");
        actualLine = HTML_TAG_PATTERN.matcher(actualLine).replaceAll(" ");
        return actualLine;
    }

    private String convertMarkdownTableLine(String line) {
        String actualLine = defaultString(line).trim();
        if (!actualLine.contains("|")) {
            return normalizeWhitespace(actualLine);
        }
        String normalized = actualLine.replaceAll("^\\|", "").replaceAll("\\|$", "");
        if (normalized.replace("-", "").replace(":", "").trim().isEmpty()) {
            return "";
        }
        List<String> cells = Arrays.stream(normalized.split("\\|"))
                .map(this::decodeHtmlEntities)
                .map(this::normalizeWhitespace)
                .filter(StringUtils::isNotBlank)
                .toList();
        if (cells.isEmpty()) {
            return "";
        }
        if (cells.size() == 2) {
            return cells.get(0) + "：" + cells.get(1);
        }
        return String.join("；", cells);
    }

    private String decodeHtmlEntities(String text) {
        String actualText = defaultString(text);
        return HTML_ENTITY_PATTERN.matcher(actualText).replaceAll(match -> switch (match.group()) {
            case "&quot;" -> "\"";
            case "&nbsp;" -> " ";
            case "&amp;" -> "&";
            case "&lt;" -> "<";
            case "&gt;" -> ">";
            default -> match.group();
        });
    }

    private List<String> splitSentences(String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        return Arrays.stream(content.replace("\r\n", "\n")
                        .replace('\r', '\n')
                        .split("[\\n。！？；]+"))
                .map(this::normalizeWhitespace)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .toList();
    }

    private void addTag(Set<String> tags, String value) {
        String actualValue = normalizeWhitespace(value);
        if (StringUtils.isBlank(actualValue)) {
            return;
        }
        tags.add(stripHeadingNumbering(actualValue));
    }

    private String lastSegment(String path) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        int index = path.lastIndexOf('/');
        return index >= 0 ? path.substring(index + 1) : path;
    }

    private String stripHeadingNumbering(String text) {
        String actualText = normalizeWhitespace(text);
        actualText = actualText.replaceAll("^[一二三四五六七八九十]+、", "");
        actualText = actualText.replaceAll("^\\d+(?:\\.\\d+)*[、.]", "");
        actualText = actualText.replaceAll("^\\d+(?:\\.\\d+)*", "");
        return normalizeWhitespace(actualText);
    }

    private String cleanHeadingText(String text) {
        return normalizeWhitespace(stripHtml(defaultString(text))
                .replace("&quot;", "\"")
                .replace("&nbsp;", " "));
    }

    private String stripHtml(String html) {
        return HTML_TAG_PATTERN.matcher(defaultString(html)).replaceAll(" ");
    }

    private String normalizeWhitespace(String text) {
        String actualText = defaultString(text)
                .replace('\u00A0', ' ')
                .replace('\t', ' ')
                .replace("\r\n", "\n")
                .replace('\r', '\n');
        return actualText.replaceAll("[ \\u3000]+", " ").replaceAll("\\n{2,}", "\n").trim();
    }

    private boolean containsAny(String text, String... keywords) {
        if (StringUtils.isBlank(text) || keywords == null || keywords.length == 0) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String stripQuotes(String value) {
        String actualValue = defaultString(value).trim();
        if ((actualValue.startsWith("\"") && actualValue.endsWith("\""))
                || (actualValue.startsWith("'") && actualValue.endsWith("'"))) {
            return actualValue.substring(1, actualValue.length() - 1);
        }
        return actualValue;
    }

    private String extractAfterColon(String line) {
        int index = line.indexOf(':');
        if (index < 0) {
            return "";
        }
        String actual = line.substring(index + 1).trim();
        if (actual.endsWith(",")) {
            actual = actual.substring(0, actual.length() - 1);
        }
        return actual;
    }

    private String normalizePathSegment(String value) {
        return defaultString(value).replace("\\", "/").replace("/", "").trim().toLowerCase(Locale.ROOT);
    }

    private String humanizeSegment(String value) {
        String actualValue = defaultString(value).replace(".md", "").replace('_', ' ').replace('-', ' ');
        if ("readme".equalsIgnoreCase(actualValue)) {
            return "文档首页";
        }
        return actualValue.trim();
    }

    private String joinValues(Set<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream()
                .map(this::normalizeWhitespace)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(";"));
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private static Map<String, String> createDefaultSectionMapping() {
        Map<String, String> mapping = new LinkedHashMap<>();
        mapping.put("", "文档首页");
        mapping.put("case", "案例展示");
        mapping.put("cluster", "服务集群");
        mapping.put("cluster/server", "服务集群");
        mapping.put("cluster/web", "Web集群");
        mapping.put("device", "硬件设备");
        mapping.put("en", "英文文档");
        mapping.put("video", "视频中心");
        mapping.put("firmware", "硬件开发");
        mapping.put("intro", "产品简介");
        mapping.put("install", "安装部署");
        mapping.put("link", "设备接入");
        mapping.put("manual", "操作手册");
        mapping.put("start", "快速上手");
        mapping.put("dev", "开发文档");
        mapping.put("relate", "相关文档");
        mapping.put("sponsor", "授权页面");
        return mapping;
    }

    private record PageMeta(String sectionLevel1,
                            String sectionPath,
                            String pageTitle,
                            String defaultMenuPath,
                            String sourceFile,
                            String sourceUrl,
                            String sourceType,
                            String language,
                            String docVersion) {
    }

    private record MarkdownSection(List<String> headingStack, String content) {
    }

    private record MenuPathIndex(List<MenuPathCandidate> candidates) {

        private static MenuPathIndex empty() {
            return new MenuPathIndex(Collections.emptyList());
        }

        private boolean isEmpty() {
            return candidates == null || candidates.isEmpty();
        }
    }

    private record MenuPathCandidate(String menuName,
                                     String menuPath,
                                     String menuType,
                                     String normalizedName,
                                     String normalizedSubject,
                                     int depth) {
    }

    private record MenuSearchTerm(String value,
                                  int exactScore,
                                  int containsScore) {
    }
}
