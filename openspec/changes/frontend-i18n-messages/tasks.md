## 1. Backend i18n Configuration

- [x] 1.1 Create `I18nConfig.java` with `SessionLocaleResolver` (default locale `zh_TW`) and `LocaleChangeInterceptor` (intercepts `lang` parameter)
- [x] 1.2 Add `spring.messages.basename=i18n/messages` and `spring.messages.encoding=UTF-8` to `application.yml`
- [x] 1.3 Add unit test for `I18nConfig` verifying locale resolver default and interceptor registration

## 2. Messages Properties Files

- [x] 2.1 Create `src/main/resources/i18n/messages.properties` (default/fallback,繁體中文) with all UI strings
- [x] 2.2 Create `src/main/resources/i18n/messages_zh_TW.properties` (繁體中文) with identical content
- [x] 2.3 Define message keys following `<page>.<element>.<detail>` naming: `dashboard.*`, `login.*`, `bootstrap.*`, `common.*`

## 3. Thymeleaf Template Refactoring

- [x] 3.1 Replace hardcoded Chinese in `templates/dashboard.html` with `#{key}` expressions (title, nav labels, identity panel, buttons, status messages)
- [x] 3.2 Replace hardcoded Chinese in `templates/auth/login.html` with `#{key}` expressions (title, form labels, button, error message)
- [x] 3.3 Replace hardcoded Chinese in `templates/auth/bootstrap.html` with `#{key}` expressions (title, form labels, button, success/error messages)
- [x] 3.4 Add `xmlns:th` namespace to `dashboard.html` and `login.html` if not present

## 4. Thymeleaf → Vue.js Bridge

- [x] 4.1 Create a Thymeleaf fragment or controller helper that assembles page-specific messages into a JSON object
- [x] 4.2 Embed `window.__ERP_MESSAGES__` via `th:inline="javascript"` in `dashboard.html` for Vue consumption
- [x] 4.3 Embed `window.__ERP_MESSAGES__` in `auth/login.html` and `auth/bootstrap.html` for Vue consumption
- [x] 4.4 Ensure embedded JSON is properly escaped (Thymeleaf auto-escaping)

## 5. JavaScript i18n Refactoring

- [x] 5.1 Update `dashboard.js` to read all UI strings from `window.__ERP_MESSAGES__` instead of hardcoded Chinese
- [x] 5.2 Update `capability-registry.js` to use i18n keys for labels (e.g., `label: 'dashboard.nav.customers'`) with Vue resolving at render time
- [x] 5.3 Update `api-client.js` error messages to read from `window.__ERP_MESSAGES__` fallback
- [x] 5.4 Update `erp-state.js` error messages to read from `window.__ERP_MESSAGES__` fallback
- [x] 5.5 Verify `auth-login.js` and `auth-bootstrap.js` also use embedded messages (if applicable)

## 6. Verification and Testing

- [x] 6.1 Add test scanning HTML templates for remaining hardcoded Chinese characters (ensure none remain)
- [x] 6.2 Add test scanning JS files for remaining hardcoded Chinese characters (ensure none remain)
- [x] 6.3 Add test verifying `messages.properties` and `messages_zh_TW.properties` contain matching key sets
- [x] 6.4 Add test verifying `window.__ERP_MESSAGES__` is present in all Vue-carrying templates
- [x] 6.5 Run full build (`mvn verify`) and confirm all tests pass
- [x] 6.6 Verify Chinese characters render correctly in browser (no Unicode escapes, no mojibake)
