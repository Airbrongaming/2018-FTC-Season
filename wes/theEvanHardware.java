/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */
public class theEvanHardware
{
    /* Public OpMode members. */
    public DcMotor theEvan = null;
   // public Servo mrKrabs = null;

    /* local OpMode members. */
    HardwareMap hwMap           =  null;

    //these peramiters will have to be changed
    //double krabsOpenPos = 1;
    //double krabsClosedPos = 0;
    //boolean krabsOpen = false;

    int theEvanMax = 1000;
    double powerThresh = 0.06;

    /* Constructor */
    public theEvanHardware(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        theEvan = hwMap.get(DcMotor.class, "the_evan");
        theEvan.setDirection(DcMotor.Direction.FORWARD);
        theEvan.setPower(0);
        theEvan.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

//        mrKrabs = hwMap.get(Servo.class,"mrkrabs");
//        mrKrabs.setPosition(0.5);

    }

//    public void toggleClaw() {
//       if (!krabsOpen){
//           mrKrabs.setPosition(krabsOpenPos);
//           krabsOpen = true;
//       }
//       else{
//           mrKrabs.setPosition(krabsClosedPos);
//           krabsOpen = false;
//       }
//
//    }

    public void moveTheEvan(double power) {
        int encoderCount = theEvan.getCurrentPosition();
        if (power < powerThresh){
        }
        else if (power > 0 && encoderCount >= theEvanMax){
        }
        else if (power < 0 && encoderCount <= theEvanMax){
        }
        else {
            theEvan.setPower(power);
        }
    }

 }

