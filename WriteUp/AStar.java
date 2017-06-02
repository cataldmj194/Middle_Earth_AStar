/** 
   Name : Michael Cataldo
   Class: CIS 421
   Assignment: A* algorithm
   Due: 10/24/2016
*/
import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;
import java.lang.IndexOutOfBoundsException;

public class AStar {

  public static ArrayList<MidEarthVertex> atlas; //list of all vertices
  public static ArrayList<MidEarthEdge> roadList; //list of all edges
  public static ArrayList<MidEarthVertex> openSet; //vertices we are looking
  public static Set<MidEarthVertex> closedSet; //vertices we have looked at
 
  public static void main(String[] args){

    //Get file from args and parse it. Run AStar depending on command line args
    //Print path found to standard out. Catch and throw exceptions.
    try{
      File f = new File(args[0]);
      Scanner sc = new Scanner(f);
      parse(sc);
      ArrayList<MidEarthVertex> path = new ArrayList<MidEarthVertex>();

      if(args[1].equals("1"))
        path = runAStar();
      else if(args[1].equals("2")){
        double qualityWeight = .1;
        double riskWeight = 10;
        double distWeight = 2;
        path = runAStar2(qualityWeight,riskWeight,distWeight);
      }else if(args[1].equals("3")){
        double qualityWeight = 10;
        double riskWeight = 3;
        double distWeight = .1;
        path = runAStar2(qualityWeight,riskWeight,distWeight);
      }
      printPath(path);

    }catch(FileNotFoundException e){
      System.out.println(e);
      System.exit(0);
    }catch(IndexOutOfBoundsException e){
      System.out.println(e);
      System.exit(0);
    }
  }

  //Postcondition: Run loop for A*. openSet contains nodes we are currently
  //               looking at. closedSet contains nodes we have already looked
  //               at. We gather the starting node from the user and set the
  //               goal node to Iron Hills. While the openSet still has nodes in
  //               it, get the node with the lowest F cost from the openSet and
  //               expand it. Returns the path when we find our way to IronHills 
  //               After we expand a node, we add the node to the closed set so
  //               we don't look at it again.
  public static ArrayList runAStar(){
    ArrayList<MidEarthVertex> path = new ArrayList<MidEarthVertex>();
    openSet = new ArrayList<MidEarthVertex>();
    closedSet = new HashSet<MidEarthVertex>();

    openSet.add(getStartingNode());
    MidEarthVertex goal = getGoal();

    while(!openSet.isEmpty()){
      MidEarthVertex current = getLowest();
      openSet.remove(current);
      if(current.getName().equals("Iron_Hills")){
        path = buildPath(goal);
        return path;
      }
      closedSet.add(current);
      expandNode(current); 
    }
    return path;
  }

  //Parameter: double qualityWeight, riskWeight, distWeight -
  //           all weights for calculating A* with different heuristics
  //Postcondition:
  //Runs the same loop for AStar as above, however factors in Road Quality,
  //Risk Level, and Distance using different weighted values.
  public static ArrayList runAStar2(double qualityWeight, double riskWeight,
                                   double distWeight){
    
    ArrayList<MidEarthVertex> path = new ArrayList<MidEarthVertex>();
    openSet = new ArrayList<MidEarthVertex>();
    closedSet = new HashSet<MidEarthVertex>();

    openSet.add(getStartingNode());
    MidEarthVertex goal = getGoal();

    while(!openSet.isEmpty()){
      MidEarthVertex current = getLowest();
      openSet.remove(current);
      if(current.getName().equals("Iron_Hills")){
        path = buildPath(goal);
        return path;
      }
      closedSet.add(current);
      expandNode2(current,qualityWeight,riskWeight,distWeight);
    }
    return path;
  }

  //Parameter: MidEarthVertex goal - our goal node
  //Postcondition: 
  //Builds our path starting from the goal and finding each parent that
  //the node has set.
  //Returns: the path taken by A*
  public static ArrayList buildPath(MidEarthVertex goal){
    MidEarthVertex parent = goal.getParent();
    ArrayList<MidEarthVertex> path = new ArrayList<MidEarthVertex>();
    path.add(goal);
    while(parent != null){
      path.add(parent);
      parent = parent.getParent();
    }
    return path;
  }

  //Parameter: ArrayList<MidEarthVertex> path - our path taken by A*
  //Postcondition: prints the path to standard out as well as the F value
  public static void printPath(ArrayList<MidEarthVertex> path){
    for(int i = path.size()-1; i >= 0; i--)
      System.out.println(path.get(i));
    System.out.println("F = " + path.get(0).getF());
  }

  //Postcondition: gets the goal node
  //Returns: the goal node
  public static MidEarthVertex getGoal(){
    MidEarthVertex goal = new MidEarthVertex();
    for(MidEarthVertex v : atlas){
      if(v.getName().equals("Iron_Hills")){
        goal = v;
      }
    }
    return goal;
  }

  //Postcondition: gets the node with the lowest F value in the openSet
  //Returns: node in openSet with lowest F value
  public static MidEarthVertex getLowest(){
    double f = 99999;
    MidEarthVertex lowest = new MidEarthVertex();
    for(MidEarthVertex v : openSet){
      if(v.getF() < f){
       lowest = v;
       f = lowest.getF(); 
      }
    }
    return lowest;
  }

  //Parameter: MidEarthVertex currentNode - the current node to expand
  //Postcondition: goes through the current node's edges / neighbours
  //               and calculates the g, h and f values. If closedSet
  //               contains the destination for our edge we skip it.
  //               if we havent seen the destination yet we add it to openset
  //               if the g we calculate is >= the g of the destination, we skip
  //               we then set the parent of the destination to the current node
  //               and then set the destination's g and f values.
  public static void expandNode(MidEarthVertex currentNode){
    ArrayList<MidEarthEdge> connections = getNeighbours(currentNode,0);
    for(MidEarthEdge e: connections){
      if(closedSet.contains(e.getDest()))
        continue;
      double temp_g = currentNode.getG() + e.getDistance();

      if(!openSet.contains(e.getDest()))
        openSet.add(e.getDest());
      else if(temp_g >= e.getDest().getG())
        continue;

      e.getDest().setParent(currentNode);
      e.getDest().setG(temp_g);      
      double f = e.getDest().getG() + getHeuristic(e.getDest());
      e.getDest().setF(f);
    }
  }

  //Parameter: MidEarthVertex currentNode - the current node to expand
  //           double riskWeight,qualityWeight,distWeight - weight nubmers
  //Postcondition: goes through the current node's edges / neighbours
  //               and calculates the g, h and f values. If closedSet
  //               contains the destination for our edge we skip it.
  //               if we havent seen the destination yet we add it to openset
  //               if the g we calculate is >= the g of the destination, we skip
  //               we then set the parent of the destination to the current node
  //               and then set the destination's g and f values.
  public static void expandNode2(MidEarthVertex currentNode,double riskWeight,
                                 double qualityWeight,double distWeight){
    ArrayList<MidEarthEdge> connections = getNeighbours(currentNode,0);
    for(MidEarthEdge e: connections){
      if(closedSet.contains(e.getDest()))
        continue;
      double temp_g = currentNode.getG() + (e.getDistance() * distWeight);
      temp_g += (e.getRiskLevel() * riskWeight) + 
                (qualityWeight * e.getRoadQuality());
      if(!openSet.contains(e.getDest()))
        openSet.add(e.getDest());
      else if(temp_g >= e.getDest().getG())
        continue;

      e.getDest().setParent(currentNode);
      e.getDest().setG(temp_g);
      double f = e.getDest().getG() + getHeuristic(e.getDest());
      e.getDest().setF(f);
    }
  }

  //Parameter: MidEarthEdge currentEdge - the edge we are looking at
  //           int total - the total value calculated this far
  //Postcondition: returns edge's distance plus total
  //Returns: the edge's distance plust total passed in
  public static int getG(MidEarthEdge currentEdge,int total){
    return currentEdge.getDistance() + total;
  }

  //Parameter: MidEarthVertex current - the current node we are looking at
  //Returns: the currentNode's distance to goal
  public static int getHeuristic(MidEarthVertex current){
    return current.distToGoal();
  }

  //Postcondition: Gathers the starting node from the user. Errors out if node
  //               provided is not in the txt document
  //Returns: the starting node, current.
  public static MidEarthVertex getStartingNode(){

    System.out.print("Entered your desired starting node: ");
    Scanner sc = new Scanner(System.in);
    String name = sc.next();
    boolean contains = false;
    MidEarthVertex current = new MidEarthVertex();

    for(MidEarthVertex v: atlas){
      if(v.getName().equals(name)){
        contains = true;
        current = v;
        current.setStart();
        break;
      }
    }
    if(!contains){
      System.out.println("No node with such a name: " + name);
      System.out.println("Exiting...");
      System.exit(0);
    }
    
    return current;
  }
 
  //Parameter: MidEarthVertex current - the current node we are looking at
  //Postcondition: Gathers all the successor nodes from the current node, adds
  //               them to an arraylist and returns it
  //Returns: successors - an arrayList containing the successor nodes
  public static ArrayList<MidEarthVertex> getNeighbours(MidEarthVertex current){
    ArrayList<MidEarthEdge> neighbours = new ArrayList<MidEarthEdge>();
    ArrayList<MidEarthVertex> successors = new ArrayList<MidEarthVertex>();
    for(MidEarthEdge e : roadList){
      if(current.getName().equals(e.getSource().getName()))
        neighbours.add(e);
    }
    for(MidEarthEdge e : neighbours){
      successors.add(e.getDest());
    }
    return successors;
  }

  //Parameter: MidEarthVertex current - the current node we are looking at
  //           int flag - used to differentiate getNeighbours methods
  //Postcondition: Gathers neighbouring edges for current node, adds them
  //               to an arraylist and returns it.
  //Returns: neighbours - an Arraylist of neighbouring edges
  public static ArrayList<MidEarthEdge> getNeighbours(MidEarthVertex current, 
                                                                    int flag){
    ArrayList<MidEarthEdge> neighbours = new ArrayList<MidEarthEdge>();
    for(MidEarthEdge e : roadList){
      if(current.getName().equals(e.getSource().getName()))
        neighbours.add(e);
    }
    return neighbours;
  }
  
  //Parameter: sc - a Scanner open on the File provided from stdin
  //Postcondition: Parses the input file into Vertices and Edges connected
  //               together. Adds vertices to ArrayList atlas, adds edges to
  //               ArrayList roadList.
  public static void parse(Scanner sc){
    atlas = new ArrayList<MidEarthVertex>();
    roadList = new ArrayList<MidEarthEdge>();

    for(int i = 0; i < 25; i++){
      String in = sc.nextLine();
      String[] line = in.split(",");
      MidEarthVertex locale;
      try{
        ArrayList<MidEarthEdge> temp = new ArrayList<MidEarthEdge>();
        String name = line[0];
        int toGoal = Integer.parseInt(line[1]);
        locale = new MidEarthVertex(name,toGoal);
        atlas.add(locale);
      }catch(NumberFormatException e){
        System.out.println("Received Number Format Exception from line:");
        System.out.println(Arrays.toString(line));
      }
    }

    for(int i = 0; i < 25; i++){
      String in = sc.nextLine();
      String[] line = in.split(",");
    
      for(int j = 0; j < line.length; j++){
        ArrayList<MidEarthEdge> temp = new ArrayList<MidEarthEdge>();
        try{
          MidEarthVertex dest = new MidEarthVertex();
          for(int k = 0; k < 25; k++){
            if(atlas.get(k).toString().equals(line[j]))
              dest = atlas.get(k);
          }
          int distance = Integer.parseInt(line[j+1]);
          int roadQuality = Integer.parseInt(line[j+2]);
          int riskLevel = Integer.parseInt(line[j+3]);

          MidEarthEdge road = new MidEarthEdge(atlas.get(i),dest,distance,
                                               roadQuality,riskLevel);
          temp.add(road);
          j+=3;
        }catch(NumberFormatException e){
          System.out.println("Received Number Format Exception from line:");
          System.out.println(Arrays.toString(line));
        }
        roadList.addAll(temp);
      }
    }
  }
}
