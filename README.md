# AtomasCORE

AtomasCORE is a Java clone of the rather lovely game [Atomas](http://sirnic.com/atomas/), created for the purpose of AI research and education.
Inspired by, and based off, the Honours project from Iliyana Ilieva from the University of Strathclyde, Glasgow.

It is the CORE version as it is the framework only, no AI included other than a random player.

##### This project is still VERY early in development and may not work as expected (or at all)
## Installation

Import into your favourite IDE.  Built using Zulu JDK 13.

## Usage

The main method is located in the controller.Main class.  Create your own AI by implementing the ai.AI interface.  The AI
object is the first thing instantiated in the main method.  Change new RandomAI() to your own.
Set auto to false to play manually.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[MIT](https://choosealicense.com/licenses/mit/)