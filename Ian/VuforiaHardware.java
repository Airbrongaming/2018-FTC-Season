package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

public class VuforiaHardware {
    Vuforia ocalizer vuforia;
    public VuforiaTrackables targetsRoverRuckus;
    public VuforiaTrackable blueRover;
    public VuforiaTrackable redFootprint;
    public VuforiaTrackable frontCraters;
    public VuforiaTrackable backSpace;
    public List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();

    HardwareMap hwmap = null;

    public void init(HardwareMap ahwmap){
        hwmap = ahwmap;
        int cameraMonitorViewId = hwmap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hwmap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AYOeonr/////AAABmSirnJnvQUnelw5JOBSA3YZQx9wGb22naoaPA/2nWtFxpJiRrDY3NzvoKMTH6zRjy4eYcbHgabgNIwD7OaOLfQM5ZlLV5rmsHwdUkUN1aC8m2nNPlESStk9Ud1pvewjIfQCx1uBqAnRrBQmGFvxnHa6LNbS+eGIVt2/dmTuwUK+WZ5Yn4e0BDO5YlcOiiGEujAmqO+3O1p8a1YM+QHA/Bk7sCnM1hx8pYDT7Qp93jemP3plVOEC3hsEki1xMMBOpp6yip/XR4zX8nFRAT0sZqI7/s50EcuUcXEbPy1Fdv6r0gJZXzsmYm8qA2SLKpinCAd5EvKs6qlaiEFfuFgBplAGW7f6Yg5C1mdjOImQxJhxC";//license key here
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        targetsRoverRuckus = this.vuforia.loadTrackablesFromAsset("RoverRuckus");
        blueRover = targetsRoverRuckus.get(0);
        blueRover.setName("Blue-Rover");
        redFootprint = targetsRoverRuckus.get(1);
        redFootprint.setName("Red-Footprint");
        frontCraters = targetsRoverRuckus.get(2);
        frontCraters.setName("Front-Craters");
        backSpace = targetsRoverRuckus.get(3);
        backSpace.setName("Back-Space");
        allTrackables.addAll(targetsRoverRuckus);
    }
}
