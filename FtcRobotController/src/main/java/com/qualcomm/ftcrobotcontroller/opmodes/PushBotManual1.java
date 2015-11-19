package com.qualcomm.ftcrobotcontroller.opmodes;

//------------------------------------------------------------------------------
//
// PushBotManual
//

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

/**
 * Provide a basic manual operational mode that uses the left and right
 * drive motors, left arm motor, servo motors and gamepad input from only one
 * gamepad for the Push Bot.
 *
 * @author SSI Robotics
 * @version 2015-09-05-20-12
 */
public class PushBotManual1 extends PushBotTelemetry

{
    private DcMotor v_motor_left_arm;

    //--------------------------------------------------------------------------
    //
    // PushBotManual1
    //
    /**
     * Construct the class.
     *
     * The system calls this member when the class is instantiated.
     */
    public PushBotManual1 ()

    {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotManual1
public void init ()
{
    super.init ();
    //
    // Connect the arm motor.
    //
    try
    {
        v_motor_left_arm = hardwareMap.dcMotor.get ("left_arm");
    }
    catch (Exception p_exeception)
    {
        m_warning_message ("left_arm");
        DbgLog.msg(p_exeception.getLocalizedMessage());

        v_motor_left_arm = null;
    }

    m_hand_position(0.0);

}
    //--------------------------------------------------------------------------
    //
    // loop
    //
    /**
     * Implement a state machine that controls the robot during
     * manual-operation.  The state machine uses gamepad input to transition
     * between states.
     *
     * The system calls this member repeatedly while the OpMode is running.
     */
    @Override public void loop ()

    {
        //----------------------------------------------------------------------
        //
        // DC Motors
        //
        // Obtain the current values of the joystick controllers.
        //
        // Note that x and y equal -1 when the joystick is pushed all of the way
        // forward (i.e. away from the human holder's body).
        //
        // The clip method guarantees the value never exceeds the range +-1.
        //
        // The DC motors are scaled to make it easier to control them at slower
        // speeds.
        //
        // The setPower methods write the motor power values to the DcMotor
        // class, but the power levels aren't applied until this method ends.
        //

        //
        // Manage the drive wheel motors.
        //
        float l_gp1_left_stick_y = -gamepad1.left_stick_y;
        float l_left_drive_power
            = (float)scale_motor_power (l_gp1_left_stick_y);

        float l_gp1_right_stick_y = -gamepad1.right_stick_y;
        float l_right_drive_power
            = (float)scale_motor_power (l_gp1_right_stick_y);

        set_drive_power (l_left_drive_power, l_right_drive_power);

        //
        // Manage the arm motor.  The right trigger makes the arm move from the
        // front of the robot to the back (i.e. up).  The left trigger makes the
        // arm move from the back to the front (i.e. down).
        //
        run_using_left_arm_encoder();
        float l_left_arm_power
            = (float)scale_arm_power (gamepad2.right_trigger)
            - (float)scale_arm_power (gamepad2.left_trigger);
        m_left_arm_power (l_left_arm_power);

        //----------------------------------------------------------------------
        //
        // Servo Motors
        //
        // Obtain the current values of the gamepad 'x' and 'b' buttons.
        //
        // Note that x and b buttons have boolean values of true and false.
        //
        // The clip method guarantees the value never exceeds the allowable
        // range of [0,1].
        //
        // The setPosition methods write the motor power values to the Servo
        // class, but the positions aren't applied until this method ends.
        //
        if (gamepad2.x) {
            m_hand_position(a_hand_position() + 0.05);
        }
        else if (gamepad2.b)
        {
            m_hand_position (a_hand_position () - 0.05);
        }

        //
        // Send telemetry data to the driver station.
        //
        update_telemetry (); // Update common telemetry
        update_gamepad_telemetry();
        telemetry.addData
                ("12"
                        , "Left Arm1: " + l_left_arm_power
                );
        telemetry.addData("32", "L Encoder " +a_left_arm_encoder_count());

    } // loop

    //--------------------------------------------------------------------------
    //
    // scale_motor_power
    //
    /**
     * Scale the joystick input using a nonlinear algorithm.
     */
    float scale_arm_power (float p_power)
    {
        //
        // Assume no scaling.
        //
        float l_scale = 0.0f;

        //
        // Ensure the values are legal.
        //
        float l_power = Range.clip(p_power, -1, 1);


        float[] l_array =
                { 0.00f, 0.025f, 0.045f, 0.05f, 0.06f
                        , 0.08f, 0.09f, 0.12f, 0.15f, 0.16f
                        , 0.18f, 0.40f, 0.60f, 0.72f, 0.85f
                        , 1.00f, 1.00f
                };

        //
        // Get the corresponding index for the specified argument/parameter.
        //
        int l_index = (int)(l_power * 16.0);
        if (l_index < 0)
        {
            l_index = -l_index;
        }
        else if (l_index > 16)
        {
            l_index = 16;
        }

        if (l_power < 0)
        {
            l_scale = -l_array[l_index];
        }
        else
        {
            l_scale = l_array[l_index];
        }

        return l_scale;

    } // scale_motor_power


    /**
     * Indicate whether the left drive motor's encoder has reached a value.
     */
    int a_left_arm_encoder_count ()
    {
        int l_return = 0;

        if (v_motor_left_arm!= null)
        {
            l_return = v_motor_left_arm.getCurrentPosition ();
        }

        return l_return;

    } // a_left_encoder_count
    public void run_using_left_arm_encoder()
    {
        if (v_motor_left_arm != null) {
            v_motor_left_arm.setChannelMode
                    (DcMotorController.RunMode.RUN_USING_ENCODERS
                    );
        }
    }
} // PushBotManual1
