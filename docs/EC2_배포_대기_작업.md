# EC2 배포 대기 작업

> 작성일: 2026-06-18
> SSH 키 문제로 EC2 접속 불가 상태. 아래 작업이 완료되어야 프로덕션(swapit.my)에 최신 코드 반영됨.

---

## 현재 상황

| 항목 | 상태 |
|---|---|
| 로컬 RDS DB (appliance_specs, market_products) | ✅ 완료 |
| jamyeong3-collab 레포 최신 커밋 (74c3f76) | ✅ 완료 |
| EC2 코드 반영 | ❌ SSH 키 불일치로 미완 |
| swapit.my 서버 생존 여부 | ✅ 살아있음 (구버전으로) |

EC2가 바라보는 origin이 `yeosangjun824-gif/beyond404-server` 일 가능성 있음 → jamyeong3-collab 레포로 변경 필요.

---

## 친구에게 전달할 작업 프롬프트

```
안녕, EC2 배포 작업 부탁할게.
SSH 키 문제가 있어서 직접 접속이 필요해.

[방법 1 - AWS 콘솔로 접속 (PEM 키 필요 없음)]
1. AWS 콘솔 로그인
2. EC2 → 인스턴스 선택 (3.37.152.42)
3. 상단 "연결" 버튼 클릭
4. "EC2 Instance Connect" 탭 → "연결" 클릭
5. 브라우저에서 터미널 열림

[EC2 접속 후 실행할 명령어]

# 1. 현재 origin 확인
git remote -v

# 2. origin이 yeosangjun824-gif이면 아래 실행
git remote set-url origin https://github.com/jamyeong3-collab/beyond404-server.git

# 3. 최신 코드 받기
git pull

# 4. 최신 커밋 확인 (74c3f76 이어야 정상)
git log --oneline -3

# 5. 빌드
./gradlew build -x test

# 6. 서버 재시작
pm2 restart swapit-server --update-env

# 7. 상태 확인
pm2 logs swapit-server --lines 30

[확인 사항]
서버 재시작 후 아래 URL 응답 확인:
https://swapit.my/actuator/health → {"status":"UP"} 이면 OK
```

---

## 이 작업이 완료되면 달라지는 것

- 프로덕션에서 모델명 → DB 조회 → 실제 size_grade 반영
- selectReplacementProduct API에서 gradeFromPrice() 자동 계산
- 566개 appliance_specs 데이터 프로덕션 적용
