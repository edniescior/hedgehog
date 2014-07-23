
DROP TABLE test_table_in_a;

CREATE TABLE test_table_in_a
(
   name varchar(12),
   code varchar(12),
   some_date date
);

DROP TABLE test_table_in_B;

CREATE TABLE test_table_in_b
(
   code varchar(12),
   some_number number(15)
);


DROP TABLE test_table_out;

CREATE TABLE test_table_out
(
        some_generated_key number(2),
	name varchar(12),
	code varchar(12),
	some_date date,
	some_number number(15)
);

DROP TABLE test_table_out2;

CREATE TABLE test_table_out2
(
	name varchar(12),
	code varchar(12)
);


--INSERT INTO test_table_in_a(name, code, some_date) VALUES ('FOO', 'F', TO_DATE('01-MAY-1972', 'DD-MON-YYYY'));
--INSERT INTO test_table_in_a(name, code, some_date) VALUES ('BAR', 'B', TO_DATE('14-FEB-1970', 'DD-MON-YYYY'));
--INSERT INTO test_table_in_a(name, code, some_date) VALUES ('CHU', 'C', TO_DATE('01-MAY-1972', 'DD-MON-YYYY'));

--INSERT INTO test_table_in_b(code, some_number) VALUES ('F', 1234);
--INSERT INTO test_table_in_b(code, some_number) VALUES ('B', 4321);

commit;


--select a.name, a.code, a.some_date, b.some_number from test_table_in_a a left join test_table_in_b b on b.code = a.code;

--select * from test_table_out;
