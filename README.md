# CPSC 210 Personal Project

## Wireless Station Network Management System
This program is about managing a **Wireless Station Network** for a **telecom company**. The biggest aim of this project is to help engineers to do **_delay reduction_** on one-way message delivery within the network. 

### **Basic Data Structure** (_Fields_)
- Network Class
  - Network Name
  - X,Y coordinate bounds (_Network area bounds_)
  - List of Stations

- Station Class
  - Station ID
  - X,Y coordinates
  - Online/Offline Status
  - Max power signal radius
  - Current power signal radius
  - Inbox message

- Connection Class
  - To Station
  - Distance (_physical_)

### **Basic Features** (_Methods_)
- Network Class
  - Check reachable from one station to another
  - Get path with the least hops from src to dest
  - Get direct distance between 2 stations
  - Visualizing stations status on UI

- Station Class
  - Configure power signal radius
  - Check reachable to another station
  - Get path with the least hops from this to dest
  - Send message to a reachable station
  - Display inbox

### **User Stories**
- As a user, I want to be able to add stations to network.
- As a user, I want to be able to set _power signal radius_ of a station.
- As a user, I want to be able to send message from one station to another.
- As a user, I want to be able to check inbox of a station.
- As a user, I want to be able to find a path of message delivery with the least hops.
- As a user, I want to be able to check connectivity from one station to another.
- As a user, I want to be able to store the current network, so that I can manage it later.
- As a user, I want to be able to visualize the connection among stations on the program UI.
- As a user, I want to be able to save my Network to file.
- As a user, I want to be able to load my Network from file.
- As a user, I want to be able to plot my Network (Visualization).

### **References**
- "programIcon.png", retrieved from "https://cdn-icons-png.flaticon.com/512/149/149181.png".

### **Phase 4: Task 2**
(Generated by NetworkTest, only a part was retained because the original was TOO long)

- Event Log:
- Sun Apr 02 01:30:46 PDT 2023
- Network Created: "Unnamed"
- Sun Apr 02 01:30:46 PDT 2023
- Station Added: #1
- Sun Apr 02 01:30:46 PDT 2023
- Station Added: #2
- Sun Apr 02 01:30:46 PDT 2023
- Station Added: #3
- Sun Apr 02 01:30:46 PDT 2023
- Station Added: #4
- Sun Apr 02 01:30:46 PDT 2023
- Station Added: #5
- Sun Apr 02 01:30:46 PDT 2023
- Station Added: #6
- Sun Apr 02 01:30:46 PDT 2023
- Station Added: #8
- Sun Apr 02 01:30:46 PDT 2023
- Station Added: #7
- Sun Apr 02 01:30:50 PDT 2023
- Station Added: #11
- Sun Apr 02 01:30:54 PDT 2023
- Message Delivered: #1 -> #3
- Sun Apr 02 01:30:54 PDT 2023
- Station #5: Online -> false
- Sun Apr 02 01:30:56 PDT 2023
- Station #2: Online -> false
- Sun Apr 02 01:30:56 PDT 2023
- Station #1: Online -> false
- Sun Apr 02 01:30:56 PDT 2023
- Station Added: #8
- Sun Apr 02 01:30:58 PDT 2023
- Station #2: Online -> false
- Sun Apr 02 01:30:58 PDT 2023
- Station #6: Online -> false
- Sun Apr 02 01:30:58 PDT 2023
- Station #7: Online -> false
- Sun Apr 02 01:31:00 PDT 2023
- Station Removed: #5
- Sun Apr 02 01:31:04 PDT 2023
- Station #1: Online -> true
- Sun Apr 02 01:31:04 PDT 2023
- Station #1: Online -> false
- Sun Apr 02 01:31:04 PDT 2023
- Station #1: Online -> false
- Sun Apr 02 01:31:04 PDT 2023
- Station #1: Online -> true
- Sun Apr 02 01:31:06 PDT 2023
- Station #1: Signal Radius -> 50.50
- Sun Apr 02 01:31:06 PDT 2023
- Station #1: Signal Radius -> 1.00
- Sun Apr 02 01:31:06 PDT 2023
- Station #1: Signal Radius -> 0.00
- Sun Apr 02 01:31:06 PDT 2023
- Station #1: Signal Radius -> 100.00
- Sun Apr 02 01:31:06 PDT 2023
- Station #1: Signal Radius -> 101.00
- Sun Apr 02 01:31:10 PDT 2023
- Network Name -> "MyNet"

### **Phase 4: Task 3**
The refactoring I want to apply the most in my design is **_Abstracting the Generic Methods & Fields_** to an _<< interface >>_ or _<< abstract >>_ class. As we can see, there is no such abstractions newly created like those described above. Since the concept of this project did not expand that wide, there is no need to extract those for now, but that's really a good design manner for the later convenience of project expansion/modification/revision. Also, this refactoring will lead an elegant design pattern which adheres the rules of high Cohesion(single responsibility) & low Coupling (no duplication dependency which may cause functional inconsistency after a method's updated), and somewhat prevent the violations for newly joined programmers.