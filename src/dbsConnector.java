
import java.sql.*;
import java.sql.DriverManager;
import java.util.ArrayList;


public class dbsConnector {

    private int response;
    private Boolean tempUser;

    private int totUsers;
    private int roomTypeID;

    private static final String JDBCDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://localhost:3306/UniBookingDBS";
    private static String USERNAME = "";
    private static String PASSWORD = "";

    private Connection  connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    private ArrayList<User> userArray = new ArrayList<>();
    private ArrayList<User> userOfficeNo = new ArrayList<>();
    private int userID;

    private ArrayList<Room> roomTypeList = new ArrayList<>();
    private ArrayList<Room> roomArray = new ArrayList<>();
    private ArrayList<Room> roomNoList = new ArrayList<>();

    private ArrayList<Booking> bookingList = new ArrayList<>();


    public int authenticateUser(String username, String password) throws ClassNotFoundException, SQLException {

        USERNAME = username;
        PASSWORD = password;

        Class.forName(JDBCDRIVER);
        try {
            connection = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            response = 0;   // 0 means a bad
            return response;
        }
        statement = connection.createStatement();
        String authUser = "select username AS 'UserName' , userPassword AS 'userPass', isAdmin AS 'perms' from users;";
        resultSet = statement.executeQuery(authUser);


        while (resultSet.next())
        {

            System.out.println("From DBS : "+resultSet.getString("UserName")+"" +
                    " "+resultSet.getString("userPass")+"" +
                    " " );

            if((resultSet.getString("UserName")).equals(USERNAME)
                && (resultSet.getString("userPass").equals(PASSWORD)))
            {
                tempUser  =  resultSet.getBoolean("perms");
                System.out.println(tempUser);

                if (tempUser)
                {
                    response = 1;   // 1 means the user is an admin
                }
                else if(!tempUser)
                {
                    response = 2; // 2 means the user is standard
                    return 2;
                }
            }

        }


        return response;
    }

    public void addUser(int userID, String firstName,String secondNamen,String email,String phone,String pass, int officeNo, Boolean permission ) throws SQLException
    {

        String username = (firstName+""+userID);
        int newUserID = userID+1;

        try {

            setUserPerm(username,pass,permission);

            Class.forName(JDBCDRIVER);
            connection = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            String sqlAdd = "INSERT INTO users VALUES (" + newUserID + ", '"+username+"', '" + email + "', '" + pass
                            + "' , '" + firstName + "', '" + secondNamen + "' , "+officeNo
                            +" , '"+phone+"', "+permission+");";
            statement.executeUpdate(sqlAdd);
            System.out.println(firstName+ " "+secondNamen+" has been successfully added");

        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setUserPerm(String username, String password, Boolean isAdmin) throws SQLException, ClassNotFoundException {


        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
        statement = connection.createStatement();
        String sqlPerms = "create user '"+username+"' @'localhost' identified by '"+password+"';";
        statement.executeUpdate(sqlPerms);

        if(isAdmin)
        {
            Class.forName(JDBCDRIVER);
            connection = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            String sqlPriv = "grant select,update,insert,delete, create user on *.* to '"+username+"' @'localhost' with grant option ;";
            statement.executeUpdate(sqlPriv);
            System.out.println(username +" has been granted Admin privileges");
        }
        else
        {
            Class.forName(JDBCDRIVER);
            connection = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            String sqlBasePrv = "grant insert,select,update,delete on UniBookingDBS.* to '"+username+"' @'localhost';";
            statement.executeUpdate(sqlBasePrv);
            System.out.println(username +" has been granted normal privileges");
        }


    }

    public int getUserCount() throws ClassNotFoundException, SQLException {

        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select count(*) from users");

        while (resultSet.next())
        {
            totUsers  =  resultSet.getInt(1);
        }

        return totUsers;
    }

    public ArrayList<User> getUsers() throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);

        statement = connection.createStatement();
        String sqlViewAllUsers = "select userID AS 'UserID'," +
                                " username AS 'UserName',"+
                                " userEmail AS 'UserEmail'," +
                                " userPassword AS 'UserPassword'," +
                                "firstName AS 'FName'," +
                                " surname AS 'sName'," +
                                " officeNo AS 'offNo'," +
                                " phoneNo AS 'phoneNum'," +
                                " isAdmin AS 'Admin' " +
                                "from users; ";
        resultSet = statement.executeQuery(sqlViewAllUsers);

        while(resultSet.next())
        {

            boolean tempBool =  resultSet.getBoolean("Admin");
            String isAdmin = String.valueOf(tempBool);

            userArray.add(new User(resultSet.getInt("UserID"),
                    resultSet.getString("UserName"),
                    resultSet.getString("UserEmail"),
                    resultSet.getString("UserPassword"),
                   resultSet.getString("FName"),
                    resultSet.getString("sName"),
                   resultSet.getInt("offNo"),
                    resultSet.getString("phoneNum"),
                    isAdmin));
        }

     return userArray;
    }

    public ArrayList<User> getOfficeNos() throws ClassNotFoundException, SQLException {
        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);

        statement = connection.createStatement();
        String sqlViewOfficeNos = "select officeNo AS 'offNo' from users; ";
        resultSet = statement.executeQuery(sqlViewOfficeNos);

        while(resultSet.next())
        {
            userOfficeNo.add(new User(resultSet.getInt("offNo")));
        }

        return userOfficeNo;
    }

    public ArrayList<Room> getRoomTyoe() throws ClassNotFoundException, SQLException {
        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);

        statement = connection.createStatement();
        String sqlViewRoomTypes = "select roomTypeName as 'Room Type' from roomtype;";
        resultSet = statement.executeQuery(sqlViewRoomTypes);

        while(resultSet.next())
        {
            roomTypeList.add(new Room(resultSet.getString("Room Type")));
        }

        return roomTypeList;
    }

    public void setRooms(String roomType, int roomNo, int roomCap ) throws ClassNotFoundException, SQLException {
        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
        statement = connection.createStatement();

        System.out.println(roomType +" "+roomNo+" "+roomCap );
        // Room Type is not applicable for Room , the ID needs to be found
        int IDvalue = getRoomTypeID(roomType);

        String sqlSetRoom = "INSERT INTO room VALUES ("+roomNo+", "+roomCap +", "+IDvalue+");";
        statement.executeUpdate(sqlSetRoom);

    }

    private int getRoomTypeID(String roomType) throws ClassNotFoundException, SQLException {

        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
        statement = connection.createStatement();

        resultSet = statement.executeQuery("select roomTypeID from roomtype where roomTypeName = '"+roomType+"';");

        while(resultSet.next())
        {
            roomTypeID = resultSet.getInt(1);
        }

        return roomTypeID;
    }

    public ArrayList<Room> getRoomsList() throws ClassNotFoundException, SQLException {
        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);

        statement = connection.createStatement();

        String sqlViewAllRooms = "SELECT room.roomNo AS 'roomNum',\n" +
                "room.roomCapacity 'roomCap',\n" +
                "roomtype.roomTypeName AS 'roomTypeN'\n" +
                "FROM room INNER JOIN roomtype \n" +
                "ON room.roomTypeID = roomtype.roomTypeID;";
        resultSet = statement.executeQuery(sqlViewAllRooms);

        while(resultSet.next())
        {
            roomArray.add(new Room(resultSet.getInt("roomNum"),
                                    resultSet.getInt("roomCap"),
                                    resultSet.getString("roomTypeN")));
        }

        return roomArray;
    }

    public ArrayList<Room> getRoomNoList() throws ClassNotFoundException, SQLException {
        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);

        statement = connection.createStatement();
        String sqlViewRoomNos = "select roomNo as 'RoomNum' from room;";
        resultSet = statement.executeQuery(sqlViewRoomNos);

        while(resultSet.next())
        {
            roomNoList.add(new Room(resultSet.getInt("RoomNum")));
        }

        return roomNoList;
    }

    public ArrayList<Booking> getBookingList() throws ClassNotFoundException, SQLException {
        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);

        statement = connection.createStatement();
        String sqlViewBookings = "select booking.bookingID as 'BookingID'," +
                " booking.roomNo as 'RoomNum'," +
                " users.userEmail AS 'userMail'," +
                " booking.dateFrom AS 'fromDate'," +
                " booking.dateTo AS 'toDate'," +
                " booking.timeFrom AS 'fromTime'," +
                " booking.timeTo AS 'toTime'," +
                " booking.duration AS 'bookDuration'," +
                " booking.isRecorring AS 'isRecor'," +
                " booking.attendees AS 'attendees'," +
                " booking.description AS 'Desc' from booking" +
                " INNER JOIN users ON booking.userID = users.userID;";
        resultSet = statement.executeQuery(sqlViewBookings);

        while(resultSet.next())
        {
            boolean tempBool = resultSet.getBoolean("isRecor");
            String isRecurring = String.valueOf(tempBool);

            bookingList.add(new Booking(resultSet.getInt("BookingID"),
                    resultSet.getInt("RoomNum"),
                    resultSet.getString("userMail"),
                    resultSet.getString("fromDate"),
                    resultSet.getString("toDate"),
                    resultSet.getString("fromTime"),
                    resultSet.getString("toTime"),
                    resultSet.getInt("bookDuration"),
                    isRecurring,
                    resultSet.getInt("attendees"),
                    resultSet.getString("Desc")));
        }

        return bookingList;
    }

    public ArrayList<Booking> getBookingList_ForUser(String username) throws ClassNotFoundException, SQLException
    {
        int userID = getUserID_DBS(username);

        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);

        statement = connection.createStatement();
        String sqlViewBookings = "select booking.bookingID as 'BookingID'," +
                " booking.roomNo as 'RoomNum'," +
                " users.userEmail AS 'userMail'," +
                " booking.dateFrom AS 'fromDate'," +
                " booking.dateTo AS 'toDate'," +
                " booking.timeFrom AS 'fromTime'," +
                " booking.timeTo AS 'toTime'," +
                " booking.duration AS 'bookDuration'," +
                " booking.isRecorring AS 'isRecor'," +
                " booking.attendees AS 'attendees'," +
                " booking.description AS 'Desc' from booking" +
                " INNER JOIN users ON booking.userID = users.userID " +
                " WHERE booking.userID = "+userID+";";
        resultSet = statement.executeQuery(sqlViewBookings);

        while(resultSet.next())
        {
            boolean tempBool = resultSet.getBoolean("isRecor");
            String isRecurring = String.valueOf(tempBool);

            bookingList.add(new Booking(resultSet.getInt("BookingID"),
                    resultSet.getInt("RoomNum"),
                    resultSet.getString("userMail"),
                    resultSet.getString("fromDate"),
                    resultSet.getString("toDate"),
                    resultSet.getString("fromTime"),
                    resultSet.getString("toTime"),
                    resultSet.getInt("bookDuration"),
                    isRecurring,
                    resultSet.getInt("attendees"),
                    resultSet.getString("Desc")));
        }

        return bookingList;
    }

    private int getUserID_DBS(String username) throws ClassNotFoundException, SQLException {
        Class.forName(JDBCDRIVER);
        connection = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);

        statement = connection.createStatement();
        String sqlGetID = "select userID as 'userID' from users where username = '"+username+"';";
        resultSet = statement.executeQuery(sqlGetID);

        while (resultSet.next())
        {
            userID = resultSet.getInt("userID");
        }

        return userID;
    }

    public void addBooking_Single()
    {

    }

}


