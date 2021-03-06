# PCBDraw

Table of contents
=================

<!--ts-->
   * [Installation](#Installation)
   * [User Guide](#User-Guide)
      * [The User Interface](#The-User-Interface)
      * [The File Menu](#The-File-Menu)
      * [The Edit Menu](#The-Edit-Menu)
      * [The Context Panel](#The-Context-Panel)
      * [The Workspace & Drawing a PCB](#The-Workspace)
   * [Carving With Easel](#Carving-With-Easel)
   * [Examples](#Examples)
   * [FAQ](#FAQ)
<!--te-->


## Installation

### Windows
* Download the windows installer from https://github.com/NicholasBerryman/PCBDraw/raw/master/Launcher/bundles/PCBDrawLauncher-1.0.exe  
* Once it has downloaded, run the installer and follow the instructions it gives.  
* After the installer has finished, the program should open automatically. You may run it in the future by opening **Nick Berryman->PCBDrawLauncher** from the start menu.


### Mac and Linux
* Ensure Java is installed (download and install from https://www.java.com/en/download/)	
* Once Java is installed, download PCBDraw from https://github.com/NicholasBerryman/PCBDraw/raw/master/Launcher/PCBDrawLauncher.jar
* Once it has downloaded, move it somewhere you won't lose it (e.g your Desktop) and run it normally

## User Guide
### The User Interface
When the program launches, after updating, the following screen should appear:  
![](https://imgur.com/HXjD97O.png)  

On this screen are four important sections:
  * The **File** Menu (top left)
  * The **Edit** Menu (beside the File Menu)
  * The **Workspace** (right)
  * The **Context** Bar (Left)
  
### The File Menu  
![](https://imgur.com/imNXxTT.png)  

The File menu has four options:  
* **New** clears the workspace and sets it up for a new circuit board
  * You will be prompted to save your current circuit board if you haven't done so yet
* **Save** allows you to save the circuit board somewhere on your computer
* **Save As** allows you to change where you want to save the circuit board
* **Open** allows you to open a circuit board that you have previously saved. The circuit board must have been created in PCBDraw. No other PCB file formats are accepted
  * '.GCode' files cannot be opened or edited this way, only '.GCB' files created using the **Save** and **Save As** option above  
  
Keyboard shortcuts are displayed beside each menu option

### The Edit Menu
![](https://imgur.com/l7WCVM6.png)  

The Edit menu has five options:  
* **Undo** undoes the previous action
* **Redo** redoes the last undone action
* **Cut** removes the selected traces and gets them ready to be pasted
* **Copy** copies the selected traces and gets them ready to be pasted
* **Paste** pastes the last traces to be copied/pasted into the workspace  

Keyboard shortcuts are displayed beside each menu option
### The Context Panel   
![](https://imgur.com/O3CrSlL.png)  

The Context Panel has three tabs:
* **Board** deals with the view of the workspace and size of the circuit board
* **Tools** lets you select the tools for drawing the circuit board
* **Export** lets you create a '.GCODE' file for use by a CNC router (e.g Carvey) for cutting you circuit board

#### Context Panel: The Board Tab
The board tab is displayed in the previous image
* **Board Width** and **Board Height** allow you to change the dimensions of your circuit board (mm)
* **Zoom** allows you to zoom in and out of the circuit board
* **Square Size** lets you choose the the size of your grid (mm)
  * Circuit board features can only be placed on grid points, so if your circuit board needs lots of very fine features, try a smaller grid size
* **Using Carvey** enables the smartclamp in the workspace, preventing you from making a circuit board that the Carvey cannot carve
  * If you are not using a Carvey, feel free to un-tick this box  

Any of these features can be changed at any point without disrupting any circuit board features you have already created.

#### Context Panel: The Tools Tab  
![](https://imgur.com/lbC1sb3.png)  

* Select the **Path** option to draw circuit traces (lines) on the workspace
* Select the **Hole** option to draw component holes on the workspace
* Select the **Select** option to select anything you have already drawn in the workspace (when you want to move or delete it).
* Click the **Move** button when you want to move everything you have selected within the workspace
* Click the **Delete** button when you want to delete everything you have selected 
  
These options are explored in greater detail in the **Workspace** section below

#### Context Panel: The Export Tab  
![](https://imgur.com/nmMKoKH.png)  

**This tab sets the options for carving the board and can cause problems if set up wrong. Use the default values unless something has gone wrong or you really know what you are doing**  
* The **Z-Down** option sets the cutting depth for the circuit board. Make this **more negative** if the cut is too shallow, make it **less negative** if the cut is too deep.
* The **Drill Down** option sets the drilling depth. If you want to only drill pilot holes, set it to some value less then the thickness of your board (as a negative number). If you want to drill the whole way through the board, set it the the thickness of your board (as a negative number).
  * If drilling through the whole board, please ensure you have placed something below it so the Router does not drill into itself
* The **Z-Up** option sets the height of the drill as it moves between cuts. Ensure this is high enough that it won't hit anything. The default value is almost always best.
* The **Path Width** determines how wide the traces on the board should be. **Reduce** this value if traces join up where they shouldn't, **increase** this value if the circuit should carry large currents.
  * If the **Path Width** is set too high, then **Paths** and **Holes** that are too close to each-other may overlap and cause shorts. If this is happening then either **reduce** this value, or move the overlapping features further apart.
  
Click the **Export** button and choose a location to export the file to when you are ready to carve your board.

## The Workspace
![](https://imgur.com/rxgpMvo.png)  

The grid here represents the circuit board.
* The size of the workspace may be changed using the **Height** and **Width** options in the **Board Tab** of the **Context Menu**
* The size of the grid squares may be changed using the **Square Size** option in the **Board Tab** of the **Context Menu**
* The black L-Shape in the above image represents the smartclamp of the Carvey. This may be enabled or disabled with the **Using Carvey** option in the **Board Tab** of the **Context Menu**

### Workspace: Drawing a circuit board
* To draw a line trace:
  * Select the **Path** tool from the **Tools Tab** of the **Context Menu**.
  * Click the start point of the line trace on the grid (**Not** click-and-drag).
  * Click the end point of the line trace on the grid (**Not** click-and-drag).
    * When **Exporting**, if the **Path Width** is too high and they are close enough to each other, then **Paths** that should be separate may overlap . If this is happening  to you, either reduce your **Path Width** or move the **Paths** away from each other.
 
![](https://imgur.com/NjNQNCD.gif)
  
* To draw a component hole:
  * Select the **Hole** tool from the **Tools Tab** of the **Context Menu**.
  * Click the point on the grid where you want the hole.  
    * When **Exporting** if the **Path Width**  is too high and they are close enough to each other then **Holes** that should be separate may overlap. If this is happening  to you, either reduce your **Path Width** or move the **Paths** away from each other.
    * **Holes** placed side-by-side or on top of each other should automatically be isolated, though they may still overlap both **Paths** and other **Holes** placed diagonally from them.

![](https://imgur.com/XDFb7jc.gif)  

* To select items you have drawn on the workspace:
  * Select the **Select** tool from the **Tools Tab** of the **Context Menu**.
  * Click the a point near the items you want to select (**Not** click-and-drag).
  * Click another point such that the green rectangle surrounds everything you want to select (**Not** click-and-drag).
    * To Select a line, ensure that either the **start point** or the **end point** of the line is within the green rectangle
  * The selected items should turn blue  

![](https://imgur.com/lPCUJp9.gif)  

* To move items you have drawn on the workspace:
  * Using the **Select** tool, select the items you want to move.
  * Click the **Move** button from the **Tools Tab** of the **Context Menu**.
  * Click a point in the workspace where you want to move it to (**Not** click-and-drag).
    * The selected items will be treated as if they are in a large rectangle when moving. The top left point of that rectangle will be the point that you click to move them to.  

![](https://imgur.com/g7rwIjw.gif)  

* To delete items you have drawn on the workspace:
  * Using the **Select** tool, select the items you want to move.
  * Click the **Delete** button from the **Tools Tab** of the **Context Menu**.
    * If you make a mistake and delete the wrong thing, make sure to **Undo** by pressing **Crtl+Z** or selecting it from the menu with **Edit->Undo**

![](https://imgur.com/W3Nxo3G.gif)  

**Once you are done with your circuit board remember to save the board and export it for carving**

## Carving with Easel
Once you have exported your completed circuit board, you will receive a gcode file that you can use to carve it.
If you are using the Carvey, this must be done with the Inventables **Easel** website http://easel.inventables.com.

### Carving with Easel: The Process
* Go to the Inventables **Easel** website http://easel.inventables.com.
* Sign in with your accound credentials (Create an account if you have not already).
* Create a **New Project**.
* Rename the project by clicking the title in the top left of the web page.
* Select your machine with **Machine->Carvey**
* Import the circuit board with **File->Import g-code->browse**
  * Select the gcode file that you exported the completed circuit board to.
* Ensure the Carvey is turned on and connected
  * Also check that the blank PCB is properly secured and the correct carving bit is in place
* Click the **Carve** button on the top right and follow the instructions it shows.
* Wait for the board to be carved.

## Examples
This page will be updated with examples and templates as I make them.
Each example has both '.gcb' file and a '.gcode' file. You can open the .gcb file in **PCBDraw** to view and edit it, and you can open the .gcode file in **Easel** (See above), to test it with your Carvey (Or other CNC Router).
Make sure to download each link using **Right Click->Save link as**.
Here is the current list:
* Mini ROV Circuit for Subs-In-Schools
  * .gcb file:    https://github.com/NicholasBerryman/PCBDraw/raw/master/Examples/MiniSubCircuitExample.gcb
  * .gcode file:  https://raw.githubusercontent.com/NicholasBerryman/PCBDraw/master/Examples/MiniSubCircuitExample.gcode

## FAQ
 * Something went wrong! What do I do?
   * If you are unsure about anything and need help, please contact me at: Nicholas.Berryman1@gmail.com. Please include **PCBDraw - Help** in the subject line.
 * There's a cool feature that I want to use but it doesn't look like your program supports it?
   * Currently the program is fairly minimal, having mostly been made in a single weekend. If there are any new features that you feel would be very useful/important then please contact me at: Nicholas.Berryman1@gmail.com. Please include **PCBDraw - Suggestion** in the subject line. I'll do my best, but can't make any guarantees about whether any specific feature will be added or how long it will take.

