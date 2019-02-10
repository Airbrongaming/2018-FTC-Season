package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "evanteleop")
public class EvanTeleop extends LinearOpMode {
    EvanHardware robot = new EvanHardware();
    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        waitForStart();
        while (opModeIsActive()) {
            double lefty = gamepad1.left_stick_y;
            boolean buttonA = gamepad1.a;
            boolean buttonB = gamepad1.b;
            if (lefty > 0.06 ) {
                robot.evanDcMoter1.setPower(lefty);
            }
            else if (lefty < -0.06) {
                robot.evanDcMoter1.setPower(lefty);
            }
            else {
                robot.evanDcMoter1.setPower(0);
            }
            if (buttonA) {
                robot.latches(true);
            }
            if (buttonB) {
                robot.latches(false);
            }
            telemetry.addData("Encoders",robot.evanDcMoter1.getCurrentPosition());
            telemetry.update();
        }
        robot.latchServo.setPosition(robot.latchingServoOpen);
    }
}
