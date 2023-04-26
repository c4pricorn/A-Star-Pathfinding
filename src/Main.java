import java.util.Scanner;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class Main
{
    private static class Cell
    {
        int x, y;
        int f, g, h;
        boolean isObstacle, isVisited, isPath;
        Cell parent;

        public Cell(int x, int y, boolean isObstacle)
        {
            this.x = x;
            this.y = y;
            this.isObstacle = isObstacle;
            isPath = false;
            isVisited = false;
            parent = null;
            f = g = h = 0;
        }

        // Calculate the heuristic value of the cell
        public void calculateHeuristic(Cell endCell) {
            h = Math.abs(endCell.x - x) + Math.abs(endCell.y - y);
        }

        // Calculate the f value of the cell
        public void calculateF()
        {
            f = g + h;
        }
    }
    private static class Grid
    {
        int numRows, numCols;
        Cell[][] cells;

        public Grid(int numRows, int numCols)
        {
            this.numRows = numRows;
            this.numCols = numCols;
            cells = new Cell[numRows][numCols];

            // Initialize each cell in the grid
            for (int i = 0; i < numRows; i++)
            {
                for (int j = 0; j < numCols; j++)
                {
                    cells[i][j] = new Cell(i, j, false);
                }
            }
        }

        // Set the given cells as obstacles in the grid
        public void setObstacles(ArrayList<Cell> obstacleCells)
        {
            for (Cell cell : obstacleCells)
            {
                cells[cell.x][cell.y].isObstacle = true;
            }
        }

        public void setPath(ArrayList<Cell> pathCells)
        {
            for (Cell cell : pathCells) {
                cells[cell.x][cell.y].isPath = true;
            }
        }

        // Get the neighbors of the given cell
        public ArrayList<Cell> getNeighbors(Cell cell)
        {
            ArrayList<Cell> neighbors = new ArrayList<>();

            // Check each neighboring cell
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++)
                {
                    // Ignore the current cell
                    if (i == 0 && j == 0)
                    {
                        continue;
                    }

                    int neighborX = cell.x + i;
                    int neighborY = cell.y + j;

                    // Check if the neighbor is out of bounds
                    if (neighborX < 0 || neighborX >= numRows || neighborY < 0 || neighborY >= numCols)
                    {
                        continue;
                    }

                    // Check if the neighbor is an obstacle
                    if (cells[neighborX][neighborY].isObstacle)
                    {
                        continue;
                    }

                    // Add the neighbor to the list
                    neighbors.add(cells[neighborX][neighborY]);
                }
            }

            return neighbors;
        }
    }

    public static void PrintGrid(Grid grid, boolean isResult)
    {
        String emptyCellStr;
        if(isResult)
            emptyCellStr = "   ";
        else
            emptyCellStr = " 0 ";

        for (int i = 0; i < grid.numRows; i++)
        {
            System.out.println(" ");
            for (int j = 0; j < grid.numCols; j++)
            {
                if(grid.cells[i][j].isPath)
                    System.out.print(" * ");
                else if(grid.cells[i][j].isObstacle)
                    System.out.print(" X ");
                else if(isResult)
                    System.out.print("   ");
                else
                    System.out.print(" 0 ");
            }
        }
        System.out.println();
    }

    // Main method
    public static void main(String[] args)
    {
        Scanner scnr = new Scanner(System.in);
        System.out.println("Enter cell size (x)");
        int i_gridSizeX = scnr.nextInt();
        scnr.nextLine();
        System.out.println("Enter cell size (y)");
        int i_gridSizeY = scnr.nextInt();
        scnr.nextLine();

        // Initialize the grid
        Grid grid = new Grid(i_gridSizeX, i_gridSizeY);

        // Set the start and end cells
        System.out.println("Enter starting cell (row)");
        int i_startCellX = scnr.nextInt();
        scnr.nextLine();
        System.out.println("Enter starting cell (column)");
        int i_startCellY = scnr.nextInt();
        scnr.nextLine();
        System.out.println("Enter target cell (row)");
        int i_endCellX = scnr.nextInt();
        scnr.nextLine();
        System.out.println("Enter target cell (column)");
        int i_endCellY = scnr.nextInt();
        scnr.nextLine();
        Cell startCell = grid.cells[i_startCellX][i_startCellY];
        Cell endCell = grid.cells[i_endCellX][i_endCellY];

        // Set the obstacles in the grid
        ArrayList<Cell> obstacleCells = new ArrayList<>();
        boolean obstacleInputContinue = false;

        while(!obstacleInputContinue)
        {
            System.out.println("Set obstacle pos X");
            int i_obstPosX = scnr.nextInt();
            scnr.nextLine();
            System.out.println("Set obstacle pos Y");
            int i_obstPosY = scnr.nextInt();
            scnr.nextLine();
            obstacleCells.add(grid.cells[i_obstPosX][i_obstPosY]);
            System.out.println("User Done? (true/false)");
            obstacleInputContinue = scnr.nextBoolean();

            grid.setObstacles(obstacleCells);
            PrintGrid(grid, false);
        }

        // Initialize the open and closed sets
        PriorityQueue<Cell> openSet = new PriorityQueue<>((c1, c2) -> c1.f - c2.f);
        ArrayList<Cell> closedSet = new ArrayList<>();

        // Add the start cell to the open set
        openSet.add(startCell);
        startCell.g = 0;
        startCell.calculateHeuristic(endCell);
        startCell.calculateF();

        // Run the A* algorithm
        while (!openSet.isEmpty()) {
            // Get the cell with the lowest f value from the open set
            Cell currentCell = openSet.poll();

            // Check if the current cell is the end cell
            if (currentCell == endCell) {
                // Build the path by following the parent pointers
                ArrayList<Cell> path = new ArrayList<>();
                Cell cell = currentCell;
                while (cell != null) {
                    path.add(cell);
                    grid.setPath(path);
                    cell = cell.parent;
                }
                PrintGrid(grid, true);
                System.out.println();
                System.out.println();
                System.out.println();
                // Print the path in reverse order
                System.out.println("Path:");
                for (int i = path.size() - 1; i >= 0; i--) {
                    Cell pathCell = path.get(i);
                    System.out.print("(" + pathCell.x + ", " + pathCell.y + ")");
                }
                break;
            }

            // Move the current cell from the open set to the closed set
            closedSet.add(currentCell);

            // Get the neighbors of the current cell
            ArrayList<Cell> neighbors = grid.getNeighbors(currentCell);

            // Process each neighbor
            for (Cell neighbor : neighbors) {
                // Check if the neighbor is already in the closed set
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                // Calculate the tentative g value of the neighbor
                int tentativeG = currentCell.g + 1;

                // Check if the neighbor is already in the open set
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                } else if (tentativeG >= neighbor.g) {
                    continue;
                }

                // Update the neighbor's parent and g value
                neighbor.parent = currentCell;
                neighbor.g = tentativeG;
                neighbor.calculateHeuristic(endCell);
                neighbor.calculateF();
            }
        }
    }
}