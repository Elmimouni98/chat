package emi.reseau;

import java.io.*;
import java.net.Socket;

import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClientChat extends Application {
	PrintWriter printWriter;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Client Chat");
		BorderPane borderPane = new BorderPane();
		Label labelHost = new Label("Host:");
		TextField textFieldHost = new TextField("localhost");
		Label labelPort = new Label("Port:");
		TextField textFieldPort = new TextField("1234");
		Button buttonConnecter = new Button("connecter");
		HBox hBox = new HBox();
		hBox.getChildren().addAll(labelHost,textFieldHost,labelPort,textFieldPort,buttonConnecter);
		hBox.setSpacing(10);
		hBox.setPadding(new Insets(10));
		hBox.setBackground(new Background(new BackgroundFill(Color.AQUAMARINE,null,null)));
		borderPane.setTop(hBox);
		VBox vBox = new VBox();
		vBox.setSpacing(10);
		vBox.setPadding(new Insets(10));
		ObservableList<String> listModel = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<String>(listModel);
		vBox.getChildren().add(listView);
		borderPane.setCenter(vBox);
		
		Label label = new Label("Message:");
		TextField message = new TextField();message.setPrefSize(500, 40);
		Button buttonEnvoyer = new Button("Envoyer");
		HBox hBox2 = new HBox();
		hBox2.setSpacing(10);
		hBox2.setPadding(new Insets(10));
		hBox2.getChildren().addAll(label,message,buttonEnvoyer);
		borderPane.setBottom(hBox2);
		Scene scene = new Scene(borderPane,800,600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		buttonConnecter.setOnAction((evt)->{
			String host = textFieldHost.getText();
			int port = Integer.parseInt(textFieldPort.getText());
			try {
				Socket socket = new Socket(host,port);
				InputStream inputStream = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(isr);
				printWriter = new PrintWriter(socket.getOutputStream(),true);
				new Thread(()-> {
					
					while(true) {
								try {
								String reponse = bufferedReader.readLine();
								Platform.runLater(()->{
									listModel.add(reponse);
								});
								} 
								catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
							}
					}
				}).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		});
		
		buttonEnvoyer.setOnAction((evt)->{
			String msg = message.getText();
			printWriter.println(msg);
		});
	}

	

}
