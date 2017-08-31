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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.fasterxml.jackson.databind.ObjectMapper;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;


public class Main extends Application {

	public static boolean run = true;
	private boolean recording = false;
	private boolean paused = false;
	private long elapsed = 0;
	private long allCharCount = 0;
	private ImageView playView;
	private ImageView stopView;
	private MenuItem recordItem;
	private TimerTask task;
	private double xOffset = 0;
	private double yOffset = 0;
	private int[] charCount = new int[128];
	private String userName ="";
	private String language="Java";
	private String project="None";

	
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
		
		Button submit = new Button("Login");
		submit.setMinWidth(130);	
		
		CheckBox rememberMeCheckBox = new CheckBox("Remember me");
		
		Hyperlink createAcountLink = new Hyperlink("Click here");
		createAcountLink.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				getHostServices().showDocument("http://localhost:8080/Programondo/registration");
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
			}
		});
		
		TextFlow flow = new TextFlow(
			    new Text("Don't have an account? "), createAcountLink
			);
		flow.setTextAlignment(TextAlignment.CENTER);
		
		checkboxWraper.getChildren().add(rememberMeCheckBox);
		submitWraper.getChildren().addAll(submit,flow);
		loginLayout.getChildren().addAll(systemIcons, topLogo);
		formLayout.getChildren().addAll(topLabelWraper,login,loginText,password,passwordText,checkboxWraper,submitWraper);
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
		
		File rememberMeFile = new File("mycode_remember_me.txt");
		try {
			FileReader reader = new FileReader(rememberMeFile);
			BufferedReader breader = new BufferedReader(reader);
			
			boolean remember =  Boolean.valueOf(breader.readLine());
			String username = breader.readLine();
			String password = breader.readLine();
			
			breader.close();
			System.out.println(remember);
			System.out.println(username);
			System.out.println(password);
			if(remember&&checkUser(username,password,true,false)){
				userName = username;
				loginStage.close();
				Stage primaryStage = new Stage();
				showMainApp(primaryStage);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	public void saveRememberMe(String login, String password){
		File rememberMeFile = new File("mycode_remember_me.txt");
		FileWriter fwriter;
		try {
			fwriter = new FileWriter(rememberMeFile);
			BufferedWriter writer = new BufferedWriter(fwriter);
			writer.write("true");
			writer.newLine();
			writer.write(login);
			writer.newLine();
			writer.write(password);
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
		JSONArray arr = null;
		try {
			URIBuilder builder;
			builder = new URIBuilder("http://localhost:8080/Programondo/checkuser");
			builder.setParameter("login", username).setParameter("password", password).setParameter("remember", String.valueOf(rememberMe));
			HttpGet httpget = new HttpGet(builder.build());
			httpget.setConfig(requestConfig);
			HttpResponse response = httpClient.execute(httpget);
			HttpEntity responseEntity = response.getEntity();
			    if(responseEntity!=null) {
			        myresponse = EntityUtils.toString(responseEntity);
			        JSONParser parser = new JSONParser();
			        
			        try {
						arr = (JSONArray) parser.parse(myresponse);
					} catch (ParseException e) {
						e.printStackTrace();
					}
			        System.out.println(myresponse);
			        System.out.println(arr);
			    }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		if(isRememberMeChecked){
			saveRememberMe(username, arr.get(1).toString());
		}
		
		return Boolean.valueOf(arr.get(0).toString());
	}
	
	public void showMainApp(Stage primaryStage){
		try {
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

			Label selectLanguageLabel = createLabel("Java:",languageLabelView,140,new Insets(0,0,0,20));
			selectLanguageLabel.setStyle(" -fx-font: 15px TimeNewRoman;");
			

			ComboBox projectComboBox = new ComboBox();
			projectComboBox.getItems().addAll("None", "Project1");
			projectComboBox.setMinWidth(100);
			projectComboBox.setValue("None");
			projectComboBox.valueProperty().addListener(new ChangeListener<String>() {
		        @Override public void changed(ObservableValue  composant, String oldValue, String newValue) {
		            project=newValue;
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
					language="Java";
					project="None";
					recording = false;
					paused = false;
					elapsed = 0;
					allCharCount = 0;
					charCount = new int[128];
					
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
		
		File rememberFile = new File("mycode_remember_me.txt");
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
					charCount[event.getVirtualKeyCode()-1]++;
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

		play.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				Image pauseImage = null;
				Image stopImage = null;

				if (recording == false && paused == false) {
					elapsed = 0;
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
		HttpClient httpClient = new DefaultHttpClient();

		try {
		    HttpPost request = new HttpPost("http://localhost:8080/Programondo/addlog?username="+userName);
		    CodingLog cl = new CodingLog();
		    
		    List<Integer> charCountInteger = new ArrayList<>();
		    for(int number:charCount){
		    	charCountInteger.add(number);
		    }
		    
		    cl.setCharCount(charCountInteger);
		    cl.setCharSum((int)allCharCount);
		    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String time = sdf.format(new Date(elapsed));
		    
		    cl.setDuration(time);
		    cl.setLanguage(language);
		    cl.setProject(project);
		    cl.setCreationDate(new Date());

	        ObjectMapper mapper = new ObjectMapper();
	        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cl);
	        
		    StringEntity params =new StringEntity(json);
		    System.out.println(json);
		    request.addHeader("content-type", "application/json");
		    request.addHeader("Accept","application/json");
		    request.setEntity(params);
		    HttpResponse response = httpClient.execute(request);
		    HttpEntity responseEntity = response.getEntity();
		    if(responseEntity!=null) {
		        String myresponse = EntityUtils.toString(responseEntity);
		        System.out.println(myresponse);
		    }
		}catch (Exception ex) {
		} finally {
		    httpClient.getConnectionManager().shutdown();
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
		Scene scene = new Scene(popup, 170, 380);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		List<String> languages = Arrays.asList("Java", "C", "C++", "C#", "Objective-C",	"Delphi", "GO", "Javascript", "Perl", "R", "Ruby", "Python", "PHP", "Visual Basic", "Swift", "Scratch");
	
		
		ListView<String> listView = new ListView<String>();
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
					try {
						Image image = new Image(getClass().getResource("/" + name.toLowerCase() + ".png").toURI().toString());
						imageView.setImage(image);
					} catch (Exception w) {
						w.printStackTrace();
					}
					setText(name);
					language = name;
					setGraphic(imageView);
					listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent click) {

							if (click.getClickCount() == 2) {

								languageLabel.setText(listView.getSelectionModel().getSelectedItem());
								Image image = null;
								try {
									image = new Image(getClass().getResource("/" + listView.getSelectionModel().getSelectedItem().toLowerCase() + ".png")
											.toURI().toString());
								} catch (URISyntaxException e) {

									e.printStackTrace();
								}
								imageView.setImage(image);
								imageView.setFitWidth(32);
								imageView.setFitHeight(32);
								languageLabel.setGraphic(imageView);

								newStage.close();
								root.getChildren().remove(root.getChildren().size() - 1);
							}
						}
					});
				}
			}
		});
		layout.getChildren().add(listView);
		popup.getChildren().addAll(layout);
		popup.setStyle("-fx-background-image: url('gplaypattern.png');");

		newStage.initStyle(StageStyle.UNDECORATED);
		newStage.initModality(Modality.APPLICATION_MODAL);
		newStage.setScene(scene);
		moveWindow(popup, newStage);
		
		newStage.setX(primaryStage.getX()+70);
		newStage.setY(primaryStage.getY()+50);
		newStage.show();

	}
}
