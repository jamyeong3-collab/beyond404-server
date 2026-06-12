# 로그인 후 예약 정보 DB 복구 구현 기록

## 작업 배경

고객용 앱에서 로그아웃 후 다시 로그인하거나, 같은 사용자가 앱에 다시 들어왔을 때 기존 예약/신청 정보가 사라지는 문제가 있었다.

원인은 로그인 정보는 `users` 테이블에 저장되지만, 프론트가 로그인 후 해당 사용자의 최신 교환 신청을 다시 조회하지 않았고, 수거 예약 정보도 메모리 상태 중심으로만 사용되고 있었기 때문이다.

## 구현 내용

1. `pickup_requests` 테이블을 JPA Entity/Repository로 연결했다.
2. 수거 예약과 바로콜 요청 시 `pickup_requests`에 실제 데이터를 저장하도록 변경했다.
3. `GET /api/swap-requests/latest?userId={userId}` API를 추가했다.
4. 서버 메모리에 신청 상태가 없더라도 DB의 `swap_requests`, `appliances`, `appliance_images`, `pickup_requests`를 기준으로 최소 상태를 복구하도록 보강했다.
5. `PATCH /api/swap-requests/{id}/appliance`와 예상 보상가 동의 단계도 DB 상태를 갱신하도록 수정했다.

## 기대 동작

- 사용자가 데모 로그인하면 `users` 테이블에 사용자 정보가 저장된다.
- 사용자가 교환 신청과 예약을 진행하면 `swap_requests`, `appliances`, `appliance_images`, `pickup_requests`에 데이터가 저장된다.
- 같은 사용자로 다시 로그인하면 최신 신청/예약 정보를 다시 불러올 수 있다.

## 확인 사항

- 백엔드 빌드: `./gradlew.bat build` 성공
- 프론트와 연결되는 최신 신청 조회 API 추가 완료

## 대화 요약

사용자는 “앱을 껐다 켜거나 다시 로그인해도 예약 정보가 기억되어야 한다”고 요청했다. 이에 따라 단순 프론트 상태 저장이 아니라 DB에 저장된 신청/예약 데이터를 기준으로 다시 복구하는 구조로 수정했다.
