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

    public Table ExecuteAndGetResult(String... returnedColumns)
    {
        return new Table(this, returnedColumns);
    }

    public void ExecuteWithoutResult()
    {
        DB.getInstance().executeQuery(query,false);
    }
}
