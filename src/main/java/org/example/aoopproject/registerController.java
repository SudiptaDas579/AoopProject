package org.example.aoopproject;

import jakarta.mail.MessagingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class registerController implements Initializable{
    @FXML
    public Stage stage;
    public Scene scene;
    public AnchorPane registerView;
    public Pane registerPane;
    public TextField userName;
    public PasswordField password;
    public TextField emailAddress;
    public TextField NID;
    public Label status;
    public Label NIDlabel;
    public Label studentIDLabel;
    public TextField studentID;
    public ComboBox<String> comboBox;
    public Label company;
    public TextField CompanyName;
    public Label driving;
    public TextField DrivingLicense;
    public Label bus;
    public TextField BusNumber;
    public Button register;
    private HashSet<General> generalHashSet;
    private HashSet<Student> studentHashSet;
    private HashSet<Authority> authorityHashSet;
    private String generatedOtp;
    private long otpGeneratedTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setBG();
        ObservableList<String> observableList = FXCollections.observableArrayList("General","Student","Authority");
        comboBox.setItems(observableList);
        comboBox.setStyle("-fx-font-family: Arial; -fx-font-size: 20; -fx-font-weight: bold;");
        DatabaseFile dbg = new DatabaseFile("general");
        dbg.start();
        generalHashSet = dbg.GeneralHashSet();

        DatabaseFile dbs = new DatabaseFile("student");
        dbs.start();
        studentHashSet = dbs.StudentHashSet();

        DatabaseFile dba = new DatabaseFile("authority");
        dba.start();
        authorityHashSet = dba.AuthorityHashSet();

        try {
            dbg.join();
            dba.join();
            dbs.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void setBG(){
        Image view = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pictures/Login.png")));
        BackgroundImage viewBG = new BackgroundImage(
                view,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        registerView.setBackground(new Background(viewBG));
    }

    public void login(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        stage =(Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    public void register(ActionEvent e) {
        if (comboBox.getValue()==null) {
            status.setText("Please Choose a Category First");
            status.setLayoutX(200);
            status.setLayoutY(350);
        } else if (userName.getText()==null || emailAddress.getText()==null || password.getText()==null) {
            status.setText("Please fill all the fields");
            status.setLayoutX(200);
            status.setLayoutY(400);
        }
        else {
            if (comboBox.getValue().equals("Student")) {
                if (userName.getText().isEmpty() || emailAddress.getText().isEmpty() || password.getText().isEmpty() || studentID.getText().isEmpty()) {
                    status.setText("Please fill all the fields");
                    status.setLayoutX(160);
                    status.setLayoutY(350);
                } else if ((!(emailAddress.getText().contains("@"))) || (!(emailAddress.getText().contains(".")))) {
                    status.setText("Please enter a valid email address");
                    status.setLayoutX(120);
                    status.setLayoutY(350);
                } else if (password.getText().length() < 8) {
                    status.setLayoutX(40);
                    status.setLayoutY(350);
                    status.setText("Password length should be at least 8 characters");
                } else {
                    Student s = new Student(userName.getText(), emailAddress.getText(), password.getText(), studentID.getText());
                    if (studentHashSet.contains(s)) {
                        status.setText("Account already exists");

                        status.setLayoutX(170);
                        status.setLayoutY(350);
                    } else if (sendAndVerifyOTP(emailAddress.getText())){
                        studentHashSet.add(s);
                        DatabaseFile db = new DatabaseFile("temp");
                        db.start();
                        db.updateInFile(new File("src/main/java/org/example/aoopproject/files/student.txt"), studentHashSet);
                        System.out.println("Account Added");
                        status.setText("Account Registered successfully");
                        status.setLayoutY(350);
                        status.setLayoutX(120);
                    } else {
                        status.setText("Registration cancelled or failed OTP");
                    }
                }
            } else if (comboBox.getValue().equals("General")) {
                if (userName.getText().isEmpty() || emailAddress.getText().isEmpty() || password.getText().isEmpty() || NID.getText().isEmpty()) {
                    status.setText("Please fill all the fields");
                    status.setLayoutX(160);
                    status.setLayoutY(400);
                } else if ((!(emailAddress.getText().contains("@"))) || (!(emailAddress.getText().contains(".")))) {
                    status.setText("Please enter a valid email address");
                    status.setLayoutX(120);
                    status.setLayoutY(400);
                } else if (password.getText().length() < 8) {
                    status.setLayoutX(40);
                    status.setLayoutY(400);
                    status.setText("Password length should be at least 8 characters");
                } else {

                    General g = new General(userName.getText(), emailAddress.getText(), password.getText(), NID.getText());
                    if (generalHashSet.contains(g)) {
                        status.setText("Account already exists");

                        status.setLayoutX(170);
                        status.setLayoutY(400);
                    } else if (sendAndVerifyOTP(emailAddress.getText())) {
                        generalHashSet.add(g);
                        DatabaseFile db = new DatabaseFile("temp");
                        db.start();
                        db.updateInFile(new File("src/main/java/org/example/aoopproject/files/general.txt"), generalHashSet);
                        System.out.println("Account Added");
                        status.setText("Account Registered successfully");
                        status.setLayoutY(400);
                        status.setLayoutX(120);
                    } else {
                        status.setText("Registration cancelled or failed OTP");
                    }
                }
            } else if (comboBox.getValue().equals("Authority")) {
                if (userName.getText().isEmpty() || emailAddress.getText().isEmpty() || password.getText().isEmpty() || CompanyName.getText().isEmpty() || DrivingLicense.getText().isEmpty() || BusNumber.getText().isEmpty()) {
                    status.setText("Please fill all the fields");
                    status.setLayoutX(160);
                    status.setLayoutY(490);
                } else if ((!(emailAddress.getText().contains("@"))) || (!(emailAddress.getText().contains(".")))) {
                    status.setText("Please enter a valid email address");
                    status.setLayoutX(120);
                    status.setLayoutY(490);
                } else if (password.getText().length() < 8) {
                    status.setLayoutX(40);
                    status.setLayoutY(490);
                    status.setText("Password length should be at least 8 characters");
                } else {
                    Authority a = new Authority(userName.getText(), emailAddress.getText(), password.getText(), CompanyName.getText(), DrivingLicense.getText(), BusNumber.getText());
                    if (authorityHashSet.contains(a)) {
                        status.setText("Account already exists");
                        status.setLayoutY(490);
                        status.setLayoutX(170);
                    } else if (sendAndVerifyOTP(emailAddress.getText())) {
                        authorityHashSet.add(a);
                        DatabaseFile db = new DatabaseFile("temp");
                        db.start();
                        db.updateInFile(new File("src/main/java/org/example/aoopproject/files/authority.txt"), authorityHashSet);
                        System.out.println("Account Added");
                        status.setText("Account Registered successfully");
                        status.setLayoutY(490);
                        status.setLayoutX(120);
                    } else {
                        status.setText("Registration cancelled or failed OTP");
                    }
                }
            }
        }
    }

    public class OTPGenerator {
        public static String generateOTP() {
            Random random = new Random();
            return String.valueOf(100000 + random.nextInt(900000));
        }
    }

    private boolean sendAndVerifyOTP(String recipientEmail) {
        try {
            generatedOtp = OTPGenerator.generateOTP();
            otpGeneratedTime = System.currentTimeMillis();
            Mail.sendEmail(recipientEmail, generatedOtp);

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("OTP Verification");
            dialog.setHeaderText("OTP sent to: " + recipientEmail);
            dialog.setContentText("Enter the OTP:");

            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) return false;

            String enteredOtp = result.get();
            if (System.currentTimeMillis() - otpGeneratedTime > 300000) {
                status.setText("OTP expired! Please try again.");
                return false;
            }

            return enteredOtp.equals(generatedOtp);

        } catch (MessagingException e) {
            e.printStackTrace();
            status.setText("Failed to send OTP. Check email configuration.");
            return false;
        }
    }

        public void options (ActionEvent e){
            if ((comboBox.getValue()).equals("General")) {
                status.setText(null);
                emailAddress.setText(null);
                password.setText(null);
                userName.setText(null);
                NIDlabel.setVisible(true);
                NID.setVisible(true);
                studentID.setVisible(false);
                studentIDLabel.setVisible(false);
                bus.setVisible(false);
                company.setVisible(false);
                driving.setVisible(false);
                CompanyName.setVisible(false);
                DrivingLicense.setVisible(false);
                BusNumber.setVisible(false);
                status.setLayoutY(390);
                register.setLayoutY(450);
            } else if ((comboBox.getValue()).equals("Student")) {
                status.setText(null);
                emailAddress.setText(null);
                password.setText(null);
                userName.setText(null);
                studentID.setVisible(true);
                studentIDLabel.setVisible(true);
                NID.setVisible(false);
                NIDlabel.setVisible(false);
                bus.setVisible(false);
                company.setVisible(false);
                driving.setVisible(false);
                CompanyName.setVisible(false);
                DrivingLicense.setVisible(false);
                BusNumber.setVisible(false);
                status.setLayoutY(390);
                register.setLayoutY(450);
            } else if ((comboBox.getValue()).equals("Authority")) {
                status.setText(null);
                emailAddress.setText(null);
                password.setText(null);
                userName.setText(null);
                bus.setVisible(true);
                company.setVisible(true);
                driving.setVisible(true);
                CompanyName.setVisible(true);
                DrivingLicense.setVisible(true);
                BusNumber.setVisible(true);
                NIDlabel.setVisible(false);
                NID.setVisible(false);
                studentID.setVisible(false);
                studentIDLabel.setVisible(false);
                status.setLayoutY(490);
                register.setLayoutY(520);
            }
        }
}
