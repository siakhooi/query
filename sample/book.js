// MongoDB initialization script for book database

// Switch to bookdb database
db = db.getSiblingDB('bookdb');

// Create bookuser if not exists
db.createUser({
  user: 'bookuser',
  pwd: 'bookpass',
  roles: [
    {
      role: 'readWrite',
      db: 'bookdb'
    }
  ]
});

// Create books collection and insert sample data
db.books.insertMany([
  {
    title: 'To Kill a Mockingbird',
    author: 'Harper Lee',
    genre: 'Fiction',
    year: 1960,
    publisher: 'J.B. Lippincott & Co.',
    pages: 281
  },
  {
    title: '1984',
    author: 'George Orwell',
    genre: 'Fiction',
    year: 1949,
    publisher: 'Secker & Warburg',
    pages: 328
  },
  {
    title: 'The Great Gatsby',
    author: 'F. Scott Fitzgerald',
    genre: 'Fiction',
    year: 1925,
    publisher: 'Charles Scribner\'s Sons',
    pages: 180
  },
  {
    title: 'Sapiens',
    author: 'Yuval Noah Harari',
    genre: 'Non-Fiction',
    year: 2011,
    publisher: 'Harvill Secker',
    pages: 443
  },
  {
    title: 'Educated',
    author: 'Tara Westover',
    genre: 'Non-Fiction',
    year: 2018,
    publisher: 'Random House',
    pages: 334
  },
  {
    title: 'The Hobbit',
    author: 'J.R.R. Tolkien',
    genre: 'Fantasy',
    year: 1937,
    publisher: 'George Allen & Unwin',
    pages: 310
  },
  {
    title: 'Harry Potter and the Philosopher\'s Stone',
    author: 'J.K. Rowling',
    genre: 'Fantasy',
    year: 1997,
    publisher: 'Bloomsbury',
    pages: 223
  },
  {
    title: 'The Da Vinci Code',
    author: 'Dan Brown',
    genre: 'Mystery',
    year: 2003,
    publisher: 'Doubleday',
    pages: 454
  },
  {
    title: 'A Brief History of Time',
    author: 'Stephen Hawking',
    genre: 'Science',
    year: 1988,
    publisher: 'Bantam Dell Publishing Group',
    pages: 256
  },
  {
    title: 'The Lean Startup',
    author: 'Eric Ries',
    genre: 'Business',
    year: 2011,
    publisher: 'Crown Business',
    pages: 336
  }
]);

print('Book database initialized successfully with ' + db.books.countDocuments() + ' books');
