# Semistructured 3-Way Merge [![Build Status](https://api.travis-ci.org/guilhermejccavalcanti/jFSTMerge.svg?branch=master)](https://travis-ci.org/guilhermejccavalcanti/jFSTMerge)

## Table of Contents
* [What is semistructured merge?](https://github.com/guilhermejccavalcanti/jFSTMerge#what-is-semistructured-merge-?)
* [Conflict Handlers](https://github.com/guilhermejccavalcanti/jFSTMerge#conflict-handlers)
* [User Guide](https://github.com/guilhermejccavalcanti/jFSTMerge#user-guide)
* [Contributor Guide](https://github.com/guilhermejccavalcanti/jFSTMerge#contributor-guide)

---

## What is semistructured merge?

Regular merge tools (such as *git merge*) are called **textual** or **unstructured merge**. Their computation is simply based on comparing consecutive string lines.  
Despite being extremely fast, they have no idea about what the developers did on their code and this leads to a large number of inconveniences for the developers: conflicts when they shouldn't be reported (**false positives**) and no conflicts when they should be there (**false negatives**).

For example, imagine that on `master` branch there is this Java class:
```
public class Math {

    int factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return factorial(n - 1) * n;
        }
    }

}
```

A developer created a branch named `left` and inserted a method named *calc* below *factorial*:
```
public class Math {

    [...]

    int calc(int a, int b) {
        return a + b;
    }

}
```

Meanwhile, another developer created a branch named `right` and inserted a method also named *calc* but above *factorial*:
```
public class Math {

    int calc(int a, int b) {
        return a * b;
    }

    [...]

}
```
When we merge both branches on master using `git merge`, we get:
```
public class Math {

    int calc(int a, int b) {
        return a * b;
    }

    int factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return factorial(n - 1) * n;
        }
    }

    int calc(int a, int b) {
        return a + b;
    }

}
```
With some experience in Java, you may already know this will lead to a compilation error: there are two methods with the same signature in the same class. This is a typical case of a false negative: the merge tool introduced a compilation error when it should report a conflict.

As a motivation to reduce such cases, **structured merge** was created. It compiles the code completely and attempts to merge it as a tree. Everything that was in the code (including expressions and statements) are nodes in the tree. This strategy outputs less inconveniences, but it's very inflexible: a structured merge tool for a version of a language works for that version and that language only. If one wants to extend the tool for another language, for example, he needs to basically recriate the entire tool.

Finally, as you may suppose, **semistructured merge** is an approach that combine both worlds: it still compiles the code, but only classes and theirs members' references. Method bodies, for example, are stored as a string instead of being stored as nodes. Then, **we can match two declarations in different contributions if they have the same identifier and merge their contents using textual merge**. This same strategy can be applied to every language, so it's easier to extend the tool.  

The result of the above example using the semistructured approach is:
```
public class Math {

    int calc(int a, int b) {
<<<<<<< MINE
        return a * b;
=======
        return a + b;
>>>>>>> YOURS
    }

    int factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return factorial(n - 1) * n;
        }
    }

}
```

---

## Conflict Handlers


Is semistructured merge the ultimate solution? Not so easy. Despite reducing a lot of textual merge's false positives and false negatives, it introduces others on its own! We call them **extra** false positives/negatives. To help fixing those, S3M runs special mechanisms after every merge named as **Conflict Handlers**: algorithms that fix some semistructured merge potential flaws.

### [Deletions Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/DeletionsHandler.java)

* **Description** Every declaration removal by a contribution is problematic for semistructured merge as the correspondent node doesn't exist in the contribution's tree. This handler focuses on deletions of **inner classes**.
* **Behavior**
    If a contribution edited an inner class while another deleted it, we check if the latter also included a reference to the edited class. If true, we keep both the original and edited classes. Otherwise, we check if they're "sufficiently similar". If true, we join them to avoid duplications, otherwise, we report a conflict.

### [Initialization Blocks Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/InitializationBlocksHandler.java)
* **Description** Semistructured merge is unable to match **initialization blocks** (static and non-static), as it uses identifiers for this purpose and these elements have no identifiers, creating duplicates at the end. This handler tries to overcome this issue, matching nodes by content similarity.
* **Behavior** If there's only one initialization block in all contributions, they're matched. Otherwise, for each initialization block in base, we search the most similar block in left and in right and merge them using textual merge.

### [Method and Constructor Renaming or Deletion Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/MethodAndConstructorRenamingAndDeletionHandler.java) 
* **Description** Semistructured merge is unable to match **methods** or **constructors** that have been renamed (as their identifiers are now different) or deleted (as their nodes aren't created). This handler handles such cases, attempting to match nodes following other rules.
* **Behavior** For each method or constructor renamed or deleted, we search left's nodes and right's nodes for a match. For this handler, is enough for one of the following conditions to be true for the match to happen:
(1) have equal bodies; (2) have similar bodies and equal signatures except for the name; (3) one body is substring of the other. If none of the conditions are satisfied, we treat the node as deleted. Finally, for each triple, we apply a decision based on the type of the renaming or deletion:
    * If both contributions renamed without body changes, we report a conflict if and only if they renamed to different signatures.
    * If both contributions deleted or renamed with body changes, we immediately report a conflict if they renamed to different signatures. Otherwise, we merge their contents using textual merge.
    * If one renamed without body changes and the other deleted renamed with body changes, we report a conflict if they renamed to different signatures or if they renamed to equal signatures but the latter included a reference to the renamed method/constructor.

### [New Element Referencing Edited One Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/NewElementReferencingEditedOneHandler.java)

* **Description** If a developer adds a **class member** that references an edited one inside its body by another developer, semistructured merge can change the semantic of the code since it doesn't care about the elements' bodies. Note that structured merge, on the other hand, deals with this naturally. This handler attempts to fix this issue.
* **Behavior** After detecting such members, we report a conflict if unstructured merge also reported it.

### [Type Ambiguity Error Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/TypeAmbiguityErrorHandler.java)

* **Description** Semistructured merge is unable to output conflicts in **import statements**: even if an import has been edited by two different developers, they're treated as different as their identifiers are different. This can introduce a compilation error (if two imports share a same class name) or a behavioral error (a different import than intended). This handler tries to avoid such errors.
* **Behavior** For each pair (.\*, .\*), (.\*, .A), or (.A, .A) of imports of both contributions (A is a class), we report a conflict if unstructured merge also reported it or if they introduced a compilation error in the merge.

### [Duplicated Declaration Handler](https://github.com/guilhermejccavalcanti/jFSTMerge/blob/master/src/main/java/br/ufpe/cin/mergers/handlers/DuplicatedDeclarationHandler.java)
* **Description** This handler is special. It only detects cases where unstructured merge would allow two methods with different signatures (just as in the example above) in the result. As semistructured merge deals with this naturally, this handler serves for statistics purposes only.

---

## User Guide

### Requisites

* **Java 8** 
* **Git [optional]** S3M can behave as a merge driver for `git merge`. If you have interest in this feature, remember to have Git [installed](https://git-scm.com/downloads).

### Installing
Check the [Releases](https://github.com/guilhermejccavalcanti/jFSTMerge/releases) page. Download and execute the most recent installer and follow its instructions.

### Usage
If integrated with Git, S3M will run automatically on every `git merge` command. No further configuration required.  
You can still run it as a standalone tool, if desired. Its `.jar` is present in the installation directory.

#### Parameters
* `-f` and `-d` parameters are the only mandatory ones, but they are exclusive (only one of them can be used).

| Parameter | Arity | Type | Description |
| :---: | :---: | :---: | --- |
| `-f` | 3 | String | Specify the files to be merged (mine, base, yours).
| `-d` | 3 | String | Specify the directories to be merged (mine, base, yours).
| `-o` | 1 | String | Destination of the merged content. *(default: yours)*
| `-c` | 1 | Boolean | Enables or disables cryptography during logs generation. *(default: true)*
| `-l` | 1 | Boolean | Enables or disables logging. *(default: true)*
| `--encoding-inference` | 1 | Boolean | Tries to infer file encodings to properly merge them. If not enabled, the tool assumes files are encoded in UTF-8. *(default: true)*
| `--ignore-space-change` | 1 | Boolean | Treats lines with the indicated type of whitespace change as unchanged for the sake of a three-way merge. Whitespace changes mixed with other changes to a line are not ignored. *(default: true)*
| `-r`, `--renaming` | 1 | String | Choose strategy on renaming conflicts. *(possible values: SAFE, MERGE, KEEPBOTHMETHODS)* *(default: SAFE)*
| `-hdd`, `--handle-duplicate-declarations` | 1 | Boolean | Enables or disables Duplicated Declaration Handler. *(default: true)*
| `-hib`, `--handle-initialization-blocks` | 1 | Boolean | Enables or disables Initialization Blocks Handler. *(default: true)*
| `-hnereo`, `--handle-new-element-referencing-edited-one` | 1 | Boolean | Enables or disables New Element Referencing Edited One Handler. *(default: true)*
| `-hmcrd`, `--handle-method-constructor-renaming-deletion` | 1 | Boolean | Enables or disables Method and Constructor Renaming or Deletion Handler. *(default: true)*
| `-htae`, `--handle-type-ambiguity-error` | 1 | Boolean | Enables or disables Type Ambiguity Error Handler. *(default: true)*

---

## Contributor Guide

### Requisites

* **Java 8** 
* **Gradle 4.6** 

### Getting Started

Cloning the repository and setting up a Gradle project should be enough to start contributing.

### Testing

We have a bunch of JUnit classes. They test mostly the behavior of the handlers.  
We encourage the usage of 

`testWhatYoureTesting_givenAConditionIsSatisfied_whenSomeActionHappens_shouldExpectedBehavior`

style of method names when writing unit tests.

There's also a special JUnit class that tests the git merge driver, that serve primarily for the installer. There are some inconveniences in order to run it (it can run only with Git integrated and a build artifact at hand). In any case, remember Travis runs these tests.

---

Copyright (c) 2016 by the Federal University of Pernambuco.

Contact Guilherme Cavalcanti &lt;<gjcc@cin.ufpe.br>&gt;.