create table spmember(
  mem_num number not null,
  id varchar2(12) unique not null,
  auth number(1) default 2 not null, /*0:탈퇴회원, 1:정지회원, 2:일반회원, 3:관리자*/
  constraint spmember_pk primary key (mem_num)
);

create table spmember_detail(
  mem_num number not null,
  name varchar2(30) not null,
  passwd varchar2(35) not null,
  phone varchar2(15) not null,
  email varchar2(50) not null,
  zipcode varchar2(5) not null,
  address1 varchar2(90) not null,
  address2 varchar2(90) not null,
  photo blob,
  photo_name varchar2(100),
  reg_date date default sysdate not null,
  modify_date date default sysdate not null,
  constraint spmember_detail_pk primary key (mem_num),
  constraint spmember_detail_fk foreign key (mem_num) references spmember (mem_num)
);

create sequence spmember_seq;

create table spboard(
  board_num number not null,
  title varchar2(100) not null,
  content clob not null,
  hit number(5) default 0 not null,
  reg_date date default sysdate not null,
  modify_date date default sysdate not null,
  uploadfile blob,
  filename varchar2(100),
  ip varchar2(40) not null,
  mem_num number not null,
  constraint spboard_pk primary key (board_num),
  constraint spboard_fk foreign key (mem_num) references spmember (mem_num)
);

create sequence spboard_seq;

create table spboard_reply(
  re_num number not null,
  re_content varchar2(900) not null,
  re_date date default sysdate not null,
  re_mdate date default sysdate not null,
  re_ip varchar2(40) not null,
  board_num number not null,
  mem_num number not null,
  constraint spboard_reply_pk primary key (re_num),
  constraint spboard_reply_fk1 foreign key (board_num) 
                               references spboard (board_num),
  constraint spboard_reply_fk2 foreign key (mem_num)
                               references spmember (mem_num)
);

create sequence reply_seq;





