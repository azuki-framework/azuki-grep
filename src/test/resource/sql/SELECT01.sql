

-- subquery_factoring_clause
SELECT
    /*+ hind */
    DISTINCT
    column1
  , column2
FROM
    table1
  , (
        SELECT
            UNIQUE
            column1
          , column2
        FROM
            table4
    ) table2
  , (
        SELECT
            ALL
            column1
          , column2
        FROM
            table5
    ) table3
GROUP BY
    column1
  , column2
ORDER SIBLINGS BY
    column1
  , column2 ASC
  , column3 DESC
  , column4 NULLS FIRST
  , column5 NULLS LAST
  , column6 ASC NULLS FIRST
  , column7 DESC NULLS LAST
FOR UPDATE
    schema1.table1.column1
  , table1.column1
  , column1
WAIT 10