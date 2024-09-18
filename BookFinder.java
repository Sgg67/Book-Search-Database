package ass3;

/** 
 * This creates a dataset of books by reading the book data from 
 * GoodReadsData.txt
 */

import java.util.*;


import java.io.*;



/**
 * A BookFinder class holds mappings between ISBNs, titles, authors, 
 * publishers, and ratings to books. each book is an object of type {@link BookData}.
 * 
 * @version Fall 2022
 * 
 */
public class BookFinder {
	
	MyHashMap<String, BookData> isbnToData;
	MyHashMap<String, ArrayList<BookData>> titleToData;
	MyHashMap<String, ArrayList<BookData>> authorToData;
	MyHashMap<String, ArrayList<BookData>> publisherToData;
	MyHashMap<Float, ArrayList<BookData>> ratingToData;
   
	
	/**
	 * Default constructor: used for tests
	 */
	public BookFinder() {
    	isbnToData = new MyHashMap<String, BookData>();
    	titleToData = new MyHashMap<String, ArrayList<BookData>>();
    	authorToData = new MyHashMap<String, ArrayList<BookData>>();
    	publisherToData = new MyHashMap<String, ArrayList<BookData>>();
    	ratingToData = new MyHashMap<Float, ArrayList<BookData>>();       
    }
	
	/**
     * Creates a BookFinder object by reading the data file at path.
     * 
     * The input file is a comma-separated text file with 5 fields per line:
     * isbn,authors,title,publisher,rating
     * 
     * Multiple authors are separated by '/' characters: 
     * for example: Frank Herbert/Domingo Santos
     * 
     * @param path The file path for the input data file.
     */
    public BookFinder (String path) {
    	this();
    	
    	fillDataFromFile(path);
    }   
    
    
    /*
     * You need to open the data file with a "UTF-8" flag, as in
     * 
     * Scanner scan = new Scanner( new File(s), "UTF-8");
     *
     * Parse each line of the file and create a new BookData object 
     * with the relevant fields. 
     * 
     * Put the newly created BookData object into isbnToData map with the isbn as the key.
     * 
     * For the other maps, add the BookData object to the ArrayList stored in the map with
     * the appropriate key (title, author, publisher, or rating). 
     * If a book has multiple authors, then each author's list should contain the BookData object.
     */
    private void fillDataFromFile(String path) {
    	Scanner scan = null;
	    try {
	        scan = new Scanner(new File(path), "UTF-8");
	        while(scan.hasNextLine()){
	        	String line = scan.nextLine();
	        	String[] word = line.split(",");	
	        	 if(word.length == 5) {
	        		 String isbn  = word[0];
	        		 String[] authors = word[1].split("/");
	        		 String title =  word[2];
	        		 String publisher = word[3];
	        		 Float rating = Float.parseFloat(word[4]);
	        		 BookData book =  new BookData(isbn, authors, title, publisher, rating);
	        		 
	        		 
	        		  addBookByISBN(isbn, book);
	                  addBookByTitle(title, book);
	                  for (String author : authors) {
	                      addBookByAuthor(author, book);
	                  }
	                  addBookByPublisher(publisher, book);
	                  addBookByRating(rating, book);
	        	 }
	        	 
	        }	
	     }
	     catch (FileNotFoundException e) {
	        System.err.println("File not found");
	    }
    	}

    
    
    /** 
     * Adds the isbn as a key and the BookData object as a value into the isbnToData map
     * 
     * @param isbn - book ISBN
     * @param bd - the BookData object
     */
    public void addBookByISBN(String isbn, BookData bd) {
    	isbnToData.put(isbn, bd);
    	
    }
    
    /** 
     * Adds the title as a key and the BookData object as a value into the titleToData map
     * Note that a title is not guaranteed to be unique, that is why 
     * you should store a list of BookData objects for each title key.
     * 
     * @param title - book title
     * @param bd - the BookData object
     */
    public void addBookByTitle (String title, BookData bd) {
     if(titleToData.containsKey(title)) {
    	 titleToData.get(title).add(bd);
     }
     else {
    	 titleToData.put(title, new ArrayList<BookData>());
    	 titleToData.get(title).add(bd);
     }
       
    }
    
    /** 
     * Adds the author as a key and the BookData object as a value into the authorToData map
     * There are of course many books for the same author, that is why 
     * you should store a list of BookData objects for each author key.
     * 
     * @param author - an author
     * @param bd - the BookData object
     */
    public void addBookByAuthor (String author, BookData bd) {
      if(authorToData.containsKey(author)) {
    	  authorToData.get(author).add(bd);
      }
      else {
    	  authorToData.put(author, new ArrayList<BookData>());
    	  authorToData.get(author).add(bd);
      }
        
    }
    
    /** 
     * Adds the publisher as a key and the BookData object as a value into the authorToData map
     * There are many books from the same publisher, that is why 
     * you should store a list of BookData objects for each key.
     * 
     * @param publisher - a publisher
     * @param bd - the BookData object
     */
    public void addBookByPublisher (String publisher, BookData bd) {
    	if(publisherToData.containsKey(publisher)) {
    		 publisherToData.get(publisher).add(bd);
    	}
    	else {
    		publisherToData.put(publisher, new ArrayList<BookData>());
    		publisherToData.get(publisher).add(bd);
    	}
    	    
    }
    
    /** 
     * Groups all the books with the same rating under the same map key
     * 
     * There are many books with the same rating, that is why 
     * you should store a list of BookData objects for each key.
     * 
     * @param rating - a book rating
     * @param bd - the BookData object
     */
    public void addBookByRating(Float rating, BookData bd) {
    	if(ratingToData.containsKey(rating)){
    		 ratingToData.get(rating).add(bd);
    	}
    	else {
    		ratingToData.put(rating,new ArrayList<BookData>());
    		ratingToData.get(rating).add(bd);
    	}
    }
    
   

	/**
	 * Returns a list of books written by the author.
	 * 
	 * @param author The author to search for.
	 * @return A list of {@link BookData} objects written by author.
	 */
	public List<BookData> searchByAuthor(String author) {
		List<BookData> books = new ArrayList<>();
	   if(authorToData.containsKey(author)) {
		  books = authorToData.get(author);
	   }
	   else {
		   System.out.println("Author does not exist");
		   return null;
	   }
	   return books;
		
	}

	/**
	 * Returns a list of books with the exact title.
	 * 
	 * @param title The title to search for.
	 * @return A list of {@link BookData} objects with the given title.
	 */
	public List<BookData> searchByTitle(String title) {
		List<BookData> books = new ArrayList<>();
	    if(titleToData.containsKey(title)) {
	    	books = titleToData.get(title);
	    }
	    else{
	    	System.out.println("Book title does not exist");
	    }
	    return books;
	  
	}

	/**
	 * Returns a list of books published by publisher.
	 * 
	 * @param publisher The publisher to search for.
	 * @return A list of {@link BookData} published by the publisher.
	 */
	public List<BookData> searchByPublisher(String publisher) {
		
		List<BookData> books = new ArrayList<>();
	    if(publisherToData.containsKey(publisher)) {
	    	books = publisherToData.get(publisher);
	    }
	    else{
	         System.out.println("Publisher does not exist");
	    }
	    return books;
	}

	/**
	 * Returns a book corresponding to an ISBN, or null if no such book is in the
	 * database.
	 * 
	 * @param isbn The ISBN to search for.
	 * @return A (unique) {@link BookData} corresponding to the isbn, or null.
	 */
	public BookData searchByIsbn(String isbn) {
	    if(isbnToData.containsKey(isbn)){
	    	return isbnToData.get(isbn);
	    }
	    System.out.println("ISBN number does not exist");
	    return null;
	}
	
	/**
	 * Returns a list of books with the same rating
	 * 
	 * @param rating The value of book rating.
	 * @return A list of {@link BookData} with this rating.
	 */
	public List<BookData> searchByRating(Float rating) {
		List<BookData> books = new ArrayList<>();
	    if(ratingToData.containsKey(rating)) {
	    	books = (ratingToData.get(rating));
	    }
	    else{
	    	System.out.println("rating does not exist");
	    }
	    return books;
	}
}
