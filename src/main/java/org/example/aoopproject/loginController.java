package org.example.aoopproject;

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

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class loginController implements Initializable{
    @FXML
    public Stage stage;
    public Scene scene;
    public AnchorPane loginView;
    public Pane loginPane;
    public TextField userName;
    public PasswordField password;
    public TextField emailAddress;
    public Label status;
    public ComboBox<String> comboBox;
    public Button register;
    public Button login;
    private HashSet<General> generalHashSet;
    private HashSet<Student> studentHashSet;
    private HashSet<Authority> authorityHashSet;


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
        loginView.setBackground(new Background(viewBG));
    }

    public void register(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
        stage =(Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    public void login(ActionEvent e) throws IOException {
        if (comboBox.getValue()==null) {
            status.setText("Please Choose a Category First");
            status.setLayoutX(210);
            status.setLayoutY(340);
        } else if (userName.getText()==null || emailAddress.getText()==null || password.getText()==null) {
            status.setText("Please fill all the fields");
            status.setLayoutX(210);
            status.setLayoutY(340);
        } else {
            if (comboBox.getValue().equals("Student")) {
                if (userName.getText()==null || emailAddress.getText()==null || password.getText()==null) {
                    status.setText("Please fill all the fields");
                    status.setLayoutX(210);
                    status.setLayoutY(340);
                } else if ((!(emailAddress.getText().contains("@"))) || (!(emailAddress.getText().contains(".")))) {
                    status.setText("Please enter a valid email address");
                    status.setLayoutX(210);
                    status.setLayoutY(340);
                } else if (password.getText().length() < 8) {
                    status.setLayoutX(100);
                    status.setLayoutY(340);
                    status.setText("Password length should be at least 8 characters");
                } else {
                    Student s = new Student(userName.getText(), emailAddress.getText(), password.getText());
                    if (studentHashSet.contains(s)) {
                        status.setText("Login Successful");
                        status.setLayoutX(210);
                        status.setLayoutY(340);

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homepage.fxml"));
                        stage =(Stage) ((Node)e.getSource()).getScene().getWindow();
                        scene = new Scene(fxmlLoader.load());
                        stage.setScene(scene);
                        ThemeManager.setScene(scene);

                    } else {
                        for (Student s1 : studentHashSet) {
                            if (s1.getEmail().equals(emailAddress.getText())) {
                                if (!s1.getPassword().equals(password.getText())) {
                                    status.setText("Wrong Password");
                                    status.setLayoutX(250);
                                    status.setLayoutY(340);
                                }else{
                                    status.setLayoutX(100);
                                    status.setLayoutY(340);
                                    status.setText("Account Not Registered Please Register First");
                                }
                            }
                        }

                    }
                }
            } else if (comboBox.getValue().equals("General")) {
                if (userName.getText()==null || emailAddress.getText()==null || password.getText()==null) {
                    status.setText("Please fill all the fields");
                    status.setLayoutX(210);
                    status.setLayoutY(340);
                } else if ((!(emailAddress.getText().contains("@"))) || (!(emailAddress.getText().contains(".")))) {
                    status.setText("Please enter a valid email address");
                    status.setLayoutX(210);
                    status.setLayoutY(340);
                } else if (password.getText().length() < 8) {
                    status.setLayoutX(100);
                    status.setLayoutY(340);
                    status.setText("Password length should be at least 8 characters");
                } else {

                    General g = new General(userName.getText(), emailAddress.getText(), password.getText());
                    if (generalHashSet.contains(g)) {
                        status.setText("Account login successful");
                        status.setLayoutX(210);
                        status.setLayoutY(340);

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homepage.fxml"));
                        stage =(Stage) ((Node)e.getSource()).getScene().getWindow();
                        scene = new Scene(fxmlLoader.load());
                        stage.setScene(scene);
                        ThemeManager.setScene(scene);

                    } else {
                        for (General g1 : generalHashSet) {
                            if (g1.getEmail().equals(emailAddress.getText())) {
                                if (!g1.getPassword().equals(password.getText())) {
                                    status.setText("Wrong Password");
                                    status.setLayoutX(250);
                                    status.setLayoutY(340);
                                }else{
                                    status.setLayoutX(100);
                                    status.setLayoutY(340);
                                    status.setText("Account Not Registered Please Register First");
                                }
                            }
                        }

                    }
                }
            } else if (comboBox.getValue().equals("Authority")) {
                if (userName.getText()==null || emailAddress.getText()==null || password.getText()==null) {
                    status.setText("Please fill all the fields");
                    status.setLayoutX(210);
                    status.setLayoutY(340);
                } else if ((!(emailAddress.getText().contains("@"))) || (!(emailAddress.getText().contains(".")))) {
                    status.setText("Please enter a valid email address");
                    status.setLayoutX(210);
                    status.setLayoutY(340);
                } else if (password.getText().length() < 8) {
                    status.setLayoutX(100);
                    status.setLayoutY(340);
                    status.setText("Password length should be at least 8 characters");
                } else {
                    Authority a = new Authority(userName.getText(), emailAddress.getText(), password.getText());
                    if (authorityHashSet.contains(a)) {
                        status.setText("Account login successful");
                        status.setLayoutX(210);
                        status.setLayoutY(340);

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AdminView.fxml"));
                        stage =(Stage) ((Node)e.getSource()).getScene().getWindow();
                        scene = new Scene(fxmlLoader.load());
                        stage.setScene(scene);
                        ThemeManager.setScene(scene);
                    } else {
                        for (Authority a1 : authorityHashSet) {
                            if (a1.getEmail().equals(emailAddress.getText())) {
                                if (!a1.getPassword().equals(password.getText())) {
                                    status.setText("Wrong Password");
                                    status.setLayoutX(250);
                                    status.setLayoutY(340);
                                }else{
                                    status.setLayoutX(100);
                                    status.setLayoutY(340);
                                    status.setText("Account Not Registered Please Register First");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void options (ActionEvent e){
        if ((comboBox.getValue()).equals("General")) {
            status.setText(null);
            emailAddress.setText(null);
            password.setText(null);
            userName.setText(null);
        } else if ((comboBox.getValue()).equals("Student")) {
            status.setText(null);
            emailAddress.setText(null);
            password.setText(null);
            userName.setText(null);
        } else if ((comboBox.getValue()).equals("Authority")) {
            status.setText(null);
            emailAddress.setText(null);
            password.setText(null);
            userName.setText(null);
        }
    }
}
