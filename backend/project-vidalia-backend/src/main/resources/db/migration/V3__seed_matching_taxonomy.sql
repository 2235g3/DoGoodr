INSERT INTO semantic_tags (name)
SELECT 'community support'
WHERE NOT EXISTS (SELECT 1 FROM semantic_tags WHERE name = 'community support');

INSERT INTO semantic_tags (name)
SELECT 'environment'
WHERE NOT EXISTS (SELECT 1 FROM semantic_tags WHERE name = 'environment');

INSERT INTO semantic_tags (name)
SELECT 'education'
WHERE NOT EXISTS (SELECT 1 FROM semantic_tags WHERE name = 'education');

INSERT INTO semantic_tags (name)
SELECT 'health and wellbeing'
WHERE NOT EXISTS (SELECT 1 FROM semantic_tags WHERE name = 'health and wellbeing');

INSERT INTO semantic_tags (name)
SELECT 'animals'
WHERE NOT EXISTS (SELECT 1 FROM semantic_tags WHERE name = 'animals');

INSERT INTO semantic_tags (name)
SELECT 'arts and culture'
WHERE NOT EXISTS (SELECT 1 FROM semantic_tags WHERE name = 'arts and culture');

INSERT INTO semantic_tags (name)
SELECT 'digital skills'
WHERE NOT EXISTS (SELECT 1 FROM semantic_tags WHERE name = 'digital skills');

INSERT INTO semantic_tags (name)
SELECT 'languages'
WHERE NOT EXISTS (SELECT 1 FROM semantic_tags WHERE name = 'languages');

INSERT INTO semantic_tags (name)
SELECT 'sports'
WHERE NOT EXISTS (SELECT 1 FROM semantic_tags WHERE name = 'sports');

INSERT INTO semantic_tags (name)
SELECT 'advocacy'
WHERE NOT EXISTS (SELECT 1 FROM semantic_tags WHERE name = 'advocacy');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Mentoring', id, false, 'SKILL'
FROM semantic_tags
WHERE name = 'education'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Mentoring');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Tutoring', id, false, 'SKILL'
FROM semantic_tags
WHERE name = 'education'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Tutoring');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Youth work', id, false, 'CAUSE'
FROM semantic_tags
WHERE name = 'community support'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Youth work');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Food banks', id, false, 'CAUSE'
FROM semantic_tags
WHERE name = 'community support'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Food banks');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Elder support', id, false, 'CAUSE'
FROM semantic_tags
WHERE name = 'community support'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Elder support');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Mental health', id, false, 'CAUSE'
FROM semantic_tags
WHERE name = 'health and wellbeing'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Mental health');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'First aid', id, false, 'SKILL'
FROM semantic_tags
WHERE name = 'health and wellbeing'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'First aid');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Conservation', id, false, 'CAUSE'
FROM semantic_tags
WHERE name = 'environment'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Conservation');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Litter picking', id, false, 'INTEREST'
FROM semantic_tags
WHERE name = 'environment'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Litter picking');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Animal care', id, false, 'CAUSE'
FROM semantic_tags
WHERE name = 'animals'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Animal care');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Event support', id, false, 'SKILL'
FROM semantic_tags
WHERE name = 'arts and culture'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Event support');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Social media', id, false, 'SKILL'
FROM semantic_tags
WHERE name = 'digital skills'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Social media');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Web design', id, false, 'SKILL'
FROM semantic_tags
WHERE name = 'digital skills'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Web design');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'English', id, false, 'LANGUAGE'
FROM semantic_tags
WHERE name = 'languages'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'English');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Spanish', id, false, 'LANGUAGE'
FROM semantic_tags
WHERE name = 'languages'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Spanish');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Coaching', id, false, 'SKILL'
FROM semantic_tags
WHERE name = 'sports'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Coaching');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Fundraising', id, false, 'SKILL'
FROM semantic_tags
WHERE name = 'advocacy'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Fundraising');

INSERT INTO label (name, semantic_tag_id, required, type)
SELECT 'Campaigning', id, false, 'SKILL'
FROM semantic_tags
WHERE name = 'advocacy'
  AND NOT EXISTS (SELECT 1 FROM label WHERE name = 'Campaigning');

INSERT INTO semantic_links (semantic_tag_one_id, semantic_tag_two_id, weight)
SELECT first_tag.id, second_tag.id, 0.75
FROM semantic_tags first_tag, semantic_tags second_tag
WHERE first_tag.name = 'community support'
  AND second_tag.name = 'health and wellbeing'
  AND NOT EXISTS (
      SELECT 1 FROM semantic_links
      WHERE semantic_tag_one_id = first_tag.id AND semantic_tag_two_id = second_tag.id
  );

INSERT INTO semantic_links (semantic_tag_one_id, semantic_tag_two_id, weight)
SELECT first_tag.id, second_tag.id, 0.65
FROM semantic_tags first_tag, semantic_tags second_tag
WHERE first_tag.name = 'education'
  AND second_tag.name = 'community support'
  AND NOT EXISTS (
      SELECT 1 FROM semantic_links
      WHERE semantic_tag_one_id = first_tag.id AND semantic_tag_two_id = second_tag.id
  );

INSERT INTO semantic_links (semantic_tag_one_id, semantic_tag_two_id, weight)
SELECT first_tag.id, second_tag.id, 0.6
FROM semantic_tags first_tag, semantic_tags second_tag
WHERE first_tag.name = 'digital skills'
  AND second_tag.name = 'advocacy'
  AND NOT EXISTS (
      SELECT 1 FROM semantic_links
      WHERE semantic_tag_one_id = first_tag.id AND semantic_tag_two_id = second_tag.id
  );

INSERT INTO semantic_links (semantic_tag_one_id, semantic_tag_two_id, weight)
SELECT first_tag.id, second_tag.id, 0.55
FROM semantic_tags first_tag, semantic_tags second_tag
WHERE first_tag.name = 'environment'
  AND second_tag.name = 'animals'
  AND NOT EXISTS (
      SELECT 1 FROM semantic_links
      WHERE semantic_tag_one_id = first_tag.id AND semantic_tag_two_id = second_tag.id
  );
