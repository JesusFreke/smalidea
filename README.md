smalidea is a smali language plugin for [IntelliJ IDEA](https://www.jetbrains.com/idea/)/[Android Studio](http://developer.android.com/sdk/index.html)

### [DOWNLOAD](https://bitbucket.org/JesusFreke/smali/downloads) ###

It is currently very experimental, and you will likely run into issues.

[![](https://raw.githubusercontent.com/wiki/JesusFreke/smali/smalidea.png)](https://raw.githubusercontent.com/wiki/JesusFreke/smali/smalidea.png)

## News
* 2020-02-23 - smalidea has been migrated to a separate repository. This new repository is a filtered copy
of the original smali repository in order to maintain the git history. However, any versions prior to this migration
are not expected to actually build. If you need to build an old version for some reason, you should check out and
build from the smali repository.
* 2016-02-27 - v0.03 is out. This is mostly a stability/bug fix release, with no significant new functionality.

## Features
### Current Features
* Syntax Highlighting/Syntax Errors
* Bytecode level debugging
  * Breakpoints
  * Instruction level single stepping
  * Adding watches for arbitrary (non-named) registers
  * Full java-style expression support in locals window, etc. while debugging
* Go to Definition
* Find Usages
* Renaming
* Referencing smali classes from java code (except it can't actually be compiled, yet)
* Issue reporting - easily create a new github issue from the error dialog
  * [![](https://raw.githubusercontent.com/wiki/JesusFreke/smali/error.png)](https://raw.githubusercontent.com/wiki/JesusFreke/smali/error.png)

### Possible Future Features
* Auto-complete (instruction names, class/method/field references, etc.)
* Compile support for smali-only projects
* Robust error detection (e.g. full bytecode verification)
* Smoother project import process
  * Automatic detection of source directory
  * Choosing sdk
* Wizard for importing an apk as a new project
* "Smali Class" entry in "New..." context menu
* Show all registers with a value in "locals" pane
* Ability to set the value of a register in "watch" pane

### "Stretch" Features
* Compile support for mixed smali+java projects
* "Introduce new register" intention
* Import (and deodex) device framework as new module (or new sdk??)
* Expose register type analysis data
  * Show the expected type of a register at any point
  * Find locations where the register's value could have been set

## Installation
1. Download the latest smalidea zip file from the [Bitbucket download page](https://bitbucket.org/JesusFreke/smali/downloads)
2. In IDEA/AS, go to Settings->Plugins and click the "Install plugin from disk" button, selecting the downloaded smalidea zip file
3. Click "Apply" and restart IDEA/AS
4. ???
5. Profit!

## Debugging an application

Note: Single-instruction stepping is only supported in IDEA 14.1 and greater, and any future version of Android Studio based on IDEA 14.1 or greater. In earlier versions, attempting to single step will step to the next .line directive, instead of stepping to the next instruction.

1. Manually disassemble an application using baksmali into a "src" subdirectory of a new project directory, e.g. `baksmali d myapp.apk -o ~/projects/myapp/src`
2. In IDEA, import a new project, and select the project directory. e.g. `~/projects/myapp`
3. Use the "Create project from existing sources" option when importing the project
4. Once the project has been created, right click on the src directory and select "Mark Directory As->Sources Root"
5. Open the project settings and select/create an appropriate JDK
6. Install/start the application on the device
7. Run ddms, and select the application's process
8. In IDEA, Create a new "Remote" debug configuration (Run->Edit Configurations), and change the debug port to 8700
9. Run->Debug
10. The application should pause if/when the breakpoint is hit, at which point you can single step, add watches, etc.

or do the following in recent Android Studio 3.2:

1. Manually disassemble an application using baksmali into a "src" subdirectory of a new project directory, e.g. `baksmali d myapp.apk -o ~/projects/myapp/src`
2. In Android Studio, close your current project and select "Open an existing Android Studio project".
3. Once the project has been created, right click on the src directory and select "Mark Directory As->Sources Root"
4. Make sure your app has `android:debuggable="true"` in Android Manifest. Turn on "USB debugging" and use "Select debug app" to select your app in "Developer options" on Android device
5. Start your application and forward JDWP service to localhost using `adb forward tcp:8700 jdwp:$(timeout 0.5 adb jdwp | tail -n 1)`
6. In Android Studio, Create a new "Remote" debug configuration (Run->Edit Configurations), and change the debug port to 8700
7. In Android Studio, select Run -> Debug
8. The application should pause if/when the breakpoint is hit, at which point you can single step, add watches, etc.