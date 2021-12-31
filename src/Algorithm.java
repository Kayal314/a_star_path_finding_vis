import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;


public class Algorithm {
    void findPath(Grid grid,Cell start,Cell end, int delay){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MinHeap open=new MinHeap(grid.rows*grid.cols);
                Hashtable<Cell, Boolean> closed = new Hashtable<>();
                Hashtable<Cell, Boolean> open_hash = new Hashtable<>();
                start.button.setBackground(Color.BLUE);
                end.button.setBackground(Color.CYAN);
                open.insert(start);
                open_hash.put(start, true);
                while(closed.size()<=792){
                    if(open.getSize()==0) {
                        JOptionPane.showMessageDialog(null,"No path exists from source to destination");
                        return;
                    }
                    Cell current=open.extract_min();
                    current.button.setBackground(new Color(255,0,0));
                    open_hash.remove(current);
                    closed.put(current,true);
                    if(current==end)
                    {
                        retracePath(end, start, delay);
                        return;
                    }
                    Iterator<Integer> neighbors=grid.getNeighbors(current.x,current.y);
                    while(neighbors.hasNext()){
                        Cell neighbor=grid.matrix[neighbors.next()][neighbors.next()];
                        if(neighbor.blocked||closed.containsKey(neighbor))
                            continue;
                        if(neighbor.g_cost>current.g_cost+ current.get_distance(neighbor) || !open_hash.containsKey(neighbor))
                        {
                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!closed.containsKey(neighbor))
                                neighbor.button.setBackground(new Color(67, 242, 58));
                            neighbor.g_cost=current.g_cost+ current.get_distance(neighbor);
                            neighbor.h_cost=end.get_distance(neighbor);
                            neighbor.f_cost=neighbor.g_cost+ neighbor.h_cost;
                            neighbor.button.setText(String.valueOf(neighbor.f_cost));
                            neighbor.parent=current;
                            if(!open_hash.containsKey(neighbor)) {
                                open_hash.put(neighbor, true);
                                open.insert(neighbor);
                            }
                        }
                    }

                }
            }
        }).start();
    }
    void retracePath(Cell end, Cell start, int delay)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cell cell = end;
                int blue=120;
                int change=5;
                cell.button.setBackground(new Color(40,90,blue));
                if(cell.parent==null)
                    return;
                do
                {
                    cell=cell.parent;
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    blue+=change;
                    if(blue>=255)
                    {
                        blue = 255;
                        change = 0;
                    }
                    cell.button.setBackground(new Color(40,90,blue));
                }while(cell!=start);
                JOptionPane.showMessageDialog(null,"Path Found");
            }
        }).start();
    }
}

class Cell
{
    int f_cost,g_cost,h_cost;
    int x,y;
    JButton button;
    boolean blocked;
    Cell parent;
    Cell(int x,int y)
    {
        this.x=x;
        button=new JButton();
        button.setBackground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 10));
        this.y=y;
        f_cost=0;
        g_cost=0;
        h_cost=0;
        blocked=false;
        parent=null;
    }
    void reset()
    {
        f_cost=0;
        g_cost=0;
        h_cost=0;
        blocked=false;
        parent=null;
        button.setBackground(Color.WHITE);
        button.setText("");
    }
    public String toString()
    {
        return "("+x+","+ y +")";
    }
    int get_distance(Cell cell, String metric)
    {
        if(metric.equals("euclidean")||metric.equals("l2"))
            return (int)(5*Math.sqrt(Math.pow(cell.x-this.x,2)+Math.pow(cell.y-this.y,2)));
        if(metric.equals("manhattan")||metric.equals("l1"))
            return 5*(Math.abs(cell.x-this.x)+Math.abs(cell.y-this.y));
        else if(metric.equals("chebyshev")||metric.equals("maximum"))
            return 5*Math.max(Math.abs(cell.x-this.x),Math.abs(cell.y-this.y));
        return 0;
    }
    // default metric: euclidean distance
    int get_distance(Cell cell)
    {
        return (int)(5*Math.sqrt(Math.pow(cell.x-this.x,2)+Math.pow(cell.y-this.y,2)));
    }
    void reverseState()
    {
        blocked=!blocked;
    }
}

class Grid
{
    int rows,cols;
    Cell[][] matrix;
    Grid(int rows,int cols){
        this.rows=rows;
        this.cols=cols;
        matrix=new Cell[rows][cols];
        for(int i=0;i<rows; i++)
            for(int j=0;j<cols;j++)
                matrix[i][j]=new Cell(i, j);
    }
    private boolean isValid(int x,int y)
    {
        return x>=0&&x<rows&&y>=0&&y<cols;
    }
    Iterator<Integer> getNeighbors(int x, int y)
    {
        LinkedList<Integer> neighbors=new LinkedList<>();
        if(isValid(x-1,y-1))
        {
            neighbors.add(x-1);
            neighbors.add(y-1);
        }
        if(isValid(x,y-1))
        {
            neighbors.add(x);
            neighbors.add(y-1);
        }
        if(isValid(x+1,y-1))
        {
            neighbors.add(x+1);
            neighbors.add(y-1);
        }
        if(isValid(x-1,y))
        {
            neighbors.add(x-1);
            neighbors.add(y);
        }
        if(isValid(x+1,y))
        {
            neighbors.add(x+1);
            neighbors.add(y);
        }
        if(isValid(x-1,y+1))
        {
            neighbors.add(x-1);
            neighbors.add(y+1);
        }
        if(isValid(x,y+1))
        {
            neighbors.add(x);
            neighbors.add(y+1);
        }
        if(isValid(x+1,y+1))
        {
            neighbors.add(x+1);
            neighbors.add(y+1);
        }return neighbors.iterator();
    }

}
class MinHeap
{

    final Cell[] arr;
    private int lastIndex;
    public MinHeap(int n)
    {
        arr=new Cell[n];
        lastIndex=-1;
    }
    void insert(Cell data) // O(log(n)) time
    {
        arr[++lastIndex]=data;
        moveUp(lastIndex);
    }
    Cell extract_min() // O(log(n)) time
    {
        Cell min=arr[0];
        arr[0]=arr[lastIndex--];
        moveDown(0);
        return min;
    }
    Cell show_min()
    {
        return arr[0];
    }
    int getSize()
    {
        return lastIndex+1;
    }
    private void moveUp(int index)
    {
        int parent_index = (index-1)/2;
        if(parent_index<0)
            return;
        if(arr[parent_index].f_cost>arr[index].f_cost) {
            swap(parent_index, index);
            moveUp(parent_index);
        }
    }
    private void moveDown(int index)
    {
        int child_index;
        if(2*index+1>lastIndex)
            return;
        else if(2*index+1==lastIndex)
            child_index=2*index+1;
        else
            child_index=arr[2*index+1].f_cost<arr[2*index+2].f_cost?2*index+1:2*index+2;
        if(arr[child_index].f_cost<arr[index].f_cost) {
            swap(child_index, index);
            moveDown(child_index);
        }

    }
    private void swap(int n, int m)
    {
        Cell temp=arr[n];
        arr[n]=arr[m];
        arr[m]=temp;
    }

}


