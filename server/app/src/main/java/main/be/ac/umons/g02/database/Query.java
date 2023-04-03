package main.be.ac.umons.g02.database;

public class Query
{
    private String query;

    public Query(String query)
    {
        this.query = query;
    }

    public String getQuery()
    {
        return query;
    }

    public Table executeAndGetResult(String... returnedColumns)
    {
        return new Table(this, returnedColumns);
    }

    public void executeWithoutResult()
    {
        DB.getInstance().executeQuery(query,false);
    }
}
