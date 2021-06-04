# Semistructured 3-Way Merge

[![Build Status](https://github.com/guilhermejccavalcanti/jFSTMerge/actions/workflows/build.yml/badge.svg)](https://github.com/guilhermejccavalcanti/jFSTMerge/actions)
[![GitHub Super-Linter](https://github.com/guilhermejccavalcanti/jFSTMerge/workflows/Lint%20Code%20Base/badge.svg)](https://github.com/guilhermejccavalcanti/jFSTMerge/actions)

## Table of Contents
* [What is semistructured merge?](https://github.com/guilhermejccavalcanti/jFSTMerge#what-is-semistructured-merge-?)
* [Conflict Handlers](https://github.com/guilhermejccavalcanti/jFSTMerge#conflict-handlers)
* [User Guide](https://github.com/guilhermejccavalcanti/jFSTMerge#user-guide)
* [Contributor Guide](https://github.com/guilhermejccavalcanti/jFSTMerge#contributor-guide)

---

## What is semistructured merge?

Regular merge tools (such as *git merge*) are called **textual** or **unstructured merge**. Their computation is simply based on comparing consecutive string lines.  
Despite being extremely fast, they have no idea about what the developers did on their code and this leads to a large number of inconveniences for the developers: conflicts are reported when they shouldn't **(false positives)**, wasting development time to manually fix them, and actual conflicts are missed by the tool and are not reported **(false negatives)**, leading to defects that affect users.

For example, imagine that on `master` branch there is this Java class:
  ```java
  public class Math {

      public int sum(int a, int b) {
          return a + b;
      }

      public boolean isEven(int a) {
          return a % 2 == 0;
      }

  }
  ```

A developer created a branch named `left` and swapped `sum` and `isEven` positions:
  ```java
  public class Math {

      public boolean isEven(int a) {
          return a % 2 == 0;
      }

      public int sum(int a, int b) {
          return a + b;
      }

  }
  ```

Meanwhile, another developer created a branch named `right` on top of `master` and renamed `sum` to `sumIntegers`:
  ```java
  public class Math {

      public int sumIntegers(int a, int b) {
          return a + b;
      }

      public boolean isEven(int a) {
          return a % 2 == 0;
      }

  }
  ```

As there are different consecutive lines in all of the three parts, unstructured merge outputs a conflict on it (and it repeats method `sum`):
  ```java
  public class Math {

  <<<<<<< MINE
  =======
      public int sumIntegers(int a, int b) {
          return a + b;
      }

  >>>>>>> YOURS
      public boolean isEven(int a) {
          return a % 2 == 0;
      }

      public int sum(int a, int b) {
          return a + b;
      }

  }
  ```
**Semistructured merge**, on the other hand, "understands" the changes made by both contributions and produces no conflict:

  ```java
  public class Math {

      public int sumIntegers(int a, int b) {
          return a + b;
      }

      public boolean isEven(int a) {
          return a % 2 == 0;
      }

  }
  ```

It parses the code completely, creating an AST (Abstract Syntax Tree) for this purpose, but it maintains the contents of the nodes as a text.
Whitespaces and comments that occur between a node and its preceding one in the code are stored as a `prefix` of the latter.
Nodes are matched if they have the same identifier (we call this superimposition) and their contents are merged using textual merge.
You can check below the identifier of some of Java declarations.

<center>

| Declaration  | Identifier             |
| :----------: | :--------------------: |
| Classes      | Name                   |
| Fields       | Name                   |
| Methods      | Signature              |
| Constructors | Signature              |
| Packages     | Whole of the statement |
| Imports      | Whole of the statement |

</center>

We use [Feature House](http://fosd.net/fh) as base framework for parsing and superimposition and we give more details about the tool in our [paper](https://dl.acm.org/citation.cfm?id=3133883&picked=formats).

---



## Conflict Handlers

Conflict Handlers (or just Handlers) are algorithms that run in sequence after every semistructured merge (or if the user desires so), analysing the merge output and taking actions to refine the result according to the peculiarities of the multiple types of the language's constructions.

### [Deletions Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/DeletionsHandler.java)

Executes when a developer A changed the content of an inner class while another developer B deleted or renamed it.

If A included a reference to the changed class, the handler keeps both A's and B's classes (if B deleted it, it keeps only A's).  
Otherwise, if B included a reference to its renamed class, the handler outputs a conflict between A's and B's classes. Else, the handler merges both classes.

### [Initialization Blocks Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/InitializationBlocksHandler.java)

Executes when there's at least one initialization block in the code.

If there's exactly one initialization block in A, B and base's code, they're merged.  
Otherwise, for each initialization block in base, the handler searches for the first A's and B's initialization block with string similarity higher or equal than 0.7 and merge them. If there's none, the handler uses an empty string for the merge.


### [Method and Constructor Renaming or Deletion Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/MethodAndConstructorRenamingAndDeletionHandler.java)

Executes when a developer renamed or deleted a method or a constructor.  

For each method or constructor in base, if its signature is not present in A's or B's code, they're marked as: (1) renamed without body changes if there's a method or constructor with the same body in the developers' code or (2) renamed or deleted with body changes otherwise.  
Then, for each marked method or constructor in base, the handler searches for the first A's and B's method or constructor that satisfies one of the following conditions: (1) equal body; (2) string similarity higher than 0.7 in the body and equal signature but the name; (3) one body is contained in the other. If there's none, they're treated as deleted.  
Finally, for each triple of methods or constructors made by the previous search (A's, base's and B's), the handler does an operation based on one of its user-chosen variants:

- Safe (default): applies a [decision tree](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/documentation/Renaming-Handler-Table.png) to decide the result.
- Keep Both Methods: always keeps A's and B's methods in the triple.
- Merge Methods: runs textual merge on A's and B's methods.


### [New Element Referencing Edited One Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/NewElementReferencingEditedOneHandler.java)

Executes when developer A added a method or field that refers to a method or field edited by developer B.

For each A's and for each B's method or field, if there's an unstructured merge conflict surrounding them and if A's refers to B's, the handler outputs a conflict. Otherwise, the handler keeps both.

### [Type Ambiguity Error Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/TypeAmbiguityErrorHandler.java)

Executes when developer A or B add at least one import statement.

For each A's and for each B's import statements, if they're both importing packages or they're importing classes having equal names, the handler outputs a conflict if there's a type ambiguity compilation error in the merge code.  
Else if A is importing a class and B is importing a package, the handler outputs a conflict if unstructured merge reported a conflict surrounding the imports.  
If none of these conditions are true, the handler keeps the import statements.


### [Duplicated Declaration Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/DuplicatedDeclarationHandler.java)

Executes as a statistical tool, when unstructured merge result presents a duplicated declaration from a method or field.

The handler counts duplicated declaration compilation errors in the code generated by unstructured merge.

---

## Logging
For research purposes, S3M's stores a error and some statistical logs in `${HOME}/.jfstmerge` directory to research. The formers are typically cryptographed to preserve their integrity, but this can be disabled.

---

## User Guide

### Requirements

* **Java 8** (Java version "1.8.0_212" or above)
* **Git** (optional) S3M can behave as a merge driver for `git merge`. If you have interest in this feature, remember to have Git [installed](https://git-scm.com/downloads). You can find more details about *git merge drivers* [here](https://www.git-scm.com/docs/gitattributes#_defining_a_custom_merge_driver).

<!--- 
### Installing
Check the [Releases](https://github.com/guilhermejccavalcanti/jFSTMerge/releases) page. Download and execute the most recent installer and follow its instructions.
-->

### Git integration (as a merge driver)

1. Download the [binary](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/binary/jFSTMerge.jar) file;
2. Add the following lines to your `.gitconfig` file (typically localized in the folder `$HOME` in Unix or `%USERPROFILE%` in Windows), replacing `pathTo` with the path to the binary file in your machine:

  ```conf
  [core]
      attributesfile = ~/.gitattributes
  [merge "s3m"]
      name = semi_structured_3_way_merge_tool_for_java
      driver = java  -jar "\"pathTo/jFSTMerge.jar\"" %A %O %B -o %A -g
  ```

3. Add the following line to your `.gitattributes` file (also localized in the `$HOME` / `%USERPROFILE%` folder, create the file if not created already):

  ```conf
  *.java merge=s3m
  ```

### Usage
If integrated with Git (as a merge driver), S3M will run automatically every time you invoke the `git merge` command.
No further configuration required.
You can still run it as a standalone tool, if desired, with the `.jar` file present in the [/binary](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/binary/) folder.
You can use the command below after dowloading the `jFSTMerge.jar` file:

`java -jar jFSTMerge.jar leftPath basePath rightPath`

where `leftPath`, `basePath` and `rightPath` can be either a file or a directory.

#### Parameters
| Parameter | Arity | Type | Description |
| :---: | :---: | :---: | --- |
| `-f` | 3 | String | Specify the files to be merged (mine, base, yours).
| `-d` | 3 | String | Specify the directories to be merged (mine, base, yours).
| `-o` | 1 | String | Destination of the merged content. *(default: yours)*
| `-c` | 1 | Boolean | Enables or disables cryptography during log generation. *(default: true)*
| `-l` | 1 | Boolean | Enables or disables logging. *(default: true)*
| `--encoding-inference` | 1 | Boolean | Tries to infer file encodings to properly merge them. If not enabled, the tool assumes files are encoded in UTF-8. *(default: true)*
| `--ignore-space-change` | 1 | Boolean | Lines with whitespace changes only are considered as unchanged for the sake of a three-way merge. *(default: true)*
| `-r`, `--renaming` | 1 | String | Choose strategy on renaming conflicts. *(possible values: SAFE, MERGE, KEEPBOTHMETHODS)* *(default: SAFE)*
| `-hdd`, `--handle-duplicate-declarations` | 1 | Boolean | Enables or disables Duplicated Declaration Handler. *(default: true)*
| `-hib`, `--handle-initialization-blocks` | 1 | Boolean | Enables or disables Initialization Blocks Handler. *(default: true)*
| `-hnereo`, `--handle-new-element-referencing-edited-one` | 1 | Boolean | Enables or disables New Element Referencing Edited One Handler. *(default: true)*
| `-hmcrd`, `--handle-method-constructor-renaming-deletion` | 1 | Boolean | Enables or disables Method and Constructor Renaming or Deletion Handler. *(default: true)*
| `-htae`, `--handle-type-ambiguity-error` | 1 | Boolean | Enables or disables Type Ambiguity Error Handler. *(default: true)*

---

## Contributor Guide

### Contributor Requirements

* **Java 8** (Java version "1.8.0_212" or above)
* **Gradle 4.6**

### Getting Started

Cloning the repository and setting up a Gradle project should be enough to start contributing.

### Build

We run [Gradle](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/build.gradle) as build tool, alongside a wrapper. One can build the tool in command line running `gradlew build`. Additionally, [here](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/documentation/setup-eclipse.pdf) you can find a two-step setup guide on the Eclipse IDE.

### Testing

We have [a bunch of JUnit classes](https://github.com/guilhermejccavalcanti/jFSTMerge/tree/master/src/test/java/br/ufpe/cin). They mostly test the behavior of the handlers.  
We encourage the usage of

`testWhatYoureTesting_givenAConditionIsSatisfied_whenSomeActionHappens_shouldExpectedBehavior`

style of method names when writing unit tests.

There's also [two unique JUnit classes](https://github.com/guilhermejccavalcanti/jFSTMerge/tree/master/testfiles/shelltests):
1. one for testing the git merge driver, that serve primarily for the installer (see below);
2. and another that runs periodically as a Cron Job (see below).

### Continuous Integration

We run [GitHub Actions](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/.github/workflows/) as CI tool.
It runs a typical gradle build, the unique JUnit tests described above and linters for every new or edited file.

---

Copyright (c) 2016-2019 by the Federal University of Pernambuco.

Paulo Borba &lt;<phmb@cin.ufpe.br>&gt;  
Guilherme Cavalcanti &lt;<gjcc@cin.ufpe.br>&gt;  
João Victor &lt;<jvsfc@cin.ufpe.br>&gt;
