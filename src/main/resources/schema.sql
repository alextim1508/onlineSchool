DROP TABLE IF EXISTS  acl_entry;
DROP TABLE IF EXISTS  acl_object_identity;
DROP TABLE IF EXISTS  acl_sid;
DROP TABLE IF EXISTS  acl_class;


CREATE TABLE acl_sid( id BIGINT NOT NULL PRIMARY KEY auto_increment,
                      principal TINYINT(1) NOT NULL,
                      sid VARCHAR(100) NOT NULL,
                      CONSTRAINT unique_uk_1 UNIQUE(sid, principal));

CREATE TABLE acl_class( id BIGINT NOT NULL PRIMARY KEY auto_increment,
                        class VARCHAR(100) NOT NULL,
                        CONSTRAINT unique_uk_2 UNIQUE(class));

CREATE TABLE acl_object_identity( id BIGINT PRIMARY KEY auto_increment,
                                  object_id_class BIGINT NOT NULL,
                                  object_id_identity BIGINT NOT NULL,
                                  parent_object BIGINT,
                                  owner_sid BIGINT,
	                                entries_inheriting TINYINT(1) NOT NULL,
                                  CONSTRAINT unique_uk_3 UNIQUE(object_id_class, object_id_identity),
                                  CONSTRAINT foreign_fk_1 FOREIGN KEY(parent_object) REFERENCES acl_object_identity(id),
                                  CONSTRAINT foreign_fk_2 FOREIGN KEY(object_id_class) REFERENCES acl_class(id),
                                  CONSTRAINT foreign_fk_3 FOREIGN KEY(owner_sid) REFERENCES acl_sid(id));

CREATE TABLE acl_entry( id BIGINT PRIMARY KEY auto_increment,
	                      acl_object_identity BIGINT NOT NULL,
	                      ace_order INT NOT NULL,
	                      sid BIGINT NOT NULL,
	                      mask INTEGER NOT NULL,
	                      granting TINYINT(1) NOT NULL,
                        audit_success TINYINT(1) NOT NULL,
                        audit_failure TINYINT(1) NOT NULL,
                        CONSTRAINT unique_uk_4 UNIQUE(acl_object_identity, ace_order),
                        CONSTRAINT foreign_fk_4 FOREIGN KEY(acl_object_identity) REFERENCES acl_object_identity(id),
                        CONSTRAINT foreign_fk_5 FOREIGN KEY(sid) REFERENCES acl_sid(id));



--#####################
DROP TABLE IF EXISTS Meeting_Data;
DROP TABLE IF EXISTS Meetings_Users;
DROP TABLE IF EXISTS Meetings;
DROP TABLE IF EXISTS Parties_Students;
DROP TABLE IF EXISTS Parties_Teachers;
DROP TABLE IF EXISTS Parties;
DROP TABLE IF EXISTS Lessons;
DROP TABLE IF EXISTS Courses;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Users;


CREATE TABLE IF NOT EXISTS Courses (id BIGINT NOT NULL AUTO_INCREMENT,
                                    title VARCHAR(45) NOT NULL,
                                    description VARCHAR(45) NOT NULL,
                                    PRIMARY KEY (id),
                                    UNIQUE INDEX title_course_unique_index (title ASC));


CREATE TABLE IF NOT EXISTS Lessons (id BIGINT NOT NULL AUTO_INCREMENT,
                                    title VARCHAR(45) NOT NULL,
                                    homework VARCHAR(45) NULL,
                                    course_id BIGINT NOT NULL,
                                    PRIMARY KEY (id),
                                    UNIQUE INDEX title_course_id_lessons_unique_index (title ASC, course_id),
                                    CONSTRAINT fk_Lessons_Courses FOREIGN KEY (course_id) REFERENCES Courses(id));


CREATE TABLE IF NOT EXISTS Users (  id BIGINT NOT NULL AUTO_INCREMENT,
                                    username VARCHAR(45) NOT NULL,
                                    name VARCHAR(45) NOT NULL,
                                    surname VARCHAR(45) NOT NULL,
                                    email VARCHAR(45) NOT NULL,
                                    encoded_password VARCHAR(45) NOT NULL,
                                    phone VARCHAR(45) NULL,
                                    sent_sms VARCHAR(45) NULL,
                                    account_non_expired BIT NULL,
                                    account_non_locked BIT NULL,
                                    credentials_non_expired BIT NULL,
                                    enabled BIT NULL,
                                    PRIMARY KEY (id),
                                    UNIQUE INDEX email_user_unique_index (email ASC),
                                    UNIQUE INDEX username_user_unique_index (username ASC));


CREATE TABLE IF NOT EXISTS Roles(user_id BIGINT NOT NULL,
                                 role VARCHAR(45) NULL,
                                 UNIQUE INDEX role_roles_unique_index (role ASC),
                                 CONSTRAINT fk_Roles_User FOREIGN KEY (user_id) REFERENCES Users (id));


CREATE TABLE IF NOT EXISTS Parties ( id BIGINT NOT NULL AUTO_INCREMENT,
                                    title VARCHAR(45) NULL,
                                    course_id BIGINT NOT NULL,
                                    status VARCHAR(45) NOT NULL,
                                    PRIMARY KEY (id),
                                    UNIQUE INDEX title_parties_unique_index (title ASC),
                                    CONSTRAINT fk_Parties_Courses1 FOREIGN KEY (course_id) REFERENCES Courses(id));



CREATE TABLE IF NOT EXISTS Parties_Students (party_id BIGINT NOT NULL,
                                            user_id BIGINT NOT NULL,
                                            PRIMARY KEY (party_id, user_id),
                                            CONSTRAINT fk_Parties_has_Users_Parties1 FOREIGN KEY (party_id) REFERENCES Parties (id),
                                            CONSTRAINT fk_Parties_has_Users_Users1 FOREIGN KEY (user_id) REFERENCES Users (id));



CREATE TABLE IF NOT EXISTS Parties_Teachers (party_id BIGINT NOT NULL,
                                            user_id BIGINT NOT NULL,
                                            PRIMARY KEY (party_id, user_id),
                                            CONSTRAINT fk_Parties_has_Users1_Parties1 FOREIGN KEY (party_id) REFERENCES Parties (id),
                                            CONSTRAINT fk_Parties_has_Users1_Users1 FOREIGN KEY (user_id) REFERENCES Users (id));


CREATE TABLE IF NOT EXISTS Meetings(  id BIGINT NOT NULL AUTO_INCREMENT,
                                      meeting_date TIMESTAMP NOT NULL,
                                      lesson_id BIGINT NOT NULL,
                                      party_id BIGINT NOT NULL,
                                      PRIMARY KEY (id),
                                      CONSTRAINT fk_Meetings_Lessons1 FOREIGN KEY (lesson_id) REFERENCES Lessons (id),
                                      CONSTRAINT fk_Meetings_Parties1 FOREIGN KEY (party_id) REFERENCES Parties (id));




CREATE TABLE IF NOT EXISTS Meetings_Users(meeting_id BIGINT NOT NULL,
                                          user_id BIGINT NOT NULL,
                                          presence BIT NULL,
                                          PRIMARY KEY (meeting_id, user_id),
                                          CONSTRAINT fk_Meetings_has_Users_Meetings1 FOREIGN KEY (meeting_id) REFERENCES Meetings(id),
                                          CONSTRAINT fk_Meetings_has_Users_Users1 FOREIGN KEY (user_id) REFERENCES Users(id));




CREATE TABLE IF NOT EXISTS Meeting_Data ( id BIGINT NOT NULL AUTO_INCREMENT,
                                          title VARCHAR(45) NULL,
                                          url VARCHAR(45) NULL,
                                          meeting_id BIGINT NOT NULL,
                                          PRIMARY KEY (id),
                                          CONSTRAINT fk_MeetingData_Meetings1 FOREIGN KEY (meeting_id) REFERENCES Meetings(id));
