class TerminalPrintingTools{
	static PINK      		= '\033[95m'
	static LIGHT_BLUE      	= '\033[94m'
	static BLUE        		= '\033[34m'
	static GREEN         	= '\033[92m'
	static LIGHT_RED        = '\033[91m'
	static RED         		= '\033[31m'
	static CYAN        		= '\033[36m'
	static RED_HIGHLIGHT    = '\033[41m'
	static LIGHT_YELLOW 	= '\033[93m'
	static YELLOW      		= '\033[33m'
	static ENDC         	= '\033[0m'

	static def pink = { msg ->
		  print "${PINK}${msg}${ENDC}"
	}

	static def lightBlue = { msg ->
	    print "${LIGHT_BLUE}${msg}${ENDC}"
	}

	static def blue = { msg ->
	    print "${BLUE}${msg}${ENDC}"
	}

	static def green = { msg ->
	    print "${GREEN}${msg}${ENDC}"
	}

	static def lightRed = { msg ->
	    print "${LIGHT_RED}${msg}${ENDC}"
	}

	static def red = { msg ->
	    print "${RED}${msg}${ENDC}"
	}

	static def cyan = { msg ->
	    print "${CYAN}${msg}${ENDC}"
	}

	static def redHighlight = { msg ->
	    print "${RED_HIGHLIGHT}${msg}${ENDC}"
	}

	static def lightYellow = { msg ->
	    print "${LIGHT_YELLOW}${msg}${ENDC}"
	}

	static def yellow = { msg ->
	    print "${YELLOW}${msg}${ENDC}"
	}




  static def pinkl = { msg ->
      println "${PINK}${msg}${ENDC}"
  }

  static def lightBluel = { msg ->
      println "${LIGHT_BLUE}${msg}${ENDC}"
  }

  static def bluel = { msg ->
      println "${BLUE}${msg}${ENDC}"
  }

  static def greenl = { msg ->
      println "${GREEN}${msg}${ENDC}"
  }

  static def lightRedl = { msg ->
      println "${LIGHT_RED}${msg}${ENDC}"
  }

  static def redl = { msg ->
      println "${RED}${msg}${ENDC}"
  }

  static def cyanl = { msg ->
      println "${CYAN}${msg}${ENDC}"
  }

  static def redHighlightl = { msg ->
      println "${RED_HIGHLIGHT}${msg}${ENDC}"
  }

  static def lightYellowl = { msg ->
      println "${LIGHT_YELLOW}${msg}${ENDC}"
  }

  static def yellowl = { msg ->
      println "${YELLOW}${msg}${ENDC}"
  }
}
