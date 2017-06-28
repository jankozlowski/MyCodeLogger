package application;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;
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

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Main extends Application {

	public static boolean run = true;
	private boolean recording = false;
	private boolean paused = false;
	private long elapsed = 0;
	private long charCount = 0;
	private ImageView playView;
	private ImageView stopView;
	private MenuItem recordItem;
	private TimerTask task;
	private double xOffset = 0;
	private double yOffset = 0;

	@Override

	public void start(Stage primaryStage) {
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

			languageProjectBox.getChildren().addAll(selectLanguageLabel, projectComboBox);
			languageProjectBox.setPadding(new Insets(10, 20, 5, 20));
			languageProjectBox.setAlignment(Pos.CENTER);

			HBox lineBox3 = createBlueLine(new Insets(15, 15, 5, 15));

			HBox playStopBox = new HBox();
			Image playImage = new Image(getClass().getResource("/play.png").toURI().toString());
			playView = createImageWithScale("/play.png", 1, 1);
			stopView = createImageWithScale("/stopgrey.png", 1, 1);
			playStopBox.setPadding(new Insets(20, 20, 20, 50));
			playStopBox.getChildren().addAll(playView, stopView);

			VBox layout = new VBox();
			layout.getChildren().addAll(systemIcons, topLogo, timeLabel, timeCounter, lineBox, LOCLabel, LOCCounter,
					lineBox2, labelsLanguageBox, languageProjectBox, lineBox3, playStopBox);

			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 300, 500);
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
					charCount++;
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
										charLabel.setText(charCount + " characters");
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

					} catch (URISyntaxException e) {
						e.printStackTrace();
					}

				}
			}
		});

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
		Scene scene = new Scene(popup, 170, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		List<String> languages = Arrays.asList("Java", "C", "C++", "C#", "Objective-C",	"Delphi", "GO", "Javascript", "Perl", "R", "Ruby", "Python", "PHP", "Visual Basic", "Swift", "Scratch");
	
		
		ListView<String> listView = new ListView<String>();
		ObservableList<String> items = FXCollections.observableArrayList(languages);
		listView.setItems(items);
		listView.setMinSize(170, 400);

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
