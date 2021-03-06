package org.firstinspires.ftc.teamcode;
import java.lang.Math;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name="Basic: Iterative OpMode", group="Iterative Opmode")
public class driveTrainOpMode extends OpMode {
    driveTrainHardwareMapArrays robot = new driveTrainHardwareMapArrays();
    boolean tankControls = false;
    int start = 0;

    @Override
    public void init(){
        robot.init(hardwareMap);
    }
    @Override
    //to start the the robot
    public void start() {
    }

    @Override
    // it adds the movement fuction to the start of the robot planes (driver control).
    public void loop () {
        double Rx = gamepad1.right_stick_x;
        double Ry = gamepad1.right_stick_y;
        double Ly = gamepad1.left_stick_y;
        if (tankControls) {
            robot.tankcontrolsMovent(Ry, Ly);

        } else {
            robot.FPSmovementByControl(Rx, Ly);
        }

    }
}
