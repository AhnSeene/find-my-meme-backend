-- 외래 키 제약 조건을 먼저 삭제합니다.
SET FOREIGN_KEY_CHECKS=0;
ALTER TABLE post_tag DROP FOREIGN KEY FK_post_tag_tag; -- tag_id 외래 키 제거
ALTER TABLE meme_post_like DROP FOREIGN KEY FK_meme_post_like_post; -- meme_post_id 외래 키 제거
ALTER TABLE meme_post_like DROP FOREIGN KEY FK_meme_post_like_user; -- user_id 외래 키 제거
ALTER TABLE find_post_comment DROP FOREIGN KEY FK_find_post_comment_post; -- find_post_id 외래 키 제거
ALTER TABLE find_post_comment DROP FOREIGN KEY FK_find_post_comment_user; -- user_id 외래 키 제거
ALTER TABLE find_post_comment_image DROP FOREIGN KEY FK_find_post_comment_image_comment; -- comment_id 외래 키 제거
ALTER TABLE find_post_image DROP FOREIGN KEY FK_find_post_image_post; -- find_post_id 외래 키 제거
SET FOREIGN_KEY_CHECKS=1;
DROP TABLE IF EXISTS find_post_image;
DROP TABLE IF EXISTS find_post_comment_image;
DROP TABLE IF EXISTS find_post_comment;
DROP TABLE IF EXISTS find_post;
DROP TABLE IF EXISTS meme_post_like;
DROP TABLE IF EXISTS meme_post;
DROP TABLE IF EXISTS post_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS file_meta;
DROP TABLE IF EXISTS users;

-- 1. 사용자 테이블 생성
CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       created_at DATETIME(6) NOT NULL,
                       updated_at DATETIME(6) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       profile_image_url VARCHAR(255) DEFAULT NULL,
                       role ENUM('ROLE_USER', 'ROLE_ADMIN') DEFAULT 'ROLE_USER',
                       PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. 태그 테이블 생성
CREATE TABLE `tag` (
                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                       `parent_tag_id` BIGINT DEFAULT NULL,
                       `name` VARCHAR(255) NOT NULL,
                       `slug` VARCHAR(255) NOT NULL,
                       PRIMARY KEY (`id`),
                       UNIQUE KEY `UK_slug` (`slug`),
                       KEY `FK_tag_parent` (`parent_tag_id`),
                       CONSTRAINT `FK_tag_parent` FOREIGN KEY (`parent_tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. 파일 메타데이터 테이블 생성
CREATE TABLE `file_meta` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT,
                             `height` INT NOT NULL,
                             `width` INT NOT NULL,
                             `size` BIGINT NOT NULL,
                             `user_id` BIGINT NOT NULL,
                             `extension` VARCHAR(255) NOT NULL,
                             `file_url` VARCHAR(255) NOT NULL,
                             `original_filename` VARCHAR(255) NOT NULL,
                             PRIMARY KEY (`id`),
                             KEY `FK_file_meta_user` (`user_id`),
                             CONSTRAINT `FK_file_meta_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. 밈 포스트 태그 연결 테이블 생성
CREATE TABLE `post_tag` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT,
                            `post_id` BIGINT NOT NULL,
                            `tag_id` BIGINT NOT NULL,
                            `post_type` ENUM('FIND_POST', 'MEME_POST') NOT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FK_post_tag_tag` (`tag_id`),
                            CONSTRAINT `FK_post_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. 밈 포스트 테이블 생성
CREATE TABLE `meme_post` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT,
                             `height` INT NOT NULL,
                             `width` INT NOT NULL,
                             `created_at` DATETIME(6) NOT NULL,
                             `deleted_at` DATETIME(6) DEFAULT NULL,
                             `download_count` BIGINT NOT NULL,
                             `like_count` BIGINT NOT NULL,
                             `size` BIGINT NOT NULL,
                             `updated_at` DATETIME(6) NOT NULL,
                             `user_id` BIGINT NOT NULL,
                             `view_count` BIGINT NOT NULL,
                             `extension` VARCHAR(255) NOT NULL,
                             `image_url` VARCHAR(255) NOT NULL,
                             `original_filename` VARCHAR(255) NOT NULL,
                             PRIMARY KEY (`id`),
                             KEY `FK_meme_post_user` (`user_id`),
                             CONSTRAINT `FK_meme_post_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6. 밈 포스트 좋아요 테이블 생성
CREATE TABLE `meme_post_like` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `created_at` DATETIME(6) NOT NULL,
                                  `meme_post_id` BIGINT NOT NULL,
                                  `updated_at` DATETIME(6) NOT NULL,
                                  `user_id` BIGINT NOT NULL,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `UK_meme_post_user` (`meme_post_id`, `user_id`),
                                  KEY `FK_meme_post_like_user` (`user_id`),
                                  CONSTRAINT `FK_meme_post_like_post` FOREIGN KEY (`meme_post_id`) REFERENCES `meme_post` (`id`) ON DELETE CASCADE,
                                  CONSTRAINT `FK_meme_post_like_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `find_post` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT,
                             `comment_count` INT DEFAULT 0,
                             `created_at` DATETIME(6) NOT NULL,
                             `deleted_at` DATETIME(6) DEFAULT NULL,
                             `selected_comment_id` BIGINT DEFAULT NULL,
                             `updated_at` DATETIME(6) NOT NULL,
                             `user_id` BIGINT NOT NULL,
                             `view_count` BIGINT DEFAULT 0,
                             `title` VARCHAR(255) NOT NULL,
                             `content` TEXT NOT NULL,
                             `find_status` ENUM('FIND', 'FOUND') NOT NULL,
                             `html_content` TEXT NOT NULL,
                             PRIMARY KEY (`id`),
                             KEY `FK_find_post_user` (`user_id`),
                             CONSTRAINT `FK_find_post_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 8. 찾기 포스트 댓글 테이블 생성
CREATE TABLE `find_post_comment` (
                                     `id` BIGINT NOT NULL AUTO_INCREMENT,
                                     `selected` BIT(1) NOT NULL DEFAULT 0,
                                     `created_at` DATETIME(6) NOT NULL,
                                     `deleted_at` DATETIME(6) DEFAULT NULL,
                                     `find_post_id` BIGINT NOT NULL,
                                     `parent_comment_id` BIGINT DEFAULT NULL,
                                     `updated_at` DATETIME(6) NOT NULL,
                                     `user_id` BIGINT NOT NULL,
                                     `content` TEXT NOT NULL,
                                     `html_content` TEXT NOT NULL,
                                     PRIMARY KEY (`id`),
                                     KEY `FK_find_post_comment_post` (`find_post_id`),
                                     KEY `FK_find_post_comment_parent` (`parent_comment_id`),
                                     KEY `FK_find_post_comment_user` (`user_id`),
                                     CONSTRAINT `FK_find_post_comment_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                     CONSTRAINT `FK_find_post_comment_post` FOREIGN KEY (`find_post_id`) REFERENCES `find_post` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 9. 댓글 이미지 테이블 생성
CREATE TABLE `find_post_comment_image` (
                                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                                           `comment_id` BIGINT NOT NULL,
                                           `created_at` DATETIME(6) NOT NULL,
                                           `updated_at` DATETIME(6) NOT NULL,
                                           `image_url` VARCHAR(255) NOT NULL,
                                           `original_filename` VARCHAR(255) NOT NULL,
                                           PRIMARY KEY (`id`),
                                           KEY `FK_find_post_comment_image_comment` (`comment_id`),
                                           CONSTRAINT `FK_find_post_comment_image_comment` FOREIGN KEY (`comment_id`) REFERENCES `find_post_comment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 10. 찾기 포스트 이미지 테이블 생성
CREATE TABLE `find_post_image` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT,
                                   `find_post_id` BIGINT NOT NULL,
                                   `created_at` DATETIME(6) NOT NULL,
                                   `updated_at` DATETIME(6) NOT NULL,
                                   `image_url` VARCHAR(255) NOT NULL,
                                   `original_filename` VARCHAR(255) NOT NULL,
                                   PRIMARY KEY (`id`),
                                   KEY `FK_find_post_image_post` (`find_post_id`),
                                   CONSTRAINT `FK_find_post_image_post` FOREIGN KEY (`find_post_id`) REFERENCES `find_post` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 기존 대분류 태그 삽입
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('감정', NULL, 'emotion');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('인사', NULL, 'greeting');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('TV', NULL, 'tv');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('동물', NULL, 'animal');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('느낌', NULL, 'feeling');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('배경', NULL, 'background');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('캐릭터', NULL, 'character');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('애니메이션', NULL, 'animation');

-- 소분류 태그 삽입
-- 감정 소분류
SET @emotion_id = (SELECT id FROM tag WHERE name = '감정');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('귀여운', @emotion_id, 'cute');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('화난', @emotion_id, 'angry');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('웃긴', @emotion_id, 'funny');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('슬픈', @emotion_id, 'sad');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('놀란', @emotion_id, 'surprised');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('당황', @emotion_id, 'embarrassed');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('질투', @emotion_id, 'jealous');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('피곤', @emotion_id, 'tired');

-- 인사 소분류
SET @greeting_id = (SELECT id FROM tag WHERE name = '인사');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('안녕하세요', @greeting_id, 'hello');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('감사합니다', @greeting_id, 'thank-you');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('미안합니다', @greeting_id, 'sorry');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('새해인사', @greeting_id, 'new-year-greeting');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('생일 축하', @greeting_id, 'happy-birthday');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('추석', @greeting_id, 'chuseok');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('크리스마스', @greeting_id, 'christmas');

-- TV 소분류
SET @tv_id = (SELECT id FROM tag WHERE name = 'TV');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('무한도전', @tv_id, 'infinite-challenge');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('기타', @tv_id, 'others');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('쇼미더머니', @tv_id, 'show-me-the-money');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('라디오스타', @tv_id, 'radio-star');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('아는형님', @tv_id, 'knowing-brothers');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('비정상회담', @tv_id, 'non-summit');

-- 동물 소분류
SET @animal_id = (SELECT id FROM tag WHERE name = '동물');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('고양이', @animal_id, 'cat');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('강아지', @animal_id, 'dog');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('토끼', @animal_id, 'rabbit');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('새', @animal_id, 'bird');

-- 배경 소분류
SET @background_id = (SELECT id FROM tag WHERE name = '배경');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('자연', @background_id, 'nature');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('도시', @background_id, 'city');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('해변', @background_id, 'beach');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('산', @background_id, 'mountain');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('관광지', @background_id, 'tourist-attraction');

-- 캐릭터 소분류
SET @character_id = (SELECT id FROM tag WHERE name = '캐릭터');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('미키 마우스', @character_id, 'mickey-mouse');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('스파이더맨', @character_id, 'spiderman');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('아이언맨', @character_id, 'ironman');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('포켓몬', @character_id, 'pokemon');

-- 애니메이션 소분류
SET @animation_id = (SELECT id FROM tag WHERE name = '애니메이션');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('토이 스토리', @animation_id, 'toy-story');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('겨울왕국', @animation_id, 'frozen');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('짱구는 못말려', @animation_id, 'crayon-shin-chan');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('원피스', @animation_id, 'one-piece');

SET SESSION cte_max_recursion_depth = 1000000;

-- users 테이블에 더미 데이터 삽입
INSERT INTO users (username, created_at, updated_at, password, email, profile_image_url, role)
WITH RECURSIVE cte (n, created_at) AS (
    SELECT 1, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650) DAY) + INTERVAL FLOOR(RAND() * 86400) SECOND)  -- 최근 10년 내의 임의의 날짜와 시간 생성
    UNION ALL
    SELECT n + 1, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650) DAY) + INTERVAL FLOOR(RAND() * 86400) SECOND)
    FROM cte WHERE n < 100000 -- 생성하고 싶은 더미 데이터의 개수
)
SELECT
    CONCAT('user', REPLACE(UUID(), '-', '')) AS username,    -- 'User' 다음에 5자리 숫자로 구성된 이름 생성
    created_at,  -- 생성된 created_at 사용
    created_at AS updated_at,  -- updated_at을 created_at과 동일하게 설정
    CONCAT('password', LPAD(n, 5, '0')) AS password, -- 'password' 다음에 5자리 숫자
    CONCAT('User', UUID(), '@example.com') AS email, -- 'user' 다음에 5자리 숫자 이메일
    CONCAT('http://example.com/image', LPAD(n, 5, '0'), '.jpg') AS profile_image_url, -- 프로필 이미지 URL
    'ROLE_USER' AS role -- 역할
FROM cte;

INSERT INTO file_meta (height, width, size, user_id, extension, file_url, original_filename)
WITH RECURSIVE cte (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1
    FROM cte WHERE n < 100000  -- 생성하고 싶은 더미 데이터 수
)
SELECT
    FLOOR(100 + RAND() * 1000) AS height,  -- 100~1100 사이의 랜덤한 높이
    FLOOR(100 + RAND() * 1000) AS width,   -- 100~1100 사이의 랜덤한 너비
    FLOOR(1000 + RAND() * 100000) AS size, -- 1000~101000 사이의 랜덤한 파일 크기 (bytes)
    FLOOR(1 + RAND() * 100000) AS user_id,  -- 1부터 100000 사이의 랜덤한 user_id
    CASE
        WHEN FLOOR(RAND() * 2) = 0 THEN 'jpg'
        WHEN FLOOR(RAND() * 2) = 1 THEN 'png'
        ELSE 'gif'
        END AS extension,                          -- 랜덤한 파일 확장자
    CONCAT('http://example.com/file/', n, '.',
           CASE
               WHEN FLOOR(RAND() * 2) = 0 THEN 'jpg'
               WHEN FLOOR(RAND() * 2) = 1 THEN 'png'
               ELSE 'gif'
               END) AS file_url,                     -- 랜덤한 파일 URL
    CONCAT('original_file_', n, '.',
           CASE
               WHEN FLOOR(RAND() * 2) = 0 THEN 'jpg'
               WHEN FLOOR(RAND() * 2) = 1 THEN 'png'
               ELSE 'gif'
               END) AS original_filename               -- 원본 파일 이름
FROM cte;
-- 1. meme_post 및 post_tag를 위한 임시 테이블 생성
CREATE TEMPORARY TABLE temp_meme_posts (
                                           post_id BIGINT NOT NULL,
                                           user_id BIGINT NOT NULL
);

-- 2. meme_post 삽입 및 임시 테이블에 ID 저장
INSERT INTO meme_post (height, width, created_at, deleted_at, download_count, like_count, size, updated_at, user_id, view_count, extension, image_url, original_filename)
WITH RECURSIVE cte (n, created_at) AS (
    SELECT 1, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650) DAY) + INTERVAL FLOOR(RAND() * 86400) SECOND)
    UNION ALL
    SELECT n + 1, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650) DAY) + INTERVAL FLOOR(RAND() * 86400) SECOND)
    FROM cte WHERE n < 100000
)
SELECT
    FLOOR(RAND() * 1080) + 1 AS height,
    FLOOR(RAND() * 1920) + 1 AS width,
    created_at,
    NULL AS deleted_at,
    FLOOR(RAND() * 1000) AS download_count,
    FLOOR(RAND() * 10000) AS like_count,
    FLOOR(RAND() * 500000) + 1 AS size,
    created_at AS updated_at,
    FLOOR(1 + RAND() * 100000) AS user_id,
    FLOOR(RAND() * 100000) AS view_count,
    CASE WHEN FLOOR(RAND() * 2) = 0 THEN 'jpg' ELSE 'png' END AS extension,
    CONCAT('http://example.com/meme', n, '.jpg') AS image_url,
    CONCAT('original_filename', n, '.jpg') AS original_filename
FROM cte;

-- 3. 생성된 meme_post의 ID를 임시 테이블에 저장
INSERT INTO temp_meme_posts (post_id, user_id)
SELECT id, user_id FROM meme_post ORDER BY id DESC LIMIT 100000;

-- 4. post_tag 생성
INSERT INTO post_tag (post_id, tag_id, post_type)
SELECT
    tmp.post_id,
    FLOOR(9 + RAND() * (46 - 9 + 1)) AS tag_id,  -- 9에서 46 사이의 랜덤한 tag_id
    'MEME_POST' AS post_type
FROM temp_meme_posts tmp
         CROSS JOIN (
    SELECT 1 AS seq UNION ALL SELECT 2 UNION ALL SELECT 3  -- 최대 3개의 태그를 추가하기 위해 사용
) AS t
WHERE t.seq <= (FLOOR(RAND() * 3) + 1)  -- 1부터 3개의 태그를 무작위로 선택
ORDER BY RAND()  -- 무작위로 정렬하여 태그를 추가
LIMIT 300000;  -- 원하는 태그 수만큼 추가

-- 5. 임시 테이블 삭제
DROP TEMPORARY TABLE IF EXISTS temp_meme_posts;


-- 임시 테이블 생성
CREATE TEMPORARY TABLE temp_meme_post_like (
                                               created_at DATETIME(6),
                                               meme_post_id BIGINT,
                                               updated_at DATETIME(6),
                                               user_id BIGINT
);

-- 임시 테이블에 데이터 삽입
INSERT INTO temp_meme_post_like (created_at, meme_post_id, updated_at, user_id)
SELECT
    TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650) DAY) + INTERVAL FLOOR(RAND() * 86400) SECOND) AS rand_time,
    FLOOR(1 + RAND() * 100000) AS meme_post_id,
    TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650) DAY) + INTERVAL FLOOR(RAND() * 86400) SECOND) AS rand_time,
    FLOOR(1 + RAND() * 100000) AS user_id
FROM (
         SELECT 1 AS seq
         FROM (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS seq1,
              (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS seq2,
              (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS seq3,
              (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS seq4,
              (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS seq5,
              (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS seq6,
              (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS seq7
     ) AS tmp
LIMIT 200000; -- 충분한 수의 조합을 생성

-- 중복되지 않은 데이터 메인 테이블에 삽입
INSERT INTO meme_post_like (created_at, meme_post_id, updated_at, user_id)
SELECT DISTINCT created_at, meme_post_id, updated_at, user_id
FROM temp_meme_post_like
WHERE NOT EXISTS (
    SELECT 1
    FROM meme_post_like
    WHERE meme_post_like.user_id = temp_meme_post_like.user_id
      AND meme_post_like.meme_post_id = temp_meme_post_like.meme_post_id
);




INSERT INTO `find_post` (`comment_count`, `created_at`, `deleted_at`, `selected_comment_id`, `updated_at`, `user_id`, `view_count`, `title`, `content`, `find_status`, `html_content`)
WITH RECURSIVE cte (n, created_at) AS (
    SELECT 1, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650) DAY) + INTERVAL FLOOR(RAND() * 86400) SECOND)  -- 최근 10년 내의 임의의 날짜와 시간 생성
    UNION ALL
    SELECT n + 1, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650) DAY) + INTERVAL FLOOR(RAND() * 86400) SECOND)
    FROM cte WHERE n < 100000 -- 생성하고 싶은 더미 데이터의 개수
)
SELECT
    FLOOR(RAND() * 100) AS comment_count,  -- 0부터 100 사이의 랜덤한 댓글 수
    created_at,  -- 생성된 created_at 사용
    NULL AS deleted_at,  -- 삭제된 날짜는 NULL로 설정
    NULL AS selected_comment_id,  -- 처음에는 NULL로 설정
    created_at AS updated_at,  -- updated_at을 created_at과 동일하게 설정
    FLOOR(1 + RAND() * 100000) AS user_id,  -- 1부터 100 사이의 랜덤한 user_id (예시로 최대 100으로 설정)
    FLOOR(RAND() * 1000) AS view_count,  -- 0부터 1000 사이의 랜덤한 조회 수
    CONCAT('찾기 포스트 제목 ', n) AS title,  -- 포스트 제목
    CONCAT('이것은 찾기 포스트 ', n, '의 내용입니다.') AS content,  -- 포스트 내용
    CASE WHEN FLOOR(RAND() * 2) = 0 THEN 'FIND' ELSE 'FOUND' END AS find_status,  -- 랜덤한 찾기 상태 선택
    CONCAT('<p>이것은 찾기 포스트 ', n, '의 HTML 내용입니다.</p>') AS html_content  -- HTML 내용
FROM cte;



INSERT INTO find_post_comment (content, created_at, user_id, find_post_id, updated_at, selected, html_content)
WITH RECURSIVE cte (n, created_at) AS (
    SELECT 1, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650) DAY) + INTERVAL FLOOR(RAND() * 86400) SECOND)  -- 최근 10년 내의 임의의 날짜와 시간 생성
    UNION ALL
    SELECT n + 1, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650) DAY) + INTERVAL FLOOR(RAND() * 86400) SECOND)
    FROM cte WHERE n < 100000 -- 생성하고 싶은 더미 댓글 수
)
SELECT
    CONCAT('이것은 댓글 ', n, '의 내용입니다.') AS content,  -- 댓글 내용
    created_at,  -- 생성된 created_at 사용
    FLOOR(1 + RAND() * 100000) AS user_id,  -- 1부터 100000 사이의 랜덤한 user_id
    FLOOR(1 + RAND() * (SELECT MAX(id) FROM find_post)) AS find_post_id,  -- 랜덤한 find_post_id 선택
    created_at AS updated_at,  -- updated_at을 created_at과 동일하게 설정
    FALSE AS selected,  -- selected는 기본값으로 FALSE 설정
    CONCAT('<p>이것은 댓글 ', n, '의 HTML 내용입니다.</p>') AS html_content  -- HTML 내용
FROM cte;


UPDATE find_post AS p
SET selected_comment_id = (
    SELECT id FROM find_post_comment AS c
    WHERE c.find_post_id = p.id
    ORDER BY RAND() LIMIT 1
)
WHERE EXISTS (
    SELECT 1 FROM find_post_comment AS c
    WHERE c.find_post_id = p.id
)
  AND FLOOR(RAND() * 2) = 0;  -- 50% 확률로 업데이트 (즉, 채택되지 않을 수 있음)


SELECT COUNT(*) AS user_count FROM users;
SELECT COUNT(*) AS file_meta_count FROM file_meta;
SELECT COUNT(*) AS tag_count FROM tag;
SELECT COUNT(*) AS post_tag_count FROM post_tag;
SELECT COUNT(*) AS meme_post_count FROM meme_post;
SELECT COUNT(*) AS find_post_count FROM find_post;
SELECT COUNT(*) AS find_post_comment_count FROM find_post_comment;
SELECT count(distinct post_id) FROM post_tag;
SELECT COUNT(*) FROM meme_post_like;
