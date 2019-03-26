CREATE TABLE personnels ( 

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE patients ( 

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE codes (

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE ref_sensors (

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE sensors (

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE rooms (

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE buildings (

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE location (

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE ref_passages (

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE history_passages (

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE ref_alerts (

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE history_alerts (

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);



CREATE TABLE ref_breakdowns ( 

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);

CREATE TABLE history_breakdowns ( 

    id SERIAL NOT NULL PRIMARY KEY, 

    data json NOT NULL

);