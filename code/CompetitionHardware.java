package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.CameraDevice;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.ArrayList;
import java.util.List;


public class CompetitionHardware {

    // Hardware Variables
    DcMotor[] motors = new DcMotor[4];
    public DcMotor theEvan;
    public Servo markerMover;
    IntakeHardware intake = new IntakeHardware();
    VuforiaHardware cam = new VuforiaHardware();
    public Servo phoneServo;



    //Gyro Variables
    public BNO055IMU imu;
    public Orientation angles = new Orientation();
    public Acceleration gravity = new Acceleration();
    public int correctHeading = 1; // 1: First Angle; 2: Second Angle; 3: Third Angle

    // Drivetrain coolios variables
    double thresh  = 0.06;
    double encCountsPerRev = 28 * 19.2 * 84 / 100; // electrical * internal * externaly
    double wheelRadius = 2.25;
    double wheelCircumference = 2 * Math.PI * wheelRadius;
    double countsPerInch = encCountsPerRev / wheelCircumference;
    double robotLength = 14.5;
    double robotWidth = 17.5;
    double robotDiameter = Math.sqrt(Math.pow(robotLength,2)+Math.pow(robotWidth,2));
    double robotCircumference = Math.PI*robotDiameter;

    // Measured variables in inches
    double distanceToSamples = 16.35; // was 26.5
    double distanceFromDepotToCrater = 78;
    double distanceToDepot = 49;
    double distanceToAvoidMineral = 46;

    // The Evan variables
    int theEvanMax = -4408; // 4408

    // Team Marker variables
    double storePos = 0.44;
    double ejectPos = 0.09;
    boolean tm_isEjected = false;

    // Phone Servo Variables
    double phoneOutPos = 0.1;
    double phoneMidPos = 0.2;
    double phoneInPos = 0.4;

    // Vuforia Variables
    int location = -1;
    boolean targetSeen = false;
    boolean cameraStatus = false;
    int goldPos = -1;

    // Vuforia Objects
    VuforiaLocalizer vuforia;
    public VuforiaTrackables targetsRoverRuckus;
    public VuforiaTrackable blueRover;
    public VuforiaTrackable redFootprint;
    public VuforiaTrackable frontCraters;
    public VuforiaTrackable backSpace;
    public List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();

    public TFObjectDetector tfod;
    public static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    public static final String LABEL_GOLD_MINERAL = "Gold Mineral"; // these were private
    public static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    public ElapsedTime runtime = new ElapsedTime();

    // Hardware Map Variables
    HardwareMap hwmap = null;

    // Thread Objects
    LocationThread lt;
    DriveThread dt;
    EvanThread et;
    MarkerThread mt;

    // function vars that can't work within function
    boolean min;

    public CompetitionHardware(boolean cameraStatus){
        this.cameraStatus = cameraStatus;
    }//Constructor

    public void init(HardwareMap ahwmap){
        hwmap = ahwmap;
        intake.init(hwmap);

        for (int i = 0; i < motors.length; i++){
            motors[i] = hwmap.get(DcMotor.class, "motor" + i);
            if (i % 2 == 0) { // even
                motors[i].setDirection(DcMotor.Direction.REVERSE);
            } else {
                motors[i].setDirection(DcMotor.Direction.FORWARD);
            }
            motors[i].setPower(0);
            motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        if (cameraStatus) {
            // vuforia targets
            cam.init(hwmap);
        }

        // The Evan init
        theEvan = hwmap.get(DcMotor.class, "lift");
        theEvan.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        theEvan.setDirection(DcMotorSimple.Direction.FORWARD);
        theEvan.setPower(0);
        theEvan.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        theEvan.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Marker Deployer init
        markerMover = hwmap.get(Servo.class, "marker");
        markerMover.setPosition(storePos);

        // Phone Servo init
        phoneServo = hwmap.get(Servo.class, "phone_servo");
        phoneServo.setPosition(phoneMidPos);

        // Initialize threads just in case
        //createLocationThread();
        //createDriveThread(0,0);
        createMarkerThread();
        createEvanThread(0);
    }

    public void imuInit(HardwareMap ahwmap) {
        hwmap = ahwmap;
        BNO055IMU.Parameters imuParameters = new BNO055IMU.Parameters();

        imu = hwmap.get(BNO055IMU.class, "imu");
        imuParameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        imuParameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imuParameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        imuParameters.loggingEnabled      = true;
        imuParameters.loggingTag          = "IMU";
        imuParameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu.initialize(imuParameters);
    }

    public float getHeading() {
        if (correctHeading == 1) {
            return angles.firstAngle;
        } else if (correctHeading == 2) {
            return angles.secondAngle;
        } else { // Using third angle by default
            return angles.thirdAngle;
        }
    }

    public void tankcontrolsMovent (double gamepad1Ry, double gamepad1Ly, boolean turbo){
        if (gamepad1Ly > thresh || gamepad1Ly < thresh) {
            if (turbo) {
                motors[0].setPower(-gamepad1Ly);
                motors[2].setPower(-gamepad1Ly);
            }
            else{
                motors[0].setPower(-gamepad1Ly/2);
                motors[2].setPower(-gamepad1Ly/2);
            }
        } else {
            motors[0].setPower(0);
            motors[2].setPower(0);
        }
        if (gamepad1Ry > thresh || gamepad1Ry < thresh) {
            if (turbo) {
                motors[1].setPower(-gamepad1Ry);
                motors[3].setPower(-gamepad1Ry);
            }
            else{
                motors[1].setPower(-gamepad1Ry/2);
                motors[3].setPower(-gamepad1Ry/2);
            }

        } else {
            motors[1].setPower(0);
            motors[3].setPower(0);
        }

    }

    public void FPSmovementByControl ( double gamepad1SpeedND, double gamepad1T, boolean turbo){

        if (Math.abs(gamepad1T) > Math.abs(gamepad1SpeedND)) {
            if (gamepad1T > thresh) {
                if(turbo) {
                    motors[0].setPower(-gamepad1T);
                    motors[1].setPower(-gamepad1T);
                    motors[2].setPower(-gamepad1T);
                    motors[3].setPower(-gamepad1T);
                }
                else {
                    motors[0].setPower(-gamepad1T/2);
                    motors[1].setPower(-gamepad1T/2);
                    motors[2].setPower(-gamepad1T/2);
                    motors[3].setPower(-gamepad1T/2);
                }
            }
            else if (gamepad1T < -thresh) {
                if(turbo) {
                    motors[0].setPower(-gamepad1T);
                    motors[1].setPower(-gamepad1T);
                    motors[2].setPower(-gamepad1T);
                    motors[3].setPower(-gamepad1T);
                }
                else {
                    motors[0].setPower(-gamepad1T/2);
                    motors[1].setPower(-gamepad1T/2);
                    motors[2].setPower(-gamepad1T/2);
                    motors[3].setPower(-gamepad1T/2);
                }

            } else {
                motors[0].setPower(0);
                motors[1].setPower(0);
                motors[2].setPower(0);
                motors[3].setPower(0);
            }

        } else {
            //saying if gamepad1SpeedND is greater than power that is 0.06 than move Motors[i] in a postive way.
            if (gamepad1SpeedND > thresh) {
                if(turbo) {
                    motors[0].setPower(gamepad1SpeedND);
                    motors[1].setPower(-gamepad1SpeedND);
                    motors[2].setPower(gamepad1SpeedND);
                    motors[3].setPower(-gamepad1SpeedND);
                }
                else {
                    motors[0].setPower(gamepad1SpeedND/2);
                    motors[1].setPower(-gamepad1SpeedND/2);
                    motors[2].setPower(gamepad1SpeedND/2);
                    motors[3].setPower(-gamepad1SpeedND/2);
                }
            }
            //saying if gamepad1SpeedND is lessthan than rewop that is -0.06 than move Motors[i] in a negative way.
            else if (gamepad1SpeedND < -thresh) {
                if(turbo) {
                    motors[0].setPower(gamepad1SpeedND);
                    motors[1].setPower(-gamepad1SpeedND);
                    motors[2].setPower(gamepad1SpeedND);
                    motors[3].setPower(-gamepad1SpeedND);
                }
                else{
                    motors[0].setPower(gamepad1SpeedND/2);
                    motors[1].setPower(-gamepad1SpeedND/2);
                    motors[2].setPower(gamepad1SpeedND/2);
                    motors[3].setPower(-gamepad1SpeedND/2);
                }
            }
            //saying if the Motor[i] is not doing anything than have the power of the motor to 0.
            else {
                motors[0].setPower(0);
                motors[1].setPower(0);
                motors[2].setPower(0);
                motors[3].setPower(0);

            }
        }
    }

    public void createDriveThread(double power ,double inches){
        dt = new DriveThread(power,inches);
    }

    public void createRotateThread(double power, double degrees){
        dt = new DriveThread(power,degrees,true);
    }

    public void createLocationThread() {
        lt = new LocationThread();
    }

    public void createMarkerThread() {
        mt = new MarkerThread();
    }

    public void createEvanThread(double power) {
        et = new EvanThread(power);
    }

    public class DriveThread extends Thread {
        double power;
        double inchesOrDegress;
        int rotation = -1;

        public DriveThread(double power, double inches) {
            this.power = power;
            this.inchesOrDegress = inches;
            this.rotation = 0;
        }

        public DriveThread(double power, double degress, boolean rotation) {
            this.inchesOrDegress = degress;
            this.power = power;
            this.rotation = 1;
        }

        public void run() {
            if (this.rotation == 0) {
                this.driveInInches(this.power, this.inchesOrDegress, false);
            } else if (this.rotation == 1) {
                this.rotateInDegrees(this.power, this.inchesOrDegress);
            }
        }

        public void driveInInches (double power,double inches,boolean rotation) {
            boolean busy = true;
            double rev = inches / wheelCircumference;
            int counts = (int)(rev * encCountsPerRev);
            int sign = 1;

            if (rotation) {
                for (int i = 0; i < motors.length; i++) {
                    if (power > 0) {
                        sign = -1;
                    } else {
                        sign = 1;
                    }
                    int currentPosition = motors[i].getCurrentPosition();
                    motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    motors[i].setTargetPosition(currentPosition + sign * counts);
                    motors[i].setPower(Math.abs(power)*sign);
                }
            } else {
                for (int i = 0; i < motors.length; i++) {
                    int even = i%2;
                    if (even == 0){
                        if (power > 0) {
                            sign = -1;
                        } else {
                            sign = 1;
                        }
                    }
                    else {
                        if (power > 0) {
                            sign = 1;
                        } else {
                            sign = -1;
                        }
                    }
                    int currentPosition = motors[i].getCurrentPosition();
                    motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    motors[i].setTargetPosition(currentPosition + sign * counts);
                    motors[i].setPower(sign*Math.abs(power));
                }
            }

            while (busy) {
                for (int i = 0; i < motors.length; i++) {
                    busy = busy && motors[i].isBusy();
                }
            }

            for (int i = 0; i < motors.length; i++) {
                motors[i].setPower(0);
                motors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }

        }

        public void rotateInDegrees(double power,double degrees){
            driveInInches(power,degrees/360*robotCircumference,true);
        }
    }

    public class LocationThread extends Thread {

        public LocationThread() {

        }

        public void run(){
            try{
                determineLoc();
            }
            catch(Exception e) {
                location = -100;
            }
        }

        public int determineLoc() {
            if (targetSeen) {
                return location;
            }
            targetsRoverRuckus.activate();
            while (!targetSeen) {
                for (VuforiaTrackable trackable : allTrackables) {
                    if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                        if (trackable.getName() == "Front-Craters") { // South West
                            location = 0;
                        }
                        if (trackable.getName() == "Red-Footprint") { // South East
                            location = 1;
                        }
                        if (trackable.getName() == "Blue-Rover") { // North West
                            location = 2;
                        }
                        if (trackable.getName() == "Back-Space") { // North East
                            location = 3;
                        }
                        targetSeen = true;
                    }
                    if (targetSeen) {
                        break;
                    }
                }
            }
            targetsRoverRuckus.deactivate();
            CameraDevice.getInstance().setFlashTorchMode(false);

            return location;
        }
    }

    public class EvanThread extends Thread{
        double power;
        int encoderCount = theEvan.getCurrentPosition();
        public EvanThread(double power){
            this.power = power;
        }
        public void run(){
            try{
                moveTheEvan(this.power);
            }
            catch (Exception e){
                // sumting wong
            }
        }
        public void moveTheEvan(double power) {
            theEvan.setPower(power);
            while(true) {
                encoderCount = theEvan.getCurrentPosition();
                if (Math.abs(power) < thresh) {break;}
                else if (power < 0 && encoderCount <= theEvanMax) {break;}
                else if (power > 0 && encoderCount >= 0) {break;}
            }
            theEvan.setPower(0);
        }
    }

    public class MarkerThread extends Thread {
        public MarkerThread() {

        }
        public void run() {
            toggleMarker();
        }
        public void toggleMarker(){
            if (tm_isEjected){
                markerMover.setPosition(storePos);
                tm_isEjected = false;
            }
            else{
                markerMover.setPosition(ejectPos);
                tm_isEjected = true;
            }
        }
    }

    public void resetLoc(){
        targetSeen = false;
        location = -1;
    }

    // TODO: THIS IS NOT USED. IF YOU WANNA TEST SAMPLING THE MINERALS, THEN PUT THIS SOMEWHERE IN AUTO
    public void scanPhone() {
        if (phoneServo.getPosition() <= phoneOutPos){
            min = true;
        }
        else if (phoneServo.getPosition() >= phoneInPos){
            min = false;
        }
       if (min){
           phoneServo.setPosition(phoneServo.getPosition() +0.05);
       }
       else{
           phoneServo.setPosition(phoneServo.getPosition() -0.05);
       }
    }
    // TODO: Implement this, Brandon
    public void findGold() {
        // -1: Can't find Gold; 0: Left; 1: Middle; 2: Right
        if (cam.tfod != null) {
            cam.tfod.activate();
        }
        // getUpdatedRecognitions() will return null if no new information is available since
        // the last time that call was made.
        assert cam.tfod != null;
        List<Recognition> updatedRecognitions = cam.tfod.getUpdatedRecognitions();
        if (updatedRecognitions != null) {

            if (updatedRecognitions.size() == 3) {
                int goldMineralX = -1;
                int silverMineral1X = -1;
                int silverMineral2X = -1;
                for (Recognition recognition : updatedRecognitions) {
                    if (recognition.getLabel().equals(cam.LABEL_GOLD_MINERAL)) {
                        goldMineralX = (int) recognition.getLeft();
                    } else if (silverMineral1X == -1) {
                        silverMineral1X = (int) recognition.getLeft();
                    } else {
                        silverMineral2X = (int) recognition.getLeft();
                    }
                }
                if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                    if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                        // Middle
                        goldPos = 0;
                    } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                        //Right
                        goldPos = 2;
                    } else {
                        // Middle
                        goldPos = 1;
                    }
                }
            }
        }
    }

}