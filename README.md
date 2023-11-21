
# Code Visualizer

The **Code Visualizer** project is a tool that allows users to visualize Java code in different formats, such as UML diagrams and XML schema. It leverages the JavaParser library for parsing Java source code and the PlantUML library for generating UML diagrams.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
  - [Command-Line Options](#command-line-options)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Introduction

The **Code Visualizer** project aims to provide developers with a tool to visualize Java code for better understanding and analysis. It supports the generation of UML diagrams and XML schema from Java source code.

## Features

- Generate UML diagrams from Java source code
- Generate XML schema from Java source code
- Command-line interface for easy integration into workflows
- Interactive rendering of PlantUML diagrams (PNG or ASCII)

## Getting Started

### Prerequisites

- Java (JDK) installed on your machine
- Maven for building and managing the project

### Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/yourusername/code-visualizer.git
    ```

2. Navigate to the project directory:

    ```bash
    cd code-visualizer
    ```

3. Build the project using Maven:

    ```bash
    mvn clean install
    ```

## Usage

### Command-Line Options

The **Code Visualizer** supports the following command-line options:

- `-i` or `--input`: Specify the input java source folder.
- `-g` or `--generate`: Specify the output type (uml or xsd).
- `-o` or `--output`: Specify the output folder path.
- `-r` or `--render`: Specify the rendering format (png or ascii).

Example usage:

```bash
java -jar code-visualizer.jar -g uml -o output/ -r png
```
## Contributing

## License

This project is licensed under the [MIT License](LICENSE). Feel free to use, modify, and distribute the code for your own projects.
## Acknowledgments

The **Code Visualizer** project wouldn't be possible without the contributions and support from the following:

- [JavaParser](https://javaparser.org/)
- [PlantUML](https://plantuml.com/)

Thank you to all the contributors and communities behind these fantastic tools!
