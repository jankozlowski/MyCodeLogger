package application;

import java.awt.AWTException;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.ErrorManager;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;


public class Main extends Application {

	public static boolean run = true;
	private boolean recording = false;
	private boolean paused = false;
	private long elapsed = 0;
	private long allCharCount = 0;
	private long buttonDeleteStartTime = 0;
	private ImageView playView;
	private ImageView stopView;
	private MenuItem recordItem;
	private TimerTask task;
	private double xOffset = 0;
	private double yOffset = 0;
	private int[] charCount = new int[128];
	private Instant recordStart;
	private String userName ="";
	private Set<Language> language = new HashSet<>();
	private List<Instant> clickDate = new ArrayList<>();
	private long project = -1;
	private String ACCESS_TOKEN ="";
	private Label errorMessageLabel = new Label("");
	private String BASE_URL = "http://localhost:8080/";

	
	@Override

	public void start(Stage primaryStage) {
		showLogin();
	}
	
	
	public void showLogin(){
		
		ImageView logoView = createTopImage("/brandText.png",300,60);

		HBox topLogo = new HBox();
		topLogo.setPrefWidth(500);
		topLogo.getChildren().addAll(logoView);
		topLogo.setStyle("-fx-border-style: solid; -fx-border-width: 0 0 2 0;-fx-border-color: #347ab4;");

		ImageView closeView = createTopImage("/close.png",16,16);
		ImageView minimizeView = createTopImage("/minimize.png",16,16);
		ImageView onTopView = createTopImage("/pin.png",16,16);
		ImageView emptyView = createTopImage("/empty.png",252,16);
		
		HBox systemIcons = new HBox();
		systemIcons.getChildren().addAll(emptyView, minimizeView, onTopView, closeView);
		
		Stage loginStage = new Stage();
		loginStage.initStyle(StageStyle.UNDECORATED);
		BorderPane sceneLayout = new BorderPane();
		
		VBox loginLayout = new VBox();
		VBox formLayout = new VBox();
		VBox topLabelWraper = new VBox();
		VBox submitWraper = new VBox();
		VBox checkboxWraper = new VBox();
		checkboxWraper.setPadding(new Insets(15,0,0,0));
		submitWraper.setAlignment(Pos.CENTER);
		topLabelWraper.setAlignment(Pos.CENTER);
		topLabelWraper.setPadding(new Insets(30, 0, 20, 0));
		formLayout.setPadding(new Insets(0, 40, 40, 40));
		formLayout.setSpacing(5);
		
		Label screenLabel = new Label("Log in:");
		screenLabel.setStyle("-fx-font-size: 32;");
		topLabelWraper.getChildren().add(screenLabel);
		
		loginLayout.setMinWidth(130);
		Label login = new Label("Login");
		login.setMinWidth(130);
		
		TextField loginText = new TextField();
		loginText.setMinWidth(130);
		Label password = new Label("Password");
		password.setMinWidth(130);
		password.setPadding(new Insets(10, 0, 0, 0));
		PasswordField passwordText = new PasswordField();
		passwordText.setMinWidth(130);
		errorMessageLabel.setMinWidth(225);
		errorMessageLabel.setTextFill(Color.RED);
		errorMessageLabel.setAlignment(Pos.CENTER);
		
		
		Button submit = new Button("Login");
		submit.setMinWidth(130);	
		
		CheckBox rememberMeCheckBox = new CheckBox("Remember me");
		
		Hyperlink createAcountLink = new Hyperlink("Click here");
		createAcountLink.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				getHostServices().showDocument("http://localhost:3000/registration");
			}
        });
		
		submit.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				if(checkUser(loginText.getText(),passwordText.getText(),false,rememberMeCheckBox.isSelected())){
					userName=loginText.getText();
					loginStage.close();
					Stage primaryStage = new Stage();
					showMainApp(primaryStage);
				}
				else {
					
				}
			}
		});
		
		TextFlow flow = new TextFlow(
			    new Text("Don't have an account? "), createAcountLink
			);
		flow.setTextAlignment(TextAlignment.CENTER);
		
		checkboxWraper.getChildren().add(rememberMeCheckBox);
		submitWraper.getChildren().addAll(submit,flow);
		loginLayout.getChildren().addAll(systemIcons, topLogo);
		formLayout.getChildren().addAll(topLabelWraper,login,loginText,password,passwordText,checkboxWraper,errorMessageLabel,submitWraper);
		sceneLayout.setTop(loginLayout);
		sceneLayout.setCenter(formLayout);
		sceneLayout.setStyle("-fx-background-image: url('gplaypattern.png');");
		Scene loginScene = new Scene(sceneLayout, 300, 480);
		
		loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		loginStage.setScene(loginScene);
		loginStage.show();
		moveWindow(sceneLayout, loginStage);
		
		checkRememberMeOnStartup(loginStage);
		
	}
	
	public boolean checkRememberMeOnStartup(Stage loginStage){
		
		File rememberMeFile = new File("data/mycode_remember_me.txt");
		try {
			FileReader reader = new FileReader(rememberMeFile);
			BufferedReader breader = new BufferedReader(reader);
			
			boolean remember =  Boolean.valueOf(breader.readLine());
			String username = breader.readLine();
			String accessToken = breader.readLine();
			
			breader.close();
			System.out.println(remember);
			System.out.println(username);
			System.out.println(accessToken);
			if(remember){
				userName = username;
				ACCESS_TOKEN = accessToken;
				loginStage.close();
				Stage primaryStage = new Stage();
				showMainApp(primaryStage);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	public void saveRememberMe(String login, String acessToken){
		File directory = new File("data");
		
		if(!directory.exists()){
			directory.mkdir();
		}
		File rememberMeFile = new File("data/mycode_remember_me.txt");
		FileWriter fwriter;
		try {
			fwriter = new FileWriter(rememberMeFile);
			BufferedWriter writer = new BufferedWriter(fwriter);
			writer.write("true");
			writer.newLine();
			writer.write(login);
			writer.newLine();
			writer.write(acessToken);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean checkUser(String username, String password, boolean rememberMe, boolean isRememberMeChecked) {
		String myresponse = "false";
		HttpClient httpClient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000)
				.build();
		JSONObject obj = null;
		try {
			URIBuilder builder;
			JSONParser parser = new JSONParser();
			ObjectMapper mapper = new ObjectMapper();
			builder = new URIBuilder(BASE_URL + "api/auth/signin");
			HttpPost httppost = new HttpPost(builder.build());
			httppost.setConfig(requestConfig);
			httppost.addHeader("content-type", "application/json");
			
			LoginRequest loginRequest = new LoginRequest();
			loginRequest.setName(username);
			loginRequest.setPassword(password);
			
			StringEntity params =new StringEntity(mapper.writeValueAsString(loginRequest));
			httppost.setEntity(params);
			
			
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity responseEntity = response.getEntity();
			    if(responseEntity!=null) {
			        myresponse = EntityUtils.toString(responseEntity);
			       
			        try {
						obj = (JSONObject) parser.parse(myresponse);
					} catch (ParseException e) {
						e.printStackTrace();
					}
			        System.out.println(myresponse);
			        System.out.println(obj.get("success"));
			        System.out.println(obj.get("message"));
			        System.out.println(obj.get("accessToken"));
			        
			        if(obj.get("accessToken")==null) {
			        	errorMessageLabel.setText((String) obj.get("message"));
			        	return false;
			        }
			        else {
			        	if(isRememberMeChecked){
			    			saveRememberMe(username, (String) obj.get("accessToken"));
			    		}
			    		ACCESS_TOKEN = (String) obj.get("accessToken");
			    		return true;
			        }
			        
			    }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	
		
		return false;
	}
	
	public void showMainApp(Stage primaryStage){
		
		try {
			Language firstLanguage = new Language();
			firstLanguage.setName("Java");
			firstLanguage.setLocation((getClass().getResource("/java.png").toURI().toString()).substring(6));
			language.add(firstLanguage);
			
			
			primaryStage.initStyle(StageStyle.UNDECORATED);

			ImageView logoView = createTopImage("/brandText.png",300,60);

			HBox topLogo = new HBox();
			topLogo.setPrefWidth(500);
			topLogo.getChildren().addAll(logoView);
			topLogo.setStyle("-fx-border-style: solid; -fx-border-width: 0 0 2 0;-fx-border-color: #347ab4;");

			ImageView closeView = createTopImage("/close.png",16,16);
			ImageView minimizeView = createTopImage("/minimize.png",16,16);
			ImageView onTopView = createTopImage("/pin.png",16,16);
			ImageView emptyView = createTopImage("/empty.png",252,16);
			
			HBox systemIcons = new HBox();
			systemIcons.getChildren().addAll(emptyView, minimizeView, onTopView, closeView);
			
			ImageView stopWatchView = createImageWithScale("/stopwatch.png",0.75,0.75);
			Label timeLabel = createLabel("Duration: ", stopWatchView, 300, new Insets(15, 0, 0, 20));

			Label timeCounter = createLabel("00:00:00:000", null, 300, new Insets(0, 0, 0, 20));
			timeCounter.setStyle(" -fx-font: 30px TimeNewRoman;");

			HBox lineBox = createBlueLine(new Insets(5, 15, 5, 15));
			
			ImageView LOCView = createImageWithScale("/binary.png",1,1);

			Label LOCLabel = createLabel("Lines Of Code:",LOCView,300,new Insets(10, 0, 0, 20));
			
			Label LOCCounter = createLabel("00000 characters",null,300,new Insets(0, 0, 0, 20));
			LOCCounter.setStyle(" -fx-font: 30px TimeNewRoman;");
			
			HBox lineBox2 = createBlueLine(new Insets(5, 15, 5, 15));
			
			ImageView languageView = createImageWithScale("/coding.png",1,1);
			Label languageLabel = createLabel("Language:",languageView,143,new Insets(0,0,0,0));
		
			ImageView projectView =  createImageWithScale("/project.png",1,1);
			Label projectLabel =  createLabel("Project:",projectView,150,new Insets(0,0,0,0));

			HBox labelsLanguageBox = new HBox();
			labelsLanguageBox.getChildren().addAll(languageLabel, projectLabel);
			labelsLanguageBox.setPadding(new Insets(10, 0, 0, 34));

			HBox languageProjectBox = new HBox();
			ImageView languageLabelView =  createTopImage("/java.png", 32, 32);

			Label selectLanguageLabel = createLabel("Java",languageLabelView,140,new Insets(0,0,0,20));
			selectLanguageLabel.setStyle(" -fx-font: 15px TimeNewRoman;");
			
			System.out.println("out");
			ObservableList<Project> projects  = downloadProjectsFromSpring();
			
			ComboBox<Project> projectComboBox = new ComboBox<>();
			Project emptyProject = new Project();
			emptyProject.setProjectId(-1);
			emptyProject.setName("None");
			
			projectComboBox.getItems().addAll(emptyProject);
			for(Project pro : projects) {
				projectComboBox.getItems().add(pro);
				
			}
			projectComboBox.setMinWidth(100);
			projectComboBox.setValue(emptyProject);
			
			projectComboBox.setConverter(new StringConverter<Project>() {

			    @Override
			    public String toString(Project object) {
			        return object.getName();
			    }

			    @Override
			    public Project fromString(String string) {
			        return projectComboBox.getItems().stream().filter(ap -> 
			            ap.getName().equals(string)).findFirst().orElse(null);
			    }
			});
			
			projectComboBox.valueProperty().addListener(new ChangeListener<Project>() {
		     

				@Override
				public void changed(ObservableValue<? extends Project> observable, Project oldValue, Project newValue) {
					
					if(newValue != null) {
				        project =  newValue.getProjectId();
					}
				}    
		      });

			languageProjectBox.getChildren().addAll(selectLanguageLabel, projectComboBox);
			languageProjectBox.setPadding(new Insets(10, 20, 5, 20));
			languageProjectBox.setAlignment(Pos.CENTER);

			HBox lineBox3 = createBlueLine(new Insets(15, 15, 5, 15));
			
			
			HBox playStopBox = new HBox();
			
			HBox imageViewWraper = new HBox();
			ImageView logoutView = createImageWithScale("/logout.png",0.45,0.45);
			imageViewWraper.getChildren().add(logoutView);
			imageViewWraper.setPadding(new Insets(60,0,0,0));
			//Image playImage = new Image(getClass().getResource("/play.png").toURI().toString());
			playView = createImageWithScale("/play.png", 1, 1);
			stopView = createImageWithScale("/stopgrey.png", 1, 1);
			playStopBox.setPadding(new Insets(20, 20, 20, 50));
			playStopBox.getChildren().addAll(playView, stopView,imageViewWraper);

			VBox layout = new VBox();
			layout.getChildren().addAll(systemIcons, topLogo, timeLabel, timeCounter, lineBox, LOCLabel, LOCCounter,
					lineBox2, labelsLanguageBox, languageProjectBox, lineBox3, playStopBox);

			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 300, 480);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			root.getChildren().addAll(layout);
			root.setStyle("-fx-background-image: url('gplaypattern.png');");
			primaryStage.setResizable(false);
			primaryStage.sizeToScene();
			primaryStage.setTitle("MyCode Logger");

			primaryStage.setScene(scene);
			primaryStage.show();

			primaryStage.getIcons().add(new Image(getClass().getResource("/smalllogo.png").toURI().toString()));

			minimizeView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					primaryStage.setIconified(true);
					event.consume();
				}
			});

			onTopView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (primaryStage.isAlwaysOnTop()) {
						primaryStage.setAlwaysOnTop(false);
					} else {
						primaryStage.setAlwaysOnTop(true);
					}
					event.consume();
				}
			});

			closeView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					primaryStage.hide();
					event.consume();
				}
			});

			selectLanguageLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					try {
						languagePopup(primaryStage, root, selectLanguageLabel);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			});
			
			imageViewWraper.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					//end loging
					//playEvent(stopView);
					//send codinglog
					if(!timeCounter.getText().equals("00:00:00:000")){
						sendLogToSpring();
					}
					
					//set variables
					userName ="";
					language=new HashSet<>();
					Language firstLanguage = new Language();
					firstLanguage.setName("Java");
					language.add(firstLanguage);
					project=-1;
					recording = false;
					paused = false;
					elapsed = 0;
					allCharCount = 0;
					clickDate = new ArrayList<Instant>();
					charCount = new int[128];
					recordStart = null;
					ACCESS_TOKEN ="";
					errorMessageLabel.setText("");
					changRememberMeToFalse();
					primaryStage.close();
					showLogin();
				}
			});

			startRecording(playView, stopView, timeCounter, LOCCounter);
			moveWindow(root, primaryStage);
			createTrayIcon(primaryStage);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		Thread thread = new Thread("Loging keys thread") {
			public void run() {
				logKeybord();
			}
		};

		thread.start();

	}
	
	
	public void changRememberMeToFalse(){
		
		File rememberFile = new File("data/mycode_remember_me.txt");
		FileWriter fwriter;
		try {
			fwriter = new FileWriter(rememberFile);
			BufferedWriter bwriter = new BufferedWriter(fwriter);
			bwriter.write("false");
			bwriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public ImageView createTopImage(String imageSource, int width, int height){
		Image image = null;
		try {
			image = new Image(getClass().getResource(imageSource).toURI().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		ImageView view = new ImageView(image);
		view.setFitWidth(width);
		view.setFitHeight(height);
		
		return view;
	}

	public ImageView createImageWithScale(String imageSource, double xScale, double yScale){
		Image image = null;
		try {
			image = new Image(getClass().getResource(imageSource).toURI().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		ImageView view = new ImageView(image);
		view.setScaleX(xScale);
		view.setScaleY(yScale);
		
		return view;
	}
	
	public Label createLabel(String title, ImageView image, int width, Insets bounds){
		Label label = new Label(title, image);
		label.setMinWidth(width);
		label.setPadding(bounds);
		return label;
	}
	
	public HBox createBlueLine(Insets bounds){
		
		HBox lineBox = new HBox();

		Line line = new Line(0, 0, 270, 0);
		line.setStroke(new Color(0.203125, 0.4765625, 0.703125, 1));
		
		lineBox.getChildren().addAll(line);
		lineBox.setPadding(bounds);
		return lineBox;
	}
	
	public void moveWindow(BorderPane root, Stage primaryStage) {

		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});
		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				primaryStage.setX(event.getScreenX() - xOffset);
				primaryStage.setY(event.getScreenY() - yOffset);
			}
		});
		
		
	}

	public void createTrayIcon(final Stage stage) {
		java.awt.Image image = null;
		SystemTray tray = null;
		Platform.setImplicitExit(false);
		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();

			try {
				URL url = new URL(getClass().getResource("/logomicro.png").toURI().toString());
				image = ImageIO.read(url);
			} catch (Exception ex) {
				System.out.println(ex);
			}

		}
		stage.show();

		ActionListener listenerRecord = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MenuItem item = (MenuItem) e.getSource();
				String label = item.getLabel();
				if (label.equals("Record") || label.equals("Continue")) {
					playEvent(playView);
				} else {
					playEvent(stopView);
				}
			}
		};

		ActionListener listenerShow = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						stage.show();
					}
				});
			}
		};

		ActionListener listenerClose = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				stage.hide();
			}
		});

		PopupMenu popup = new PopupMenu();
		MenuItem showItem = new MenuItem("Open");
		recordItem = new MenuItem("Record");
		MenuItem exitItem = new MenuItem("Close");

		showItem.addActionListener(listenerShow);
		recordItem.addActionListener(listenerRecord);
		exitItem.addActionListener(listenerClose);

		popup.add(showItem);
		popup.add(recordItem);
		popup.add(exitItem);

		TrayIcon icon = new TrayIcon(image, "MyCode Logger", popup);
		icon.setImageAutoSize(true);
		try {
			tray.add(icon);
		} catch (AWTException e) {
			System.err.println(e);
		}

		icon.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
			}

			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						stage.show();
					}
				});
			}
		});
	}

	public void playEvent(ImageView view) {
		Event.fireEvent(view, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, true, true,
				true, true, true, true, true, true, true, true, null));
	}

	public void logKeybord() {

		GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook();

		System.out.println("Global keyboard hook successfully started, press [escape] key to shutdown.");
		keyboardHook.addKeyListener(new GlobalKeyAdapter() {
			@Override
			public void keyPressed(GlobalKeyEvent event) {
				System.out.println(event);
				
				
				
				// if(event.getVirtualKeyCode()==GlobalKeyEvent.VK_ESCAPE)
				// run = false;
			}

			@Override
			public void keyReleased(GlobalKeyEvent event) {
				System.out.println(event);
				if (recording) {
					allCharCount++;
					try {
						charCount[event.getVirtualKeyCode()]++;
					}
					catch(Exception e) {}
					clickDate.add(Instant.now());
				}        
			}
		
		});

		try {
			while (run)
				Thread.sleep(128);
		} catch (InterruptedException e) {
			/* nothing to do here */ } finally {
			keyboardHook.shutdownHook();
		}

	}

	public void startRecording(ImageView play, ImageView stop, Label timeLabel, Label charLabel) {

		recordStart = Instant.now();
		
	
		
		play.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				Image pauseImage = null;
				Image stopImage = null;

				if (recording == false && paused == false) {
					recordStart = Instant.now();
					elapsed = 0;
					System.out.println(recordStart);
					System.out.println("start");
				}

				if (recording) {
					try {

						pauseImage = new Image(getClass().getResource("/paused.png").toURI().toString());
						play.setImage(pauseImage);
						recording = false;
						paused = true;
						recordItem.setLabel("Continue");
						task.cancel();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}

				} else {
					try {
						pauseImage = new Image(getClass().getResource("/pause.png").toURI().toString());
						stopImage = new Image(getClass().getResource("/stop.png").toURI().toString());
						play.setImage(pauseImage);
						stop.setImage(stopImage);
						recording = true;
						paused = false;
						recordItem.setLabel("Stop");
						task = new TimerTask() {

							long interval = 1;

							@Override
							public void run() {
								elapsed += interval;

								Platform.runLater(new Runnable() {
									public void run() {
										SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
										sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
										String time = sdf.format(new Date(elapsed));
										timeLabel.setText(time);
										charLabel.setText(allCharCount + " characters");
									}
								});
							}
						};
						Timer timer = new Timer();
						timer.scheduleAtFixedRate(task, 1, 1);

					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}

			}
		});

		stop.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Image pauseImage = null;
				Image stopImage = null;
				if (recording || paused) {
					try {
						task.cancel();

						pauseImage = new Image(getClass().getResource("/play.png").toURI().toString());
						stopImage = new Image(getClass().getResource("/stopgrey.png").toURI().toString());
						play.setImage(pauseImage);
						stop.setImage(stopImage);
						recording = false;
						paused = false;
						recordItem.setLabel("Record");
						
						sendLogToSpring();
						charCount = new int[128];
						clickDate = new ArrayList<Instant>();

					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					finally{
						allCharCount=0;
					}

				}
			}

			
		});

	}
	
	public void sendLogToSpring() {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		try {
		    HttpPost request = new HttpPost(BASE_URL + "api/log/add/"+userName+"/"+project);
		    CodeLog cl = new CodeLog();
		    
		    Map<Integer,Integer> charCountMap = new LinkedHashMap<>();
		    int count =0;
		    for(int number:charCount){
		    	charCountMap.put(Integer.valueOf(count),Integer.valueOf(number));
		    	count++;
		    }
		    
		    cl.setCharCount(charCountMap);
		    cl.setCharSum((int)allCharCount);
		    cl.setClickDate(clickDate);
		    cl.setRecordStart(recordStart);
		    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String time = sdf.format(new Date(elapsed));
		    
		    cl.setDuration(time);
		    
		    cl.setLanguage(language);
		    cl.setProject(null);
		   
	        ObjectMapper mapper = new ObjectMapper();
	        
	        mapper.registerModule(new JavaTimeModule());
	        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cl);
	        
		    StringEntity params =new StringEntity(json);
		    System.out.println(json);
		    request.addHeader("content-type", "application/json");
		    request.addHeader("Accept","application/json");
		    request.addHeader("Authorization", "Bearer " + ACCESS_TOKEN);
		    request.setEntity(params);
		    HttpResponse response = httpClient.execute(request);
		    HttpEntity responseEntity = response.getEntity();
		    if(responseEntity!=null) {
		        String myresponse = EntityUtils.toString(responseEntity);
		        System.out.println(myresponse);
		    }
		    
		    
		    
		}catch (Exception ex) {
		} finally {
		    try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(Language lang:language) {
			System.out.println(lang.getName());
			System.out.println(lang.getLocation());
			if(lang.getLocation()!=null) {
				uploadFileToSpring(lang.getLocation());
			}
	    }
		
	}
	
	
	public ObservableList<Project> downloadProjectsFromSpring() {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		ObservableList<Project> projectList = FXCollections.observableArrayList();
		
		try {
		    HttpGet request = new HttpGet(BASE_URL + "api/project/"+userName+"/all");
		    request.addHeader("Authorization", "Bearer " + ACCESS_TOKEN);
		    HttpResponse response = httpClient.execute(request);
		    HttpEntity responseEntity = response.getEntity();
		    if(responseEntity!=null) {
		    	
		        String myresponse = EntityUtils.toString(responseEntity);
		        System.out.println(myresponse);
		        
		        JSONParser parser = new JSONParser();
		        JSONArray arr = (JSONArray)parser.parse(myresponse);
		        
		        
		        
		        for(int jsonIndex=0; jsonIndex<arr.size(); jsonIndex++) {
		        	Project project = new Project();
		        	project.setProjectId((long)((JSONObject)arr.get(jsonIndex)).get("projectId"));
		        	project.setName((String)((JSONObject)arr.get(jsonIndex)).get("name"));
		        	projectList.add(project);
		        }
		        
		    }
		}
		catch(Exception e) {
			
		}
		
		return projectList;
	}
	
	public void uploadFileToSpring(String filePath) {
		File file = new File(filePath);
		FileBody fileBody = new FileBody(file);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
				.addTextBody("params", "{....}").addPart("file", fileBody);
		HttpEntity multiPartEntity = builder.build();

		String url = BASE_URL + "api/file/uploadFile/"+userName;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(multiPartEntity);
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			HttpResponse response = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void languagePopup(Stage primaryStage, BorderPane root, Label languageLabel) throws URISyntaxException {

		Rectangle shadow = new Rectangle(0, 0, 600, 1000);
		shadow.setFill(new Color(0, 0, 0, 0.5));
		StackPane shadowStack = new StackPane();
		shadowStack.getChildren().add(shadow);
		root.getChildren().add(shadowStack);

		VBox layout = new VBox();
		Stage newStage = new Stage();
		BorderPane popup = new BorderPane();
		Scene scene = new Scene(popup, 170, 430);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		List<String> languages = new ArrayList(Arrays.asList("Java", "C", "C++", "C-Sharp", "Objective-C",	"Delphi", "GO", "Javascript", "Perl", "R", "Ruby", "Python", "PHP", "Visual Basic", "Swift", "Scratch", "Add"));
		List<Language> customLanguages = readLanguageFile();
		
		for(Language lang : customLanguages) {
			
			languages.add(languages.size()-1, lang.getName());

		}
		
		ListView<String> listView = new ListView<String>();
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		ObservableList<String> items = FXCollections.observableArrayList(languages);
		listView.setItems(items);
		listView.setMinSize(170, 380);

		listView.setCellFactory(param -> new ListCell<String>() {
			private ImageView imageView = new ImageView();
			
			@Override
			public void updateItem(String name, boolean empty) {
				super.updateItem(name, empty);
				
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					String path = "";
					try {
						Image image = null;
						
						if(!name.equals("Add")&&getIndex()<16) {
							image = new Image(getClass().getResource("/" + name.toLowerCase() + ".png").toURI().toString());
							path = (getClass().getResource("/" + name.toLowerCase() + ".png").toURI().toString()).substring(6);
						}
						else if(name.equals("Add")) {
							image = new Image(getClass().getResource("/plus.png").toURI().toString());
						}
						else {
							File file = new File("data/"+customLanguages.get(getIndex()-16).getLocation());
							image = new Image(file.toURI().toString());
							path = file.toURI().toString().substring(6);;
						}
						imageView.setFitWidth(64);
						imageView.setFitHeight(64);
						imageView.setImage(image);
					} catch (Exception w) {
						w.printStackTrace();
					}
					setText(name);
			
					
					setGraphic(imageView);
					listView.setOnMouseReleased(new EventHandler<MouseEvent>() {
					
						@Override
						public void handle(MouseEvent click) {
							if (click.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
								 System.out.println(buttonDeleteStartTime);
								 
				                if (System.currentTimeMillis() - buttonDeleteStartTime > 1 * 300 &&listView.getSelectionModel().getSelectedIndex()>15&&!listView.getSelectionModel().getSelectedItem().toString().equals("Add")) {
				                	deleteLanguageDialog(newStage,popup,listView);
				                } 
				            }
							
						}
					});
					
					listView.setOnMousePressed(new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent click) {
							System.out.println(click.getEventType());
							if(click.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
								buttonDeleteStartTime = System.currentTimeMillis();
								System.out.println(System.currentTimeMillis());
					               System.out.println(buttonDeleteStartTime);
							}
							
						}
					});
					
					listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent click) {
							
							if (click.getClickCount() == 2) {

								if(listView.getSelectionModel().getSelectedItem().toString().equals("Add")) {
									System.out.println("Add");
									addLanguagePopup(newStage, popup, listView, customLanguages);
								}
								else {
								
								languageLabel.setText(listView.getSelectionModel().getSelectedItem());
								Image image = null;
								String path ="";
								try {
									if(listView.getSelectionModel().getSelectedIndex()<16) {
										image = new Image(getClass().getResource("/" + listView.getSelectionModel().getSelectedItem().toLowerCase() + ".png")
											.toURI().toString());
										path = (getClass().getResource("/" + listView.getSelectionModel().getSelectedItem().toLowerCase() + ".png")
												.toURI().toString()).substring(6);
									}
									else {
										File file = new File("data/"+customLanguages.get(listView.getSelectionModel().getSelectedIndex()-16).getLocation());
										image = new Image(file.toURI().toString());
										path = file.toURI().toString().substring(6);;
									}
								} catch (URISyntaxException e) {

									e.printStackTrace();
								}
								language.clear();
								Language selectedLanguage = new Language();
								selectedLanguage.setName(listView.getSelectionModel().getSelectedItem());
								selectedLanguage.setLocation(path);
								language.add(selectedLanguage);
								imageView.setImage(image);
								imageView.setFitWidth(32);
								imageView.setFitHeight(32);
								languageLabel.setGraphic(imageView);
								newStage.close();
								root.getChildren().remove(root.getChildren().size() - 1);
								}
								
								
							}
						}
					});
				}
			}
		});
		
		Button ok = new Button("OK");
		ok.setMinWidth(170);	
		
		ok.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				
				ObservableList<String> selectedList = listView.getSelectionModel().getSelectedItems();
				ObservableList<Integer> selectedIndexes = listView.getSelectionModel().getSelectedIndices();
				boolean addSelected = false;
				for(String item: selectedList) {
					if(item.equals("Add")) {
						addSelected=true;
					}
				}
				
				Image image = null;
				
				if(selectedList.size()==1&&!addSelected) {
					String path = "";
					try {
						if(listView.getSelectionModel().getSelectedIndex()<16) {
							image = new Image(getClass().getResource("/" + listView.getSelectionModel().getSelectedItem().toLowerCase() + ".png")
								.toURI().toString());
							path = (getClass().getResource("/" + listView.getSelectionModel().getSelectedItem().toLowerCase() + ".png")
									.toURI().toString()).substring(6);
						}
						else {
							File file = new File("data/"+customLanguages.get(listView.getSelectionModel().getSelectedIndex()-16).getLocation());
							image = new Image(file.toURI().toString());
							path = file.toURI().toString().substring(6);;
						}
					} catch (URISyntaxException e) {

						e.printStackTrace();
					}
					language.clear();
					Language selectedLanguage = new Language();
					selectedLanguage.setName(listView.getSelectionModel().getSelectedItem());
					selectedLanguage.setLocation(path);
					language.add(selectedLanguage);
					
					languageLabel.setStyle("-fx-font-size: 14px;");
					languageLabel.setText(listView.getSelectionModel().getSelectedItem());
				}
				else if(selectedList.size()>1&&!addSelected){
					language.clear();
					languageLabel.setStyle("-fx-font-size: 12px;");
					languageLabel.setText(selectedList.size() + " languages");
					
					
					try {
						image = new Image(getClass().getResource("/coding_lang.png").toURI().toString());
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					for(int i=0; i<selectedList.size(); i++) {
						Language selectedLanguage = new Language();
						selectedLanguage.setName(selectedList.get(i));
						String path = "";
						try {
						if(selectedIndexes.get(i)<16) {
							
							path = (getClass().getResource("/" + selectedList.get(i).toLowerCase() + ".png")
									.getPath().toString());
						}
						else {
							File file = new File("data/"+customLanguages.get(selectedIndexes.get(i)-16).getLocation());
							path = file.getAbsolutePath().toString();
						}
						}catch(Exception e) {
							
						}
						selectedLanguage.setLocation(path);						
						language.add(selectedLanguage);
					}
					
				}
				else if(addSelected) {
					addLanguagePopup(newStage, popup, listView,customLanguages);
				}
				
				if (!addSelected) {
					ImageView imageView = new ImageView();

					imageView.setImage(image);

					imageView.setImage(image);
					imageView.setFitWidth(32);
					imageView.setFitHeight(32);
					languageLabel.setGraphic(imageView);

					newStage.close();
					root.getChildren().remove(root.getChildren().size() - 1);
				}
			}
		});
		
		layout.getChildren().addAll(listView,ok);
		popup.getChildren().addAll(layout);
		popup.setStyle("-fx-background-color: transparent;");

		newStage.initStyle(StageStyle.UNDECORATED);
		newStage.initModality(Modality.APPLICATION_MODAL);
		newStage.setScene(scene);
		moveWindow(popup, newStage);
		
		newStage.setX(primaryStage.getX()+70);
		newStage.setY(primaryStage.getY()+30);
		
		newStage.initStyle(StageStyle.TRANSPARENT);
		scene.setFill(Color.TRANSPARENT);
		newStage.show();

	}
	
	public void addLanguagePopup(Stage primaryStage, BorderPane root, ListView<String> listView, List<Language> customLanguages) {
		
		Rectangle shadow = new Rectangle(0, 0, 600, 1000);
		shadow.setFill(new Color(0, 0, 0, 0.5));
		StackPane shadowStack = new StackPane();
		shadowStack.getChildren().add(shadow);
		root.getChildren().add(shadowStack);
		
		
		VBox layout = new VBox();
		HBox labellayout = new HBox();
		HBox imagelayout = new HBox();
		HBox buttonlayout = new HBox();
		Stage newStage = new Stage();
		BorderPane popup = new BorderPane();
		Scene scene = new Scene(popup, 340, 140);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		Label imageSource = new Label("Image Source: ");
		Label label = new Label("Label: ");
		Label emptyLabel = new Label("   ");
		
		TextField sourceEditText = new TextField();
		TextField labelEditText = new TextField();
		
		ImageView imageView = new ImageView();
		Image image = null;
		try {
			image = new Image(getClass().getResource("/openfile.png")
					.toURI().toString());
		} catch (URISyntaxException e) {

			e.printStackTrace();
		}
		imageView.setImage(image);
		imageView.setFitWidth(26);
		imageView.setFitHeight(26);
		
		
		Button ok = new Button("OK");
		ok.setMinWidth(170);	
		Button cancel = new Button("CANCEL");
		cancel.setMinWidth(170);	
		
		label.setMinWidth(80);
		emptyLabel.setMinWidth(10);
		imageSource.setMinWidth(80);
		labelEditText.setMinWidth(180);
		sourceEditText.setMinWidth(180);
		
		labellayout.setPadding(new Insets(15,0,15,15));
		imagelayout.setPadding(new Insets(0,0,0,15));
		label.setPadding(new Insets(4,0,0,0));
		imageSource.setPadding(new Insets(4,0,0,0));
		
		
		labellayout.getChildren().addAll(label,labelEditText);
		imagelayout.getChildren().addAll(imageSource,sourceEditText,emptyLabel,imageView);
		buttonlayout.getChildren().addAll(ok,cancel);
		
		layout.getChildren().addAll(labellayout,imagelayout,buttonlayout);
		popup.getChildren().addAll(layout);
	//	popup.setStyle("-fx-background-color: transparent;");

		newStage.initStyle(StageStyle.UNDECORATED);
		newStage.initModality(Modality.APPLICATION_MODAL);
		newStage.setScene(scene);
		moveWindow(popup, newStage);
		
		newStage.setX(primaryStage.getX()-90);
		newStage.setY(primaryStage.getY()+120);
		
		
		newStage.initStyle(StageStyle.TRANSPARENT);
		scene.setFill(Color.TRANSPARENT);
		newStage.show();
		
		FileChooser fileChooser = new FileChooser();
		
		cancel.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				newStage.close();
				root.getChildren().remove(root.getChildren().size() - 1);
				
			}
		});
		imageView.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				File file = fileChooser.showOpenDialog(newStage);
                if (file != null) {
                	sourceEditText.setText(file.getAbsolutePath()); 
                }
			}
		});
		ok.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				
				
				File sourceFile = new File(sourceEditText.getText().toString());
				File savefileLocation = new File("data/"+sourceFile.getName());
				try {
					copyFile(sourceFile,savefileLocation);
				} catch (IOException e) {
					e.printStackTrace();
				}
				writeNewLanguageToFile(labelEditText.getText().toString(),sourceFile.getName());
				
				Language newLanguage = new Language();
				newLanguage.setName(labelEditText.getText().toString());
				newLanguage.setLocation(sourceFile.getName());
				customLanguages.add(newLanguage);
				
				listView.getItems().add(listView.getItems().size()-1, labelEditText.getText().toString());
				
				newStage.close();
				root.getChildren().remove(root.getChildren().size() - 1);
			}
		});
	}
	
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    
		File directory = new File("data");
		
		if(!directory.exists()){
			directory.mkdir();
		}
		
		
		if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
	public void writeNewLanguageToFile(String label, String filename) {
		File directory = new File("data");
		
		if(!directory.exists()){
			directory.mkdir();
		}
		File languageFile = new File("data/languages.txt");
		FileWriter fwriter;
		try {
			fwriter = new FileWriter(languageFile,true);
			BufferedWriter writer = new BufferedWriter(fwriter);
			writer.write(label);
			writer.write(" shazam ");
			writer.write(filename);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteLanguageDialog(Stage primaryStage, BorderPane root, ListView<String> listView) {
		Rectangle shadow = new Rectangle(0, 0, 600, 1000);
		shadow.setFill(new Color(0, 0, 0, 0.5));
		StackPane shadowStack = new StackPane();
		shadowStack.getChildren().add(shadow);
		root.getChildren().add(shadowStack);
		
		
		VBox layout = new VBox();
		HBox buttonLayout = new HBox();
		Stage newStage = new Stage();
		BorderPane popup = new BorderPane();
		Scene scene = new Scene(popup, 340, 100);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		
		Label label = new Label("Delete this item?");
		label.setMinWidth(250);	
		Button yes = new Button("YES");
		yes.setMinWidth(170);	
		Button no = new Button("NO");
		no.setMinWidth(170);	
		label.setFont(new Font(16));
	
		label.setPadding(new Insets(10,0,0,110));
	
		
		buttonLayout.getChildren().addAll(yes,no);
		layout.getChildren().addAll(label,buttonLayout);
		popup.getChildren().addAll(layout);
	//	popup.setStyle("-fx-background-color: transparent;");

		newStage.initStyle(StageStyle.UNDECORATED);
		newStage.initModality(Modality.APPLICATION_MODAL);
		newStage.setScene(scene);
		moveWindow(popup, newStage);
		
		newStage.setX(primaryStage.getX()-90);
		newStage.setY(primaryStage.getY()+120);
		
		
		newStage.initStyle(StageStyle.TRANSPARENT);
		scene.setFill(Color.TRANSPARENT);
		newStage.show();
		
		yes.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				deleteLanguageFromFile(listView.getSelectionModel().getSelectedItem().toString());
				listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
				newStage.close();
				root.getChildren().remove(root.getChildren().size() - 1);
				
			}
		});
		
		no.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				newStage.close();
				root.getChildren().remove(root.getChildren().size() - 1);
				
			}
		});
		
	}
	
	public void deleteLanguageFromFile(String label) {
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("data/languages.txt"));
			String line = br.readLine();
		
			
			while (line != null) {
				
				if(line!=null) {
					String[] parts = line.split(" shazam ");
					if(label.equals(parts[0])) {
						deleteLine(line, new File("data/languages.txt").getAbsolutePath());
						
					}
				}
				
					line = br.readLine();
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void deleteLine(String line, String filePath) {

	    File file = new File(filePath);

	    File file2 = new File(file.getParent() + "\\temp" + file.getName());
	    PrintWriter pw = null;
	    Scanner read = null;

	    FileInputStream fis = null;
	    FileOutputStream fos = null;
	    FileChannel src = null;
	    FileChannel dest = null;

	    try {


	        pw = new PrintWriter(file2);
	        read = new Scanner(file);

	        while (read.hasNextLine()) {

	            String currline = read.nextLine();

	            if (line.equalsIgnoreCase(currline)) {
	                continue;
	            } else {
	                pw.println(currline);
	            }
	        }

	        pw.flush();

	        fis = new FileInputStream(file2);
	        src = fis.getChannel();
	        fos = new FileOutputStream(file);
	        dest = fos.getChannel();

	        dest.transferFrom(src, 0, src.size());


	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {     
	        pw.close();
	        read.close();

	        try {
	            fis.close();
	            fos.close();
	            src.close();
	            dest.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        if (file2.delete()) {
	            System.out.println("File is deleted");
	        } else {
	            System.out.println("Error occured! File: " + file2.getName() + " is not deleted!");
	        }
	    }

	}
	
	public List<Language> readLanguageFile() {
		
		List<String> lines = new ArrayList<String>();
		List<Language> sources = new ArrayList<Language>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("data/languages.txt"));

			String line = br.readLine();
			while (line != null) {
				lines.add(line);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(String str : lines) {
			String[] parts = str.split(" shazam ");
			Language lang = new Language();
			lang.setName(parts[0]);
			lang.setLocation(parts[1]);
			sources.add(lang);
		}

		return sources;
	}
}
