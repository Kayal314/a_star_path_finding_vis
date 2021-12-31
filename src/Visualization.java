import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Visualization extends Algorithm{
    public JPanel panel;
    private final Grid grid;
    Cell start, end;
    int moveCount;
    JSlider speedControl;
    JButton searchButton;
    JButton resetButton;
    Visualization()
    {
        grid = new Grid(18, 29);
        JFrame frame=new JFrame("A* Path Finding Algorithm Visualization");
        frame.setSize(1560,1020);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel=new JPanel();
        panel.setLayout(null);
        frame.add(panel);
        speedControl=new JSlider(JSlider.VERTICAL,0,300,250);
        speedControl.setBounds(1490,230,30,400); // for speeding up/down the visualization
        panel.add(speedControl);
        JLabel speedLabel=new JLabel("SPEED");
        speedLabel.setBounds(1485,170,70,50);
        panel.add(speedLabel);
        moveCount=0;
        addControls();
        initializeGrid();
        frame.setResizable(false);
        frame.setVisible(true);
    }
    private void initializeGrid()
    {
        for (int i = 20; i < 920; i += 50) // 18
        {
            for(int j = 20; j < 1470; j += 50) // 29
            {
                int x = (i - 20) / 50;
                int y = (j - 20) / 50;
                grid.matrix[x][y].button.setBounds(j, i, 50, 50);
                panel.add(grid.matrix[x][y].button);
                grid.matrix[x][y].button.addActionListener(e -> {
                    if(moveCount == 0) {
                        grid.matrix[x][y].button.setBackground(Color.BLUE);
                        start=grid.matrix[x][y];
                        resetButton.setBackground(new Color(50, 155, 168));
                    }
                    else if(moveCount == 1) {
                        grid.matrix[x][y].button.setBackground(Color.CYAN);
                        end=grid.matrix[x][y];
                        searchButton.setBackground(new Color(50, 155, 168));
                    }
                    else if(!(start!=null&&grid.matrix[x][y]==start)&&!(end!=null&&grid.matrix[x][y]==end)) {
                            grid.matrix[x][y].reverseState();
                            if (grid.matrix[x][y].blocked)
                                grid.matrix[x][y].button.setBackground(Color.BLACK);
                            else
                                grid.matrix[x][y].button.setBackground(Color.WHITE);
                    }
                    moveCount++;
                });
            }
        }
    }
    private void addControls()
    {
        searchButton= new JButton("SEARCH");
        searchButton.setBackground(Color.GRAY);
        searchButton.setBounds(605,925,140,50);
        resetButton= new JButton("RESET");
        resetButton.setBackground(Color.GRAY);
        resetButton.setBounds(755,925,140,50);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(moveCount>1) {
                    int delay=310-speedControl.getValue();
                    findPath(grid, start, end, delay);
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i=0;i<18;i++)
                    for(int j=0;j<29;j++)
                        grid.matrix[i][j].reset();
                searchButton.setBackground(Color.GRAY);
                resetButton.setBackground(Color.GRAY);
                moveCount=0;
                start=null;
                end=null;
            }
        });
        panel.add(searchButton);
        panel.add(resetButton);
    }
    public static void main(String[] args) {
        new Visualization();
    }

}
