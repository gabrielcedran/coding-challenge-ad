INSERT INTO
    CITY (ID, NAME)
VALUES
    (1, 'Zaragoza'),
    (2, 'Granada'),
    (3, 'Madrid');

INSERT INTO
    DESTINATION (ID, ID_ORIGIN_CITY, ID_DESTINATION_CITY, JOURNEY_TIME_IN_MINUTES)
VALUES
    (1, 1, 2, 60),
    (2, 1, 3, 90),
    (3, 2, 1, 30),
    (4, 2, 3, 120);