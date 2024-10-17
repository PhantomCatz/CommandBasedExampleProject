package frc.robot.Subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import lombok.RequiredArgsConstructor;

public class Intake extends SubsystemBase {
  private final TalonFX motor;
  private final TalonFXConfiguration config;
  private final DutyCycleOut dutyCycleControl = new DutyCycleOut(0.0).withUpdateFreqHz(0.0);

  @RequiredArgsConstructor // TODO change intake speed to seperate
    public static enum elevator { 
      STOW(() -> 0.0),
      GROUND(() -> 20);

      private final DoubleSupplier requestedRollerSpeed;
      private double getRollerSpeed() {
        return requestedRollerSpeed.getAsDouble();
      }
    }

  public Intake() {
    motor = new TalonFX(11);
    config = new TalonFXConfiguration();

    config.CurrentLimits.SupplyCurrentLimit = 60.0;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

    config.MotorOutput.NeutralMode = NeutralModeValue.Coast;

    motor.getConfigurator().apply(config);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void ElevatorPosition(elevator setPosition) {

  }

  public void variableIntakeRoller(elevator percentOutput) {
    motor.setControl(dutyCycleControl.withOutput(percentOutput.getRollerSpeed()));
  }

  // public Command intakeRollerIn() {
  //   return startEnd(() -> variableIntakeRoller(setPercentageOutput.INTAKE),
  //                   () -> variableIntakeRoller(setPercentageOutput.IDLE))
  //                   ;
  // }

  // public Command intakeRollerOut() {
  //   return startEnd(() -> variableIntakeRoller(setPercentageOutput.EJECT),
  //                   () -> variableIntakeRoller(setPercentageOutput.IDLE))
  //                   ;
  // }

}
