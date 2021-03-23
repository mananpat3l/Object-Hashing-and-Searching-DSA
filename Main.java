import java.awt.print.Book;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    /** I , Manan Patel , 000826892 - original work and i have not copied from anyone else's work
     * and also i have not let anybody copy my code.
     */

    /**
     * The Hashset Search was the quickest. That is because the search runs in O(1) time. It is always constant regardless of how big the dataset is.
     * The starting point of the application
     */
    public static void main(String[] args)
       {
           ArrayList<Character> characters = new ArrayList<>();
        Ring Ring = new Ring();
        for (Name name : Name.values()) {
            characters.add(new Character(name.toString()));
        }

        /* Part A Starts Here */
        ArrayList<BookWord> bookWords = new ArrayList<>();
        //object created to store every word in the dictionary from the US.txt file as a BookWord.
        ArrayList<BookWord> dictionaryWords = new ArrayList<>();
        // File is stored in a resources folder in the project
        final String filename = "src/TheLordOfTheRIngs.txt";
        final String dictionaryFileName = "src/US.txt";
        long partAStart = System.nanoTime();
        //  Read the files for the book and dictionary with some additional steps taken to prepare for PART A and PART B
           int count = 0;
           int dictionaryWordCount = 0;
        int totalWordCount = 0;
        try {
            Scanner fin = new Scanner(new File(filename));
            fin.useDelimiter("\\s|\"|\\(|\\)|\\.|\\,|\\?|\\!|\\_|\\-|\\:|\\;|\\n");  // Filter - DO NOT CHANGE
            Scanner dictionaryScanner = new Scanner(new File(dictionaryFileName));
            dictionaryScanner.useDelimiter("\n");
            while (fin.hasNext() || dictionaryScanner.hasNext()) {
                if (fin.hasNext()) {
                    BookWord bookWord = new BookWord(fin.next().toLowerCase());
                    if (bookWord.getText().length() > 0) {
                        totalWordCount++;

                        //  If a bookWord has already been added, increment its count. Otherwise, add the bookWord to bookWords.
                        if (bookWords.contains(bookWord)) {
                            bookWords.get(bookWords.indexOf(bookWord)).incrementCount();
                        } else {
                            bookWord.incrementCount();
                            bookWords.add(bookWord);
                            count++;
                        }
                        // Iterate through Characters to check if bookWord is equal to an enumerated Character name.
                        breakLoop:
                        for (Character character : characters) {
                            //If a bookWord is Enum.Name, then add the Location to the Locations for that Character.
                            if (character.getName().equals(bookWord.getText())) {
                                character.addLocation(totalWordCount);
                                break breakLoop;
                            }
                        }
                        //If a bookWord is 'ring', then add the Location to the Locations for Ring.
                        if (bookWord.getText().equals("ring"))
                            Ring.addLocation(totalWordCount);
                    }
                }
                if (dictionaryScanner.hasNext()) {
                    String dictionaryWord = dictionaryScanner.next().toLowerCase();
                    if (dictionaryWord.length() > 0) {
                        //  Add each word from the dictionary file to the dictionary words ArrayList.
                        dictionaryWords.add(new BookWord(dictionaryWord));
                        dictionaryWordCount++;
                    }
                }
            }
            fin.close();
            dictionaryScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exception caught: " + e.getMessage());
        }
           //  Part A - HASHING AND SEARCH PERFORMANCE
        System.out.println("There are " + count + " words in the file " + filename);
        // ADD other code after here
           System.out.println("There are " + dictionaryWordCount + " words in the file: " + dictionaryFileName);

           //  Sorting the dictionary by text.
           Collections.sort(dictionaryWords, (t1, t2) -> t1.getText().compareTo(t2.getText()));

           //  Determining the number of unique words with a count of 1 in the ArrayList of BookWords.
           int diffWord = 0;
           for (BookWord bookWord : bookWords) {
               if (bookWord.getCount() == 1)
                   diffWord++;
           }
           System.out.println("Number of Different words = " + diffWord);
           //Use a two-keyed sorting algorithm by introducing a lambda function, which compares on the basis of count,
           //and on the basis of text for each BookWord (i.e. two-keyed comparison).
           long start = System.nanoTime();
           Collections.sort(bookWords, (t1, t2) -> {
               int totalOrder;
               //Multi-keyed ordering condition where the first object is ordered BEFORE the second object (has lesser order).
               int countOrder = t1.getCount().compareTo(t2.getCount());
               int textOrder = t1.getText().compareTo(t2.getText());

               if (countOrder == 0)
                   totalOrder = textOrder;
               else
                   totalOrder = countOrder;
               return totalOrder;
           });
           long end = System.nanoTime();
           System.out.println("Time to sort book words = " + (double) (end - start) / 1000000000 + " secs");
           System.out.println("\n The list of the 15 most frequent words and counts:");
           for (int i = 1; i < 11; i++)
               System.out.println(i + ". " + bookWords.get(bookWords.size() - i).toString());

           //Search for words exactly 64 count that is listed alphabetically
           System.out.println("\n WORDS OCCURRING 64 TIMES:");
           for (BookWord bookWord : bookWords) {
               if (bookWord.getCount() == 64) {
                   System.out.println(bookWord.toString());
               }
           }
           // Search for words not in the dictionary with ArrayList.contains() and measure time.
           start = System.nanoTime();
           int misspelledWordCount = 0;
           for (BookWord bookWord : bookWords)
               if (!dictionaryWords.contains(bookWord))
                   misspelledWordCount++;
           end = System.nanoTime();
           double linearSearchTime = (double) (end - start) / 1E+12;
           System.out.println("\n LINEAR SEARCH:" + misspelledWordCount + " misspelled words");

           // Search for words not in the dictionary with Collectiosecs.binarySearch() and measure time.
           start = System.nanoTime();
           misspelledWordCount = 0;
           for (BookWord bookWord : bookWords)
               if (Collections.binarySearch(dictionaryWords, bookWord, (t1, t2) -> t1.getText().compareTo(t2.getText())) < 0)
                   misspelledWordCount++;
           end = System.nanoTime();
           double binarySearchTime = (double) (end - start) / 1E+12;
           System.out.println("\n BINARY SEARCH:" + misspelledWordCount + " misspelled words");

           //  Creating a SimpleHashSet of dictionary book words.
           start = System.nanoTime();
           SimpleHashSet<BookWord> dictionaryHashed = new SimpleHashSet<>();
           for (BookWord word : dictionaryWords)
               dictionaryHashed.insert(word);
           end = System.nanoTime();
           double hashTime = (double) (end - start) / 1E+12;
           // Search for words not in the dictionary with SimpleHashSet.contains() and measure time.
           start = System.nanoTime();
           misspelledWordCount = 0;
           for (BookWord bookWord : bookWords)
               if (!dictionaryHashed.contains(bookWord))
                   misspelledWordCount++;
           end = System.nanoTime();
           double hashedSearchTime = (double) (end - start) / 1E+12;

           System.out.println("\n HASHSET SEARCH:" + misspelledWordCount + " misspelled words\n");
           System.out.println("Number of buckets = " + dictionaryHashed.getNumberOfBuckets());
           System.out.println("Largest bucket size = " + dictionaryHashed.getLargestBucketSize());
           System.out.println("Number of empty buckets = " + dictionaryHashed.getNumberOfEmptyBuckets());
           System.out.println("% empty buckets = " +
                   (double) dictionaryHashed.getNumberOfEmptyBuckets() / dictionaryHashed.getNumberOfBuckets() * 100);

           System.out.println("\nRatio of Linear to Hash = " + linearSearchTime / hashedSearchTime);
           System.out.println("Ratio of Binary to Hash = " + binarySearchTime / hashedSearchTime);
           System.out.println("\nTime for PART A = " + (double) (System.nanoTime() - partAStart) / 1000000000+ " secs\n");


           //  PART B - PROXIMITY SEARCH
           System.out.println("ORDER OF WHO REALLY WANTS the RING\n===================================\n");
           long startPartB = System.nanoTime();
           final int cutOff = 42;
           ArrayList<Integer> ringLocations = Ring.getLocations();
           for(int i = 0; i < bookWords.size(); i++){
               if(bookWords.get(i).getText().equalsIgnoreCase("ring"))
                   ringLocations.add(i);
           }

           startPartB = System.nanoTime();
           for(Integer i : ringLocations){  //First, loop through ringPositions
               if(i < cutOff){
                   for(int j = 0; j <= i + cutOff; j++){
                       incrementCall(j, characters, bookWords);
                   }
               }
               else if(i + cutOff > bookWords.size()){ //Second, characterPositions
                   for(int j = i - cutOff; j < bookWords.size(); j++){
                       incrementCall(j, characters, bookWords);
                   }
               }
               else{
                   for(int j = i - cutOff; j <= i + cutOff; j++){
                       incrementCall(j, characters, bookWords);
                   }
               }
           }
           //Collection sort for the characters

           Collections.sort(characters, new Comparator<Character>() {
               @Override
               public int compare(Character o1, Character o2) {
                   return Double.compare(o1.getClosenessFactor(), o2.getClosenessFactor());
               }
           }.reversed());

           //for printing the part B
           for(Character c : characters)
               System.out.printf("\n[%s,%d] Close to Ring %d ClosenessFactor %2.4f", c.getName(), c.getOccurrences(),+
                       c.getProximity(), c.getClosenessFactor());

           System.out.println("\nTime taken by part B: " + (System.nanoTime() - startPartB) / 1000000000 + " secs" );


       }
    public static void incrementCall(int index, ArrayList<Character> characters, ArrayList<BookWord> bookWords){
        for(Character character : characters){
            if(bookWords.get(index).getText().equals(character.getName()))
                character.incrementClosenessCount();
        }
    }
           /*final int cutOff = 42;  //Proximity cutoff.
           ArrayList<Integer> ringLocations = Ring.getLocations();
           //  Iterate through Locations for each character or word of interest in the book.
           for (Character character : characters) {
               }
               System.out.println(character.toString());
           }
           System.out.println("\nTime for PART B = " + (double) (System.nanoTime() - startPartB)/1000000000 + " secs");
       }*/
}
//END

