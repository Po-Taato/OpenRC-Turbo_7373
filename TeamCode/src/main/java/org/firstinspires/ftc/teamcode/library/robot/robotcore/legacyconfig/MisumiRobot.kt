package org.firstinspires.ftc.teamcode.library.robot.robotcore.legacyconfig

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.hardware.rev.RevBlinkinLedDriver
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.teamcode.library.robot.robotcore.IMUController
import org.firstinspires.ftc.teamcode.library.robot.systems.drive.legacy.Holonomic
import org.firstinspires.ftc.teamcode.library.robot.systems.drive.legacy.OdometryModule
import org.firstinspires.ftc.teamcode.library.robot.systems.drive.roadrunner.HolonomicRR
import org.firstinspires.ftc.teamcode.library.robot.systems.drive.roadrunner.TwoWheelOdometryLocalizer
import org.firstinspires.ftc.teamcode.library.robot.systems.wrappedservos.FoundationGrabbers
import org.firstinspires.ftc.teamcode.library.robot.systems.wrappedservos.AutoBlockIntake
import org.firstinspires.ftc.teamcode.library.robot.systems.wrappedservos.CapstonePlacer
import org.firstinspires.ftc.teamcode.library.robot.systems.wrappedservos.IntakeBlockGrabber

@Deprecated("replaced by ExtMisumiRobot")
open class MisumiRobot(protected val hardwareMap: HardwareMap) {
    // Drivetrain Variables
     @JvmField val frontLeftMotor          : DcMotorEx     = hwInit("frontLeftMotor")
     @JvmField val backLeftMotor           : DcMotorEx     = hwInit("backLeftMotor")
     @JvmField val frontRightMotor         : DcMotorEx     = hwInit("frontRightMotor")
     @JvmField val backRightMotor          : DcMotorEx     = hwInit("backRightMotor")

     @OdometryDevice("left")
     @JvmField val intakeLiftLeft          : DcMotorEx     = hwInit("intakeLiftLeft")
     @JvmField val intakeLiftRight         : DcMotorEx     = hwInit("intakeLiftRight")
     @JvmField val intakePivot             : DcMotorEx     = hwInit("intakePivot")

     @OdometryDevice("rear")
     @JvmField val odometryRearAsMotor     : DcMotorEx     = hwInit("odometryRearAsMotor")

    // Servo/PWM Variables
    @JvmField val foundationGrabFrontLeft : Servo                 = hwInit("foundationGrabFrontLeft")
    @JvmField val foundationGrabFrontRight: Servo                 = hwInit("foundationGrabFrontRight")
    @JvmField val foundationGrabSideFront : Servo                 = hwInit("foundationGrabSideFront")
    @JvmField val foundationGrabSideRear  : Servo                 = hwInit("foundationGrabSideRear")

    @JvmField val autoBlockGrabFront      : Servo                 = hwInit("autoBlockGrabFront")
    @JvmField val autoBlockPivotFront     : Servo                 = hwInit("autoBlockPivotFront")
    @JvmField val autoBlockGrabRear       : Servo                 = hwInit("autoBlockGrabRear")
    @JvmField val autoBlockPivotRear      : Servo                 = hwInit("autoBlockPivotRear")

    @JvmField val intakeBlockGrabberServo : Servo                 = hwInit("intakeBlockGrabber")
    @JvmField val capstonePlacerAsServo   : Servo                 = hwInit("capstonePlacement")
    @JvmField val blinkin                 : RevBlinkinLedDriver   = hwInit("blinkin")

    // Expansion Hub Variables
    @JvmField val expansionhubs           : List<LynxModule>      = hardwareMap.getAll(LynxModule::class.java).apply { forEach {it.bulkCachingMode = LynxModule.BulkCachingMode.AUTO} }

    // IMU Variables
    @JvmField val imuControllerA          : IMUController = IMUController(hardwareMap = hardwareMap, id = 'A')
    @JvmField val imuControllerB          : IMUController = IMUController(hardwareMap = hardwareMap, id = 'B')

//     Robot Systems Variables
//     @JvmField val foundationGrabbersFront : FoundationGrabbers = FoundationGrabbers(foundationGrabFrontLeft, 0.00, 0.49,
//        foundationGrabFrontRight, 0.80, 0.25)

     @JvmField val intakeBlockGrabber      : IntakeBlockGrabber = IntakeBlockGrabber(intakeBlockGrabberServo, 0.00, 0.30, 1.00)

     @JvmField val holonomic               : Holonomic = Holonomic(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor)
     @JvmField val holonomicRR             : HolonomicRR           = HolonomicRR(imuControllerA,
                                                                                 frontLeftMotor, backLeftMotor, backRightMotor, frontRightMotor,
                                                                                 TwoWheelOdometryLocalizer(intakeLiftLeft, odometryRearAsMotor, imuControllerA))

     @JvmField val autoBlockIntakeFront    : AutoBlockIntake = AutoBlockIntake(
             pivotServo = autoBlockPivotFront,  pivot18 = 0.92, pivotMid = 0.79, pivotVertical = 0.89, pivotPickup = 0.52,
             grabberServo = autoBlockGrabFront, grabUp = 0.55, grabMid = 0.62, grabPickup = 0.95)
     @JvmField val autoBlockIntakeRear     : AutoBlockIntake = AutoBlockIntake(
            pivotServo = autoBlockPivotRear,  pivot18 = 0.20, pivotMid = 0.48, pivotVertical = 0.36, pivotPickup = 0.71,
            grabberServo = autoBlockGrabRear, grabUp = 0.55, grabMid = 0.80, grabPickup = 0.99)

     @JvmField val capstonePlacer = CapstonePlacer(capstonePlacerAsServo, pos18 = 0.35, posInside = 0.15, posDeploy = 0.75)

     @JvmField val odometryModuleLeft = OdometryModule(intakeLiftLeft as DcMotor)
     @JvmField val odometryModuleRear = OdometryModule(odometryRearAsMotor as DcMotor)


    protected inline fun <reified T> hwInit(name:String): T = hardwareMap.get(T::class.java, name)
}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class OdometryDevice(val name:String)
