drop table tcompetition_team;
drop table tmatch;
drop table tsaison;
drop table tcompetition;
drop table tteam;
drop table tpays;





drop SEQUENCE pays_seq;
drop SEQUENCE competition_seq;
drop SEQUENCE team_seq;
drop SEQUENCE saison_seq;
drop SEQUENCE match_seq;
drop SEQUENCE competition_team_seq;



create table tsaison (
  id number(5) not null primary key,
  date_debut Date not null,
  date_fin DATE not null,
  current_match_day number(2) not null,
  winner number,
  competition_id number not null,
  api_id number
);  
create sequence saison_seq;

create table tpays (
  id number(5) not null primary key,
  nom VARCHAR2(50) not null,
  drapeau VARCHAR2(50),
  api_id number
  );  
create sequence pays_seq;


create table tcompetition (
   id number not null primary key,
   nom VARCHAR2(50) not null,
   code VARCHAR2(5),
   pays_id number not null,
   api_id number,
   CONSTRAINT FK_Competition_pays FOREIGN KEY (pays_id) REFERENCES tpays (id)
);
create sequence competition_seq;

create table tteam (
  id number(5) not null primary key,
  name VARCHAR2(50) not null,
  shortname VARCHAR2(20) null,
  tla VARCHAR2(20) null,
  address VARCHAR2(200) null,
  phone VARCHAR2(30) null,
  website VARCHAR2(50) null,
  founded VARCHAR2(20) null,
  club_colors VARCHAR2(200) null,
  venue VARCHAR2(200) null,
  last_updated DATE null,
  pays_id number not null,
  api_id number,
  CONSTRAINT FK_Team_Pays FOREIGN KEY (pays_id) REFERENCES tpays (id)
);  
create sequence team_seq;

create table tcompetition_team (
  id number(8) not null primary key,
  competition_id number(5) not null,
  team_id number(5) not null,
  saison_id number(5),
  CONSTRAINT FK_ct_competition FOREIGN KEY (competition_id) REFERENCES tcompetition (id),
  CONSTRAINT FK_ct_team FOREIGN KEY (team_id) REFERENCES tteam (id),
  CONSTRAINT FK_ct_saison FOREIGN KEY (saison_id) REFERENCES tsaison (id)
);
create sequence competition_team_seq;

create table tmatch (
  id number not null primary key,
  utc_date Date null,
  match_day number(2) not null,
  date_maj Date not null,
  team_home_id number not null,
  team_away_id number not null,
  status varchar(15) not null,
  but_home_full number(2),
  but_away_full number(2),
  but_home_half number(2),
  but_away_half number(2),
  but_home_extra number(2),
  but_away_extra number(2),
  but_home_penalties number(2),
  but_away_penalties number(2),
  saison_id number not null,
  competition_id number not null,
  api_id number,
  CONSTRAINT FK_score_team_home FOREIGN KEY (team_home_id) REFERENCES tteam (id),
  CONSTRAINT FK_score_team_away FOREIGN KEY (team_away_id) REFERENCES tteam (id),
  CONSTRAINT FK_cm_competition FOREIGN KEY (competition_id) REFERENCES tcompetition (id),
  CONSTRAINT FK_cm_saison FOREIGN KEY (saison_id) REFERENCES tsaison (id)

);
create sequence match_seq;

-- create table tcompetition_match (
--   id number not null primary key,
--   saison_id number not null,
--   competition_id number not null,
--   match_id number,
--   CONSTRAINT FK_cm_competition FOREIGN KEY (competition_id) REFERENCES tcompetition (id),
--   CONSTRAINT FK_cm_match FOREIGN KEY (match_id) REFERENCES tmatch (id),
--   CONSTRAINT FK_cm_saison FOREIGN KEY (saison_id) REFERENCES tsaison (id)
-- );
--  create sequence competition_match_seq;
  
alter table tsaison
add CONSTRAINT FK_saison_team FOREIGN KEY (winner) REFERENCES tteam (id);

alter table tsaison
add CONSTRAINT FK_saison_competition FOREIGN KEY (competition_id) REFERENCES tcompetition (id);


select * from tpays;
select * from tcompetition; 
-- where pays_id=23;
select * from tteam;
select * from TCOMPETITION_TEAM;
select * from tsaison;

select competitio0_.team_id as team_id3_1_0_, competitio0_.id as id1_1_0_, competitio0_.id as id1_1_1_, competitio0_.competition_id as competition_id2_1_1_, competitio0_.team_id as team_id3_1_1_, competitio1_.id as id1_0_2_, competitio1_.api_id as api_id2_0_2_, competitio1_.code as code3_0_2_, competitio1_.nom as nom4_0_2_, competitio1_.pays_id as pays_id5_0_2_, paysentity2_.id as id1_2_3_, paysentity2_.api_id as api_id2_2_3_, paysentity2_.nom as nom3_2_3_ 
from tcompetition_team competitio0_ left outer join tcompetition competitio1_ on competitio0_.competition_id=competitio1_.id left outer join tpays paysentity2_ on competitio1_.pays_id=paysentity2_.id
where competitio0_.team_id=21;

update tpays set drapeau='./assets/drap/espagne.png' where id = 50;
update tpays set drapeau='./assets/drap/angleterre.png' where id = 19;
update tpays set drapeau='./assets/drap/france.png' where id = 23;






