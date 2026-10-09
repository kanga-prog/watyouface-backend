-- Manual PostgreSQL migration. Run only after reviewing the duplicate preflight:
-- SELECT listing_id, COUNT(*) FROM "transaction" GROUP BY listing_id HAVING COUNT(*) > 1;
-- Any returned rows must be reconciled before applying this migration.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM "transaction"
        GROUP BY listing_id
        HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'Cannot add uq_transaction_listing_id: duplicate listing transactions exist';
    END IF;

    ALTER TABLE "transaction"
        ADD CONSTRAINT uq_transaction_listing_id UNIQUE (listing_id);
END
$$;
