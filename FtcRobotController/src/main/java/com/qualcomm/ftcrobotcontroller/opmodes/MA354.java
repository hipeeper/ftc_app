package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by molsmith on 1/31/2016.
 */
public class MA354 extends PushBotAuto1
 {
    final int DRIVE_DISTANCE_6_INCHES = 2880;
    public void init() {
        super.init();
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

    }
//--------------------------------------------------------------------------
    //
    // run_using_left_drive_encoder
    //
    /**
     * Set the left drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_using_left_arm_encoder ()

    {
        if (v_motor_left_arm != null)
        {
            v_motor_left_arm.setChannelMode
                    ( DcMotorController.RunMode.RUN_USING_ENCODERS
                    );
        }

    } // run_using_left_drive_encoder
    //--------------------------------------------------------------------------
    //
    // has_left_drive_encoder_reached
    //
    /**
     * Indicate whether the left drive motor's encoder has reached a value.
     */
    boolean has_left_arm_encoder_reached (double p_count)

    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        if (v_motor_left_arm != null)
        {
            //
            // Has the encoder reached the specified values?
            //
            // TODO Implement stall code using these variables.
            //
            if (Math.abs (v_motor_left_arm.getCurrentPosition ()) > p_count)
            {
                //
                // Set the status to a positive indication.
                //
                l_return = true;
            }
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_left_drive_encoder_reached

    //--------------------------------------------------------------------------
    //
    // has_right_drive_encoder_reached
    //
    /**
     * Indicate whether the right drive motor's encoder has reached a value.
     */
    //--------------------------------------------------------------------------
    //
    // has_left_drive_encoder_reset
    //
    /**
     * Indicate whether the left drive encoder has been completely reset.
     */
    boolean has_left_arm_encoder_reset ()
    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Has the left encoder reached zero?
        //
        if (a_left_arm_encoder_count () == 0)
        {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_left_drive_encoder_reset
    int a_left_arm_encoder_count ()
    {
        int l_return = 0;

        if (v_motor_left_arm!= null)
        {
            l_return = v_motor_left_arm.getCurrentPosition ();
        }

        return l_return;

    } // a_left_encoder_count
    //--------------------------------------------------------------------------
    //
    // reset_left_drive_encoder
    //
    /**
     * Reset the left drive wheel encoder.
     */
    public void reset_left_arm_encoder ()

    {
        if (v_motor_left_arm != null)
        {
            v_motor_left_arm.setChannelMode
                    ( DcMotorController.RunMode.RESET_ENCODERS
                    );
        }

    } // reset_left_drive_encoder

    //--------------------------------------------------------------------------
    //
    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    public void loop() {
        //----------------------------------------------------------------------
        //
        // State: Initialize (i.e. state_0).
        //
        switch (v_state) {
            //
            // Synchronize the state machine and hardware.
            //
            case 0:
                //
                // Reset the encoders to ensure they are at a known good value.
                //
                reset_drive_encoders();

                v_state++;

                break;
            case 1:
                reset_left_arm_encoder();

                //
                // Transition to the next state when this method is called again.
                //
                v_state++;

                break;
            case 2:
                if (drive_using_encoders(1.0, 1.0, 0, 18000)) {
                    v_state++;
                }
                break;
            //Wait...
            case 3:
                if (have_drive_encoders_reset()) {
                    v_state++;
                }
                break;
            case 4:
                if (drive_using_encoders(0.0,1.0, 0, 3000)){
                    v_state++;
                }
                break;

            case 5:
                if (have_drive_encoders_reset()) {
                    v_state++;
                }
                break;
            case 6:
                run_using_left_arm_encoder();
                m_left_arm_power(0.25);
                if (has_left_arm_encoder_reached(500)) {
                reset_left_arm_encoder();
                m_left_arm_power(0.0);
                    v_state++;
                }
                break;

            case 7:
                if (have_drive_encoders_reset()) {
                    v_state++;
                }
                break;
            case 8:
                if (drive_using_encoders(-1.0,-1.0,0, 12000)) {
                    v_state++;
                }
                break;

            default:
                break;
        }//Switch
        update_telemetry (); // Update common telemetry
        telemetry.addData("18", "State: " + v_state);
        telemetry.addData("32", "L Encoder " +a_left_arm_encoder_count());
    }//loop

    private int v_state = 0;
    private DcMotor v_motor_left_arm;

}//Class