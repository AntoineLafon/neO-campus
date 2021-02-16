CREATE TABLE capteur (
    nomC CHAR(30) NOT NULL PRIMARY KEY,
    batiment CHAR(10),
    etage SMALLINT NOT NULL,
    lieu CHAR(20),
    nomTC CHAR(30),
    minBase FLOAT,
    maxBase FLOAT
)

CREATE TABLE mesure 
(
    valeur FLOAT,
    idC CHAR(30) NOT NULL,
    temps TIMESTAMP NOT NULL,
    PRIMARY KEY (idC,temps),
    FOREIGN KEY (idC) REFERENCES capteur (nomC)
)