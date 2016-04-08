package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.hardware.GyroSensor;
/**
 * Created by Arimae on 4/8/2016.
 */
public class summer extends PushBotAuto1

{
        final int CLICKS_PER_INCH = 8800/36;
        GyroSensor sensorGyro;
        int heading;
        int start_heading;

//--------------------------------------------------------------------------
//
// PushBotAuto1
//
/**
 * Construct the class.
 *
 * The system calls this member when the class is instantiated.
 */
public summer ()

        {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

        } // PushBotAuto1

@Override
public void init() {
        super.init();

        try {
        sensorGyro = hardwareMap.gyroSensor.get("gyro");
        sensorGyro.calibrate();
        }
        catch (Exception p_exception) {

        sensorGyro = null;
        }

        }


//--------------------------------------------------------------------------
//
// start
//
/**
 * Perform any actions that are necessary when the OpMode is enabled.
 *
 * The system calls this member once when the OpMode is enabled.
 */
@Override public void start ()

        {
        //
        // Call the PushBotHardware (super/base class) start method.
        //
        super.start ();

        //
        // Reset the motor encoders on the drive wheels.
        //
        reset_drive_encoders ();

        } // start

//--------------------------------------------------------------------------
//
// loop
//
/**
 * Implement a state machine that controls the robot during auto-operation.
 * The state machine uses a class member and encoder input to transition
 * between states.
 *
 * The system calls this member repeatedly while the OpMode is running.
 */
@Override public void loop ()

        {
        if (!sensorGyro.isCalibrating()) {
        heading = sensorGyro.getHeading();
        }
        //----------------------------------------------------------------------
        //
        // State: Initialize (i.e. state_0).
        //
        switch (v_state)
        {
        //
        // Synchronize the state machine and hardware.
        //
        case 0:
        //
        // Reset the encoders to ensure they are at a known good value.
        //
        reset_drive_encoders ();

        //
        // Transition to the next state when this method is called again.
        //
        if (!sensorGyro.isCalibrating()) {
        v_state++;
        }
        break;
        //
        // Drive forward until the encoders exceed the specified values.
        //
        case 1:
        //
        // Tell the system that motor encoders will be used.  This call MUST
        // be in this state and NOT the previous or the encoders will not
        // work.  It doesn't need to be in subsequent states.
        //
        run_using_encoders ();

        //
        // Start the drive wheel motors at full power.
        //
        set_drive_power (1.0f, 1.0f);

        //
        // Have the motor shafts turned the required amount?
        //
        // If they haven't, then the op-mode remains in this state (i.e this
        // block will be executed the next time this method is called).
        //
        if (have_drive_encoders_reached (36*CLICKS_PER_INCH, 0))
        {
        //
        // Reset the encoders to ensure they are at a known good value.
        //
        reset_drive_encoders ();

        //
        // Stop the motors.
        //
        set_drive_power (0.0f, 0.0f);

        //
        // Transition to the next state when this method is called
        // again.
        //
        v_state++;
        }
        break;
        //
        // Wait...
        //
        case 2:
        if (have_drive_encoders_reset ())
        {
        v_state++;
        }
        break;
        //
        // Turn left until the encoders exceed the specified values.
        //
        case 3:
        run_using_encoders();
        set_drive_power(-1.0f, 1.0f);
        if (turned_from_to_heading(0, 90, true))
        {
        reset_drive_encoders ();
        set_drive_power (0.0f, 0.0f);
        v_state++;
        }
        break;
        //
        // Wait...
        //
        case 4:
        if (have_drive_encoders_reset ())
        {
        v_state++;
        }
        break;
        //
        // Turn right until the encoders exceed the specified values.
        //
        case 5:
        run_using_encoders ();
        set_drive_power (1.0f, 1.0f);
        if (have_drive_encoders_reached (134*CLICKS_PER_INCH, 0))
        {
        reset_drive_encoders ();
        set_drive_power (0.0f, 0.0f);
        v_state++;
        }
        break;
        //
        // Wait...
        //
        case 6:
        if (have_drive_encoders_reset ())
        {
        v_state++;
        }
        break;

        case 7:
        run_using_encoders();
        set_drive_power (1.0f, -1.0f);
        if (turned_from_to_heading(90, 0, false))
        {
        reset_drive_encoders ();
        set_drive_power (0.0f, 0.0f);
         v_state++;
          }
          break;
//
// Perform no action - stay in this case until the OpMode is stopped.
// This method will still be called regardless of the state machine.
//
default:
        //
        // The autonomous actions have been accomplished (i.e. the state has
        // transitioned into its final state.
        //
        break;
        }

        //
        // Send telemetry data to the driver station.
        //
        update_telemetry (); // Update common telemetry
        telemetry.addData ("18", "State: " + v_state);
        // don't start until the gyro is calibrated
        if (sensorGyro.isCalibrating()) {
        telemetry.addData("16", "Gyro calibratiing");
        }
        else {
        heading = sensorGyro.getHeading();
        telemetry.addData("16", "Gyro: " + heading);
        }
        } // loop

public int magnitude_turned_degrees(int start_heading) {
        if (!a_gyro_ready()) {
        return 0;   // don't know amount turned
        }
final int current_heading = a_current_heading();

final int result = Math.abs(signed_turn_in_degrees(start_heading));
        return result;
        }

/**
 * You can save the initial heading using start_heading = a_current_heading();
 *
 * Use this function if you want to check if you are turning clockwise or counter-clockwise.
 * Otherwise, use magnitude_turned_degrees if you are simply trying to turn a certain angle
 * and are confident which way you are turning.
 *
 * @param start_heading
 * @return for up to 179 degrees of turn, reports positive (clockwise) or negative (counter-clockwise) turn
 */
public int signed_turn_in_degrees(int start_heading) {
        if (!a_gyro_ready()) {
        return 0;   // don't know amount turned
        }
final int current_heading = a_current_heading();

final int clockwise_no_wrap = current_heading - start_heading;
        if (clockwise_no_wrap >= 0 && clockwise_no_wrap <= 180) {
        return clockwise_no_wrap;
        }
final int clockwise_with_wrap = (current_heading+360)-start_heading;
        if (clockwise_with_wrap <= 180) {
        return clockwise_with_wrap;
        }
final int counterclockwise_no_wrap = current_heading - start_heading;
        if (counterclockwise_no_wrap <= 0 &&
        counterclockwise_no_wrap >= -180) {
        return counterclockwise_no_wrap;
        }
final int counterclockwise_with_wrap = (current_heading-360) - start_heading;
        return counterclockwise_with_wrap;
        }

// returns true if gyro reports a turn AT LEAST AS FAR as end_heading
// from start_heading in the given direction
public boolean turned_from_to_heading(int start_heading, int end_heading, boolean turning_clockwise) {
final int MAX_TURN = 180;
final int clip_start_heading = clip_at_360(start_heading);
final int clip_end_heading = clip_at_360(end_heading);

        if (!a_gyro_ready()) {
        return false;   // don't know if reached heading
        }
final int current_heading = a_current_heading();

        if (turning_clockwise) {
        if (clip_end_heading > clip_start_heading) {
        return current_heading >= clip_end_heading;
        }
        else {
        // turn must wrap past zero and then reach end_heading (but not continue on to a 360!)
        return current_heading >= clip_end_heading &&
        current_heading <= (clip_start_heading+clip_end_heading)/2;
        }
        }
        else {  // counter-clockwise
        if (clip_end_heading < clip_start_heading) {
        return current_heading <= clip_end_heading;
        }
        else {
        // turn must wrap past zero and then reach end_heading (but not continue on to a 360!)
        return current_heading <= clip_end_heading &&
        current_heading >= (clip_start_heading+clip_end_heading)/2;
        }
        }
        }

/**
 *
 * @return true if gyro heading can be read
 */
public boolean a_gyro_ready() {
        boolean result = false;
        if (sensorGyro != null && !sensorGyro.isCalibrating()) {
        result = true;
        }
        return result;
        }

/**
 *
 * @return zero if calibrating or no gyro, else current heading
 */
public int a_current_heading() {
        int result = 0;
        if (sensorGyro == null) {
        // no action
        }
        else if (sensorGyro.isCalibrating()) {
        // no action
        }
        else {
        result = sensorGyro.getHeading();
        }
        return result;
        }

/**
 *
 * @param heading
 * @return heading circled into [0,359]
 */
public int clip_at_360(int heading) {
        int result = heading;
        while (result >= 360) {
        result -= 360;
        }
        while (result <= 0) {
        result += 360;
        }
        return result;
        }

//--------------------------------------------------------------------------
//
// v_state
//
/**
 * This class member remembers which state is currently active.  When the
 * start method is called, the state will be initialized (0).  When the loop
 * starts, the state will change from initialize to state_1.  When state_1
 * actions are complete, the state will change to state_2.  This implements
 * a state machine for the loop method.
 */
private int v_state = 0;

        } // PushBotAuto1

