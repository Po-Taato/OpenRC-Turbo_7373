package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.library.functions.AllianceColor;
import org.firstinspires.ftc.teamcode.library.functions.ExtDirMusicPlayer;
import org.firstinspires.ftc.teamcode.library.functions.FieldSide;
import org.firstinspires.ftc.teamcode.library.functions.MathExtensionsKt;
import org.firstinspires.ftc.teamcode.library.functions.Point3D;
import org.firstinspires.ftc.teamcode.library.functions.Position;
import org.firstinspires.ftc.teamcode.library.robot.robotcore.BasicRobot;
import org.firstinspires.ftc.teamcode.library.robot.robotcore.IMUController;
import org.firstinspires.ftc.teamcode.library.vision.skystone.VisionInitializer;
import org.firstinspires.ftc.teamcode.library.vision.skystone.VuforiaController;

import static org.firstinspires.ftc.teamcode.library.functions.MathExtensionsKt.cmToIn;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous", group = "Main")
public class Autonomous extends LinearOpMode {
    BasicRobot robot;
    IMUController imuController;
    boolean goingRight = false;
    public static int TIMEMS = 1500;

    @Override
    public void runOpMode() throws InterruptedException {

        /*
                Initialize main autonomous variables
         */
        robot = new BasicRobot(hardwareMap);
        imuController = new IMUController(hardwareMap, AxesOrder.ZYX);
        AutoMenuControllerIterative menuController = new AutoMenuControllerIterative(telemetry);
        robot.intakeBlockGrabber.release();

        /*
                Operate telemetry menu
         */
        while (!isStarted() && !isStopRequested()) {
            if (gamepad1.dpad_up) {
                menuController.menu.previousItem();
                while (gamepad1.dpad_up && !isStopRequested()) ;
            } else if (gamepad1.dpad_down) {
                menuController.menu.nextItem();
                while (gamepad1.dpad_down && !isStopRequested()) ;
            } else if (gamepad1.dpad_left) {
                menuController.menu.iterateBackward();
                while (gamepad1.dpad_left && !isStopRequested()) ;
            } else if (gamepad1.dpad_right) {
                menuController.menu.iterateForward();
                while (gamepad1.dpad_right && !isStopRequested()) ;
            }
        }

        waitForStart();
        double startLeftDist = robot.leftDistanceSensor.getDistance(DistanceUnit.INCH);
        if (!isStopRequested()) {
        /*
                RoboSpotify
         */
            ExtDirMusicPlayer player = new ExtDirMusicPlayer(menuController.getMusicFile(), true);
            player.play();

            PIDFCoefficients pidf = ((DcMotorEx) robot.frontLeftMotor).getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION);
            telemetry.addData("p", pidf.p);
            telemetry.addData("i", pidf.i);
            telemetry.addData("d", pidf.d);
            telemetry.addData("f", pidf.f);
            telemetry.update();
//            sleep(5000);
        /*
                Robot Actions
         */


            if (menuController.getParkOnly()) {
                sleep(menuController.getDelayBeforeParking() * 1000);
                if (!isStopRequested()) {
                    if (menuController.getStartingPosition() == FieldSide.WAFFLE_SIDE) {
                        if (menuController.getAllianceColor() == AllianceColor.RED)
                            drive(36, 0, 0.5);
                        else drive(-36, 0, 0.5);
                    }

                    if (menuController.getStartingPosition() == FieldSide.LOADING_ZONE) {
                        if (menuController.getAllianceColor() == AllianceColor.RED)
                            drive(0, -20, 0.5);
                        else drive(0, 20, 0.5);
                    }
                }

            } else {
                if (menuController.getStartingPosition() == FieldSide.WAFFLE_SIDE) {
                    if (menuController.getAllianceColor() == AllianceColor.RED) {
                        if (menuController.getBuildingSiteSlide()) drive(-24, 0, 0.8);
                        // Drive forward to clear the wall
                        //                drive(0, 5, 0.7);
                        //                sleep(500);
                        //                // Rotate 180 degrees using IMU PI controller
                        //                imuPIRotate(180);

                        // Find distance away from the wall (REMOVED)
//                        double distWall = 42 - robot.distanceSensor_side.getDistance(DistanceUnit.INCH);

                        // Drive to the foundation
                        drive(15 - robot.leftDistanceSensor.getDistance(DistanceUnit.INCH), -29, 0.4);
                        sleep(250);

                        // Deploy the foundation grabber, grabbing the foundation
                        robot.foundationGrabbers.lock();
                        sleep(1000);

                        // Drive back to the wall
                        drive(0, 32, 0.7);
//                        timeDrive(0, 0.5, 0, 2000);
                        // Release the foundation grabbers
                        robot.foundationGrabbers.unlock();
                        sleep(500);


                        if (menuController.getParkAfterTask()) {
//                            // Drive toward the alliance bridge to start moving around the foundation
//                            drive(30, 0, 0.2);
//                            // Drive parallel to the bridges to move to the other side of the foundation
//                            drive(0, -18, 0.2);
//                            drive(-10, 0, 0.2);
//
//                            drive(60, 14, .7);
//
//                            imuPIRotate(90);
//                            telemetry.addData("heading", MathExtensionsKt.toDegrees(imuController.getHeading()));
//                            telemetry.update();
//                            sleep(2000);

//                            // Park under the bridge
//                            drive(23, 0, 0.6);
//                            if (menuController.getParkNearDS()) timeDrive(0,0.3, 0, 1000);
//                            else timeDrive(0, -0.3, 0, 500);

                            drive(35, 0, 0.2);
                            // Drive parallel to the bridges to move to the other side of the foundation
                            if (menuController.getFoundationRedundancy()) {
                                drive(0, -17, 0.2);
                                //push foundation
                                drive(-20, 0, 0.2);
                                sleep(1000);
                                // drive back
                                drive(26, 0, 0.2);
                            } else {
                                drive(16, 0, 0.5);
                                drive(0, -8, 0.4);
                            }
                            if (menuController.getParkNearDS()) drive(0, 24, 0.2);
                            else {
                                timeDrive(0, -0.4, 0, 500);
                                sleep(500);
                                robot.holonomic.stop();
                            }
                        }

                    } else { // Blue side waffle
                        double startingRuntime = getRuntime();
                        if (menuController.getBuildingSiteSlide()) drive(24, 0, 0.7);
                        // Drive forward to clear the wall
                        //                drive(0, 5, 0.4);
                        //                sleep(500);
                        // Find distance away from the wall
                        //                double distWall = 44 - robot.distanceSensor_side.getDistance(DistanceUnit.INCH);

                        // Drive to the foundation
                        drive(0, -29, 0.4);
                        sleep(250);
                        telemetry.addData("blab blab blab", "wow taco");
                        telemetry.update();
                        // Deploy the foundation grabber, grabbing the foundation
                        robot.foundationGrabbers.lock();
                        sleep(2000);

                        // Drive back to the wall
//                        drive(0, 36, 0.2);
                        timeDrive(0, 0.5, 0, 2000);

                        // Release the foundation grabbers
                        robot.foundationGrabbers.unlock();
                        sleep(500);
                        if (menuController.getParkAfterTask()) {
                            // Drive toward the alliance bridge to start moving around the foundation
                            drive(-35, 0, 0.2);
                            // Drive parallel to the bridges to move to the other side of the foundation
                            if (menuController.getFoundationRedundancy()) {
                                drive(0, -17, 0.2);
                                //push foundation
                                drive(20, 0, 0.2);
                                sleep(1000);
                                // drive back
                                drive(-26, 0, 0.2);
                            } else {
                                drive(-12, 0, 0.5);
                                while (getRuntime() - startingRuntime < menuController.getDelayBeforeParking());
                            }
                            if (menuController.getParkNearDS()) drive(0, 24, 0.2);
                            else {
                                timeDrive(0, -0.4, 0, 500);
                                sleep(500);
                                robot.holonomic.stop();
                            }
                        }

                    }
                }
                else if (menuController.getStartingPosition() == FieldSide.LOADING_ZONE) {
                    VuforiaLocalizer vuforia = VisionInitializer.createVuforia(VisionInitializer.CameraType.PHONE_REAR, hardwareMap);
                    VuforiaController vuforiaController = new VuforiaController(vuforia, telemetry);
                    vuforiaController.activate();

                    // Drive closer to the stone to see it more reliably
                    drive(15, 0, .2);
                    sleep(1000);
                    Point3D vuforiaTargetPoint = vuforiaController.analyzeVuforiaResult();
                    for (int i = 0; i < 5 & vuforiaTargetPoint == null; i++) {
                        vuforiaTargetPoint = vuforiaController.analyzeVuforiaResult();
                        sleep(500);
                    }

                    Position skystonePosition = Position.RIGHT;

                    if (vuforiaTargetPoint != null) {
                        telemetry.addData("Skystone y", vuforiaTargetPoint.y);
                        if (vuforiaTargetPoint.y < 1.0) skystonePosition = Position.LEFT;
                        else skystonePosition = Position.CENTER;
                    }

                    telemetry.addData("Skystone position", skystonePosition);
                    telemetry.update();
                    double driveToSkystoneDist;
                    if (menuController.getAllianceColor() == AllianceColor.RED) {
                        switch (skystonePosition) {
                            case CENTER:
                                driveToSkystoneDist = 0.0;
                                break;
                            case RIGHT:
                                driveToSkystoneDist = -7.0;
                                break;
                            default:
                                driveToSkystoneDist = 10.5;
                                break;
                        }
                    } else {
                        switch (skystonePosition) {
                            case CENTER:
                                driveToSkystoneDist = 0.75;
                                break;
                            case RIGHT:
                                driveToSkystoneDist = -6.0;
                                break;
                            default:
                                driveToSkystoneDist = 10.5;
                                break;
                        }
                    }

                    // Drive forwards or back to align with
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            doArmLift(1.257);
                        }
                    }).start();
                    drive(0, driveToSkystoneDist, 0.2);
                    sleep(2000);
                    // Rotate to face Skystone
                    imuPIRotate(-88.0);

                    // PI controller for lifting arm


                    /*
                    IMPORTANT!!! (AKA REALLY IMPORTANT!!!)
                    Check location of these blocks in case below y value needs to be increased/decreased!!!
                     */
                    double targetValue = 33.0;
                    double currentValue;
                    double P = 0.04;
                    double timeAtLastChange = getRuntime();
                    double lastValue = 100;
                    while (opModeIsActive() && ((currentValue = robot.frontDistanceSensor.getDistance(DistanceUnit.CM)) > targetValue) && (menuController.getSkystoneRedundancy() ? ((getRuntime() - timeAtLastChange) < 1.5) : true)) {
                        robot.holonomic.runWithoutEncoder(0, MathExtensionsKt.upperLimit(P * (currentValue - targetValue), 0.15), 0);
                        telemetry.addData("Target", targetValue);
                        telemetry.addData("Current", currentValue);
                        telemetry.addData("Last Time", timeAtLastChange);
                        telemetry.addData("Last Change", lastValue);
                        telemetry.update();
                        if (currentValue != lastValue) {
                            lastValue = currentValue;
                            timeAtLastChange = getRuntime();
                        }
                    }
                    robot.holonomic.stop();
                    //                drive(0, 16, 0.2);

                    while (opModeIsActive() && robot.intakePivotPotentiometer.getVoltage() < 1.68)
                        robot.intakePivotMotor.setPower(0.01);
                    robot.intakePivotMotor.setPower(0.12);
                    drive(0, 5, 0.2);
                    robot.intakeBlockGrabber.hold();
                    robot.intakeBlockManipulator.setPower(1);
                    robot.intakePivotMotor.setPower(0.0);
                    sleep(1500);
                    drive(0, -14, 0.4);
//                    doArmLift();
                    double intoBuildingZoneDist = 0.0;
                    if (menuController.getAllianceColor() == AllianceColor.RED) {
                        switch (skystonePosition) {
                            case LEFT:
                                intoBuildingZoneDist += 8;
                            case CENTER:
                                intoBuildingZoneDist += 8;
                            case RIGHT:
                                intoBuildingZoneDist += 44;
                        }
                    } else {
                        switch (skystonePosition) {
                            case RIGHT:
                                intoBuildingZoneDist -= 8;
                            case CENTER:
                                intoBuildingZoneDist -= 8;
                            case LEFT:
                                intoBuildingZoneDist -= 44;

                        }
                    }
                    drive(intoBuildingZoneDist, 0, 0.2);
                    doArmLift(1.257);
                    robot.intakeBlockGrabber.release();
                    robot.intakeBlockManipulator.setPower(-1);
                    sleep(500);
                    robot.intakePivotMotor.setPower(0.0);


                    if (menuController.getAllianceColor() == AllianceColor.RED) drive(-20, 0, 0.2);
                    else drive(20, 0, 0.2);
                    robot.intakePivotMotor.setPower(0.0);
                    if (menuController.getParkNearDS()) timeDrive(0, -0.7, 0, 750);
                    else timeDrive(0, 0.4, 0, 750);
                }
                else { // FIELD POSITION IS LOADING ZONE!!!

                    double xDistance = 0.0;

                    System.out.println("BEEEEEEEEN");
                    // Lift arm
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            doArmLift(1.68);
                        }
                    }).start();

                    drive(0, 28, .4);
                    if (menuController.getAllianceColor() == AllianceColor.RED) {
                        drive(-25, 0, .4);
                        timeDrive(-0.4, 0, 0, 750);
//                        drive(-5, 0, .2);
                    } else {
                        drive(29, 0, .4);
//                        timeDrive(0.4, 0, 0, 750);.
//                        drive(5, 0, .2);
                    }
//                    timeDrive(-0.5, 0, 0, TIMEMS);

                    drive(0, 6.5, .3);

                    Position skystonePosition = Position.RIGHT;
                    double leftRed;
                    double leftGreen;
                    double leftBlue;
                    double leftBlue15;
                    double leftRedGreenAvg;
                    double rightRed;
                    double rightGreen;
                    double rightBlue;
                    double rightBlue15;
                    double rightRedGreenAvg;
                    boolean leftIsStone = ((double) (leftRedGreenAvg = (((leftRed = robot.leftColorSensor.red()) + (leftGreen = robot.leftColorSensor.green())) / 2)) > (leftBlue15 = 1.5 * (leftBlue = robot.leftColorSensor.blue())));
                    boolean rightIsStone = ((double) (rightRedGreenAvg = (((rightRed = robot.rightColorSensor.red()) + (rightGreen = robot.rightColorSensor.green())) / 2)) > (rightBlue15 = 1.5 * (rightBlue = robot.rightColorSensor.blue())));


                    if (leftIsStone & !rightIsStone) {

                        if (menuController.getAllianceColor() == AllianceColor.RED) {
                            skystonePosition = Position.CENTER;
                            drive(4.80, 0, 0.4);

                            // TODO set driving distance, should be positive
                            xDistance = 160;
                        } else {
                            skystonePosition = Position.RIGHT;
                            drive(-11.5, 0, 0.4);

                            // TODO set driving distance, should be negative
                            xDistance = -112;
                        }

                    } else if (!leftIsStone & rightIsStone) {

                        if (menuController.getAllianceColor() == AllianceColor.RED) {
                            skystonePosition = Position.LEFT;
                            drive(21, 3, 0.4);

                            // TODO set driving distance, should be positive
                            xDistance = 120;

                        } else {
                            skystonePosition = Position.CENTER;
                            drive(-4, 3, 0.4);

                            // TODO set driving distance, should be negative
                            xDistance = -120;
                        }

                    } else {

                        if (menuController.getAllianceColor() == AllianceColor.RED) {
                            skystonePosition = Position.RIGHT;
                            drive(12, 0, 0.4);

                            // TODO set driving distance, should be positive
                            xDistance = 115;
                        } else {
                            skystonePosition = Position.LEFT;
                            drive(-11.5, 3, 0.4);

                            // TODO set driving distance, should be negative
                            xDistance = -112;
                        }

                    }

                    System.out.println("leftIsStone" + leftIsStone);
                    System.out.println("leftRed" + leftRed);
                    System.out.println("leftGreen" + leftGreen);
                    System.out.println("leftBlue" + leftBlue);
                    System.out.println("leftBlue 1.5" + leftBlue15);
                    System.out.println("leftRG avg" + leftRedGreenAvg);
                    telemetry.addLine();
                    System.out.println("rightIsStone" + rightIsStone);
                    System.out.println("rightRed" + rightRed);
                    System.out.println("rightGreen" + rightGreen);
                    System.out.println("rightBlue" + rightBlue);
                    System.out.println("rightBlue 1.5" + rightBlue15);
                    System.out.println("rightRG avg" + rightRedGreenAvg);
                    System.out.println("Skystone position" + skystonePosition);
                    telemetry.update();

                    sleep(1000);

                    drive(0, 5, 0.6);
                    robot.intakeBlockGrabber.hold();
                    sleep(500);
                    robot.intakeBlockManipulator.setPower(1);
                    sleep(250);
                    drive(0, -11, .4);
//                    drive(95,0, 0.7);
//                    timeDrive(0.6, 0, 0, 500);
//                    {
//                        double p1 = 0.012;
//                        double i1 = 0.005;
//                        double d1 = 0.0;
//                        double c1 = 100.0;
//                        double t1 = cmToIn(39.0);
//                        double errorSum = 0;
//
//
//                        double p2 = 1.0;
//                        double i2 = 0.0;
//                        double d2 = 0.0;
//                        double t2 = imuController.getHeading();
//
//                        double startingRuntime = getRuntime();
//                        double lastXError;
//                        double errorDeriv;
//                        boolean invalidate = false;
//                        boolean invalidated = false;
//                        double e1;
//                        if (menuController.getAllianceColor() == AllianceColor.RED) lastXError = cmToIn(robot.rightDistanceSensor.cmUltrasonic()) -t1;
//                        else lastXError = cmToIn(robot.leftDistanceSensor.cmUltrasonic()) - t1;
//
//                        while (((c1 > t1  | Double.isNaN(c1) | getRuntime()-startingRuntime<1.6  | (invalidate=((Math.abs(errorDeriv=(e1=c1-t1)) > 25)))) & opModeIsActive())) {
//                            if (menuController.getAllianceColor() == AllianceColor.RED) c1 = cmToIn(robot.rightDistanceSensor.cmUltrasonic());
//                            else c1 = cmToIn(robot.leftDistanceSensor.cmUltrasonic());
//
//                            e1 = c1 - t1;
//
//                            System.out.println("\n---------E1 = "+e1);
//                            System.out.println("---------------C1 = "+c1);
//                            System.out.println("---------------LXE = "+lastXError);
//                            System.out.println("---------------abs e1-lxe="+Math.abs(e1-lastXError));
//                            System.out.println("---------------abs e1-lxe CAUGHT="+(Math.abs(e1-lastXError)>25));
//                            System.out.println("---------------invalidate= "+invalidate);
//                            System.out.println("starting runtime: "+startingRuntime + "    current: "+getRuntime()+ "   error: " + (getRuntime()-startingRuntime));
//                            double e2 = imuController.getHeading() - t2;
//                            if (e1 <= 30) {
//                                errorSum += e1;
//                            }
//                            if (menuController.getAllianceColor()==AllianceColor.RED)
//                                robot.holonomic.runWithoutEncoder((p1 * e1 + i1 * errorSum), 0, p2 * e2);
//                            else
//                                robot.holonomic.runWithoutEncoder(-(p1 * e1 + i1 * errorSum), 0, p2 * e2);
//
//                            telemetry.addData("x target", t1);
//                            telemetry.addData("x current", c1);
//                            telemetry.addData("x error", e1);
//                            telemetry.addData("heading target", t2);
//                            telemetry.addData("heading error", e2);
//                            telemetry.update();
//                            lastXError = e1;
//                        }
//                        System.out.println("\n---------E1 = "+e1);
//                        System.out.println("---------------C1 = "+c1);
//                        System.out.println("---------------LXE = "+lastXError);
//                        System.out.println("---------------abs e1-lxe="+Math.abs(e1-lastXError));
//                        System.out.println("---------------abs e1-lxe CAUGHT="+(Math.abs(e1-lastXError)>25));
//                        System.out.println("starting runtime: "+startingRuntime + "    current: "+getRuntime()+ "   error: " + (getRuntime()-startingRuntime));
//                        System.out.println("---------------FINAL C1"+c1);
//                    }

                    {
                        double p1 = 0.0007;
                        double i1 = 0.005;
                        double d1 = 0.0;
                        double c1 = 100.0;
                        double t1 = findAvgXDist(xDistance);
                        double errorSum = 0;


                        double p2 = 1.5;
                        double i2 = 0.0;
                        double d2 = 0.0;
                        double t2 = imuController.getHeading();

                        double startingRuntime = getRuntime();

                        double e1;
                        robot.holonomic.setMotorsMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        robot.holonomic.setMotorsMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        while (((c1 > t1 | Double.isNaN(c1) | getRuntime() - startingRuntime < 1.6)) & opModeIsActive()) {
                            c1 = avgReadEncoderDistances();

                            e1 = c1 - t1;

                            System.out.println("\n---------E1 = " + e1);
                            System.out.println("---------------C1 = " + c1);
//                            System.out.println("---------------LXE = "+lastXError);
//                            System.out.println("---------------abs e1-lxe="+Math.abs(e1-lastXError));
//                            System.out.println("---------------abs e1-lxe CAUGHT="+(Math.abs(e1-lastXError)>25));
//                            System.out.println("---------------invalidate= "+invalidate);
                            System.out.println("starting runtime: " + startingRuntime + "    current: " + getRuntime() + "   error: " + (getRuntime() - startingRuntime));
                            double e2 = imuController.getHeading() - t2;
                            if (e1 <= 30) {
                                errorSum += e1;
                            }
                            if (menuController.getAllianceColor() == AllianceColor.RED)
                                robot.holonomic.runWithoutEncoder(-(p1 * e1 + i1 * errorSum), 0, p2 * e2);
                            else
                                robot.holonomic.runWithoutEncoder(-(p1 * e1 + i1 * errorSum), 0, p2 * e2);

                            telemetry.addData("x target", t1);
                            telemetry.addData("x current", c1);
                            telemetry.addData("x error", e1);
                            telemetry.addData("heading target", t2);
                            telemetry.addData("heading error", e2);
                            telemetry.update();
//                            lastXError = e1;
                        }
//                        System.out.println("\n---------E1 = "+e1);
//                        System.out.println("---------------C1 = "+c1);
//                        System.out.println("---------------LXE = "+lastXError);
//                        System.out.println("---------------abs e1-lxe="+Math.abs(e1-lastXError));
//                        System.out.println("---------------abs e1-lxe CAUGHT="+(Math.abs(e1-lastXError)>25));
//                        System.out.println("starting runtime: "+startingRuntime + "    current: "+getRuntime()+ "   error: " + (getRuntime()-startingRuntime));
//                        System.out.println("---------------FINAL C1"+c1);
                    }

//                    drive(xDistance, 4, 0.5);

                    robot.holonomic.stop();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            doArmLift(1.237);
                        }
                    }).start();

//                    drive(0, 12, .5);
                    sleep(500);
                    double distLeft;
                    double distRight;
                    while (opModeIsActive() & ((distLeft = robot.leftColorDistanceSensor.getDistance(DistanceUnit.CM)) > 10 | (distRight = robot.rightColorDistanceSensor.getDistance(DistanceUnit.CM)) > 10 | Double.isNaN(distRight) | Double.isNaN(distLeft))) {
                        robot.holonomic.runWithoutEncoder(0, 0.3, 0);
                        telemetry.addData("dist left", distLeft);
                        telemetry.addData("dist right", distRight);
                        telemetry.update();
                    }
                    robot.holonomic.stop();
                    robot.intakeBlockGrabber.release();
                    robot.intakeBlockManipulator.setPower(-1.0);
                    sleep(1000);
                    robot.intakeBlockManipulator.setPower(0.0);
                    drive(0, -8, 0.5);
                    double temp = robot.frontDistanceSensor.getDistance(DistanceUnit.INCH);
                    turn(180, 0.5);
                    robot.foundationGrabbers.setPosition(0.43);
                    sleep(250);
                    drive(0, -temp * .9333 + .4216, 0.5);
                    robot.foundationGrabbers.lock();
                    sleep(750);
                    robot.intakePivotMotor.setPower(0);
//                    drive(0, 35, 0.8);
//                    drive(0, 5, .3);
                    timeDrive(0, 0.5, 0, 2000);

//                        timeDrive(0, 0.5, 0, 2000);
                    // Release the foundation grabbers
                    robot.foundationGrabbers.unlock();
                    sleep(1000);
                    // Drive toward the alliance bridge to start moving around the foundation

                    if (menuController.getAllianceColor() == AllianceColor.RED) {
                        drive(35, 0, 0.2);
                        // Drive parallel to the bridges to move to the other side of the foundation
                        drive(0, -18, 0.2);
                        if (menuController.getFoundationRedundancy()) {
                            drive(-20, 0, 0.2);
                            drive(35, -5, .7);
                        } else {
                            drive(28, -7, .7);
                        }

                    } else {
                        drive(-35, 0, 0.2);
                        // Drive parallel to the bridges to move to the other side of the foundation
                        drive(0, -18, 0.2);
                        if (menuController.getFoundationRedundancy()) {
                            drive(20, 0, 0.2);
                            drive(-35, -5, .7);
                        } else {
                            drive(-28, -7, .7);
                        }
                    }


                    if (menuController.getParkNearDS()) drive(0, 24, 0.2);
                    else {
                        timeDrive(0, -0.4, 0, 500);
                        sleep(500);
                        robot.holonomic.stop();
                    }
                }
            }
        /*
                End of OpMode - close resources
         */
            player.stop();
        }
    }

    public void imuPIRotate(double angle) {
        double currentValue = MathExtensionsKt.toDegrees(imuController.getHeading());
        double targetValue = currentValue + angle;

        double Kp = .02; // Proportional Constant
        double Ki = .0007; // Integral Constant
        double et; // Error
        double proportionPower;
        double integralPower;
        double power;
        double errorSum = 0;
        double originalRuntime = getRuntime();
        while (currentValue != targetValue && opModeIsActive() && (getRuntime() - originalRuntime) < 4) {
            currentValue = MathExtensionsKt.toDegrees(imuController.getHeading());
            telemetry.addData("Current value", currentValue);
            telemetry.addData("Target value", targetValue);

            if (currentValue < 0) {
                et = -(Math.abs(targetValue) - Math.abs(currentValue));
            } else {
                et = targetValue - currentValue;
            }


            if (Kp * et > .8) {
                proportionPower = .8;
            } else {
                proportionPower = Kp * et;
            }

            if (Math.abs(et) < 45) {
                errorSum += et;
            }

            integralPower = Ki * errorSum;

            power = -(proportionPower + integralPower);
            telemetry.addData("et", et);
            telemetry.addData("propPw", proportionPower);
            telemetry.addData("intPw", integralPower);
            telemetry.addData("errorsum", errorSum);
            telemetry.addData("Power", power);
            robot.holonomic.runWithoutEncoder(0, 0, power * 0.30);
            telemetry.update();
        }
        robot.holonomic.stop();
    }

    private void doArmLift(double target) {
        double currentValue = 3.0;
        double targetValue = target;
        double PPower = 3.5;
        double originalRuntime = getRuntime();
        while ((currentValue = robot.intakePivotPotentiometer.getVoltage()) > targetValue && opModeIsActive() && (getRuntime() - originalRuntime) < 1) {
            robot.intakePivotMotor.setPower(-PPower * (targetValue - robot.intakePivotPotentiometer.getVoltage()));
            telemetry.addData("Current", currentValue);
            telemetry.update();
        }
        robot.intakePivotMotor.setPower(0.07);
    }

    private void timeDrive(double x, double y, double z, long timeMs) {
        robot.holonomic.runWithoutEncoder(x, y, z);
        sleep(timeMs);
        robot.holonomic.stop();
    }

    private void turn(double degrees, double power) {
        robot.holonomic.turnUsingEncoder(180, 0.5);
        double originalRuntime = getRuntime();
        while (opModeIsActive() && robot.holonomic.motorsAreBusy() && getRuntime() - originalRuntime < 2)
            ;
    }

    private void drive(double x, double y, double power) {
        robot.holonomic.runUsingEncoder(x, y, power);
        double originalRuntime = getRuntime();
        while (opModeIsActive() && robot.holonomic.motorsAreBusy() && getRuntime() - originalRuntime < 3) {

        }
    }

    private double findAvgXDist(double xTarget) {
        double WHEEL_DIAMETER = 4.0;
        double WHEEL_CIRCUMFERENCE = (WHEEL_DIAMETER * Math.PI);
        double TICKS_PER_REVOLUTION = 450.0;
        double TICKS_PER_INCH /* 134.4*/ = TICKS_PER_REVOLUTION / WHEEL_CIRCUMFERENCE;

        double r;
        double theta;
        double axisConversionAngle = Math.PI / 4;
        double xPrime;
        double yPrime;
        double xPower;
        double yPower;

        double LFDistanceIN;
        double LRDistanceIN;
        double RRDistanceIN;
        double RFDistanceIN;
        double LFPower;
        double LRPower;
        double RRPower;
        double RFPower;

        // calculate r
        r = Math.sqrt(Math.pow(xTarget, 2) + Math.pow(0, 2));
        // calculate theta
        if (xTarget == 0) xTarget = 0.00001;
        theta = Math.atan(0 / xTarget);
        if (xTarget < 0) theta += Math.PI;
        // calculate x and y prime
        xPrime = r * Math.cos(theta - axisConversionAngle);
        yPrime = r * Math.sin(theta - axisConversionAngle);

        double result = ((Math.abs(xPrime*TICKS_PER_INCH) + Math.abs(yPrime*TICKS_PER_INCH)) / 2);
        if (xTarget < 0) {
            goingRight = false;
            result *= -1;
        } else {
            goingRight = true;
        }
        return result;
    }

    private double avgReadEncoderDistances() {
//        LFDistanceIN = xPrime;
//        LRDistanceIN = yPrime;
//        RRDistanceIN = -xPrime;
//        RFDistanceIN = -yPrime;
        double flmcp =  Math.abs(robot.frontLeftMotor.getCurrentPosition());
        double brmcp =  Math.abs(robot.backRightMotor.getCurrentPosition());
        double frmcp =  Math.abs(robot.frontRightMotor.getCurrentPosition());
        double blmcp =  Math.abs(robot.backLeftMotor.getCurrentPosition());

        telemetry.addData("flm cp", flmcp);
        telemetry.addData("brm cp", brmcp);
        telemetry.addData("frm cp", frmcp);
        telemetry.addData("blm cp", blmcp);
        double sum = flmcp + brmcp + frmcp + blmcp;
        double avg = sum/4;

        telemetry.addData("sum", sum);
        telemetry.addData("avg", avg);

        return goingRight?avg:-avg;
//        return robot.frontLeftMotor.getCurrentPosition();

    }


}