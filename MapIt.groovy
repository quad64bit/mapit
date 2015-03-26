import static TerminalPrintingTools.*

//Application runtime options! -------------------------------------------------
mapSize = 16
difficulty = 5
printSteps = false

//Gloabls ----------------------------------------------------------------------
ran = new Random()
map = new String[mapSize][mapSize]
playerLoc = [col:1, row:1]
exitLoc = [col:mapSize-2, row:mapSize-2]

//Location object definition ---------------------------------------------------
class Location implements Comparable<Location>{
  Integer col
  Integer row
  Integer d = 0

  String toString(){
    "($row,$col,$d)"
  }

  boolean equals(location){
    if(location instanceof Location){
      return location.col == col && location.row == row
    }
    return false
  }

  int compareTo(location){
    return  d <=> location.d
  }
}

//Initialize the the map, and add the player and exit --------------------------
def init(){
  (0..<mapSize).each{ row ->
    (0..<mapSize).each{ col ->
      //Add the border walls
      if(col == 0 || col == mapSize-1 || row == 0 || row == mapSize-1){
        map[row][col] = "X"
      } else{ // Add the empty map
        map[row][col] = " "
      }
    }
  }
  //Set the player stating position
  map[playerLoc.row][1] = "S"
  pinkl "start: ${playerLoc.row} x 1"

  //Set the exit
  map[exitLoc.row][mapSize-2] = "E"
  pinkl "end: ${exitLoc.row} x ${mapSize-2}"
}

//Enable or Disable specific terrain modifiers ---------------------------------
def injectObstacles(){
  // randomPoles()  // these don't work super well yet
  // randomRods()
  randomBlocks()
}

// Create random length veritcal walls -----------------------------------------
def randomPoles(){
  (difficulty*mapSize / 7 as int).times{
    index = ran.nextInt(map.size()-4)
    start = ran.nextInt(map.size()-2)
    length = ran.nextInt(mapSize-index-4)

    (start..(Math.min(start+length, mapSize - 1))).each{
      map[it][index] = "X"
    }
  }
}

// Create random length horizontal walls ---------------------------------------
def randomRods(){
  (difficulty*mapSize / 7 as int).times{
    index = ran.nextInt(map.size()-4)
    start = ran.nextInt(map.size()-2)
    length = ran.nextInt(mapSize-index-4)

    (start..(Math.min(start+length, mapSize - 1))).each{
      map[index][it] = "X"
    }
  }
}

// Add random wall segments one at a time --------------------------------------
def randomBlocks(){
  (difficulty*mapSize).times{
    map[ran.nextInt(mapSize-1)+1][ran.nextInt(mapSize-1)+1] = "X"
  }
}

// Print the current map to the terminal ---------------------------------------
// You can optionally include an overlay to print on top - such as a path or
// overlay map
def printMap(path = null){
  map.eachWithIndex{ row, i ->
    row.eachWithIndex{ col, j ->
      formatted = col+"  "
      plot = path.find{it.col == j && it.row == i}
      if(path && plot){
        pink "*  "
      } else{
        switch(col){
          case "X": print formatted; break
          case "S": green formatted; break;
          case "E": lightBlue formatted; break;
          default: yellow formatted
        }
      }
    }
    println ""
  }
}

// Pick a random player starting location on the leftmost column ---------------
def randomStart(){
  loc = ran.nextInt(mapSize-2)+1
  playerLoc = [row:loc, col:1]
}

// Pick a random exit location on the rightmost column -------------------------
def randomEnd(){
  loc = ran.nextInt(mapSize-2)+1
  exitLoc = [row:loc, col:mapSize-2]
}

// Shortest path implementation ------------------------------------------------
// Based on http://en.wikipedia.org/wiki/Pathfinding#Sample_algorithm
def findPath(){
  //Processing Queue
  queue = [] as Queue
  // Add the exit to the queue first
  queue << new Location(col:exitLoc.col, row:exitLoc.row)
  // The final successful sequence (unpruned) stored here
  sequence = []
  //Loop Control
  count = 0
  run = true
  lastSize = 0
  sizeCount = 0
  success = false

  while(queue.size() > 0 && run){
    list = getAdjacents(queue.peek())
    list.each{ newItem ->
      existing = queue.find{ (it.col == newItem.col && it.row == newItem.row) }
      if(map[newItem.row][newItem.col] == "X"){ //it's a wall
        //skip it
      } else if(existing?.d >= newItem.d){ //it's already in the queue
        //skip it
      } else{ //it's a new path location to check
        queue << newItem
      }
    }

    //Grab the top item from the queue for next round of processing
    top = queue.poll()
    //If we're at the start, we can stop looking
    if(top.col == playerLoc.col && top.row == playerLoc.row){
      run = false // Kill the loop
      println ""
      success = true
      sequence << top // Add last item to the sequence
      break
    }
    //If we're not done yet, and the item is not already in the sequence, add it
    if(!(top in sequence)){
      sequence << top
    }
    //This is a nasty way to figure out if we're stuck. If the sequence doesn't
    //grow for 100*mapSize cycles, then we're probably stuck, so terminate
    if(lastSize != sequence.size()){
      lastSize = sequence.size()
      sizeCount = 1
    } else{
      sizeCount++
    }
    if(sizeCount > 100*mapSize){
      run = false
    }

    count++ //count the iterations of the main loop

    //Print the sequence if enabeled
    if(printSteps && count % (mapSize * mapSize/2 as int) == 0){
      println ""
      greenl "Steps: ${count}"
      printMap(sequence)
    }
  }

  //if the result of the main loop was successful (path found) rather than getting
  //stuck, then can move on to pruning
  if(success){
    printMap(sequence)
    println ""
    //Create a weighted map from the sequence list created in main loop
    weightedMap = new Integer[mapSize][mapSize]
    sequence.each{ loc ->
      weightedMap[loc.row][loc.col] = loc.d
    }

    //Print the weighted Map
    weightedMap.each{ row ->
      row.each{ val ->
        if(val >= 0){
          pink "${val}".padRight(3)
        } else{
          print "~  "
        }
      }
      println ""
    }

    //The resulting shorted path
    shortestPath = []
    //Loop control
    run = true
    //The current location to begin pruning
    loc = new Location(col:playerLoc.col, row:playerLoc.row)
    nextSet = null
    //While there is a next item in sequence which has a near neighbor
    while((nextSet = getnearestNeighbor(loc, weightedMap))){
      //We can get multiple path choices (including where we have already been).
      //Pick the first one and if we have already been there, skip it can go to
      //the next
      while(shortestPath.contains(nextSet.peek())){
        nextSet.poll() //pop it off the queue and discard
      }
      //The next element to travel to
      next = nextSet.poll()
      if(next){ //if next isn't null
        if(next.d == 0){ //if we hit 0 weight, we are done (at the exit)
          break
        }
        shortestPath << next //otherwise, add current square to shortest path sequence
      }

      loc = next // setup next loop
    }
    println ""
    printMap(shortestPath)

  } else{ //we got stuck somewhere, so print last map and exit
    println ""
    printMap(sequence)
    redl "!!! STUCK !!!"
  }
}

//Find the list of closest neighbors, ignoring walls, and sorting by weight ASC
def getnearestNeighbor(location, weightedMap){
  if(!location){return null}
  l = []

  l << new Location(col:location.col+1, row:location.row, d:weightedMap[location.row][location.col+1])
  l << new Location(col:location.col, row:location.row+1, d:weightedMap[location.row+1][location.col])
  l << new Location(col:location.col-1, row:location.row, d:weightedMap[location.row][location.col-1])
  l << new Location(col:location.col, row:location.row-1, d:weightedMap[location.row-1][location.col])

  l = l.findAll{ it.d >= 0 && map[it.row][it.col] != "X" }.sort()
  l as Queue
}

//Get set of adjacent squares
def getAdjacents(location){
  list = []

  list << new Location(col:location.col+1, row:location.row, d:location.d+1)
  list << new Location(col:location.col, row:location.row+1, d:location.d+1)
  list << new Location(col:location.col-1, row:location.row, d:location.d+1)
  list << new Location(col:location.col, row:location.row-1, d:location.d+1)
  list
}

//Script Execution -------------------------------------------------------------
randomStart() //randomize start location (optional)
randomEnd()   //randomize ending location (optional)
init()
injectObstacles()
printMap()
findPath()
