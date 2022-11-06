create table decision_maker.customer_segment
(
	personal_code varchar2(20 char) not null,
	segment_id    number            not null,
	constraint pk$customer_segment primary key (personal_code) using index tablespace dm_ind,
	constraint fk$customer_segment$segment_id foreign key (segment_id) references segment (id) on delete cascade
)
	tablespace dm_tab
/