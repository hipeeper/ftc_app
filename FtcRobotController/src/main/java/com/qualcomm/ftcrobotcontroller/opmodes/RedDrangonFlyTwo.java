package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by molsmith on 11/19/2015.
 */
public class RedDrangonFlyTwo extends PushBotAuto1{
    GyroSensor sensorGyro;
        int heading = 0;
        public void init()
        {

            super.init();
            // get a reference to our GyroSensor object.
            sensorGyro = hardwareMap.gyroSensor.get("gyro");

            // calibrate the gyro.
            sensorGyro.calibrate();
            m_hand_position(0.0);

        }

        public void run_using_left_arm_encoder()

        {
            if (v_motor_left_arm != null) {
                v_motor_left_arm.setChannelMode
                        (DcMotorController.RunMode.RUN_USING_ENCODERS
                        );
            }
        }

        boolean has_left_arm_encoder_reached(double p_count)

        {
            //
            // Assume failure.
            //
            boolean l_return = false;

            if (v_motor_left_arm != null) {//
                //
                // Has the encoder reached the specified values?
                //
                // TODO Implement stall code using these variables.
                //
                if (Math.abs(v_motor_left_arm.getCurrentPosition()) > p_count) {
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
        // has_left_drive_encoder_reached
        //

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
        //--------------------------------------------------------------------------
        //
        // reset_left_drive_encoder
        //
        /**
         * Reset the left drive wheel encoder.
         */
        public void reset_left_arm_encoder()

        {
            if (v_motor_left_arm != null) {
                v_motor_left_arm.setChannelMode
                        (DcMotorController.RunMode.RESET_ENCODERS
                        );
            }

        } // reset_left_drive_encoder

        //--------------------------------------------------------------------------
        //
        boolean has_left_arm_encoder_reset ()
        {
            //
            // Assume failure.
            //
            boolean l_return = false;

            //
            // Has the left encoder reached zero?
            //
            if (a_left_arm_encoder_count() == 0)
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
        public void start() {
            super.start();

        }

        @Override
        public void loop() {
            super.loop();
            heading = sensorGyro.getHeading();
            switch (v_state) {
                case 1:
                    reset_drive_encoders();
                    v_state++;
                    // reseting drive encoders
                    break;
                case 2:
                    if (have_drive_encoders_reset()) {
                        // reset drive encoders
                        v_state++;
                    }
                    break;
                case 3:

                    if(drive_using_encoders(-0.5f,0.5f, 200.7, 200.7)) {
                        v_state++;
                    }
                    // turn left
                    break;
                case 4:
                    if (have_drive_encoders_reset()) {
                        // reset drive encoders
                        v_state++;
                    }
                    break;
                case 5:
                    // raise arm
                    run_using_left_arm_encoder();
                    m_left_arm_power(0.6);
                    if(has_left_arm_encoder_reached(900)) {
                        reset_left_arm_encoder();
                        v_state++;
                    }
                    break;
                case 6:
                    if (has_left_arm_encoder_reset()) {
                        // reset arm encoder
                        v_state++;
                    }
                    break;
                case 7:
                    if(drive_using_encoders(0.4f, 0.4, 2371.8, 2371.8)) {
                        //Push the button
                        v_state++;
                    }
                    break;
                case 8:
                    if (have_drive_encoders_reset()) {
                        v_state++;
                    }
                    //reseting the encoders
                    break;
                case 9:
                    if(drive_using_encoders(-.5f, -.5f, 2371.8, 2371.8)) {
                        //back up
                        v_state++;
                    }
                    break;
                case 10:
                    if (have_drive_encoders_reset()) {
                        // reset drive encoders
                        v_state++;
                    }
                    break;
                default: break;
                // no action
            }
            update_telemetry(); // Update common telemetry
            telemetry.addData("18", "State: " + v_state);
            telemetry.addData("32", "L Encoder " +a_left_arm_encoder_count());
            telemetry.addData("4. h", String.format("%03d", heading));
        }

        private int v_state = 1;
        private DcMotor v_motor_left_arm;
        private Servo v_servo_right_hand;
        private Servo v_servo_left_hand;

    }
