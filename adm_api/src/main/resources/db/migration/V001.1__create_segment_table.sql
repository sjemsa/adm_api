create table decision_maker.segment
(
	id              number not null,
	name            varchar2(10 char),
	credit_modifier number not null,
	constraint pk$segment primary key (id) using index tablespace dm_ind
) tablespace dm_tab
/