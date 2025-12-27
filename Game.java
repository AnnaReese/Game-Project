import java.util.*;
/**
 * Game class for the Haunted House adventure game.
 *
 * Game name: Haunted House
 *
 * Description:
 * You wake up outside of a castle. 
 * You hit your head hard after slipping. 
 * You're confused and only have a paperclip01 on you. 
 * You quickly notice a ghost inside the castle that wanders around teleporting randomly. 
 * You find a letter in your pocket telling you that you must find a note and deliver it to the ghost inside. 
 * The letter also says that if the ghost finds you without the note it will kill you (game ends and you lose). 
 * You must deliver the note to the ghost to finish and win. 
 * You leave the letter on the ground and start exploring
 *
 * Important implementation features: 
 * There is a ghost that teleports from room to room using Math.random() within a method. 
 * There is a feature (write pen) that allows a person to type in the console and have their message stored in the book item. 
 * Back takes a person to the previous room. 
 * Go (direction) allows them to travel from room to room.
 * Drop drops an item. Pickup pickups an item. 
 * Inventory shows every item you carry. 
 * Look allows a user to see the items in a room. 
 * Unlock allows a user to get from the outside to the foyer. 
 * Search allows a user to search select items. 
 * Cut allows a user to cut the cake. 
 * Rip allows a user to rip open the stuffedBunny. 
 * Call allows a user to call the ghost with the whistle. 
 * Deliver allows a user to deliver the note to the ghost and win
 *  
 *  @author  Anna
 *  @version .1
 */

public class Game 
{
    // These are the commands that are available.
    private final String INITIAL_COMMANDS = "go quit help map back drop pickup inventory look unlock search cut write rip call deliver ";
    // This is the current room that that player is in
    private Room currentRoom;
    // This is the list of items that the person is carrying
    private Room previousRoom;
    // Stores the previous room we were in
    private String inventory;
    // tells if the game is over or not
    private boolean finished;
    // tells if stuffedBunny has already been ripped
    private boolean ripstuffedBunny = false;
    // tells if cake has already been cut
    private boolean cutCake = false;
    // tells if book has been written in
    private boolean writeBook = false;
    // tells if whistle has been called
    private boolean called = false;
    // You do not have note00
    private boolean haveNote = false;
    // tells if map has been called or not
    private boolean mapCalled = false;
    // tells the weight of inventory
    private int weight = 1;
    // the max weight for inventory
    final public int MAX_WEIGHT = 10;
    // moves ghost every ten commands a user does
    final public int EVERY_TEN = 10;
    // holds the user added string inside the book
    private String book = "";
    // These are references to the rooms that are available.  
    // The actual rooms are created in createRooms().
    private Room outside, foyer, kitchen, library, bedroom, balcony;  
    // counts the number of commands + 1 done, used for moving ghost
    private int gameCount = 1; 
    // Picture that is being shown
    private Image image; 
    // This is the parser that deals with input.  
    private Parser parser;


    /**
     * Game constructor
     */
    public Game() 
    {
        createRooms();
        parser = new Parser(INITIAL_COMMANDS);
        inventory = "paperclip01 ";
        finished = false;
        changeImage();

    }


    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        // create the rooms
        outside = new Room();
        foyer = new Room();
        kitchen = new Room();
        library = new Room();
        bedroom = new Room();
        balcony = new Room(); 

        // set the descriptions
        outside.setDescription("outside of the castle");
        foyer.setDescription("entrance of the castle (foyer)");
        kitchen.setDescription("the castle's kitchen");
        library.setDescription("the castle's library");
        bedroom.setDescription("the castle's bedroom");
        balcony.setDescription("the castle's balcony");
        
        // initialise room exits
        outside.setExits(foyer, null, null, null);
        foyer.setExits(null, kitchen, outside, null);
        kitchen.setExits(library, null, null, foyer);
        library.setExits(null, null, kitchen, bedroom);
        bedroom.setExits(balcony, library, null, null);
        balcony.setExits(null, null, bedroom, null);

        // set the items
        outside.setItems("doorLock03 "); // paperclip to get inside of door (unlock doorLock, only works if have paperclip)
        foyer.setItems("coat02 umbrella02 "); // search coat (no note),umbrella is useless
        kitchen.setItems("cake03 knife02 "); // cut the cake to try to find note (no note)
        library.setItems("book03 pen01 "); // search book to look for note (find a message in there, but it's absolutely useless and is really just rambels), write pen (this writes a message in the book just for fun and only if you have the book)
        bedroom.setItems("pillow01 stuffedBunny02 "); // rip stuffedBunny with knife to look for note (its in there), pillow is useless
        balcony.setItems("whistle01 "); // must deliver note to ghost so it doesn't kill you, must have ghost in the room though which you can call with whistle (call whistle)
        

        // start game outside
        currentRoom = outside; 

        // ghost starts on balcony
        balcony.addItems("ghost"); 

    }


    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.


        while (!finished)
        {
            if (currentRoom.getItems().indexOf("ghost") > -1 && !haveNote) { // ghost is in room, but you do not have note, you lose
                finished = true;
                System.out.println("You have lost the game because you have entered a room with the wandering ghost which has killed you because you are without the note. Or the ghost wandered into your room while you are without the note and so it has killed you.");
            }

            Command command = parser.getCommand();
            processCommand(command);
            changeImage(); // changes image
            if (gameCount % EVERY_TEN == 0) { // moves ghost every 10 commands called 
                ghostExit(); // removes ghost from old room 
                moveGhost(); // adds ghost to new room 
            }

            gameCount++; // counts number of commands done + 1
        }

        System.out.println("Thank you for playing.  Goodbye."); // finished

    }


    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to Adventure!");
        System.out.println("Adventure is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println("You wake up outside of a castle. You hit you're head hard after slipping. You're confused and only have a paperclip01 on you. You quickly notice a ghost inside the castle that wanders around teleporting randomly. You find a letter in your pocket telling you that you must find a note and deliver it to the ghost inside. The letter also says that if the ghost finds you without the note it will kill you (game ends and you lose). You must deliver the note to the ghost to finish and win. You leave the letter on the ground and start exploring");
        System.out.println();
        System.out.println(currentRoom);
    }


    /**
     * Given a command, process the command.
     * If this command ends the game, true is returned, otherwise false is returned.
     */
    private void processCommand(Command command) 
    {
        if(command.isUnknown())
        {
            System.out.println("I don't know what you mean...");
            return;
        }


        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        } else if (commandWord.equals("go")) {
            goRoom(command);
        } else if (commandWord.equals("back")) {
            back(command);
        } else if (commandWord.equals("drop")) {
            drop(command);
        } else if (commandWord.equals("inventory")) {
            inventory(command);
        } else if (commandWord.equals("pickup")) {
            pickup(command);
        } else if (commandWord.equals("look")) {
            look(command);
        } else if (commandWord.equals("unlock")) {
            unlock(command);
        } else if (commandWord.equals("search")) {
            search(command);
        } else if (commandWord.equals("cut")) {
            cut(command);
        } else if (commandWord.equals("write")) {
            write(command);
        } else if (commandWord.equals("rip")) {
            rip(command);
        } else if (commandWord.equals("map")) {
            map(command);
        } else if (commandWord.equals("call")) {
            call(command);
        } else if (commandWord.equals("deliver")) {
            deliver(command);
        } else if (commandWord.equals("quit")) {
            finished = true;  // signal that we want to quit
        } else {
            System.out.println("Command not implemented yet.");
        }

    }


    /**
     * Print out some help information.
     * Here we print a cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("Your command words are: "); 
        parser.showCommands();
        System.out.println();
        System.out.println("Hint for where the note could be: It is on this holiday that the (blank) hides something");
    }


    /** 
     * Try to go to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know where to go
            System.out.println("Go where?");
            return;
        }

        // gets word after goRoom
        String direction = command.getSecondWord();

        // See what the next room in that direction is
        Room nextRoom = currentRoom.nextRoom(direction);

        if (nextRoom == null) // there is no room in that direction
            System.out.println("There is no door!");
        else // go into the next room
        {

            if (currentRoom == outside) {
                System.out.println("Cannot go from outside to foyer (north) without unlocking the doorLock03");
            } // can't go from outside to foyer w/o unlocking door using paperclip and doorLock
           
            else {
            // we just entered a new room
            previousRoom = currentRoom; 
            currentRoom = nextRoom;
            changeImage(); // method to change image we see
            System.out.println(currentRoom.toString()); // Feedback
            }
        }
    }


    /** 
     * Goes back to the previous room 
     */
    private void back(Command command) 
    {

        // See what the previous room in that direction is
        Room nextRoom = previousRoom; 

        if (previousRoom == null) // there is no room in that direction
            System.out.println("There is previous no room!");
        else // go into the previous room
        {
            // we just entered the previous room 
            currentRoom = previousRoom;
            changeImage(); // method to change image we see
            System.out.println("You have just entered " + currentRoom.toString() + ". You cannot call back twice in a row. You will just stay in the same room as you are now"); // Feedback
        }
    }


    /** 
     * Drops item
     */
    private void drop(Command command) 
    {
    
        String item = command.getSecondWord();
        String roomItems = currentRoom.getItems();

        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know where to go
            System.out.println("Drop what?");
            return;
        }


        if (item == null) // there is no item
            System.out.println("There is no item!");
        else if (inventory.indexOf(item) == -1)
            System.out.println("You do not have this item in your inventory to drop");
        else // drops item
        {
            
            // we just dropped the item and it is out of our inventory
            String inventoryOne = inventory.substring(0, inventory.indexOf(item));
            String inventoryTwo = inventory.substring(inventory.indexOf(item) + item.length());
            inventory = inventoryOne + inventoryTwo;
            System.out.println(inventory.toString()); // Feedback

            // now the dropped item is part of the currentRoom items
            currentRoom.setItems(item + " " + roomItems);

            // sets the new weight
            int itemWeight = Integer.parseInt(item.substring(item.length() - 2));
            weight = weight - itemWeight;

            // if you dropped the note, this sets haveNote to false
            if (item.equals("note00")) {
                haveNote = false;
            }

            System.out.println("Your weight is " + weight + " you cannot pickup items if you exceed a weight of 10"); // Feedback
            
        }
    }


    /** 
     * Lists inventory
     */
    private void inventory(Command command) 
    {   
        System.out.println("You have " + inventory); //Feedback 
        System.out.println();
        System.out.println("The weight of your items totals to " + weight); //Feedback          
    }


    /** 
     * Picks up item
     */
    private void pickup(Command command) 
    {
        
        String item = command.getSecondWord();
        String roomItems = currentRoom.getItems();

        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know what to pickup
            System.out.println("Pickup what?");
            return;
        }

        String itemNum = item.substring(item.length() - 2);

        if (roomItems.indexOf(item) == -1) {
            System.out.println("This item is not in this room, does not exist, or you have already picked it up and it's in your inventory");
        } else if (item == null) { // there is no item
            System.out.println("There is no item!");
        } else if (itemNum.indexOf("0") == -1) {
            System.out.println("Please type weight of item");
        } else if (weight + Integer.parseInt(item.substring(item.length() - 2)) > MAX_WEIGHT) { // weight is too great to pickup item
            System.out.println("Your inventory plus this new item weighs too much for you to pick up another item");
            System.out.println("Your inventory is " + inventory);
            System.out.println("Your weight is " + weight);
        } else { // picks up item 
            // we just picked up the item

            // gets rid of item from current room
            String roomItemsOne = roomItems.substring(0, roomItems.indexOf(item));
            String roomItemsTwo = roomItems.substring(roomItems.indexOf(item) + item.length());
            roomItems = roomItemsOne + roomItemsTwo;
            currentRoom.setItems(roomItems);

            // adds the weight of an item to the total weight
            int itemWeight = Integer.parseInt(item.substring(item.length() - 2));
            weight = weight + itemWeight;

            // if you picked up the note, this sets haveNote to true
            if (item.equals("note00")) {
                haveNote = true;
            }

            // adds item to inventory
            inventory = inventory + " " + item;
            System.out.println(inventory.toString()); // Feedback
            System.out.println();
            System.out.println("Your weight is " + weight + " you cannot pickup items if you exceed a weight of 10"); // Feedback
        }
    }


    // Looks for items in a room
    private void look(Command command) {

            System.out.println("the room has " + currentRoom.getItems());

    }


    /** 
     * unlocks the doorLock, only works if you have both the paperclip and the doorLock. 
     * Now you are in the foyer. 
     * If you exit the foyer to the outside and try to get back in the door again, you will need to unlock the doorLock again to get in
     */
    private void unlock(Command command) 
    {
        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know where to go
            System.out.println("Unlock what?");
            return;
        }

        String object = command.getSecondWord();


        if (object == null) // there is no doorLock
            System.out.println("Must find doorLock03");
        else if (currentRoom != outside) 
            System.out.println("You must be outside to unlock the doorLock03");
        else if (inventory.indexOf("paperclip") > -1 && inventory.indexOf("doorLock03") > -1 && object.equals("doorLock03")) {
            // you have both items and can move to the next room
            currentRoom = foyer;
            changeImage();
            System.out.println("You have now left outside and are in the foyer. Keep looking for the note. Tip: If you go back outside without both doorLock03 and paperclip01 you will not be able to get back inside");
        }
        else if (!object.equals("doorLock03")) {
            // the unlock command is being called on something that is not a doorLock
            System.out.println("You are trying to unlock the wrong object or an object that does not exist");
        }
        else if (inventory.indexOf("paperclip") > -1 && inventory.indexOf("doorLock03") == -1) {
            // you must find the doorLock
            System.out.println("You must find the doorLock03. If you are outside now and were previously not outside and dropped it somewhere in the house type \"quit\". This is because there is now no way for you to get inside (you have lost). If this is not the case, carry on");
        }
        else if (inventory.indexOf("paperclip") == -1 && inventory.indexOf("doorLock03") > -1) {
            // you must pick up the paperclip
            System.out.println("You must find the paperclip. If you are outside now and were previously not outside and dropped it somewhere in the house type \"quit\". This is because there is now no way for you to get inside (you have lost). If this is not the case, carry on");
        }
        else if (inventory.indexOf("paperclip") == -1 && inventory.indexOf("doorLock03") == -1) {
            // you must find both the doorLock and the paperclip
            System.out.println("You must find the doorLock03 and paperclip. If you are outside now and were previously not outside and dropped them somewhere in the house type \"quit\". This is because there is now no way for you to get inside (you have lost). If this is not the case, carry on");
        }
    }


    /** 
     * You can search the coat and book
     */
    private void search(Command command) 
    {

        String object = command.getSecondWord();

        if (!command.hasSecondWord())
        {
            // if there is no second word, we don't know what to search
            System.out.println("Search what?");
            return;
        }
        // if writeBook is on (meaning you have written in the book) search book jumps to the message() method which shows orgianl message + yours
        else if (writeBook && inventory.indexOf("book03") > -1 && object.equals("book03")) {
                message();
        } else if (writeBook && object.equals("book03") && inventory.indexOf("book03") == -1) {
                System.out.println("You must have the book03 in your inventory to view your message");
        } else if (object == null) // there is no object to search
            System.out.println("There is no object!");
        else if (!object.equals("coat02") && !object.equals("book03")) 
            // calling search on the wrong object or DNE object
            System.out.println("You are searching the wrong object or an object that does not exist"); 
        else // search book or coat
        {

            if (object.equals("coat02") && inventory.indexOf("coat02") > -1) { // have coat and search it
                System.out.println("You search the coat02 looking for the note. Unfortunately, all you find are five gum wrappers, an unwrapped peppermint (which you don't try to eat, who knows where it's been), and a buisness card for a local clown that claims to make \"the best balloons in town\" ");
            } else if (object.equals("coat02") && inventory.indexOf("coat02") == -1){ // need to find coat
                System.out.println("You must have the coat02 in your inventory to search it");
            }
           
            else if (object.equals("book03") && inventory.indexOf("book03") > -1) { // have book and search it
                System.out.println("You start to read through the book03... Four score and 11 years ago there lived a little squirrel by the name of Gertrudis ... oh great, this book is absolutely useless " );
                System.out.println();
            } else if (object.equals("book03") && inventory.indexOf("book03") == -1){ // need to find book
                System.out.println("You must have the book03 in your inventory to search it");
            }

            else 
                System.out.println("You cannot search this object. Try another action");
        }
    }


    /** 
     * You can cut the cake to look for the note. 
     * In order to do this you must've picked up the knife and the cake
     */
    private void cut(Command command) 
    {
        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know what to cut
            System.out.println("Cut what?");
            return;
        }

        String object = command.getSecondWord();


        if (object == null) // there is no cake
            System.out.println("You must find the cake03");
        else if (cutCake) {
            System.out.println("You have already cut the cake03 and cannot do this action again");
        }
        else if (inventory.indexOf("cake03") > -1 && inventory.indexOf("knife02") > -1 && object.equals("cake03")) {
            // you cut the cake
            cutCake = true;
            System.out.println("You have now cut the cake03. There's no note in there, but it's a delightful red velet cake with fluffy white cream cheese frosting. You take a second to eat a slice (with your hands, this house has no forks to pickup) and quickly realize you need to stop eating the cake and get back to the task at hand of finding a note so the ghost doesn't kill you");
        }
        else if (!object.equals("cake03")) {
            // the cut command is being called on something that is not a cake
            System.out.println("You are trying to cut the wrong object or an object that does not exist");
        }
        else if (inventory.indexOf("knife02") > -1 && inventory.indexOf("cake03") == -1) { // you have the knife, but not the cake
            System.out.println("You must find the cake03");
        }
        else if (inventory.indexOf("knife02") == -1 && inventory.indexOf("cake03") > -1) { // you have the cake, but not the knife
            System.out.println("You must find the knife02");
        }
        else if (inventory.indexOf("knife02") == -1 && inventory.indexOf("cake03") == -1) { // you need to find both the cake and the knife
            System.out.println("You must find the cake03 and knife02");
        }
    }



    /** 
     * You can use the pen to write in the book. 
     * This reqires both the pen and the book in your inventory
     */
    private void write(Command command) 
    {
        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know what to write with
            System.out.println("Write with what?");
            return;
        }

        String object = command.getSecondWord();


        if (object == null) // there is no pen
            System.out.println("You must find the pen01");
        else if (inventory.indexOf("pen01") > -1 && inventory.indexOf("book03") > -1 && object.equals("pen01")) {
            // you can write with the pen in the book
            System.out.println("This isn't very helpful in your progression in the game, but for fun you now can write a message that will appear in the book. Write your message and enter it and it will now be stored in the book03. You can type \"search book03\" to see your message");

            message(); // calls the message command which allows user to write in the book
        }
        else if (!object.equals("pen01")) {
            // the write command is being called on something that is not a pen
            System.out.println("You are trying to write with the wrong object or an object that does not exist");
        }
        else if (inventory.indexOf("book03") > -1 && inventory.indexOf("pen01") == -1) { // you have the book, but not the pen
            System.out.println("You must find the pen01 to write with");
        }
        else if (inventory.indexOf("book03") == -1 && inventory.indexOf("pen01") > -1) { // you have the pen, but not the book
            System.out.println("You must find the book03 to write in");
        }
        else if (inventory.indexOf("pen01") == -1 && inventory.indexOf("book03") == -1) { // you need to find both the pen and the book
            System.out.println("You must find the pen01 and book03");
        }
    }


    /** 
     * You can use the knife to rip the stuffedBunny. 
     * This reqires both the stuffedBunny and the knife in your inventory
     */
    private void rip(Command command) 
    {
        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know what to rip open
            System.out.println("Rip what?");
            return;
        }

        String object = command.getSecondWord();

        
            if (object == null) // there is no stuffedBunny
                System.out.println("You must find the stuffedBunny02");
            else if (ripstuffedBunny) 
                System.out.println("You have already ripped open the bunny. You cannot do this action again. If you have dropped the note you must go find it and pick it up");
            else if (inventory.indexOf("stuffedBunny02") > -1 && inventory.indexOf("knife02") > -1 && object.equals("stuffedBunny02")) {
                // you can rip open the stuffedBunny with the knife
                System.out.println("You rip open the stuffedBunny02 using the knife02. To your surprise there's a note (note00) in an envolope which you can pickup to add to your inventory."); 

                currentRoom.setItems(currentRoom.getItems().toString() +" note00");

                ripstuffedBunny = true;

                haveNote = true;
            }
            else if (!object.equals("stuffedBunny02")) {
                // the write command is being called on something that is not the stuffedBunny
                System.out.println("You are trying to rip open the wrong object, an object that does not exist, or you have forgotten to type the weight after the item name");
            }
            else if (inventory.indexOf("knife02") > -1 && inventory.indexOf("stuffedBunny02") == -1) { // you have the knife, but not the stuffedBunny
                System.out.println("You must find the stuffedBunny02 to rip open");
            }
            else if (inventory.indexOf("knife02") == -1 && inventory.indexOf("stuffedBunny02") > -1) { // you have the knife, but not the stuffedBunny
                System.out.println("You must find the knife02 to rip open with");
            }
            else if (inventory.indexOf("stuffedBunny02") == -1 && inventory.indexOf("knife02") == -1) { // you need to find both the stuffedBunny and the knife
                System.out.println("You must find the knife02 and stuffedBunny02");
        }
    }

   
   /**
   * The method used so person can write in book
   */
    private void message() {

        if (!writeBook) {
        // This is how you read input from the console
            Scanner reader = new Scanner(System.in);
            String inputLine = reader.nextLine(); // holds the full input line
            book = inputLine;
            System.out.println(book);

            writeBook = true; // turns writeBook on so next time search book is called and then message() is called your written message will be shown

        } else {
            System.out.println("You start to read through the book03... Four score and 11 years ago there lived a little squirrel by the name of Gertrudis ... oh great, this book is absolutely useless.");
            System.out.println();
            System.out.println(book);
            
        }


    }


    /**
    * changes the image when you go to another room
    */
    private void changeImage() {
        image = new Image();

        if (finished) {
            image.setImage("Outside.jpg");
        }

        if (mapCalled) {
            mapCalled = false;
            return;
        }

        if (currentRoom == outside) {
            image.setImage("Outside.jpg");
        }
        else if (currentRoom == foyer) {
            image.setImage("Foyer.jpg");
        }
        else if (currentRoom == kitchen) {
            image.setImage("Kitchen.jpg");
        }
        else if (currentRoom == library) {
            image.setImage("Library.jpg");
        }
        else if (currentRoom == bedroom) {
            image.setImage("Bedroom.jpg");
        }
        else if (currentRoom == balcony) {
            image.setImage("Balcony.jpg");
        }
    }


    /**
    * Method that shows the map
    */
    public void map(Command command) {

        image.setImage("Map.jpg");
    
        mapCalled = true;

    }


    /**
    * Method that moves ghost charecter
    */
    public void moveGhost() {

       int num = (int)(Math.random() * 6) + 1;
            
       if (num == 1) {
            outside.addItems("ghost");
       } else if (num == 2) {
            foyer.addItems("ghost");
       } else if (num == 3) {
            kitchen.addItems("ghost");
       } else if (num == 4) {
            library.addItems("ghost");
       } else if (num == 5) {
            bedroom.addItems("ghost");
       } else if (num == 6) {
            balcony.addItems("ghost");
       }
    }


    /**
    * Method that removes ghost from old room
    */
    public void ghostExit() {

        if (balcony.getItems().indexOf("ghost") > -1) {
            balcony.setItems(balcony.getItems().substring(0, balcony.getItems().indexOf("ghost")) + balcony.getItems().substring(0, balcony.getItems().indexOf("ghost") + 5));

        } if (outside.getItems().indexOf("ghost") > -1) {
            outside.setItems(outside.getItems().substring(0, outside.getItems().indexOf("ghost")) + outside.getItems().substring(0, outside.getItems().indexOf("ghost") + 5));

        } if (foyer.getItems().indexOf("ghost") > -1) {
            foyer.setItems(foyer.getItems().substring(0, foyer.getItems().indexOf("ghost")) + foyer.getItems().substring(0, foyer.getItems().indexOf("ghost") + 5));

        } if (kitchen.getItems().indexOf("ghost") > -1) {
            kitchen.setItems(kitchen.getItems().substring(0, kitchen.getItems().indexOf("ghost")) + kitchen.getItems().substring(0, kitchen.getItems().indexOf("ghost") + 5));

        } if (library.getItems().indexOf("ghost") > -1) {
            library.setItems(library.getItems().substring(0, library.getItems().indexOf("ghost")) + library.getItems().substring(0, library.getItems().indexOf("ghost") + 5));

        } if (bedroom.getItems().indexOf("ghost") > -1) {
            bedroom.setItems(bedroom.getItems().substring(0, bedroom.getItems().indexOf("ghost")) + bedroom.getItems().substring(0, bedroom.getItems().indexOf("ghost") + 5));

        }
        
    }


    /**
    * method that calls the ghost using the whistle
    */
    public void call(Command command) {

        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know what to call
            System.out.println("Call what?");
            return;
        }

        String object = command.getSecondWord();

        if (!object.equals("whistle01")) {
            System.out.println("You are calling something that doesn't exist or are calling with the wrong object");
        } else if (object.equals("whistle01") && inventory.indexOf("whistle01") > -1 && inventory.indexOf("note00") > -1) { // you have note and whistle
            called = true; // ghost has been called
            currentRoom.addItems("ghost");
            System.out.println("You have now called the ghost to your room, the ghost does not kill you because you have the note. You may now deliver the note to the ghost");
        } else {
            System.out.println("You are missing something to call the ghost with the whistle01 or are missing the whistle01 itself");
        }
    }


    /**
    * method that delivers the note to the ghost so you win
    */
    public void deliver(Command command) {
        if(!command.hasSecondWord())
        {
            // if there is no second word, we don't know what to deliver
            System.out.println("Deliver what?");
            return;
        }

        String object = command.getSecondWord();

        if (!object.equals("note00")) {
            System.out.println("You are calling something that doesn't exist or are calling with the wrong object");
        } else if (called && inventory.indexOf("note00") > -1 && currentRoom.getItems().indexOf("ghost") > -1) { // win senario
            System.out.println("You have delivered the note and have won. The ghost looks happy and upon seeing the note disappears. The house is no longer haunted");
            finished = true;
        } else {
            System.out.println("You must call the ghost with the whistle01 before delivering the note (even if you are in the same room as the ghost), you must have the note00, and the ghost must be in the room (the ghost could've moved since you called them with the whistle)");
        }

    }


}
