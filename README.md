# EventApp

EventApp is an event application that contains information about various events and their organizers. The site has four different views: Home, Events, My Events, and Manage Events. 
Home welcome page and list of all Events are visible to all users. 
Every logged-in user can access the My Events page, where they can see their own events. 
The Manage Events view is only visible to admin users. In the project's resource folder, there is a test data file EventAppData.sql. 
It contains, among other things, 4 users, one of whom is an admin (Emma), as well as data for various events.


## Running the application

1. Clone the project from GitHub.
2. Create a new database in MySQL: CREATE DATABASE eventserver;
3. The project is configured so that test data should be loaded automatically when you start the application. The test data is located in the directory eventapp\src\main\resources\EventAppData.sql. If the automatic data loading doesn't work, you can import the test data manually.
4. Start the project and navigate to the address: http://localhost:8080


