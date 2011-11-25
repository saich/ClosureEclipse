Closure Compiler on Eclipse
======================

Integrates [Closure Compiler](http://code.google.com/closure/compiler/) to Eclipse IDE.

The major idea is to integrate the features of Closure Compiler into the IDE itself, like, syntax checking, variable references checking, and types, and warn about common JavaScript pitfalls. 

How to use?
------------
1. Install the plugin by copying the JAR to the plugins folder of your Eclipse Installation Folder.
2. Right-click on a Javascript project and click "Enable Closure Compiler Nature"
3. To change the properties, look for "Closure Compiler" in "Javascript" subgroup in the Project Properties.
4. The Problems View now include the warnings and errrors reported by the Closure Compiler also!
5. To exclude a file from compilation, right-click on the file and click on Closure Compiler -> Exclude this file from Compilation

A few known Issues
--------------
* The output of the Closure Compiler is not saved.
* Supports only Eclipse Indigo (3.7) or later.
* Project-level settings are saved for the workspace, not at project-level.
* You can't choose the version of Closure Compiler you want to use.
* Specifying the externs files is not supported yet.

---------------------------------------
Got a feature request? Or found a bug? Go ahead and log an issue [here](https://github.com/saich/ClosureEclipse/issues)
