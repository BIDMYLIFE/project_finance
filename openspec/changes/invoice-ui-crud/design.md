## Context

目前專案已有 Customer、Product、Quote entity/service/UI，資料庫 migration 已建立 `invoices`、`invoice_lines` 與 document sequence 結構，也已有 `InvoiceStatus` enum；但 Invoice domain、Repository、DTO、Controller 與頁面尚未實作。既有 payment allocation schema 將以 invoice id 連結，但付款流程不在本 change 內。

## Goals / Non-Goals

**Goals:**

- 建立 organization-scoped invoice aggregate 與明細快照，支援草稿、開立、取消及查詢。
- 將金額計算、狀態轉換、逾期判斷與 invoice number 產生集中在 Service transaction。
- 沿用現有 Customer/Product 驗證、Quote UI 的明細編輯模式、PageResponse、錯誤格式、Vue/Bootstrap/SweetAlert2 與本地資源政策。
- 將 Dashboard invoices capability 啟用並導向 `/invoices`。

**Non-Goals:**

- 不實作付款建立、付款分配、收據列印、外部稅務平台、電子發票或外部銀行整合。
- 不允許已發出發票 hard delete；不在本 change 修改既有 payment allocation service。
- 不新增外部依賴或 CDN 資源。

## Decisions

### 建立獨立 Invoice aggregate 與 Service

新增 Invoice/InvoiceLine entity、organization-scoped repositories、DTO、`InvoiceService`、API Controller 與 MVC Controller。Service 負責 customer/product active 檢查、快照、計算、狀態機與 organization scope；Controller 只負責解析、驗證與回應。直接把邏輯放入 QuoteService 或 MasterDataService 會造成跨 domain 耦合，因此不採用。

### 草稿允許修改，已發出文件採不可破壞流程

`PUT` 僅允許 DRAFT；`POST /{id}/issue` 負責開立，`POST /{id}/cancel` 負責取消，DELETE 不提供 hard delete。這樣能保留付款、報表與稽核所需歷史。狀態與 transition 檢查集中在 Service，不由前端自行決定。

### 使用既有 document sequence transaction 產生編號

開立時依 organization 與年度鎖定或原子更新 document sequence，產生唯一 invoice number，並與狀態更新位於同一 transaction；unique constraint 作為最後防線。隨機 UUID 或前端產生編號無法滿足年度序列與不可重用需求，因此不採用。

### 使用快照計算，列表與明細分離

建立/更新時從 active Product 讀取名稱、說明、單價、稅率並保存到 InvoiceLine；後續產品變更不影響既有文件。清單回應以 bounded query 與 summary 欄位為主，明細 endpoint 才載入完整 lines，避免列表一次載入不必要資料。

### 先完成 Invoice contract，再由付款流程消費

本 change 的 response 必須提供 invoice id、organization-safe customer information、currency、grand total、paid total、balance due、status 與 lines，讓後續 payment-bank-posting 能安全分配；本 change 不提前實作付款分配，避免重複責任。

## Risks / Trade-offs

- [Risk] invoice sequence locking 可能降低同 organization 高併發開立吞吐 → [Mitigation] 將 sequence lock 限制在短 transaction，保留 unique constraint 與有限重試策略。
- [Risk] 現有 payment-bank-posting 尚未完成，paid/balance 狀態的完整更新無消費者 → [Mitigation] 本 change 初始化 paid total/balance due 並保留明確 contract，付款整合由後續 change 驗證。
- [Risk] 稅務 jurisdiction 與電子發票欄位尚未確定 → [Mitigation] MVP 使用現有一般 invoice 欄位與 organization currency/tax policy，不宣稱法定電子發票合規。
- [Risk] 專案目前沒有 browser automation dependency → [Mitigation] 加入 resource/API/UI contract tests，並記錄未完成實際 browser viewport 驗證的風險。

## Migration Plan

不新增資料庫 migration，但必須先確認現有 invoice table 與 document sequence migration 已套用。部署順序為後端 domain/API、MVC 與多語系資源，再部署前端與 Dashboard registry；失敗時只回滾應用程式變更，不刪除既有發票或序列資料。
