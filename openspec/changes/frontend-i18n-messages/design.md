## Context

本專案使用 Spring Boot 3.5.5 + Thymeleaf + Vue 3（global build，無 build step）。前端所有 UI 字串目前以硬編碼 Traditional Chinese 寫死在 HTML 模板與 JavaScript 檔案中。Spring Boot 的 `spring-boot-starter-thymeleaf` 已內建 `MessageSource` 支援，但尚未設定。專案無前端 build toolchain（無 npm/webpack/vite），所有前端資源由 Maven WebJars 提供。

## Goals / Non-Goals

**Goals:**

- 建立 `messages.properties` + `messages_zh_TW.properties` i18n 基礎設施。
- 將 Thymeleaf 模板中的硬編碼中文替换為 `#{key}` 表達式。
- 提供 Thymeleaf → Vue.js 橋接：透過嵌入 JSON 讓 Vue 讀取 i18n 字串。
- 支援以 `?lang=` 查詢參數切換語言，locale 持久化在 session。
- 維持離線架構、無外部依賴、UTF-8 編碼正確性。

**Non-Goals:**

- 不建立完整的 locale 切換 UI（僅提供 `?lang=` 機制，無下拉選單）。
- 不翻譯後端錯誤訊息或 API 回應（僅前端 UI 字串）。
- 不引入前端 i18n 函式庫（如 vue-i18n）。
- 不改變認證、授權、資料庫或 API 行為。

## Decisions

### 1. 使用 Spring 內建 MessageSource，不引入外部 i18n 函式庫

Spring Boot 的 `spring-boot-starter-thymeleaf` 已包含 `ResourceBundleMessageSource`，設定 `spring.messages.basename` 即可啟用。不需要額外依賴。

替代方案是引入 `vue-i18n`，但專案無前端 build step，`vue-i18n` 需要 npm 安裝與 bundler 整合，與離線架構衝突，因此不採用。

### 2. Thymeleaf → Vue 橋接：嵌入 JSON 而非 API endpoint

在每個 Thymeleaf 模板中，使用 `th:inline="javascript"` 在 `<script>` 標籤內嵌入當前 locale 的 messages JSON：

```html
<script th:inline="javascript">
  window.__ERP_MESSAGES__ = /*[[${@messageSource.getMessage('dashboard.title', null, locale)}]]*/ '';
</script>
```

實際做法是用一個 Thymeleaf fragment 或 controller method 將所有需要的 key 組裝成 map，再以 `th:inline` 嵌入。Vue.js 從 `window.__ERP_MESSAGES__` 讀取。

替代方案是建立 `/api/v1/i18n` endpoint 讓 Vue AJAX 取得，但這會增加請求延遲、需要額外錯誤處理，且 i18n 字串是靜態的不需要 server-side 動態產生，因此不採用。

### 3. Messages key 命名格式：`<page>.<element>.<detail>`

例如：
- `dashboard.title` = 工作台
- `dashboard.nav.customers` = 客戶管理
- `dashboard.identity.email` = Email
- `login.button.submit` = 登入
- `common.error.network` = 網路連線失敗

這種格式易於按頁面分組、易于維護，且在 Thymeleaf 與 JS 中都容易引用。

### 4. LocaleResolver 使用 SessionLocaleResolver

使用 `SessionLocaleResolver` 將使用者的 locale 持久化在 HTTP session 中。`LocaleChangeInterceptor` 攔截 `?lang=` 參數並更新 session。

替代方案是 `CookieLocaleResolver`，但 cookie 有大小限制且需要額外的序列化/反序列化，session 方案更簡單且與現有認證 session 架構一致。

### 5. messages_zh_TW.properties 作為主要語言，messages.properties 作為後備

預設 `messages.properties` 存放與 `messages_zh_TW.properties` 相同的內容（繁體中文），作為找不到對應 locale 檔時的後備。未來新增英文時，建立 `messages_en.properties` 即可，不需要修改其他程式碼。

## Risks / Trade-offs

- [Risk] Thymeleaf `th:inline` 嵌入 JSON 時若 key 含特殊字元可能造成 XSS → [Mitigation] Thymeleaf 的 `th:inline` 會自動轉義 JSON 值，且 messages 內容由開發者控制，不接受使用者輸入。
- [Risk] 每個頁面載入時嵌入完整 messages JSON 可能增加 HTML 大小 → [Mitigation] 只嵌入該頁面需要的 key 子集，不嵌入全部 messages。
- [Risk] 若遗漏某個硬編碼字串未替換，使用者會看到混合語言 → [Mitigation] 透過測試掃描 HTML/JS 中的中文字符，確保無遺漏。
- [Risk] 新增 locale 檔（如英文）時需要翻譯所有字串 → [Mitigation] 先完成繁體中文 i18n 架構，英文翻譯可增量進行。

## Migration Plan

1. 新增 `I18nConfig.java` 與 `application.yml` 設定。
2. 建立 `messages.properties` 與 `messages_zh_TW.properties`。
3. 依序替換 `dashboard.html`、`auth/login.html`、`auth/bootstrap.html` 中的硬編碼中文。
4. 建立 Thymeleaf → Vue 橋接機制，替換 JS 檔案中的硬編碼中文。
5. 新增 i18n 相關測試。
6. 回滾策略：移除 `I18nConfig.java`，將 templates 與 JS 恢復為硬編碼中文，不影響其他功能。

## Open Questions

無。所有影響 spec 與實作的選擇已決定：使用 Spring MessageSource、SessionLocaleResolver、嵌入 JSON 橋接、key 命名格式。
