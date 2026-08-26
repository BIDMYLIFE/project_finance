## Purpose

讓前端所有 UI 字串（標籤、按鈕、錯誤訊息、狀態提示）從硬編碼改為透過 Spring `messages.properties` i18n 機制集中管理，支援多語言切換，並提供 Thymeleaf 與 Vue.js 的橋接機制。

## ADDED Requirements

### Requirement: Messages properties files define all frontend UI strings

系統 SHALL 提供 `messages.properties`（預設/後備）與 `messages_zh_TW.properties`（繁體中文），集中定義所有前端 UI 可見字串，包括頁面標題、表單標籤、按鈕文字、錯誤訊息、狀態提示與導覽項目。每個字串 SHALL 有唯一的 key，命名格式 SHALL 為 `<page>.<element>.<detail>`（例如 `dashboard.title`、`login.button.submit`）。

#### Scenario: Default locale falls back to zh_TW

- **WHEN** 瀏覽器未提供支援的 locale 或請求 `messages.properties` 中不存在的 key
- **THEN** 系統回退到 `messages_zh_TW.properties` 中的對應值

#### Scenario: English locale returns English strings

- **WHEN** 使用者切換到 `lang=en` 且 `messages_en.properties` 存在對應 key
- **THEN** 系統回傳英文字串

### Requirement: Spring MessageSource is configured

系統 SHALL 設定 `spring.messages.basename=i18n/messages`，讓 Spring 自動載入 messages 屬性檔。MessageSource SHALL 支援 UTF-8 編碼以正確處理中文字符。

#### Scenario: Application starts with i18n configured

- **WHEN** 應用程式啟動
- **THEN** `MessageSource` bean 可用，且能解析 `messages.properties` 中的 key

### Requirement: Locale resolver supports language switching

系統 SHALL 提供 `LocaleResolver` 與 `LocaleChangeInterceptor`，讓使用者以 `?lang=zh_TW` 或 `?lang=en` 查詢參數切換語言。切換後的 locale SHALL 持久化在 session 中，後續請求維持該語言直到再次切換。

#### Scenario: Language switch via query parameter

- **WHEN** 使用者請求 `/auth/login?lang=en`
- **THEN** 當前 session 的 locale 設為英文，後續頁面使用英文字串

#### Scenario: Default locale is zh_TW

- **WHEN** 使用者未切換語言
- **THEN** 預設 locale 為 `zh_TW`

### Requirement: Thymeleaf templates use message expressions

所有 Thymeleaf 模板中的硬編碼中文字串 SHALL 替換為 `#{key}` 表達式。模板 SHALL 不再包含任何直接的中文 UI 字串（HTML 標籤內的文字、屬性值中的文字）。

#### Scenario: Dashboard page renders via i18n

- **WHEN** 瀏覽器請求 Dashboard 頁面
- **THEN** 頁面標題、導覽項目、身份面板標籤、按鈕文字均從 messages 屬性檔解析，不包含硬編碼中文

#### Scenario: Login page renders via i18n

- **WHEN** 瀏覽器請求登入頁面
- **THEN** 表單標籤、按鈕、錯誤提示均從 messages 屬性檔解析

### Requirement: Vue.js pages receive messages via embedded JSON

系統 SHALL 在每個包含 Vue.js 的頁面中，透過 Thymeleaf 在 `<script>` 標籤內嵌入當前 locale 的 messages JSON。Vue.js 應用 SHALL 從該 JSON 讀取字串，不再使用硬編碼中文。

#### Scenario: Dashboard Vue app uses embedded messages

- **WHEN** Dashboard 頁面載入完成
- **THEN** Vue.js 可從 `window.__ERP_MESSAGES__` 讀取所有 UI 字串，且無任何硬編碼中文出現在 JS 檔案中

#### Scenario: Error messages use i18n keys

- **WHEN** API 請求失敗且共用狀態處理設定錯誤訊息
- **THEN** 錯誤訊息從嵌入的 messages JSON 讀取，不使用硬編碼字串

### Requirement: Capability registry labels come from messages

導覽的 capability registry SHALL 從 messages JSON 讀取 label，不使用硬編碼中文。registry 的 `label` 欄位 SHALL 改為存 i18n key，由 Vue 在渲染時解析。

#### Scenario: Navigation displays localized capability names

- **WHEN** Dashboard 渲染導覽
- **THEN** 「客戶管理」、「產品」等名稱從 messages JSON 解析，非硬編碼

### Requirement: Offline and encoding compliance

所有 messages 屬性檔 SHALL 使用 UTF-8 編碼。檔案 SHALL 由專案本地管理，不引入外部網路資源。 Spring `MessageSource` SHALL 設定 `encoding=UTF-8`。

#### Scenario: Messages load without external resources

- **WHEN** 應用在無網路環境啟動
- **THEN** 所有 i18n 字串從本地 messages 屬性檔載入，無外部依賴

#### Scenario: Chinese characters render correctly

- **WHEN** 瀏覽器以 UTF-8 解碼頁面
- **THEN** 所有中文字符正確顯示，不出現亂碼或 Unicode 轉義序列
