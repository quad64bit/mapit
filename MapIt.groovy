import static TerminalPrintingTools.*

mapSize = 16
difficulty = 5
printSteps = false


ran = new Random()
map = new String[mapSize][mapSize]
playerLoc = [col:1, row:1]
exitLoc = [col:mapSize-2, row:mapSize-2]

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

def init(){
  (0..<mapSize).each{ row ->
    (0..<mapSize).each{ col ->
      if(col == 0 || col == mapSize-1 || row == 0 || row == mapSize-1){
        map[row][col] = "X"
      } else{
        map[row][col] = " "
      }
    }
  }
  map[playerLoc.row][1] = "S"
  pinkl "start: ${playerLoc.row} x 1"

  map[exitLoc.row][mapSize-2] = "E"
  pinkl "end: ${exitLoc.row} x ${mapSize-2}"
}

def injectObstacles(){
  // randomPoles()
  // randomRods()
  randomBlocks()
}

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

def randomBlocks(){
  (difficulty*mapSize).times{
    map[ran.nextInt(mapSize-1)+1][ran.nextInt(mapSize-1)+1] = "X"
  }
}

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

def randomStart(){
  loc = ran.nextInt(mapSize-2)+1
  playerLoc = [row:loc, col:1]
}

def randomEnd(){
  loc = ran.nextInt(mapSize-2)+1
  exitLoc = [row:loc, col:mapSize-2]
}

def findPath(){
  queue = [] as Queue
  queue << new Location(col:exitLoc.col, row:exitLoc.row)
  sequence = []
  count = 0
  run = true
  lastSize = 0
  sizeCount = 0
  success = false
  while(queue.size() > 0 && run){
    list = getAdjacents(queue.peek())
    skipped = 0
    list.each{ newItem ->
      existing = queue.find{ (it.col == newItem.col && it.row == newItem.row) }
      if(map[newItem.row][newItem.col] == "X"){
        //skip it
      } else if(existing?.d >= newItem.d){
        //skip it
      } else{
        queue << newItem
      }
    }
    // pinkl queue
    top = queue.poll()
    if(top.col == playerLoc.col && top.row == playerLoc.row){
      run = false
      // lightYellowl "Done!"
      println ""
      success = true
      sequence << top
      // greenl sequence
      break
    }
    if(!(top in sequence)){
      sequence << top
    }
    if(lastSize != sequence.size()){
      lastSize = sequence.size()
      sizeCount = 1
    } else{
      sizeCount++
    }
    if(sizeCount > 100*mapSize){
      run = false
    }
    // greenl sequence
    count++

    if(printSteps && count % (mapSize * mapSize/2 as int) == 0){
      println ""
      greenl "Steps: ${count}"
      printMap(sequence)
    }
  }
  if(success){
    printMap(sequence)
    // lightBluel "success"
    println ""
    weightedMap = new Integer[mapSize][mapSize]
    sequence.each{ loc ->
      weightedMap[loc.row][loc.col] = loc.d
    }

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

    shortestPath = []
    run = true
    loc = new Location(col:playerLoc.col, row:playerLoc.row)
    nextSet = null
    while((nextSet = getnearestNeighbor(loc, weightedMap))){
      while(shortestPath.contains(nextSet.peek())){
        nextSet.poll()
      }
      next = nextSet.poll()
      if(next){
        if(next.d == 0){
          break
        }
        shortestPath << next
      }

      loc = next
    }
    // cyanl shortestPath
    println ""
    printMap(shortestPath)

  } else{
    println ""
    printMap(sequence)
    redl "!!! STUCK !!!"
  }
}

def getnearestNeighbor(location, weightedMap){
  if(!location){return null}
  l = []

  l << new Location(col:location.col+1, row:location.row, d:weightedMap[location.row][location.col+1])
  l << new Location(col:location.col, row:location.row+1, d:weightedMap[location.row+1][location.col])
  l << new Location(col:location.col-1, row:location.row, d:weightedMap[location.row][location.col-1])
  l << new Location(col:location.col, row:location.row-1, d:weightedMap[location.row-1][location.col])

  l = l.findAll{ it.d >= 0 && map[it.row][it.col] != "X" }.sort()
  // lightYellowl l.collect{it.d}
  l as Queue
}

def getAdjacents(location){
  list = []

  list << new Location(col:location.col+1, row:location.row, d:location.d+1)
  list << new Location(col:location.col, row:location.row+1, d:location.d+1)
  list << new Location(col:location.col-1, row:location.row, d:location.d+1)
  list << new Location(col:location.col, row:location.row-1, d:location.d+1)
  list
}

randomStart()
randomEnd()
init()
injectObstacles()
printMap()
// Thread.sleep(3000)
findPath()
