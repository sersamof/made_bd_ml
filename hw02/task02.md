

# 1.Сделать таблицу artists в Hive и вставить туда значения, используя датасет
```shell
tail -n +2 artists.csv > artists_raw.csv && hdfs dfs -copyFromLocal artists_raw.csv /user/kaggle/artists.csv
```
```sql
DROP TABLE IF EXISTS artists;
DROP TABLE IF EXISTS artists_tmp;
CREATE TABLE artists_tmp (
    mbid STRING,
    artist_mb STRING,
    artist_lastfm STRING,
    country_mb STRING,
    country_lastfm ARRAY<STRING>,
    tags_mb ARRAY<STRING>,
    tags_lastfm ARRAY<STRING>,
    listeners_lastfm BIGINT,
    scrobbles_lastfm BIGINT,
    ambiguous_artist BOOLEAN
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
COLLECTION ITEMS TERMINATED BY ';';
LOAD DATA INPATH '/user/kaggle/artists.csv' OVERWRITE INTO TABLE artists_tmp;

CREATE TABLE artists AS 
SELECT * FROM artists_tmp; 

DROP TABLE artists_tmp;
```

# 2. Исполнителя с максимальным числом скробблов
```sql
SELECT artist_lastfm, scrobbles_lastfm FROM artists
ORDER BY scrobbles_lastfm DESC
LIMIT 1;
```
The Beatles	517126254

# 3. Самый популярный тэг на ластфм
```sql
WITH tag_popularity AS (
  SELECT trim(tag) AS tag, count(*) AS tag_count FROM artists 
  LATERAL VIEW EXPLODE(tags_lastfm) exploded AS tag
  GROUP BY trim(tag)
)
SELECT * FROM tag_popularity
ORDER BY tag_count DESC
LIMIT 1;
```
seen live	99540

# 4. Самые популярные исполнители 10 самых популярных тегов ластфм
```sql
WITH tag_popularity AS (
  SELECT trim(tag) AS tag, count(*) AS tag_count FROM artists 
  LATERAL VIEW EXPLODE(tags_lastfm) exploded AS tag
  GROUP BY trim(tag)
),
top10_tags AS (
  SELECT tag, tag_count FROM tag_popularity
  ORDER BY tag_count DESC
  LIMIT 10
),
artists_tags AS (
  SELECT artist_lastfm, listeners_lastfm, trim(tag) AS artist_tag FROM artists
  LATERAL VIEW EXPLODE(tags_lastfm) exploded AS tag
),
artists_with_top10_tags AS (
  SELECT distinct artist_lastfm, listeners_lastfm FROM artists_tags
  WHERE artist_tag in (SELECT top10_tags.tag FROM top10_tags)
)
SELECT artist_lastfm, listeners_lastfm FROM artists_with_top10_tags
ORDER BY listeners_lastfm DESC
LIMIT 10
```
| artist\_lastfm | listeners\_lastfm |
| :--- | :--- |
| Coldplay | 5381567 |
| Radiohead | 4732528 |
| Red Hot Chili Peppers | 4620835 |
| Rihanna | 4558193 |
| Eminem | 4517997 |
| The Killers | 4428868 |
| Kanye West | 4390502 |
| Nirvana | 4272894 |
| Muse | 4089612 |
| Queen | 4023379 |

5. Топ-10 стран по кол-ву слушателей их артистов
```sql
SELECT trim(country) as country, sum(listeners_lastfm) as sum_listeners FROM artists
LATERAL VIEW EXPLODE(country_lastfm) exploded AS country
WHERE country != ''
GROUP BY trim(country)
ORDER BY sum_listeners DESC
LIMIT 10
```
| country | sum\_listeners |
| :--- | :--- |
| United States | 2238880627 |
| United Kingdom | 1143326102 |
| Germany | 415171928 |
| France | 341869967 |
| Sweden | 304948012 |
| Japan | 297054495 |
| Canada | 281324927 |
| Spain | 207277119 |
| Australia | 185475647 |
| Russia | 165656499 |
