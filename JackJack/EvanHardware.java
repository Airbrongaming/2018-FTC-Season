package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;


public class EvanHardware {
    public DcMotor evanDcMoter1;
    public Servo latchServo;

    public double latchingServoOpen = 1;
    public double latchingServoClosed = 0;
    public int evanUp = 1000;

    HardwareMap hwMap;

    public void init(HardwareMap ahwMap) {
        hwMap = ahwMap;

        evanDcMoter1 = hwMap.get(DcMotor.class, " MoterForEvan");
        evanDcMoter1.setDirection(DcMotor.Direction.FORWARD);

        evanDcMoter1.setPower(0);

        evanDcMoter1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        latchServo = hwMap.get(Servo.class, "servo");
        latchServo.setPosition(latchingServoClosed);


    }
    public void flipRobot(double power,int position){
        evanDcMoter1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        evanDcMoter1.setTargetPosition(position);
        evanDcMoter1.setPower(power);
        while (evanDcMoter1.isBusy()) {
        }
        evanDcMoter1.setPower(0);
        evanDcMoter1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public void latches(boolean makeOpen){
        if(makeOpen = true) {
            latchServo.setPosition(latchingServoOpen);
        }
        else {
            latchServo.setPosition(latchingServoClosed);
        }

    }
}

