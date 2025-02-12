package robot;

import robot.subsystems.*;

public class Operator {
    public static double ARM_TOP_ROCKET_BALL;
    public static double ARM_MID_ROCKET_BALL;
    public static double ARM_BOT_ROCKET_BALL;

    public static double WRIST_TOP_ROCKET_BALL;
    public static double WRIST_MID_ROCKET_BALL;
    public static double WRIST_BOT_ROCKET_BALL;
    
    public static double ARM_MID_ROCKET_HATCH;
    public static double ARM_LOW_HATCH;
    
    public static double WRIST_MID_ROCKET_HATCH;
    public static double WRIST_LOW_HATCH;
    
    public static double ARM_CARGO_BALL;
    
    public static double WRIST_CARGO_BALL;
    
    public static double ARM_GND_HATCH;
    public static double WRIST_GND_HATCH;
    
    public static double ARM_STARTUP;
    public static double WRIST_STARTUP;
    
    public static double WHEELS_SPEED_IN = 0.7;
    public static double WHEELS_SPEED_OUT = -0.7;

    // Range of arm and wrist
    public static double WRIST_LOW_RANGE;
    public static double WRIST_HIGH_RANGE;

    public static double ARM_LOW_RANGE;
    public static double ARM_HIGH_RANGE;

    // Tolerance for limiting override range
    public static double OVERRIDE_TOLERANCE = 2;
    Controller op;
    Arm arm;
    HatchMechanism hatch;
    Wheels wheels;
    Wrist wrist;

    public Operator(int port) {
        op = new Controller(port);
        arm = new Arm();
        hatch = new HatchMechanism();
        wheels = new Wheels();
        wrist = new Wrist();

        ARM_STARTUP = getArmAngle();
        WRIST_STARTUP = getWristAngle();

        ARM_TOP_ROCKET_BALL = ARM_STARTUP + 989;
        ARM_MID_ROCKET_BALL = ARM_STARTUP + 420;
        ARM_BOT_ROCKET_BALL = ARM_STARTUP + 200;

        ARM_MID_ROCKET_HATCH = ARM_STARTUP + 389;
        ARM_LOW_HATCH = ARM_STARTUP + 200;

        ARM_CARGO_BALL = ARM_STARTUP + 765;
        ARM_GND_HATCH = ARM_STARTUP + 313;
        
        ARM_LOW_RANGE = ARM_STARTUP;
        ARM_HIGH_RANGE = ARM_TOP_ROCKET_BALL;

        WRIST_TOP_ROCKET_BALL = WRIST_STARTUP - 1548;
        WRIST_MID_ROCKET_BALL = WRIST_STARTUP - 816;
        WRIST_BOT_ROCKET_BALL = WRIST_STARTUP - 630;

        WRIST_MID_ROCKET_HATCH = WRIST_STARTUP - 450;
        WRIST_LOW_HATCH = WRIST_STARTUP - 65;

        WRIST_CARGO_BALL = WRIST_STARTUP - 1782;
        WRIST_GND_HATCH = WRIST_STARTUP - 1645;

        WRIST_LOW_RANGE = WRIST_CARGO_BALL;
        WRIST_HIGH_RANGE = WRIST_STARTUP;
    }

    public void runOpControls() {
        wheelControl();
        armWristOverride();
        armWristControl();
        hatchControl();
        System.out.println(wrist.pidOutput());
        System.out.println("position "+ arm.getPot().get());
        System.out.println("set Pos "+ ARM_LOW_HATCH);
    }

    public void resetPID() {
        arm.reset();
        wrist.reset();
    }

    public double getWristAngle() {
        return wrist.getPot().get();
    }

    private void wheelControl() {
        if (getWristAngle() > (WRIST_STARTUP - OVERRIDE_TOLERANCE)) {
            System.out.println("Wheels cannot spin as the wrist is too close to the arm");
            wheels.stopWheels();
        }

        if (op.getRightBumper()) {
            wheels.runWheels(WHEELS_SPEED_IN);
        } else if (op.getLeftBumper()) {
            wheels.runWheels(WHEELS_SPEED_OUT);
        } else if (op.getLeftBumper()) {
            wheels.runWheels(WHEELS_SPEED_OUT);
        }
    }

    private void armWristOverride() {
        if (op.getRightStickButton()) {
            if ((op.getRightYAxis() < 0) && (getWristAngle() < (WRIST_HIGH_RANGE - OVERRIDE_TOLERANCE))) {
                wrist.override(-op.getRightYAxis() / 2);
            } else if ((op.getRightYAxis() > 0) && (getWristAngle() > (WRIST_LOW_RANGE + OVERRIDE_TOLERANCE))) {
                wrist.override(-op.getRightYAxis() / 2);
            } else {
                wrist.stopWrist();
            }
        } else {
            if (!wrist.isPIDEnabled()) {
                wrist.stopWrist();
            }
        }

        // Arm override controlled by left stick
        if (op.getLeftStickButton()) {
            if (((op.getLeftYAxis() > 0) && (getArmAngle() < (ARM_HIGH_RANGE - OVERRIDE_TOLERANCE)))) {
                arm.override(-op.getLeftYAxis());
            } else if ((op.getLeftYAxis() < 0) && (getArmAngle() > (ARM_LOW_RANGE + OVERRIDE_TOLERANCE))) {
                arm.override(-op.getLeftYAxis());
            } else {
                arm.stopArm();
            }
        } else {
            if (!arm.isPIDEnabled()) {
                arm.stopArm();
            }
        }
    }

    private void armWristControl() {
        if (op.getOButton()) {
            arm.setPosition(ARM_MID_ROCKET_HATCH);
            wrist.setPosition(WRIST_MID_ROCKET_HATCH);
        }
        else if (op.getXButton()) {
            arm.setPosition(ARM_BOT_ROCKET_HATCH);
            wrist.setPosition(WRIST_BOT_ROCKET_HATCH);
        }
        else if (op.getSquareButton()) {
            arm.setPosition(ARM_CARGO_HATCH);
            wrist.setPosition(WRIST_CARGO_HATCH);
        }
        else if (op.getDPadUp()) {
            arm.setPosition(ARM_TOP_ROCKET_BALL);
            wrist.setPosition(WRIST_TOP_ROCKET_BALL);
        }
        else if (op.getDPadLeft()) {
            arm.setPosition(ARM_MID_ROCKET_BALL);
            wrist.setPosition(WRIST_MID_ROCKET_BALL);
        }
        else if (op.getDPadDown()) {
            arm.setPosition(ARM_BOT_ROCKET_BALL);
            wrist.setPosition(WRIST_BOT_ROCKET_BALL);
        }
        else if (op.getDPadRight()) {
            arm.setPosition(ARM_CARGO_BALL);
            wrist.setPosition(WRIST_CARGO_BALL);
        }
    }

    private void hatchControl() {
        if (op.getTriangleButton()) {
            hatch.place();
        } else {
            hatch.retract();
        }
    }
  
    public Controller getController() {
        return op;
    }
}
