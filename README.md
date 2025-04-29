# WordFrequency Framework – Hollywood Style

## Overview

The `Fifteen.java` program demonstrates the **Hollywood Principle** —  
_"Don't call us, we'll call you"_ — where modular entities register callbacks with a central framework, and actions occur when the framework invokes them during the computation process.

The program:

- Reads and processes a text file.
- Filters out stop words loaded from a file (`stopwords.txt`).
- Counts the frequency of remaining words.
- Displays the top 25 most frequent words at the end of execution.

Each major component registers **handlers (callbacks)** for specific events (`load`, `doWork`, `end`) with the framework, rather than being called directly by other components.

## Hollywood Style Constraints

- The larger problem is **decomposed into entities** (objects) using abstraction.
- Entities **do not directly call each other** for actions.
- Entities provide **interfaces for registration** of callbacks (e.g., for `load`, `doWork`, `end`, `word events`).
- The **framework controls** the execution flow, calling the registered handlers at appropriate times.

## Entities

- `WordFrequencyFramework` — The main controller. Handles event registration and flow control.
- `StopWordFilter` — Loads and provides stop word checking functionality.
- `DataStorage` — Loads and preprocesses the input text, produces words for processing.
- `WordFrequencyCounter` — Maintains word frequency counts and prints results at the end.

## Folder Structure

Place the following files in the same directory:

- `Fifteen.java` — Main Java program following Hollywood style
- `stopwords.txt` — Comma-separated stop word list (e.g., `a,an,the,and,...`)
- `input.txt` — Text file to analyze (e.g., `pride-and-prejudice.txt`)

## How to Compile and Run

1. Open a terminal or command prompt.

2. Navigate to your project directory:

   cd path/to/your/folder

3. Compile the Java program:
   javac Fifteen.java

4. Run the program:
   java Fifteen pride-and-prejudice.txt

** For the repl.it, the text files will be in the main directory, so you have to use ../../../path to access files from the main parent directory.

Example: java Fifteen ../../../stopwords.txt ../../../pride-prejudice.txt

## Expected Output
---------------
Top 25 most frequent non-stop words in the input file, printed in descending order:

Example:
mr - 786
elizabeth - 635
very - 488
darcy - 418
such - 395
mrs - 343
much - 329