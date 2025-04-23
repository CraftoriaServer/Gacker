CREATE TABLE gacker (
    id BIGINT NOT NULL AUTO_INCREMENT,
    mcid VARCHAR(16) NOT NULL,
    uuid VARCHAR(36) NOT NULL,
    department_id VARCHAR(2) NULL,
    grade VARCHAR(2) NULL,
    PRIMARY KEY (id)
)

CREATE TABLE gacker_department (
    id VARCHAR(2) NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (faculty_id) REFERENCES gacker_faculty(id)
);