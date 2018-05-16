import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class Client extends Application
{
  //  private ObjectOutputStream clientOutputStream;
  //  private ObjectInputStream clientInputStreams;
  //  private Socket cSocket;
    private Stage bookingStage;
    private Boolean tempisAdmin;
    private  String tempUser;

    private dbsConnector connector;

    public Client()
    {
    //    this.cSocket = cSocket;
     //   this.clientOutputStream = new ObjectOutputStream(cSocket.getOutputStream());
    //    this.clientInputStreams = new ObjectInputStream(cSocket.getInputStream());
    }

    public static void main(String[] args)
    {
        //Client newClient = new Client(new Socket("127.0.0.1", 555));
        try
        {
            System.out.println("connected");
            launch(args);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        bookingStage = primaryStage;
        bookingStage.setTitle("University Booking System");
        bookingStage.setResizable(false);

        //Sign In Layout
        GridPane signInLayout = new GridPane();
        signInLayout.setPadding(new Insets(10, 15,10,15));
        signInLayout.setVgap(4);
        signInLayout.setHgap(5);

        Label loginLabel = new Label("Login");
        loginLabel.setFont(new Font("Arial", 20));
        GridPane.setConstraints(loginLabel, 1, 1);

        //Email Label and Text Field
        Label emailLabel = new Label("Username:");
        GridPane.setConstraints(emailLabel, 1,4);

        TextField emailInput = new TextField();
        emailInput.setPromptText("Enter Username");
        GridPane.setConstraints(emailInput, 1,5);

        //Password Label and Text Field
        Label passLabel = new Label("Password:");
        GridPane.setConstraints(passLabel, 1,6);

        PasswordField passInput = new PasswordField();
        passInput.setPromptText("Enter Password");
        GridPane.setConstraints(passInput, 1,7);

        Button signInButton = new Button("Sign In");
        signInButton.setMinWidth(69);

        HBox signButt = new HBox();
        signButt.getChildren().add(signInButton);
        signButt.setPadding(new Insets(10,0,0,0));
        signButt.setAlignment(Pos.BASELINE_RIGHT);
        GridPane.setConstraints(signButt,1,8);
        signInButton.setOnAction(e -> {
            if(emailInput.getText().isEmpty() )
            {
                AlertBox.displayAlert("Email Field Empty", "Please Enter Your Email");
            }
            else if(passInput.getText().isEmpty())
            {
                AlertBox.displayAlert("Password Field Empty", "Please Enter Your Password");
            }
            else
            {
                tempUser = emailInput.getText();
                String tempPass = passInput.getText();

                try {
                    authenticateRequest(tempUser,tempPass);
                } catch (SQLException | ClassNotFoundException e1) {
                    e1.printStackTrace();
                }

            }
        });

        signInLayout.getChildren().addAll(loginLabel, emailLabel,emailInput,passLabel,passInput,signButt);
        Scene loginScene = new Scene(signInLayout);
        bookingStage.setScene(loginScene);
        bookingStage.show();
    }

    private void authenticateRequest(String username, String password) throws SQLException, ClassNotFoundException {

        connector = new dbsConnector();
        int userResponse = connector.authenticateUser(username,password);

        if(userResponse == 0)
        {
            AlertBox.displayAlert("Bad Login", "You have entered the wrong credentials");
        }
        else if(userResponse == 1)
        {
            tempisAdmin = true;
            bookingStage.setScene(adminMenu());
        }
        else if(userResponse == 2)
        {
            tempisAdmin = false;
            bookingStage.setScene(regularMenu());
        }
        else
        {
            AlertBox.displayAlert("Error", "Something went wrong!");
        }
    }

    // Regular User Menu UI
    private Scene regularMenu()
    {
        // Regular Main Menu Layout
        GridPane regularLayout = new GridPane();
        regularLayout.setPadding(new Insets(10, 15,10,15));
        regularLayout.setVgap(14);
        regularLayout.setHgap(5);

        // Row 0
        Label regularMenuLabel = new Label("Welcome!         ");
        HBox topReg = topBar(regularMenuLabel);
        GridPane.setConstraints(topReg, 0, 0);

        // Row 1
        HBox row1 = new HBox();
        row1.setPadding(new Insets(5));
        row1.setSpacing(10);

        Button goToBooking = new Button("Add Booking");
        goToBooking.setMinWidth(430);
        goToAddBooking(goToBooking);

        row1.getChildren().add(goToBooking);
        GridPane.setConstraints(row1, 0, 1);

        //Row 2
        HBox row2 = new HBox();
        row2.setPadding(new Insets(5));
        row2.setSpacing(10);

        Button goToRecurringBooking = new Button("Add Recurring Booking");
        goToRecurringBooking.setMinWidth(210);
        goToRecurringBooking.setOnAction(e -> {
            try {
                bookingStage.setScene(addRecurringBookingUI());
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        Button goToCheckAvailableRooms = new Button("Check Available Rooms");
        goToCheckAvailableRooms.setMinWidth(210);
        goToCheckAvailableRooms.setOnAction(e -> bookingStage.setScene(checkAvailableBookings()));

        row2.getChildren().addAll(goToRecurringBooking,goToCheckAvailableRooms);
        GridPane.setConstraints(row2, 0, 2);

        // Row 3
        HBox row3 = new HBox();
        row3.setPadding(new Insets(5));
        row3.setSpacing(10);

        Button goToViewBookings = new Button("View Bookings");
        goToViewBookings.setMinWidth(210);
        goToViewBookings.setOnAction(e -> {
            try {
                bookingStage.setScene(viewBookingUI());
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        Button goToEditProfile = new Button("Edit Profile");
        goToEditProfile.setMinWidth(210);
        goToEditProfile.setOnAction(e -> bookingStage.setScene(editProfile()));

        row3.getChildren().addAll(goToEditProfile,goToViewBookings);
        GridPane.setConstraints(row3, 0, 3);

        // Add buttons to layout
        regularLayout.getChildren().addAll(topReg,row1,row2,row3);

        return new Scene(regularLayout);
    }

    //Super User Main Menu UI
    private Scene adminMenu()
    {
        // Regular Main Menu Layout
        GridPane adminLayout = new GridPane();
        adminLayout.setPadding(new Insets(10, 15,10,15));
        adminLayout.setVgap(14);
        adminLayout.setHgap(5);

        // Row 0
        Label regularMenuLabel = new Label("Welcome!      ");
        HBox topReg = topBar(regularMenuLabel);
        GridPane.setConstraints(topReg, 0, 0);

        // Row 1
        HBox row1 = new HBox();
        row1.setPadding(new Insets(5));
        row1.setSpacing(10);

        Button goToBooking = new Button("Add Booking");
        goToBooking.setMinWidth(210);
        goToAddBooking(goToBooking);

        Button goToRecurringBooking = new Button("Add Recurring Booking");
        goToRecurringBooking.setMinWidth(210);
        goToRecurringBooking.setOnAction(e -> {
            try {
                bookingStage.setScene(addRecurringBookingUI());
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        row1.getChildren().addAll(goToBooking,goToRecurringBooking);
        GridPane.setConstraints(row1, 0, 1);

        //Row 2
        HBox row2 = new HBox();
        row2.setPadding(new Insets(5));
        row2.setSpacing(10);

        Button goToViewBookings = new Button("View Bookings");
        goToViewBookings.setMinWidth(210);
        goToViewBookings.setOnAction(e -> {
            try {
                bookingStage.setScene(viewBookingUI());
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        Button goToViewUsers = new Button("View Users");
        goToViewUsers.setMinWidth(210);
        goToViewUsers.setOnAction(e -> {
            try {
                bookingStage.setScene(viewUsersUI());
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        row2.getChildren().addAll(goToViewBookings,goToViewUsers);
        GridPane.setConstraints(row2, 0, 2);

        // Row 3
        HBox row3 = new HBox();
        row3.setPadding(new Insets(5));
        row3.setSpacing(10);

        Button goToViewRooms = new Button("View Rooms");
        goToViewRooms.setMinWidth(210);
        toRoomList(goToViewRooms);

        Button goToCheckAvailableRooms = new Button("Check Available Rooms");
        goToCheckAvailableRooms.setMinWidth(210);
        goToCheckAvailableRooms.setOnAction(e -> bookingStage.setScene(checkAvailableBookings()));

        row3.getChildren().addAll(goToViewRooms,goToCheckAvailableRooms);
        GridPane.setConstraints(row3, 0, 3);

        // Row 4
        HBox row4 = new HBox();
        row4.setPadding(new Insets(5));
        row4.setSpacing(10);

        Button goToVAddUsers = new Button("Add Users");
        goToVAddUsers.setMinWidth(210);
        goToVAddUsers.setOnAction(e -> {
            try {
                bookingStage.setScene(addUserUI());
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        Button goToAddRooms = new Button("Add Rooms");
        goToAddRooms.setMinWidth(210);
        goToAddRooms.setOnAction(e -> {
            try {
                bookingStage.setScene(addRoomsUI());
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        row4.getChildren().addAll(goToVAddUsers,goToAddRooms);
        GridPane.setConstraints(row4, 0, 4);


        // Add buttons to layout
        adminLayout.getChildren().addAll(topReg,row1,row2,row3,row4);

        return new Scene(adminLayout);
    }

    private void toRoomList(Button goToViewRooms) {
        goToViewRooms.setOnAction(e -> {
            try {
                bookingStage.setScene(roomsListUI());
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void goToAddBooking(Button goToBooking) {
        goToBooking.setOnAction(e -> {
            try {
                //Get
                bookingStage.setScene(addBookingUI());
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });
    }


    // Add single booking layout UI
    private Scene addBookingUI() throws SQLException, ClassNotFoundException {
        //Add Booking Layout
        GridPane addBookingLayout = new GridPane();
        addBookingLayout.setPadding(new Insets(10, 15,10,15));
        addBookingLayout.setVgap(5);
        addBookingLayout.setHgap(20);

        // Row 0
        Label addBookingLabel = new Label("Add Booking     ");
        HBox topBook = topBar(addBookingLabel);
        topBook.setPadding(new Insets(0,0,10,0));
        GridPane.setConstraints(topBook, 0, 0);

        // Row 1
        HBox row01 = new HBox();
        row01.setPadding(new Insets(5));
        row01.setSpacing(10);
        Label roomNoLabel = new Label("Enter Room Number");

        Label attendeesLabel = new Label("Enter Estimated Attendees");
        attendeesLabel.setPadding(new Insets(0,0,0,100));

        row01.getChildren().addAll(roomNoLabel, attendeesLabel);
        GridPane.setConstraints(row01, 0,1);

        //Row 2
        HBox row02 = new HBox();
        row02.setPadding(new Insets(5));
        row02.setSpacing(10);

        ComboBox<Integer> roomNoInput = new ComboBox<>(roomNoList());
        roomNoInput.setMinWidth(210);

        ComboBox<Integer> attendeesInput = new ComboBox<>(roomCapInList());
        attendeesInput.setMinWidth(210);

        row02.getChildren().addAll(roomNoInput,attendeesInput);
        GridPane.setConstraints(row02, 0,2);

        //Row 3
        Label eventDescLabelC = new Label("Event Description :");
        GridPane.setConstraints(eventDescLabelC, 0,3);

        //Row 4
        HBox row04 = new HBox();
        row04.setPadding(new Insets(5));
        row04.setSpacing(10);

        TextField descInput = new TextField();
        descInput.setMinWidth(430);

        row04.getChildren().add(descInput);
        GridPane.setConstraints(row04, 0,4);

        // Row 5
        HBox row05 = new HBox();
        row05.setPadding(new Insets(5));
        row05.setSpacing(10);

        Label timeFromLabel = new Label("Time From :");

        Label timeToLabel = new Label("Time To :");
        timeToLabel.setPadding(new Insets(0,0,0,145));

        row05.getChildren().addAll(timeFromLabel,timeToLabel);
        GridPane.setConstraints(row05, 0,5);

        //Row 6
        HBox row06 = new HBox();
        row06.setPadding(new Insets(5));
        row06.setSpacing(10);

        ComboBox<String> timeFromInput = new ComboBox<>(timeList());
        timeFromInput.setMinWidth(210);

        ComboBox<String> timeToInput = new ComboBox<>(timeList());
        timeToInput.setMinWidth(210);

        row06.getChildren().addAll(timeFromInput,timeToInput);
        GridPane.setConstraints(row06, 0,6);

        //Row 7
        Label dateLabelC = new Label("Date :");
        GridPane.setConstraints(dateLabelC, 0,7);

        //Row 8
        HBox row08 = new HBox();
        row08.setPadding(new Insets(5));
        row08.setSpacing(10);

        DatePicker dateInput = new DatePicker();
        dateInput.setMinWidth(430);

        row08.getChildren().add(dateInput);
        GridPane.setConstraints(row08, 0,8);

        //Row 9
        HBox row09 = new HBox();
        row09.setPadding(new Insets(5));

        Button bookRoomButton = new Button("Book Room");
        bookRoomButton.setOnAction(e -> {
            try {
                addBookingToDBS(roomNoInput.getValue().toString(),
                        attendeesInput.getValue().toString(),
                        timeFromInput.getValue(),
                        timeToInput.getValue(),
                        dateInput);
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        row09.getChildren().add(bookRoomButton);
        row09.setPadding(new Insets(10,0,0,360));
        GridPane.setConstraints(row09, 0,9);

        addBookingLayout.getChildren().addAll(topBook,row01,row02,eventDescLabelC,
                row04,row05,dateLabelC,row06,row08,row09);

        return new Scene(addBookingLayout);
    }

    // Add recurring Booking layout UI
    private Scene addRecurringBookingUI() throws SQLException, ClassNotFoundException {
        //Add Recurring Booking
        GridPane addRecurringBookingLayout = new GridPane();
        addRecurringBookingLayout.setPadding(new Insets(10, 15,10,15));
        addRecurringBookingLayout.setVgap(5);
        addRecurringBookingLayout.setHgap(20);

        // Row 0
        Label addBookingLabel = new Label("Add Booking     ");
        HBox topRecBook = topBar(addBookingLabel);
        topRecBook.setPadding(new Insets(0,0,10,0));
        GridPane.setConstraints(topRecBook, 0, 0);

        // Row 1
        HBox row1 = new HBox();
        row1.setPadding(new Insets(5));
        row1.setSpacing(10);
        Label roomNoLabel = new Label("Enter Room Number");

        Label attendeesLabel = new Label("Enter Estimated Attendees");
        attendeesLabel.setPadding(new Insets(0,0,0,100));

        row1.getChildren().addAll(roomNoLabel, attendeesLabel);
        GridPane.setConstraints(row1, 0,1);

        //Row 2
        HBox row2 = new HBox();
        row2.setPadding(new Insets(5));
        row2.setSpacing(10);

        ComboBox<Integer> roomNoInput = new ComboBox<>(roomNoList());
        roomNoInput.setMinWidth(210);

        ComboBox<Integer> attendeesInput = new ComboBox<>(roomCapInList());
        attendeesInput.setMinWidth(210);

        row2.getChildren().addAll(roomNoInput,attendeesInput);
        GridPane.setConstraints(row2, 0,2);

        //Row 3
        HBox row3 = new HBox();
        row3.setPadding(new Insets(5));
        row3.setSpacing(10);

        Label eventDescLabel = new Label("Event Description :");

        Label durationLabel = new Label("Duration :");
        durationLabel.setPadding(new Insets(0,0,0,110));


        row3.getChildren().addAll(eventDescLabel,durationLabel);
        GridPane.setConstraints(row3, 0,3);

        //Row 4
        HBox row4 = new HBox();
        row4.setPadding(new Insets(5));
        row4.setSpacing(10);

        TextField descInput = new TextField();
        descInput.setMinWidth(210);

        ComboBox durationInput = new ComboBox(durationList());
        durationInput.setMinWidth(210);

        row4.getChildren().addAll(descInput,durationInput);
        GridPane.setConstraints(row4, 0,4);

        // Row 5
        HBox row5 = new HBox();
        row5.setPadding(new Insets(5));
        row5.setSpacing(10);

        Label timeFromLabel = new Label("Time From :");

        Label timeToLabel = new Label("Time To :");
        timeToLabel.setPadding(new Insets(0,0,0,145));

        row5.getChildren().addAll(timeFromLabel,timeToLabel);
        GridPane.setConstraints(row5, 0,5);

        //Row 6
        HBox row6 = new HBox();
        row6.setPadding(new Insets(5));
        row6.setSpacing(10);

        ComboBox<String> timeFromInput = new ComboBox<>(timeList());
        timeFromInput.setMinWidth(210);

        ComboBox<String> timeToInput = new ComboBox<>(timeList());
        timeToInput.setMinWidth(210);

        row6.getChildren().addAll(timeFromInput,timeToInput);
        GridPane.setConstraints(row6, 0,6);

        //Row 7
        HBox row7 = new HBox();
        row7.setPadding(new Insets(5));
        row7.setSpacing(10);

        Label dateToLabel = new Label("Date To:");

        Label dateFromLabel = new Label("Date From:");
        dateFromLabel.setPadding(new Insets(0,0,0,160));

        row7.getChildren().addAll(dateToLabel,dateFromLabel);
        GridPane.setConstraints(row7, 0,7);

        //Row 8
        HBox row8 = new HBox();
        row8.setPadding(new Insets(5));
        row8.setSpacing(10);

        DatePicker dateToIn = new DatePicker();
        dateToIn.setMinWidth(210);

        DatePicker dateFromIn = new DatePicker();
        dateFromIn.setMinWidth(210);

        row8.getChildren().addAll(dateToIn, dateFromIn);
        GridPane.setConstraints(row8, 0,8);

        //Row 9
        HBox row9 = new HBox();
        row9.setPadding(new Insets(5));

        Button bookRoomButton = new Button("Book Room");
        bookRoomButton.setOnAction(e ->
                addRecBookingToDBS(roomNoInput.getValue().toString(),
                attendeesInput.getValue().toString(),
                timeFromInput.getValue(),
                timeToInput.getValue(),
                dateToIn, dateFromIn));

        row9.getChildren().add(bookRoomButton);
        row9.setPadding(new Insets(10,0,0,360));
        GridPane.setConstraints(row9, 0,9);

        addRecurringBookingLayout.getChildren().addAll(topRecBook,row1,
                row2,row3,row4,row5,row6, row7, row8,row9);

        return new Scene(addRecurringBookingLayout);
    }

    // Available Rooms UI
    private Scene checkAvailableBookings()
    {
        GridPane checkAvailableLayout = new GridPane();
        checkAvailableLayout.setPadding(new Insets(10, 15,10,10));
        checkAvailableLayout.setVgap(5);
        checkAvailableLayout.setHgap(0);

        //Top Bar
        Label checkLabel = new Label("Check Available");
        HBox topCheck = topBar(checkLabel);
        GridPane.setConstraints(topCheck, 0,0);

        //Row 1
        HBox row1 = new HBox();
        row1.setPadding(new Insets(5));
        row1.setSpacing(10);

        Label dateToLabel = new Label("Date To:");

        Label dateFromLabel = new Label("Date From:");
        dateFromLabel.setPadding(new Insets(0,0,0,160));

        row1.getChildren().addAll(dateToLabel,dateFromLabel);
        GridPane.setConstraints(row1, 0,1);

        //Row 2
        HBox row2 = new HBox();
        row2.setPadding(new Insets(5));
        row2.setSpacing(10);

        DatePicker dateToIn = new DatePicker();
        dateToIn.setMinWidth(210);

        DatePicker dateFromIn = new DatePicker();
        dateFromIn.setMinWidth(210);

        row2.getChildren().addAll(dateToIn, dateFromIn);
        GridPane.setConstraints(row2, 0,2);

        // Row 3
        HBox row3 = new HBox();
        row3.setPadding(new Insets(5));
        row3.setSpacing(10);

        Label timeFromLabel = new Label("Time From :");

        Label timeToLabel = new Label("Time To :");
        timeToLabel.setPadding(new Insets(0,0,0,145));

        row3.getChildren().addAll(timeFromLabel,timeToLabel);
        GridPane.setConstraints(row3, 0,3);

        //Row 4
        HBox row4 = new HBox();
        row4.setPadding(new Insets(5));
        row4.setSpacing(10);

        ComboBox<String> timeFromInput = new ComboBox<>(timeList());
        timeFromInput.setMinWidth(210);

        ComboBox<String> timeToInput = new ComboBox<>(timeList());
        timeToInput.setMinWidth(210);

        row4.getChildren().addAll(timeFromInput,timeToInput);
        GridPane.setConstraints(row4, 0,4);

        //Row 5
        Label attendeesLabel = new Label("Estimated Attendees :");
        GridPane.setConstraints(attendeesLabel, 0,5);

        //Row 6
        HBox row6 = new HBox();
        row6.setPadding(new Insets(5));
        row6.setSpacing(10);

        ComboBox<Integer> attendeesInput = new ComboBox<>(roomCapInList());
        attendeesInput.setMinWidth(430);

        row6.getChildren().add(attendeesInput);
        GridPane.setConstraints(row6, 0,6);

        //Row 7
        HBox row7 = new HBox();
        row7.setPadding(new Insets(5));

        Button checkRoomsButt = new Button("Check Rooms");
        toRoomList(checkRoomsButt);

        row7.getChildren().add(checkRoomsButt);
        row7.setPadding(new Insets(10,0,0,360));
        GridPane.setConstraints(row7, 0,7);

        checkAvailableLayout.getChildren().addAll(topCheck,row1,row2,row3,row4,attendeesLabel,row6,row7);

        return new Scene(checkAvailableLayout);

    }

    // View Booking List Table UI
    private Scene viewBookingUI() throws SQLException, ClassNotFoundException {
        GridPane viewBookingsLayout = new GridPane();
        viewBookingsLayout.setPadding(new Insets(10, 15,10,10));
        viewBookingsLayout.setVgap(5);
        viewBookingsLayout.setHgap(0);

        //Top Bar
        Label viewBookingLabel = new Label("View Bookings   ");
        HBox topView = topBar(viewBookingLabel);
        GridPane.setConstraints(topView, 1,0);

        //Columns
        TableColumn<Booking, Integer> bookingIDColumn = new TableColumn<>("Booking ID :");
        bookingIDColumn.setMaxWidth(200);
        bookingIDColumn.setCellValueFactory(new PropertyValueFactory<>("bookingID"));

        TableColumn<Booking, String> emailColumn = new TableColumn<>("Room No :");
        emailColumn.setMaxWidth(200);
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("roomNo"));

        TableColumn<Booking, Integer> roomNoColumn = new TableColumn<>("Email :");
        roomNoColumn.setMaxWidth(200);
        roomNoColumn.setCellValueFactory(new PropertyValueFactory<>("usrEmail"));

        TableColumn<Booking, Integer> attendeesColumn = new TableColumn<>("Attendees :");
        attendeesColumn.setMaxWidth(200);
        attendeesColumn.setCellValueFactory(new PropertyValueFactory<>("attendees"));

        TableColumn<Booking, Integer> durationColumn = new TableColumn<>("Duration :");
        durationColumn.setMaxWidth(200);
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        TableColumn<Booking, Boolean> isRecurringColumn = new TableColumn<>("Recurring? :");
        isRecurringColumn.setMaxWidth(200);
        isRecurringColumn.setCellValueFactory(new PropertyValueFactory<>("isRecurring"));

        TableColumn<Booking, String> dateFromColumn = new TableColumn<>("Date From :");
        dateFromColumn.setMaxWidth(200);
        dateFromColumn.setCellValueFactory(new PropertyValueFactory<>("dateFrom"));

        TableColumn<Booking, String> dateToColumn = new TableColumn<>("Date To :");
        dateToColumn.setMaxWidth(200);
        dateToColumn.setCellValueFactory(new PropertyValueFactory<>("dateTo"));

        TableColumn<Booking, Double> timeFromColumn = new TableColumn<>("Time From :");
        timeFromColumn.setMaxWidth(200);
        timeFromColumn.setCellValueFactory(new PropertyValueFactory<>("timeFrom"));

        TableColumn<Booking, Double> timeToColumn = new TableColumn<>("Time To :");
        timeToColumn.setMaxWidth(200);
        timeToColumn.setCellValueFactory(new PropertyValueFactory<>("timeTo"));

        TableColumn<Booking, String> eventNameColumn = new TableColumn<>("Event Desc :");
        eventNameColumn.setMaxWidth(200);
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));


        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(5));
        bottomBar.setSpacing(10);

        Button edit = new Button("Edit");
        edit.setMinWidth(69);
        edit.setOnAction(e -> editBookingC());

        Button deleteBook = new Button("Delete");
        deleteBook.setMinWidth(69);
        deleteBook.setOnAction(e -> deleteBookingC());

        bottomBar.getChildren().addAll(edit,deleteBook);
        bottomBar.setAlignment(Pos.CENTER);
        GridPane.setConstraints(bottomBar, 1,2);

        TableView<Booking> bookingsTable = new TableView<>();
        bookingsTable.setMaxWidth(450);
        if(tempisAdmin) {
            bookingsTable.setItems(bookingList());
        }
        else if (!tempisAdmin)
        {
            bookingsTable.setItems(regular_bookingList());
        }


        bookingsTable.getColumns().addAll(bookingIDColumn,roomNoColumn,emailColumn,dateFromColumn,
                dateToColumn,timeFromColumn,timeToColumn,durationColumn,
                isRecurringColumn,attendeesColumn,eventNameColumn);
        GridPane.setConstraints(bookingsTable, 1,1);

        viewBookingsLayout.getChildren().addAll(topView, bookingsTable, bottomBar);
        return new Scene(viewBookingsLayout);
    }

    // Available Rooms Table View UI
    private Scene roomsListUI() throws SQLException, ClassNotFoundException {
        GridPane availableRoomLayout = new GridPane();
        availableRoomLayout.setPadding(new Insets(10, 15,10,10));
        availableRoomLayout.setVgap(5);
        availableRoomLayout.setHgap(0);

        //Top Bar
        Label availableLabel = new Label("View Rooms   ");
        HBox topView = topBar(availableLabel);
        GridPane.setConstraints(topView, 1,0);

        //Columns
        TableColumn<Room, Integer> roomNoColumn = new TableColumn<>("Room No :");
        roomNoColumn.setMinWidth(145);
        roomNoColumn.setCellValueFactory(new PropertyValueFactory<>("roomNo"));

        TableColumn<Room, Integer> roomCapColumn = new TableColumn<>("Room Capacity :");
        roomCapColumn.setMinWidth(145);
        roomCapColumn.setCellValueFactory(new PropertyValueFactory<>("roomCapacity"));

        TableColumn<Room, String> roomTypeColumn = new TableColumn<>("Room Type :");
        roomTypeColumn.setMinWidth(145);
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        TableView<Room> roomTable = new TableView<>();
        roomTable.setMaxWidth(450);
        roomTable.setItems(roomsList());
        roomTable.getColumns().addAll(roomNoColumn, roomCapColumn, roomTypeColumn);
        GridPane.setConstraints(roomTable, 1,1);

        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(5));
        bottomBar.setSpacing(10);

        Button addBooking = new Button("Book");
        addBooking.setMinWidth(69);
        addBooking.setOnAction(e -> backButton());

        bottomBar.getChildren().addAll(addBooking);
        bottomBar.setAlignment(Pos.CENTER);
        GridPane.setConstraints(bottomBar, 1,2);

        availableRoomLayout.getChildren().addAll(topView, roomTable, bottomBar);
        return new Scene(availableRoomLayout);
    }

    // Edit User Profile Layout UI
    private Scene editProfile()
    {
        GridPane editProfileLayout = new GridPane();
        editProfileLayout.setPadding(new Insets(10, 15,10,10));
        editProfileLayout.setVgap(5);
        editProfileLayout.setHgap(0);

        // Row 0
        Label editProfLabel = new Label("Edit Profile        ");
        HBox topEditProf = topBar(editProfLabel);
        topEditProf.setPadding(new Insets(0,0,10,0));
        GridPane.setConstraints(topEditProf, 0, 0);

        // Row 1
        HBox row1 = new HBox();
        row1.setPadding(new Insets(5));
        row1.setSpacing(10);
        Label firstName = new Label("Enter First Name:");

        Label secondName = new Label("Enter Second Name:");
        secondName.setPadding(new Insets(0,0,0,120));

        row1.getChildren().addAll(firstName, secondName);
        GridPane.setConstraints(row1, 0,1);

        //Row 2
        HBox row2 = new HBox();
        row2.setPadding(new Insets(5));
        row2.setSpacing(5);

        TextField firstNameIn = new TextField();
        firstNameIn.setMinWidth(210);

        TextField secondNameIn = new TextField();
        secondNameIn.setMinWidth(210);

        row2.getChildren().addAll(firstNameIn,secondNameIn);
        GridPane.setConstraints(row2, 0,2);

        // Row 3
        HBox row3 = new HBox();
        row3.setPadding(new Insets(5));
        row3.setSpacing(5);

        Label emailLabel = new Label("E-mail :");

        Label numberLabel = new Label("Phone Number :");
        numberLabel.setPadding(new Insets(0,0,0,178));

        row3.getChildren().addAll(emailLabel,numberLabel);
        GridPane.setConstraints(row3, 0,3);

        //Row 4
        HBox row4 = new HBox();
        row4.setPadding(new Insets(5));
        row4.setSpacing(10);

        TextField emailIn = new TextField();
        emailIn.setMinWidth(210);

        TextField phoneIn = new TextField();
        phoneIn.setMinWidth(210);

        row4.getChildren().addAll(emailIn,phoneIn);
        GridPane.setConstraints(row4, 0,4);

        //Row 5
        HBox row5 = new HBox();
        row5.setPadding(new Insets(5));
        row5.setSpacing(10);

        Label passwordLabel = new Label("Password:");

        Label officeNoLabel = new Label("Office No:");
        officeNoLabel.setPadding(new Insets(0,0,0,160));

        row5.getChildren().addAll(passwordLabel,officeNoLabel);
        GridPane.setConstraints(row5, 0,5);

        //Row 6
        HBox row6 = new HBox();
        row6.setPadding(new Insets(5));
        row6.setSpacing(10);

        PasswordField passIn = new PasswordField();
        passIn.setMinWidth(210);

        TextField officeNoIn = new TextField();
        officeNoIn.setMinWidth(210);

        row6.getChildren().addAll(passIn, officeNoIn);
        GridPane.setConstraints(row6, 0,6);

        //Row 7
        HBox row7 = new HBox();
        row7.setPadding(new Insets(5));

        Button editProfButt = new Button("Edit Profile");
        editProfButt.setOnAction(e ->  bookingStage.setScene(regularMenu()));

        row7.getChildren().add(editProfButt);
        row7.setPadding(new Insets(0,0,0,360));
        GridPane.setConstraints(row7, 0,7);

        editProfileLayout.getChildren().addAll(topEditProf,row1,row2,row3,row4,row5,row6,row7);

        return new Scene(editProfileLayout);
    }

    //Add User UI Layout
    private Scene addUserUI() throws SQLException, ClassNotFoundException {
        GridPane addUsersLayout = new GridPane();
        addUsersLayout.setPadding(new Insets(10, 15,10,10));
        addUsersLayout.setVgap(5);
        addUsersLayout.setHgap(0);

        // Row 0
        Label addUserLabel = new Label("Add User        ");
        HBox topAddUser = topBar(addUserLabel);
        topAddUser.setPadding(new Insets(0,0,10,0));
        GridPane.setConstraints(topAddUser, 0, 0);

        // Row 1
        HBox row1 = new HBox();
        row1.setPadding(new Insets(5));
        row1.setSpacing(10);
        Label firstName = new Label("Enter First Name:");

        Label secondName = new Label("Enter Second Name:");
        secondName.setPadding(new Insets(0,0,0,120));

        row1.getChildren().addAll(firstName, secondName);
        GridPane.setConstraints(row1, 0,1);

        //Row 2
        HBox row2 = new HBox();
        row2.setPadding(new Insets(5));
        row2.setSpacing(5);

        TextField firstNameIn = new TextField();
        firstNameIn.setMinWidth(210);

        TextField secondNameIn = new TextField();
        secondNameIn.setMinWidth(210);

        row2.getChildren().addAll(firstNameIn,secondNameIn);
        GridPane.setConstraints(row2, 0,2);

        // Row 3
        HBox row3 = new HBox();
        row3.setPadding(new Insets(5));
        row3.setSpacing(5);

        Label emailLabel = new Label("E-mail :");

        Label numberLabel = new Label("Phone Number :");
        numberLabel.setPadding(new Insets(0,0,0,178));

        row3.getChildren().addAll(emailLabel,numberLabel);
        GridPane.setConstraints(row3, 0,3);

        //Row 4
        HBox row4 = new HBox();
        row4.setPadding(new Insets(5));
        row4.setSpacing(10);

        TextField emailIn = new TextField();
        emailIn.setMinWidth(210);

        TextField phoneIn = new TextField();
        phoneIn.setMinWidth(210);

        row4.getChildren().addAll(emailIn,phoneIn);
        GridPane.setConstraints(row4, 0,4);

        //Row 5
        HBox row5 = new HBox();
        row5.setPadding(new Insets(5));
        row5.setSpacing(10);

        Label passwordLabel = new Label("Password:");

        Label officeNoLabel = new Label("Office No:");
        officeNoLabel.setPadding(new Insets(0,0,0,160));

        row5.getChildren().addAll(passwordLabel,officeNoLabel);
        GridPane.setConstraints(row5, 0,5);

        //Row 6
        HBox row6 = new HBox();
        row6.setPadding(new Insets(5));
        row6.setSpacing(10);

        PasswordField passIn = new PasswordField();
        passIn.setMinWidth(210);

        ComboBox<Integer> officeNoIn = new ComboBox<>(officeNoList());
        officeNoIn.setPromptText("Office No :");
        officeNoIn.setMinWidth(210);

        row6.getChildren().addAll(passIn, officeNoIn);
        GridPane.setConstraints(row6, 0,6);

        //Row 7
        HBox row7 = new HBox();
        row7.setPadding(new Insets(5));

        CheckBox isAdminBox = new CheckBox("Admin?");

        HBox buttonBox = new HBox();
        Button addUserButt = new Button("Add User");
        addUserButt.setOnAction(e -> {
            try {
                addUserToDBS(firstNameIn,secondNameIn,emailIn,phoneIn,
                        passIn,officeNoIn.getValue().toString(),isAdminBox);
            } catch (SQLException | ClassNotFoundException | IOException | InterruptedException e1) {
                e1.printStackTrace();
            }
        });
        buttonBox.getChildren().add(addUserButt);

        buttonBox.setPadding(new Insets(0,0,0,300));
        row7.getChildren().addAll(isAdminBox,buttonBox);

        GridPane.setConstraints(row7, 0,7);

        addUsersLayout.getChildren().addAll(topAddUser,row1,row2,row3,row4,row5,row6,row7);
        return new Scene(addUsersLayout);
    }

    private Scene addRoomsUI() throws SQLException, ClassNotFoundException {
        GridPane addRoomLayout = new GridPane();
        addRoomLayout.setPadding(new Insets(10, 15,10,10));
        addRoomLayout.setVgap(5);
        addRoomLayout.setHgap(0);

        // Row 0
        Label addRoomLabel = new Label("Add Room       ");
        HBox topAddRoom = topBar(addRoomLabel);
        topAddRoom.setPadding(new Insets(0,0,10,0));
        GridPane.setConstraints(topAddRoom, 0, 0);

        //Row 1
        Label eventDescLabel = new Label("Room Type :");
        GridPane.setConstraints(eventDescLabel, 0,1);

        //Row 2
        HBox row2 = new HBox();
        row2.setPadding(new Insets(5));
        row2.setSpacing(10);

        ComboBox<String> typeIn = new ComboBox<>(roomTypeList());
        typeIn.setPromptText("Select Room Type");
        typeIn.setMinWidth(430);

        row2.getChildren().add(typeIn);
        GridPane.setConstraints(row2, 0,2);

        // Row 3
        HBox row3 = new HBox();
        row3.setPadding(new Insets(5));
        row3.setSpacing(5);

        Label roomNoLabel = new Label("Room No :");

        Label capacityLabel = new Label("Room Capacity :");
        capacityLabel.setPadding(new Insets(0,0,0,178));

        row3.getChildren().addAll(roomNoLabel,capacityLabel);
        GridPane.setConstraints(row3, 0,3);

        //Row 4
        HBox row4 = new HBox();
        row4.setPadding(new Insets(5));
        row4.setSpacing(10);

        TextField roomNoIn = new TextField();
        roomNoIn.setPromptText("Insert Integer");
        roomNoIn.setMinWidth(210);

        ComboBox<Integer> roomCapacityIn = new ComboBox<>(roomCapInList());
        roomCapacityIn.setMinWidth(210);

        row4.getChildren().addAll(roomNoIn,roomCapacityIn);
        GridPane.setConstraints(row4, 0,4);

        //Row 5
        HBox row5 = new HBox();
        row5.setPadding(new Insets(5));

        Button addRoomButt = new Button("Add Room");
        addRoomButt.setOnAction(e -> {
            try {
                addRoomsToDBS(typeIn.getValue(), roomNoIn, roomCapacityIn.getValue().toString());
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        row5.getChildren().add(addRoomButt);
        row5.setPadding(new Insets(0,0,0,360));
        GridPane.setConstraints(row5, 0,7);

        addRoomLayout.getChildren().addAll(topAddRoom,eventDescLabel,row2,row3,row4,row5);
        return new Scene(addRoomLayout);

    }

    private Scene viewUsersUI() throws SQLException, ClassNotFoundException {
        GridPane viewUsersLayout = new GridPane();
        viewUsersLayout.setPadding(new Insets(10, 15,10,10));
        viewUsersLayout.setVgap(5);
        viewUsersLayout.setHgap(0);

        //Top Bar
        Label viewUsersLabel = new Label("View Users    ");
        HBox topViewUsers = topBar(viewUsersLabel);
        GridPane.setConstraints(topViewUsers, 1,0);

        //Columns
        TableColumn<User, String> userIDColumn = new TableColumn<>("User ID :");
        userIDColumn.setMaxWidth(200);
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email :");
        emailColumn.setMaxWidth(200);
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> nameColumn = new TableColumn<>("Name :");
        nameColumn.setMaxWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> surnameColumn = new TableColumn<>("Surname :");
        surnameColumn.setMaxWidth(200);
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<User, Integer> officeNoColumn = new TableColumn<>("Office No :");
        officeNoColumn.setMaxWidth(200);
        officeNoColumn.setCellValueFactory(new PropertyValueFactory<>("officeNo"));

        TableColumn<User, String> phoneNoColumn = new TableColumn<>("Phone No :");
        phoneNoColumn.setMaxWidth(200);
        phoneNoColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));

        TableColumn<User, Boolean> isAdminColumn = new TableColumn<>("Admin? :");
        isAdminColumn.setMaxWidth(200);
        isAdminColumn.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

        TableView<User> usersTable = new TableView<>();
        usersTable.setMaxWidth(450);
        // Add list later userList
        usersTable.setItems(userList());
        usersTable.getColumns().addAll(userIDColumn,emailColumn, nameColumn,surnameColumn,
                officeNoColumn,phoneNoColumn,isAdminColumn);
        GridPane.setConstraints(usersTable, 1,1);

        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(5));
        bottomBar.setSpacing(10);

        Button resetPassButton = new Button("Reset Password");
        resetPassButton.setMinWidth(69);
        resetPassButton.setOnAction(e -> backButton());

        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.setMinWidth(69);
        deleteUserButton.setOnAction(e -> backButton());


        bottomBar.getChildren().addAll(resetPassButton, deleteUserButton);
        bottomBar.setAlignment(Pos.CENTER);
        GridPane.setConstraints(bottomBar, 1,2);

        viewUsersLayout.getChildren().addAll(topViewUsers,usersTable,bottomBar);
        return new Scene(viewUsersLayout);


    }
    //
    // Replace the adds in the Lists with database data.
    //

    private ObservableList<User> userList() throws SQLException, ClassNotFoundException {

        connector = new dbsConnector();

        ArrayList<User> list = connector.getUsers();
        return FXCollections.observableArrayList(list);

    }

    private ObservableList<Integer> officeNoList() throws SQLException, ClassNotFoundException {

        connector = new dbsConnector();
        ObservableList<Integer> OffNoList = FXCollections.observableArrayList();
        ArrayList<User> list = connector.getOfficeNos();
        for (User user: list){
            OffNoList.add(user.getOfficeNo());
        }


        return OffNoList;
    }

    private ObservableList<String> roomTypeList() throws SQLException, ClassNotFoundException {
        connector = new dbsConnector();
        ObservableList<String> roomTypeL = FXCollections.observableArrayList();
        ArrayList<Room> list = connector.getRoomTyoe();

        for(Room room : list)
        {
            roomTypeL.add(room.getRoomType());
        }

        return roomTypeL;
    }

    private ObservableList<Room> roomsList() throws SQLException, ClassNotFoundException {

        connector = new dbsConnector();

        ArrayList<Room> list = connector.getRoomsList();
        return FXCollections.observableArrayList(list);
    }

    private ObservableList<Integer> roomCapInList()
    {
        ObservableList<Integer> capList = FXCollections.observableArrayList();

        for (int i = 20 ; i <= 100 ; i++)
        {
            capList.add(i);
        }

        return capList;
    }

    private ObservableList<Integer> durationList()
    {
        ObservableList<Integer> durationList = FXCollections.observableArrayList();
        for (int i = 1 ; i <= 24 ; i++)
        {
            durationList.add(i);
        }

        return durationList;
    }

    private ObservableList<Integer> roomNoList() throws SQLException, ClassNotFoundException {
        connector = new dbsConnector();

        ObservableList<Integer> roomNoList = FXCollections.observableArrayList();
        ArrayList<Room> list = connector.getRoomNoList();

        for(Room room : list)
        {
            roomNoList.add(room.getRoomNo());
        }

        return roomNoList;
    }

    private ObservableList<Booking> bookingList() throws SQLException, ClassNotFoundException {

        connector = new dbsConnector();

        ArrayList<Booking> list = connector.getBookingList();
        return FXCollections.observableArrayList(list);
    }

    private ObservableList<Booking> regular_bookingList() throws SQLException, ClassNotFoundException {
        connector = new dbsConnector();

        ArrayList<Booking> list = connector.getBookingList_ForUser(tempUser);
        return FXCollections.observableArrayList(list);
    }

    private ObservableList<String> timeList()
    {
        ObservableList<String> time = FXCollections.observableArrayList();
        for(int H = 9;H <= 21;H ++)
        {
            for (int M = 0; M <= 59; M++)
            {
                if(H == 21)
                {
                    time.add(String.format("%02d", H) + "." + String.format("%02d", M));
                    break;
                }
                else
                {
                    time.add(String.format("%02d", H) + "." + String.format("%02d", M));
                }
            }
        }

        return time;
    }

    //
    // Top Bar Buttons and Formatting
    //

    private Button backButton()
    {
        //Back to Home Button
        //Scene menu = regularMenu();
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if(tempisAdmin)
            {
                bookingStage.setScene(adminMenu());
            }
            else {
                bookingStage.setScene(regularMenu());
            }
        });
        return backButton;
    }

    private Button signOutButton()
    {
        //Sign Out Button Code
        Button signOutButton = new Button("Sign Out");
        signOutButton.setOnAction(e -> signOut());
        bookingStage.setOnCloseRequest(e -> {
            e.consume();
            signOut();
        });

        return signOutButton;
    }

    private HBox topBar(Label title)
    {
        title.setFont(new Font("Arial", 20));
        HBox titlePart = new HBox();
        titlePart.setPadding(new Insets(5));
        titlePart.setSpacing(10);
        titlePart.getChildren().addAll(title);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(5));
        topBar.setSpacing(10);

        topBar.getChildren().addAll(backButton(), signOutButton());
        topBar.setPadding(new Insets(0, 0,0,170 ));
        HBox both = new HBox();
        both.getChildren().addAll(titlePart,topBar);

        return both;
    }

    private void signOut()
    {
        Boolean confirmSignOut = ConfirmationBox.displayConfirm("Sign Out?", "Are you sure you want to sign out?");
        if(confirmSignOut)
        bookingStage.close();
    }


    //
    //  Server Side Calls Code
    //

    private void addBookingToDBS(String roomNoF, String attendeesF, String timeFromF,
                                 String timeToF, DatePicker dateF) throws SQLException, ClassNotFoundException {
        Boolean choice = ConfirmationBox.displayConfirm("Book Room?",
                "Do you want to book with the credentials given?");

        int roomNo = (Integer.parseInt(roomNoF));
        int attendees = (Integer.parseInt(attendeesF));

        if (choice)
        {
            bookingStage.setScene(regularMenu());

        }
        else
        {
            bookingStage.setScene(addBookingUI());
        }

    }

    private void addRecBookingToDBS (String roomNoF, String attendeesF, String timeFromF,
                                     String timeToF, DatePicker dateFrom, DatePicker dateTo)
    {

    }

    private void addUserToDBS(TextField firstNameIn, TextField secondNameIn, TextField emailIn,
                              TextField phoneIn, PasswordField passIn, String officeNoIn,
                              CheckBox isAdmin) throws SQLException, ClassNotFoundException,
                              IOException, InterruptedException {
        Boolean choice = ConfirmationBox.displayConfirm("Add User?",
                "Do you want to book with the credentials given?");
        connector = new dbsConnector();
        int totalUsers = connector.getUserCount();
        String firstName = firstNameIn.getText();
        String secondName = secondNameIn.getText();
        String email = emailIn.getText();
        String phone = phoneIn.getText();
        String pass = passIn.getText();

        int officeNo = Integer.parseInt(officeNoIn);

        Boolean permission = isAdmin.isSelected();
        if(choice) {

            connector = new dbsConnector();
            connector.addUser(totalUsers,firstName,secondName,email,phone,pass,officeNo,permission);
            bookingStage.setScene(adminMenu());
        }
        else
        {
            bookingStage.setScene(addUserUI());
        }
    }

    private void addRoomsToDBS(String roomType, TextField roomNo,
                               String roomCapacity) throws SQLException, ClassNotFoundException {
        Boolean choice = ConfirmationBox.displayConfirm("Add Room?",
                "Do you want to add a room with the information given?");

        int roomNum = Integer.parseInt(roomNo.getText());
        int roomCap = Integer.parseInt(roomCapacity);

        if(choice)
        {
            connector = new dbsConnector();
            connector.setRooms(roomType,roomNum,roomCap);
            bookingStage.setScene(adminMenu());
        }
        else
        {
            bookingStage.setScene(addRoomsUI());
        }

    }

    private void editBookingC()
    {

    }

    private void deleteBookingC()
    {

    }


}
