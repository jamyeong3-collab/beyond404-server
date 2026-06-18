# SwapIt 최종 가격 산정 흐름 (핵심 참고 문서)

> 작성일: 2026-06-18  
> 작성자: 상준 (dx)  
> 이 문서가 가격 산정 관련 **최우선 참고 문서**입니다. 다른 문서(SWAPIT_PRICING_LOGIC.md, PRICING_FLOW_IMPLEMENTATION.md)와 내용이 충돌하면 이 문서를 기준으로 하세요.

---

## ⚠️ 합치기 전 반드시 확인할 사항

### 1. OpenAI API 미연결 (가장 중요)

현재 `analyzePhoto` API는 **mock 상태**입니다.  
GPT-4o가 라벨 사진에서 모델명을 실제로 추출하지 않고, 더미 모델명이 들어가고 있습니다.

```
현재: 사진 촬영 → mock 모델명 → DB 조회 실패 → "중형" fallback → 스크랩 계산
목표: 사진 촬영 → GPT-4o 실제 모델명 → DB 조회 성공 → 실제 size_grade → 스크랩 계산
```

**합칠 때 본체가 해야 할 것:**
- `analyzePhoto` API에서 GPT-4o Vision 실제 호출 연결
- 응답에서 `modelName` 필드 추출 후 `updateAppliance`로 전달

---

### 2. EC2 서버 git pull 필요

EC2가 아직 **구버전 코드**입니다. 아래 migration이 반영되지 않았습니다.

| Migration | 내용 | 상태 |
|---|---|---|
| V5 | `appliance_specs` 테이블 생성 | ❌ EC2 미반영 |
| V6 | 566개 LG 제품 스펙 데이터 | ❌ EC2 미반영 |
| V7 | `market_products` 5개 신제품 | ❌ EC2 미반영 |

**EC2 배포 방법:** `docs/EC2_배포_대기_작업.md` 참고

---

### 3. 프론트엔드 병합 시 충돌 가능성

`PurchasePanel.tsx`를 수정했습니다. 다른 팀원이 같은 파일을 건드렸다면 충돌 납니다.

**변경 내용 요약:**
- 하드코딩 제품 30개 → DB 기반 5개 제품으로 교체
- 탭 클릭 시 모든 탭에서 동일하게 5개 제품 나열
- `swapRequestId` prop 추가
- "주문하기" 클릭 시 `selectReplacementProduct` API 호출 추가

충돌 시 **이 변경 내용을 반드시 살려야** 합니다.

---

## 전체 가격 산정 흐름

### STEP 1 — 라벨 사진 → 소형/중형/대형 판단

```
[라벨 사진 촬영]
        ↓
[GPT-4o Vision]  ← ⚠️ 현재 미연결 (mock 상태)
        ↓ 모델명 추출 (예: "gr-b247squu")
[updateAppliance API]
        ↓
[appliance_specs 테이블 조회] — V6, 566개 LG 제품 스펙
        ↓
조회 성공 → 실제 size_grade, 실제 용량
조회 실패 → "중형" fallback (오류 없이 자동 처리)
```

**사이즈 판단 기준:**

| 제품 | 소형 | 중형 | 대형 |
|---|---|---|---|
| 냉장고 | 300L 미만 | 300~600L | 600L 초과 |
| 세탁기 | 6kg 이하 | 중간 | 17kg 이상 |
| TV | 43인치 미만 | 43~65인치 | 65인치 초과 |
| 에어컨 | 25kg 미만 | 25~35kg | 35kg 초과 |
| 전자레인지 | 20L 미만 | 20~30L | 30L 초과 |

---

### STEP 2 — 소형/중형/대형 → 스크랩 가치

`scrapValueFor()` in `SwapRequestState.java`

| 제품 | 소형 | 중형 | 대형 |
|---|---|---|---|
| 냉장고 | 20,000원 | 39,000원 | 55,000원 |
| 세탁기 | 30,000원 | 45,000원 | 60,000원 |
| 에어컨 | 45,000원 | 62,000원 | 80,000원 |
| TV | 10,000원 | 18,000원 | 28,000원 |
| 전자레인지 | (전체) | 8,000원 | |

이 값이 `swapRequest.preValuation`에 담겨 프론트로 전달됩니다.

---

### STEP 3 — 신제품 선택 → 최종 크레딧 계산

**신제품 등급 분류 (가격 기준 자동):**

| 등급 | 가격 구간 |
|---|---|
| 프리미엄 | 150만원 이상 |
| 일반 | 50만원 ~ 150만원 미만 |
| 보급형 | 50만원 미만 |

**크레딧 비율 매트릭스:**

| 신제품 등급 | 1회 (최초) | 2회 (실버) | 3회 이상 (VIP) |
|---|---|---|---|
| 프리미엄 | 10% | 12% | 15% |
| 일반 | 7% | 10% | 12% |
| 보급형 | 4% | 7% | 9% |

**최종 공식:**
```
최종 크레딧 = 스크랩 가치 + 신제품가 × 등급별 비율
예상 결제   = 신제품 정가 - 최종 크레딧
```

> min() 캡 없음. 등급별 비율이 그대로 적용됩니다.

---

### 계산 예시

**냉장고 중형 반납 + LG 디오스 AI 오브제컬렉션 832L 선택 (최초 이용):**

```
스크랩 가치:   냉장고 중형 → 39,000원
신제품 가격:   2,350,000원
신제품 등급:   프리미엄 (150만원 이상)
비율:          10% (1회/최초)

최종 크레딧:   39,000 + 2,350,000 × 10% = 39,000 + 235,000 = 274,000원
예상 결제:     2,350,000 - 274,000 = 2,076,000원
```

---

## 구현 위치 요약

| 로직 | 위치 |
|---|---|
| 소형/중형/대형 판단 | `SwapRequestService.java` → `updateAppliance()` |
| 스크랩 가치 테이블 | `SwapRequestState.java` → `scrapValueFor()` |
| 신제품 등급 분류 | `SwapRequestService.java` → `gradeFromPrice()` |
| 크레딧 비율 매트릭스 | `SwapRequestState.java` → `creditRateFor()` |
| 최종 크레딧 계산 (서버) | `SwapRequestState.java` → `calculateEstimatedFinalCredit()` |
| 최종 크레딧 계산 (프론트 미리보기) | `PurchasePanel.tsx` → `calculatePurchaseBenefit()` |
| 신제품 선택 API | `POST /api/swap-requests/{id}/replacement-product` |

---

## DB 구조

```
appliance_specs (V5/V6)
├── model_name   → GPT-4o가 인식한 모델명으로 조회
├── size_grade   → 소형/중형/대형
└── capacity     → 실제 용량/크기

market_products (V7)
├── category     → washing_machine / refrigerator / tv / air_conditioner / microwave
├── product_name → 표시 이름
├── price        → 정가 (등급 분류 기준)
└── image_url    → lge.co.kr 공식 이미지
```
