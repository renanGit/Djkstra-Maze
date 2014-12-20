package assign2;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author Renan Santana
 * Assignment 2
 * Sept 21, 2014
 * COP4534 - Algorithm Techniques
 */

/*
 * The Maze :: Methods
 * SquareData: contents of a squre
 * ClearVisits: reset the visit of square to be false
 * PrintPath: prints the shortest path 
 * PQEntry: Priority Queue
 * ComputeDistences: Dijstra's Algorithm
 * AdjacentSquares: a square will its adj squares
 * AdjWall: a square can have a wall next to it
 * ReadFile: reads the maze 
*/
class Maze {
    
    public static final int INFINITY = Integer.MAX_VALUE / 3;
    public static final int[][] moves = {{-1,0,0,1}
                                        ,{0,-1,1,0}};
    
    public class SquareData{
        private LinkedList<SquareData> adj;
        private int distance;
        private LinkedList<SquareData> listOfWalls;
        private boolean isAlreadyDone;
        private SquareData sq;
        private String position;
        
        SquareData(){
            adj = new LinkedList<>();
            listOfWalls = new LinkedList<>();
            isAlreadyDone = false;
        }
        
        public List<SquareData> getAdjacents(){ return adj; }
        public void addAdjacent( SquareData s ){ adj.add(s); }
        
        public void setDistance( int d ){ distance = d; }
        public int getDistance(){ return distance; }

        public boolean hasWall( SquareData adj ){ return listOfWalls.contains(adj); }
        public void addWall( SquareData wall ){ listOfWalls.add(wall); }
        
        public boolean isAlreadyDone() { return isAlreadyDone; }
        public void setAlreadyDone() { isAlreadyDone = true; }
        public void clearAlreadyDone() { isAlreadyDone = false; }

        public SquareData getPrevious() { return sq; }
        public void setPrevious( SquareData sq ) { this.sq = sq; }
        
        public String getPosition(){ return position; }
        public void setPosition( String pos ){ position = pos; }
    }
    
    /*
     * Reset the visits on the square to be false
    */
    public void clearVisits(){
        for ( SquareData[] row : square )
            for( SquareData col : row )
                col.clearAlreadyDone();
    }
    
    /*
     * Simply starts at the end and appends the direction at the head of the 
     * string until we reach the start (start doesn't have a previous so stop).
     * Clear the visits on the graph.
    */
    public void printPath(){
        SquareData target, path = square[ numRows-1 ][ numCols-1 ];
        target = path;
        String fullpath = "";
        
        while( path.getPrevious() != null ){
            String[] current = path.getPosition().split(" ");
            String[] prev = path.getPrevious().getPosition().split(" ");
            
            // Some what clever. Subtract the positions to get the 
            // direction.
            int n1 = Integer.parseInt(current[0]) - Integer.parseInt(prev[0]);
            int n2 = Integer.parseInt(current[1]) - Integer.parseInt(prev[1]);

            String direction = n1 + "" + n2;
            switch( direction ){
                case "-10": fullpath = "N" + fullpath;
                    break;
                case "10": fullpath = "S" + fullpath;
                    break;
                case "0-1": fullpath = "W" + fullpath;
                    break;
                case "01": fullpath = "E" + fullpath;
                    break;
            }
            path = path.getPrevious();
        }
        
        int cost = target.getDistance();
        double walls = (cost - fullpath.length())/penalty;
        System.out.println("Cost of path: " + cost
                + "\tWalls Knocked Down: " + (int)walls);
        System.out.println( fullpath );
        clearVisits();
    }
    
    /*
     * Priority Queue needs the distance and which square we are on.
    */
    private class PQEntry implements Comparable<PQEntry>{
        private int dist;
        private SquareData square;
        
        public PQEntry ( int d, SquareData s ) { dist = d; square = s; }
        public SquareData getSquare() { return square; }
        
        public int compareTo( PQEntry other ) { return dist - other.dist; }
    }
    
    /*
     * Dijstra's Algorithm 
     * 1. Requires a start node. The finish node is currently
     *    at the last row/col.
     * 2. Priority queue: get the adjcent square.
     * 3. Determine if that square has a better cost (i.e path)
     * 4. If better add to the PQ or NOT better ignore the path
     * 4b. Save the previous square. Allows to know the absolute path later.
     * 5. Repeat (2)
    */
    public void computeDistances( int penalty ) {
        // Start node
        SquareData start = square[ 0 ][ 0 ];
        this.penalty = penalty;
        
        // Initialize all squares to be unreachable
        for ( SquareData[] row : square )
            for( SquareData col : row )
                col.setDistance( INFINITY );
        
        start.setDistance( 0 );
        PriorityQueue<PQEntry> pq = new PriorityQueue<>();
        
        pq.add( new PQEntry( 0, start) );
        
        while( !pq.isEmpty() ){
            PQEntry e = pq.remove();
            SquareData v = e.getSquare();
            
            if(v.isAlreadyDone())
                continue;
            v.setAlreadyDone();
            
            // Loop the adjacents
            for( SquareData w: v.getAdjacents() ){
                // The default cost of a edge
                int cvw = 1;
                
                // Hit a wall?
                if(v.hasWall(w))
                    cvw += penalty;
                
                if( v.getDistance() + cvw < w.getDistance() ){
                    w.setDistance( v.getDistance() + cvw );
                    pq.add( new PQEntry(w.getDistance(), w) );
                    w.setPrevious( v );
                }
            }
        }
    }
    
    /*
     * Adjacent Squares
     * 1. loop through the four preset directions
     * 1b. test the bounds of the preset "moves"
     * 1c. add the square to the adjacentcy list
    */
    public void adjacentSquares( SquareData sqData, int row, int col ){
        
        for(int i = 0; i < 4; i++){           
            int tempRow = row + moves[ 0 ][ i ];
            int tempCol = col + moves[ 1 ][ i ];
            
            if( tempRow > -1 && tempCol > -1 &&
                    tempRow < numRows && tempCol < numCols ){
                SquareData adj = ( SquareData )square[ tempRow ][ tempCol ];
                if( adj == null ){ adj = new SquareData(); }
                
                sqData.addAdjacent(adj);
                square[ tempRow ][ tempCol ] = adj;
            }
        }
    }
    
    /*
     * Adjacent Wall
     * I figured that the wall list will have at most 3 walls.
     * 1. Walls on the border are rejected (there's no need for it)
     * 2. Test if the wall is in the list
     * 2b. We save both sides of the wall (i.e v->w (East..) / w->v (West..))
    */
    public void adjWall( SquareData sqData, int row, int col ){
         
        if( (row < 0 || col < 0) || (row >= numRows || col >= numCols) ){ 
            return; }
        
        SquareData adj = ( SquareData )square[ row ][ col ];
        if( adj == null ){ adj = new SquareData(); }
        
        // 1. v->w hasWall 2. w->v haswall
        if( !sqData.hasWall(adj) )
            sqData.addWall( adj );
        
        if( !adj.hasWall(sqData) )
            adj.addWall( sqData );
        
        square[ row ][ col ] = adj;
    }
    
    /*
     * Read File
     * Massage the input into a data structure we can work with.
    */
    public void readFile( String file )throws Exception{
        // Test the file if it exist / error if not
        BufferedReader br;
        
        try{
            br = new BufferedReader(new FileReader(file));
        }catch( FileNotFoundException e ){ 
            System.out.println("Missing file: " + file); 
            throw new Exception();
        }
        
        String line;
        
        // Row/Col has to be int / error if not / maze has to be valid size
        if( (line = br.readLine()) != null ){
            String[] rowsCols = line.split(" ");
            try{
                numRows = Integer.parseInt( rowsCols[0] );
                numCols = Integer.parseInt( rowsCols[1] );
                
                if(numRows <= 0 || numCols <= 0)
                    throw new NumberFormatException();
                
            }catch(NumberFormatException e){ 
                System.out.println("Invalid Input : " + line); 
                throw new Exception();
            }
        }
        
        square = new SquareData[ numRows ][ numCols ];
        
        // Reading the File
        while( (line = br.readLine()) != null ){
            
            // First two tokens have to be int / next token is String
            String[] tokens = line.split(" ");
            try{
                int row = Integer.parseInt( tokens[0] );
                int col = Integer.parseInt( tokens[1] );
                
                // Check the bounds of the input
                if(row >= numRows || col >= numCols || row < 0 || col < 0)
                    throw new NumberFormatException();
                
                if(tokens.length == 2){ throw new NumberFormatException(); }
                
                // Check if square exist
                SquareData sqData = (SquareData)square[ row ][ col ];
                if( sqData == null ){ sqData = new SquareData(); }
               
                for ( int i = 0; i < tokens[2].length(); i++ ) {
                    switch ( tokens[2].charAt(i) ) {
                        case 'N': adjWall( sqData, row-1, col );
                            break;
                        case 'S': adjWall( sqData, row+1, col );
                            break;
                        case 'W': adjWall( sqData, row, col-1 );
                            break;
                        case 'E': adjWall( sqData, row, col+1 );
                            break;
                    }
                    square[ row ][ col ] = sqData;
                }
            }catch( NumberFormatException e ){
                    System.out.println("Format error, skipping : " + line); 
            }
        }
        
        // Check/Add adjacent squares
        for ( int row = 0; row < numRows; row++ ) {
            for ( int col = 0; col < numCols; col++ ) {
                SquareData sqData = ( SquareData )square[ row ][ col ];
                if(sqData == null){ sqData = new SquareData(); }
                
                adjacentSquares( sqData, row, col );
                sqData.setPosition( row + " " + col );
                square[ row ][ col ] = sqData;
            }
        }
    }
    
    private SquareData[][] square;
    private int penalty;
    private int numRows;
    private int numCols;
}

public class Assign2 {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    /*
     * Format of args: -p "int" -f "file"
    */
    public static void main(String[] args) throws IOException{
        
        List<Integer> penalties = new LinkedList<>();
        
        // false for -p input / true for -f input
        boolean mode = false;
        
        // read -p data / read -f data
        for(String str : args){
            if(str.equals("-f")){
                mode = true;
                continue;
            }
            if(str.equals("-p")){
                mode = false;
                continue;
            }
            if(!mode){
                try{
                    penalties.add(Integer.parseInt(str));
                }catch(NumberFormatException e){ 
                    System.out.println(str + " is not type int."); }
                continue;
            }
            
            System.out.println("** * MAZE -- File " + str + " * **");
            
            Maze maze = new Maze();
            try{
                maze.readFile(str);
            }catch(Exception e){ continue; }
            
            for (int penalty : penalties) {
                System.out.println("\nWall Penalty " + penalty + ": ");
                maze.computeDistances(penalty);
                maze.printPath();
            }
            System.out.println("** ** **\n");
        }
    }
}