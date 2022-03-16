CREATE FUNCTION change_trigger() RETURNS trigger AS
$$
BEGIN

    IF TG_OP = 'INSERT'
    THEN

        INSERT INTO change (changed_at)
        VALUES (now());

        RETURN NEW;
    ELSIF TG_OP = 'UPDATE'
    THEN

        INSERT INTO change (changed_at)
        VALUES (now());

        RETURN NEW;
    ELSIF TG_OP = 'DELETE'
    THEN

        INSERT INTO change (changed_at)
        VALUES (now());

        RETURN OLD;
    END IF;

    RETURN NEW;
END;

$$ LANGUAGE 'plpgsql';