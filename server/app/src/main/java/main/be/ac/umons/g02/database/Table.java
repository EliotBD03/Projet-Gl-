package main.be.ac.umons.g02.database;


import java.util.ArrayList;

public class Table
{
    private ArrayList<ArrayList<String>> table;
    public static final ArrayList<ArrayList<String>> EMPTY_TABLE = new ArrayList<>();

    public Table(Query query, String... returnedColumns)
    {
        try
        {
            if(query != null)
                DB.getInstance().executeQuery(query.getQuery(), true);
            this.table = transpose(DB.getInstance().getResults(returnedColumns));
        }
        catch (Exception e)
        {
            table = null;
            System.out.println(e.getMessage());
        }
    }

    private ArrayList<ArrayList<String>> transpose(ArrayList<ArrayList<String>> table) throws Exception
    {
        if(table.get(0).size() == 0)
            throw new Exception("the resulting table hasn't any element");

        int rows = table.size();
        int cols = table.get(0).size();

        ArrayList<ArrayList<String>> transposed = new ArrayList<>(cols);
        for(int i = 0; i < cols; i++)
            transposed.add(new ArrayList<>(rows));

        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                transposed.get(j).add(table.get(i).get(j));

        return transposed;
    }

    public int getIntElem(int i, int j)
    {
        return Integer.parseInt(table.get(i).get(j));
    }

    public double getDoubleElem(int i, int j){return Double.parseDouble(table.get(i).get(j));}
    public String getStringElem(int i, int j)
    {
        return this.table.get(i).get(j);
    }

    public ArrayList<ArrayList<String>> getTable()
    {
        if(table == null)
            return EMPTY_TABLE;

        ArrayList<ArrayList<String>> copy = new ArrayList<>();
        for(ArrayList<String> row : table)
            copy.add(new ArrayList<>(row));
        return copy;
    }

    public ArrayList<String> getColumn(int index)
    {
        ArrayList<String> col = new ArrayList<>();
        for(ArrayList<String> row : table)
            col.add(row.get(index));
        return col;
    }
}
