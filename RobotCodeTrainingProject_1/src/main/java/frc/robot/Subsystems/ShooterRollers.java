package frc.robot.Subsystems;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Utilities.LinearProfile;
import lombok.RequiredArgsConstructor;

public class ShooterRollers extends SubsystemBase {

  private final TalonFX motor1;
  private final TalonFX motor2;

  private final LinearProfile leftProfile;
  private final LinearProfile rightProfile;

  private final TalonFXConfiguration config;
  private final DutyCycleOut dutyCycleControl = new DutyCycleOut(0.0).withUpdateFreqHz(0.0);

  private ShooterSpeed targetSpeed = ShooterSpeed.IDLE;
  private BooleanSupplier prepareShootSupplier = () -> false;

  private boolean wasClosedLoop = false;
  private boolean isFlywheelClosedLoop = false;

  @RequiredArgsConstructor // TODO change intake speed to seperate
    public enum ShooterSpeed { 
      IDLE(() -> 0.0, () -> 0.0),
      SHOOT(() -> 500.0, () -> -750.0),
      INTAKE(() -> -100, () -> 200);

      private final DoubleSupplier requestedRollerSpeedRight;
      private final DoubleSupplier requestedRollerSpeedLeft;

      private double getRollerSpeedRight() {
        return requestedRollerSpeedRight.getAsDouble();
      }
      private double getRollerSpeedLeft() {
        return requestedRollerSpeedLeft.getAsDouble();
      }

    }

  public ShooterRollers() {
    
    motor1 = new TalonFX(0);
    motor2 = new TalonFX(1);
    config = new TalonFXConfiguration();

    config.CurrentLimits.SupplyCurrentLimit = 60.0;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

    config.MotorOutput.NeutralMode = NeutralModeValue.Coast;

    motor1.getConfigurator().apply(config);
    motor2.getConfigurator().apply(config);

    leftProfile = new LinearProfile(9000.0, 0.02);
    rightProfile = new LinearProfile(9000.0, 0.02);
    
  }

  @Override
  public void periodic() {
    double leftGoal = targetSpeed.getRollerSpeedLeft();
    double rightGoal = targetSpeed.getRollerSpeedRight();
    boolean idlePrepareShoot = targetSpeed == ShooterSpeed.IDLE && prepareShootSupplier.getAsBoolean();
    if (idlePrepareShoot) {
      leftGoal = ShooterSpeed.SHOOT.getRollerSpeedLeft();
      rightGoal = ShooterSpeed.SHOOT.getRollerSpeedRight();
    }

  }

  private void setTargetSpeed(ShooterSpeed targetSpeed) {
    // Characterizing and idle flag
    if (targetSpeed == ShooterSpeed.IDLE) {
      wasClosedLoop = isFlywheelClosedLoop;
      isFlywheelClosedLoop = false;
      this.targetSpeed = targetSpeed;
      return; //don't set a goal
    }

    // Closed loop setting check
    if(this.targetSpeed == targetSpeed) {
      isFlywheelClosedLoop = true;
    }

    // Enable close loop
    if (!isFlywheelClosedLoop) {
      leftProfile.setGoal(targetSpeed.getRollerSpeedLeft(), motor1.getVelocity().getValueAsDouble());
      rightProfile.setGoal(targetSpeed.getRollerSpeedRight(), motor2.getVelocity().getValueAsDouble());
      isFlywheelClosedLoop = true;
    }
    this.targetSpeed = targetSpeed;
  }

  public Command shooterRollerShoot() {
    return startEnd(() -> setTargetSpeed(ShooterSpeed.SHOOT),
                    () -> setTargetSpeed(ShooterSpeed.IDLE))
                    ;
  }

  public Command shooterRollerIntake() {
    return startEnd(() -> setTargetSpeed(ShooterSpeed.INTAKE),
                    () -> setTargetSpeed(ShooterSpeed.IDLE))
                    ;
  }

}
