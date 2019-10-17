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
  * **This does NOT save the existing circuit board.** Ensure the existing board is saved before creating a new circuit board after editing an existing one.
* **Save** allows you to save the circuit board somewhere on your computer. The circuit board **MUST** be saved manually and will **NOT** save automatically.
* **Open** allows you to open a circuit board that you have previously saved. The circuit board must have been created in PCBDraw. No other PCB file formats are accepted.
  * '.GCode' files cannot be opened or edited this way, only '.GCB' files created using the **Save** option above

### The Context Bar
![](https://imgur.com/XA54tsW.png)
The Context Bar has three tabs:
* **Board** deals with the view of the workspace and size of the circuit board
* **Tools** lets you select the tools for drawing the circuit board
* **Export** lets you create a '.GCODE' file for use by a CNC router (e.g The Carvey) for cutting you circuit board




