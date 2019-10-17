# PCBDraw

## Installation
### Windows
* Download the windows installer from https://github.com/NicholasBerryman/PCBDraw/blob/master/dist/bundles/PCBDraw-1.0.exe?raw=true  
* Once it has downloaded, run the installer and follow the instructions it gives.  
* After the installer has finished, the program should open automatically. You may run it in the future by opening NickBerryman/PCBDraw from the start menu.

### Mac and Linux
* Ensure Java is installed (download and install from https://www.java.com/en/download/)
* Once Java is installed, download PCBDraw from https://github.com/NicholasBerryman/PCBDraw/blob/master/dist/PCBDraw.jar?raw=true
* Once it has downloaded, move it somewhere you won't lose it (e.g The Desktop) and run it normally

## User Guide
### The User Interface
When the program launches, the following screen should appear:  
![](https://imgur.com/cHY2ZcA.png)
On this screen are three important sections:
  * The File menu (top left)
  * The Workspace (right)
  * The Context Bar (Left)
  
#### The File Menu  
![](https://imgur.com/AUsrSIK.png)  
The File menu has three options:  
* **New** clears the workspace and sets it up for a new circuit board
  * **This does NOT save the existing circuit board.** Ensure the existing board is saved before creating a new circuit board.
* **Save** allows you to save the circuit board somewhere on your computer. The circuit board **MUST** be saved manually and will **NOT** save automatically.
* **Open** allows you to open a circuit board that you have previously saved. The circuit board must have been created in PCBDraw. No other PCB file formats are accepted.
  * '.GCode' files cannot be opened or edited this way, only '.GCB' files created using the **Save** option above

### The Context Bar
![](https://imgur.com/XA54tsW.png)
The Context Bar has three tabs:
* **Board** deals with the view of the workspace and size of the circuit board
* **Tools** lets you select the tools for drawing the circuit board
* **Export** lets you create a '.GCODE' file for use by a CNC router (e.g The Carvey) for cutting you circuit board

#### Context Bar: The Board Tab
The board tab is displayed in the previous image
* The board dimensions can be adjusted with the **Board Width** and **Board Height** text boxes. Remember to press the **enter** key on your keyboard in order to update the size once you have entered it.
* You can zoom in on the board using the **up** arrow next to the **Zoom** text box, and can zoom out using the **down** arrow
* The size of the grid squares can be adjusted using the **Square Size** text box. Remember to press the **enter** key on your keyboard in order to update the size once you have entered it.  
  * Circuit board features can only be placed on/between grid points, so if your circuit board has lots of very fine features, try a smalled grid size
* If you are using the Carvey, tick the **Using Carvey** checkbox. This enables the smartclamp in the workspace, preventing you from making a circuit board that the Carvey cannot carve.
Any of these features can be changed at any point without disrupting any circuit board features you have already created.

#### Context Bar: The Tools Tab
![](https://imgur.com/eQ4nNCw.png)
* Select the **Path** option to draw circuit traces (lines) on the workspace
* Select the **Hole** option to draw component holes on the workspace
* Select the **Select** option to select anything you have already drawn in the workspace (when you want to move or delete it).
* Click the **Move** button when you want to move everything you have selected within the workspace
* Click the **Delete** button when you want to delete everything you have selected
  * There is currently **NO** undo option. **Save your work** before deleting anything that might be important.
These options are explored in greater detail in the **Workspace** section below

#### Context Bar: The Export Tab
![](https://imgur.com/QP835V3.png)  

**This tab sets the options for carving the board and can cause problems if set up wrong, use the default values unless something has gone wrong or you know what you are doing**  
* The **Z-Down** option sets the cutting depth for the circuit board. Make this **more negative** if the cut is too shallow, make it **less negative** if the cut is too deep.
* The **Drill Down** option sets the drilling depth. If you want to only drill pilot holes, set it to some value less then the thickness of your board (as a negative number). If you want to drill the whole way through the board, set it the the thickness of your board (as a negative number).
  * If drilling through the whole board, please ensure you have placed something below it so the Router does not drill into itself
* The **Z-Up** option sets the height of the drill as it moves between cuts. Ensure this is high enough that it won't hit anything. The default value is almost always best.
* The **Feedrate** option sets the speed of cutting. This option should **NOT** be changed unless you are using a CNC Router other than the Carvey, or are very familiar with CNC Machining. Leave at the default value otherwise.
* The **Path Width** determines how wide the traces on the board should be. **Reduce** this value if traces join up where they shouldn't, **increase** this value if the circuit should carry large currents.
  
Click the **Export** button and choose a location to export the file to when you are ready to carve your board.

## The Workspace
![](https://imgur.com/WewlDeR.png)
This grid here represents the circuit board.
* The size of the workspace may be changed using the **Height** and **Width** options in the **Board Tab** of the **Context Menu**
* The size of the grid squares may be changed using the **Square Size** option in the **Board Tab** of the **Context Menu**
* The black L-Shape in the above image represents the smartclamp of the Carvey. This may be enabled or disavled with the **Using Carvey** option in the **Board Tab** of the **Context Menu**

### The Workspace: Drawing a circuit board
* To draw a line trace:
  * Select the **Path** tool from the **Tools Tab** of the **Context Menu**.
  * Click the start point of the line trace on the grid (**Not** click-and-drag).
  * Click the end point of the line trace on the grid (**Not** click-and-drag).
![](https://imgur.com/vyzPKui.png)
  
* To draw a component hole:
  * Select the **Hole** tool from the **Tools Tab** of the **Context Menu**.
  * Click the point on the grid where you want the hole.
![](https://imgur.com/JEUxCG0.png)

* To select items you have drawn on the workspace:
  * Select the **Select** tool from the **Tools Tab** of the **Context Menu**.
  * Click the a point near the items you want to select (**Not** click-and-drag).
  * Click another point such that the green rectangle surrounds everything you want to select (**Not** click-and-drag).
    * To Select a line, ensure that either the **start point** or the **end point** of the line is within the green rectangle
  * The selected items should turn blue
![](https://imgur.com/W2gLNum.png)

* To move items you have drawn on the workspace:
  * Using the **Select** tool, select the items you want to move.
  * Click the **Move** button from the **Tools Tab** of the **Context Menu**.
  * Click a point in the workspace where you want to move it to (**Not** click-and-drag).
    * The selected items will be treated as if they are in a large rectangle when moving. The top left point of that rectangle will be the point that you click to move them to.

* To delete items you have drawn on the workspace:
  * Using the **Select** tool, select the items you want to move.
  * Click the **Delete** button from the **Tools Tab** of the **Context Menu**.
    * There is currently **NO** undo option. **Save your work** before deleting anything that might be important.

**Once you are done with your circuit board remember to save the board and export it for carving**











