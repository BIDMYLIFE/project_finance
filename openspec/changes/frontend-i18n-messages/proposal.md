## Why

所有前端 UI 字串（標籤、按鈕、錯誤訊息、狀態提示）目前以硬編碼 Traditional Chinese 寫死在 HTML 模板與 JavaScript 檔案中。若要支援多語言（例如英文介面）或至少让中文字串可集中管理，需要改用 Spring 的 `messages.properties` i18n 機制。目前沒有任何 `messages.properties`、`LocaleResolver` 或 `#{}` Thymeleaf 表達式的使用。

## What Changes

- 新增 `messages.properties`（預設/後備）與 `messages_zh_TW.properties`（繁體中文），集中管理所有前端 UI 字串。
- 設定 `spring.messages.basename=i18n/messages` 讓 Spring 自動載入。
- 新增 `LocaleResolver` 與 `LocaleChangeInterceptor`，支援以 `?lang=zh_TW` 或 `?lang=en` 切換語言。
- 將 Thymeleaf 模板中的硬編碼中文字串替換為 `#{key}` 表達式。
- 建立 Thymeleaf → Vue.js 橋接機制：在每個頁面的 `<script>` 中嵌入 messages JSON，供 Vue `{{ }}` 綁定使用。
- 將 JavaScript 檔案中的硬編碼中文字串（錯誤訊息、狀態提示）替換為從嵌入的 messages JSON 讀取。

## Capabilities

### New Capabilities

- `frontend-i18n`: 前端國際化基礎設施，包括 messages 屬性檔、LocaleResolver、Thymeleaf #{key} 整合、Vue.js JSON 橋接與多語言切換。

### Modified Capabilities

<!-- 無既有 capability 的需求變更。Dashboard、登入、初始化頁面的行為不變，僅字串來源改為 i18n。 -->

## Impact

- **後端**：新增 `I18nConfig.java`（LocaleResolver + LocaleChangeInterceptor），`application.yml` 新增 `spring.messages.basename`。
- **前端模板**：`dashboard.html`、`auth/login.html`、`auth/bootstrap.html` 的硬編碼中文字串替換為 `#{key}`。
- **前端 JS**：`dashboard.js`、`api-client.js`、`erp-state.js` 的硬編碼中文字串改為從嵌入的 messages JSON 讀取。
- **靜態資源**：新增 `i18n/messages.properties` 與 `i18n/messages_zh_TW.properties`。
- **依賴**：無新增外部依賴；Spring Boot Thymeleaf starter 已內建 MessageSource 支援。
- **相容性**：預設語言不變（繁體中文），僅架構改變；不影響既有 API、認證、資料庫或測試。
