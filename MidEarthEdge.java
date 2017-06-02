/** 
   Name : Michael Cataldo
   Class: CIS 421
   Assignment: A* Algorithm
   Due: 10/24/2016
*/

public class MidEarthEdge {

  MidEarthVertex source;
  MidEarthVertex dest;
  int distance;
  int roadQuality;
  int riskLevel;

  public MidEarthEdge(MidEarthVertex source, MidEarthVertex dest, 
                     int distance, int roadQuality, int riskLevel){

    this.source = source;
    this.dest = dest;
    this.distance = distance;
    this.roadQuality = roadQuality;
    this.riskLevel = riskLevel;

  }

  public MidEarthVertex getSource(){
    return source;
  }

  public MidEarthVertex getDest(){
    return dest;
  }

  public int getDistance(){
    return distance;
  }

  public int getRoadQuality(){
    return roadQuality;
  }

  public int getRiskLevel(){
    return riskLevel;
  }

  public String toString(){
    return source + " -> " + dest + " : " + distance + " " + roadQuality +
           " " + riskLevel;
  }  

}

