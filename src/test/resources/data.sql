-- 대분류 태그 삽입
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('감정', NULL, 'emotion');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('인사', NULL, 'greeting');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('TV', NULL, 'tv');

-- 소분류 태그 삽입
-- 감정 소분류
-- 대분류 태그의 id 값을 알아야 하므로, 삽입 후 조회
SET @emotion_id = (SELECT id FROM tag WHERE name = '감정');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('귀여운', @emotion_id, 'cute');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('화난', @emotion_id, 'angry');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('웃긴', @emotion_id, 'funny');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('슬픈', @emotion_id, 'sad');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('놀란', @emotion_id, 'surprised');

-- 인사 소분류
SET @greeting_id = (SELECT id FROM tag WHERE name = '인사');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('안녕하세요', @greeting_id, 'hello');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('감사합니다', @greeting_id, 'thank-you');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('미안합니다', @greeting_id, 'sorry');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('새해인사', @greeting_id, 'new-year-greeting');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('생일 축하', @greeting_id, 'happy-birthday');

-- TV 소분류
SET @tv_id = (SELECT id FROM tag WHERE name = 'TV');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('무한도전', @tv_id, 'infinite-challenge');
INSERT INTO tag (name, parent_tag_id, slug) VALUES ('기타', @tv_id, 'others');
