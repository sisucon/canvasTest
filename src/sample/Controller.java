package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Calendar;
import java.util.Random;

public class Controller {
	Point[] Points = new Point[bol];
	Calendar calendar = Calendar.getInstance();
	double mousePosX,mousePosY;
	static int bol = 0;
	static double WIDTH = 1800.0;
	static double HEIGHT = 1000.0;
	static  double Pointrad = 5;
	double MaxAttractionValue = 0.2;
	double attractionLenth = 150;
	@FXML
	private CheckBox mouseAttraction;
	@FXML
	private Group myGroup;
	@FXML
	private Label nowHSB;
	@FXML
	private TextField bolValue,attractionValue,radValue,attractionLenValue;
	MyCanvas myCanvas;
	
	/***
	 * 初始化
	 */
	public void initialize(){
		Random random = new Random();
		for (int i = 0; i < bol; i++) {
			Points[i] = new Point(random.nextDouble()*WIDTH,random.nextDouble()*HEIGHT,Pointrad,Pointrad);
		}
		showAll();
		myCanvas = new MyCanvas(WIDTH,HEIGHT);
		new mythread().start();
	}
	
	public void show() {
		myGroup.getChildren().clear();
		myCanvas.nextPane();
		myGroup.getChildren().add(myCanvas);
	}
	
	@FXML
	public void renew() {
		Platform.runLater(() ->{
			if (bolValue.getText() != null && attractionValue.getText() != null && radValue.getText() != null) {
				bol = Integer.valueOf(bolValue.getText());
				MaxAttractionValue = Double.parseDouble(attractionValue.getText());
				Pointrad = Double.parseDouble(radValue.getText());
				attractionLenth = Double.parseDouble(attractionLenValue.getText());
				Point[] temp = new Point[bol];
				Random random = new Random();
				for (int i = 0; i < bol; i++) {
					temp[i] =  new Point(random.nextDouble()*WIDTH,random.nextDouble()*HEIGHT,Pointrad,Pointrad);
				}
				Points = temp;
			} else {
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setContentText("你输的是个啥??");
				alert.setTitle("WTF??");
				alert.showAndWait();
			}
		});
	}
	
	@FXML
	public void getMousePos_move(MouseEvent mouseEvent) {
		if (mouseAttraction.isSelected()) {
			mousePosX = mouseEvent.getX();
			mousePosY = mouseEvent.getY();
		}
	}
	
	@FXML
	public void getMousePos(MouseEvent mouseEvent) {
		Platform.runLater(() ->{
			bol += 1;
			Point[] temp = new Point[bol];
			for (int i = 0; i < bol-1; i++) {
				temp[i] = Points[i];
			}
			temp[bol-1] = new Point(mouseEvent.getX(),mouseEvent.getY(),Pointrad,Pointrad);
			Points = new Point[bol];
			for (int i = 0; i < bol; i++) {
				Points[i] = temp[i];
			}
		});
	}
	class mythread extends Thread
	{
		@Override
		public void run()
		{
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Platform.runLater(() ->{
				show();
				new mythread().start();
			});
		}
	}
	
	class MyCanvas extends Canvas {
		private GraphicsContext gc;
		public MyCanvas(double width,double height) {
			super(width,height);
			gc = getGraphicsContext2D();
			draw(gc);
		}
		public void nextPane() {
			draw(gc);
		}
		
		public void draw(GraphicsContext gc) {
			    gc.clearRect(0,0,WIDTH,HEIGHT);
				gc.setFill(Color.RED);
			    calendar = Calendar.getInstance();
			    double m = calendar.get(Calendar.MINUTE);
			    double s = calendar.get(Calendar.SECOND);
			    gc.setLineWidth(2);
			gc.setStroke(Color.hsb(((m*60+s)/10)%360, 1, 1));
			gc.strokeLine(0,0,WIDTH,0);
			gc.strokeLine(0,HEIGHT,WIDTH,HEIGHT);
			gc.strokeLine(0,0,0,HEIGHT);
			gc.strokeLine(WIDTH,0,WIDTH,HEIGHT);
			nowHSB.setText(("now hue="+((m*60+s)/10)%360)+"m="+m+"s="+s);
			for (int i = 0; i <bol ; i++) {
					gc.fillOval(Points[i].x,Points[i].y,Points[i].w,Points[i].h);
					for (int j = i+1; j <bol ; j++) {
						double sx = Points[i].x - Points[j].x;
						double sy = Points[i].y - Points[j].y;
						double a =  Math.sqrt(Math.pow(sx, 2) + Math.pow(sy, 2));
						if (a <= attractionLenth) {
							gc.setLineWidth((attractionLenth/a/10)%10);
							gc.strokeLine(Points[i].x+Pointrad/2,Points[i].y+Pointrad/2,Points[j].x+Pointrad/2,Points[j].y+Pointrad/2);
							if (Points[i].x > Points[j].x) {
								Points[i].vx -= ((Math.pow(attractionLenth/2,2))/Math.pow(a,2))%MaxAttractionValue;
								Points[j].vx += ((Math.pow(attractionLenth/2,2))/Math.pow(a,2))%MaxAttractionValue;
							} else {
								Points[i].vx += ((Math.pow(attractionLenth/2,2))/Math.pow(a,2))%MaxAttractionValue;
								Points[j].vx -= ((Math.pow(attractionLenth/2,2))/Math.pow(a,2))%MaxAttractionValue;
							}
							if (Points[i].y > Points[j].y) {
								Points[i].vy -= ((Math.pow(attractionLenth/2,2))/Math.pow(a,2))%MaxAttractionValue;
								Points[j].vy += ((Math.pow(attractionLenth/2,2))/Math.pow(a,2))%MaxAttractionValue;
							} else {
								Points[i].vy += ((Math.pow(attractionLenth/2,2))/Math.pow(a,2))%MaxAttractionValue;
								Points[j].vy -= ((Math.pow(attractionLenth/2,2))/Math.pow(a,2))%MaxAttractionValue;
							}
						}
					}
				if (mouseAttraction.isSelected()) {
						double sx = Points[i].x-mousePosX;
						double sy = Points[i].y-mousePosY;
						double a = Math.sqrt((Math.pow(sx,2)+Math.pow(sy,2)));
					if (a <= 300) {
						gc.setLineWidth((300/a/10)%10);
						gc.strokeLine(Points[i].x+Pointrad/2,Points[i].y+Pointrad/2,mousePosX,mousePosY);
						if (Points[i].x > mousePosX) {
							Points[i].vx -= (5625/Math.pow(a,2))%MaxAttractionValue;
						} else {
							Points[i].vx += (5625/Math.pow(a,2))%MaxAttractionValue;
						}
						if (Points[i].y > mousePosY) {
							Points[i].vy -= (5625/Math.pow(a,2))%MaxAttractionValue;
						} else {
							Points[i].vy += (5625/Math.pow(a,2))%MaxAttractionValue;
						}
					}
				}
					Points[i].getNext();
				}
				gc.restore();
			
		}
	}
	//点类
	class Point
	{
		double x,y,w,h,vx,vy;
		Point(double x,double y,double w,double h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			Random random = new Random();
			vx = random.nextDouble();
			vy = random.nextDouble();
			int flag = random.nextInt();
			if (flag % 2 == 0) {
				vx = -1*vx;
				vy = -1*vy;
			}
		}
		//计算下一帧画面
		void getNext() {
			x += vx;
			y += vy;
			if (x <= 0||y<=0||x>=WIDTH||y>=HEIGHT) {
				if (x <= 0) {
					x = 0;
					vx = -vx/2;
				}
				if (y <= 0) {
					y=0;
					vy = -vy/2;
				}
				if (x >= WIDTH) {
					x = WIDTH;
					vx = -vx/2;
				}
				if (y >= HEIGHT) {
					y = HEIGHT;
					vy = -vy/2;
				}
				if ((x <= 0 && y <= 0) || (x >= WIDTH && y >= HEIGHT)||(x>=WIDTH&&y<=0)||(x<=0&&y>=HEIGHT)) {
					vx = -vx/2;
					vy = -vy/2;
				}
			}
		}
	}
	
	
	
	private void showAll(){
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		HEIGHT = gd.getDisplayMode().getHeight()*0.6;
		WIDTH = width*0.6;
	}


}
