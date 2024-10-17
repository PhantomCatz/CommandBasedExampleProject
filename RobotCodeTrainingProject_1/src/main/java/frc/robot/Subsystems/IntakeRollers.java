package frc.robot.Subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.NeutralOut;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.controls.VoltageOut;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import lombok.RequiredArgsConstructor;

public class IntakeRollers extends SubsystemBase {
  private final TalonFX motor;
  private final DutyCycleOut dutyCycleControl = new DutyCycleOut(0.0).withUpdateFreqHz(0.0);

  private final Slot0Configs controllerConfig = new Slot0Configs();
  private final VoltageOut voltageControl = new VoltageOut(0).withUpdateFreqHz(0.0);
  private final VelocityVoltage velocityControl = new VelocityVoltage(0).withUpdateFreqHz(0.0);
  private final NeutralOut neutralControl = new NeutralOut().withUpdateFreqHz(0.0);
  private final DutyCycleOut dudtyCycleControl = new DutyCycleOut(0.0).withUpdateFreqHz(0.0);
  
  private final StatusSignal<Double> rollerVelocity;
  private final StatusSignal<Double> rollerAppliedVolts;
  private final StatusSignal<Double> rollerSupplyCurrent;
  private final StatusSignal<Double> rollerTorqueCurrent;
  private final StatusSignal<Double> rollerTempCelsius;
  
  @RequiredArgsConstructor 
    public static enum setPercentageOutput { 
      IDLE(() ->  0.0),
      INTAKE(() -> 0.6),
      EJECT(() -> -0.6),
      HANDOFF_IN(() -> 0.1),
      HANDOFF_OUT(() -> -0.1);

      private final DoubleSupplier requestedRollerSpeed;
      private double getRollerSpeed() {
        return requestedRollerSpeed.getAsDouble();
      }
    }

  public IntakeRollers() {
    motor = new TalonFX(11);

    TalonFXConfiguration config = new TalonFXConfiguration();

    config = new TalonFXConfiguration();

    config.CurrentLimits.SupplyCurrentLimit = 60.0;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;

    config.MotorOutput.NeutralMode = NeutralModeValue.Coast;

    motor.getConfigurator().apply(config);


    controllerConfig.kP = 0.18;
    controllerConfig.kI = 0;
    controllerConfig.kD = 0.0006;
    controllerConfig.kS = 0.38767;
    controllerConfig.kV = 0.00108;
    controllerConfig.kA = 0.0;

    // Set Signals
    rollerVelocity = motor.getVelocity();
    rollerAppliedVolts = motor.getMotorVoltage();
    rollerSupplyCurrent = motor.getSupplyCurrent();
    rollerTorqueCurrent = motor.getTorqueCurrent();
    rollerTempCelsius = motor.getDeviceTemp();

    BaseStatusSignal.setUpdateFrequencyForAll(
      100.0,
      rollerVelocity,
      rollerAppliedVolts,
      rollerSupplyCurrent,
      rollerTorqueCurrent,
      rollerTempCelsius
    );
  }

  @Override
  public void periodic() {
  }
  
  private void setTargetSpeed(setPercentageOutput percentOutput) {
    motor.setControl(dutyCycleControl.withOutput(percentOutput.getRollerSpeed()));
    System.out.println(percentOutput.getRollerSpeed());
  }

  public Command intakeRollerIn() {
    return startEnd(() -> setTargetSpeed(setPercentageOutput.INTAKE),
                    () -> setTargetSpeed(setPercentageOutput.HANDOFF_IN));
  }

  public Command intakeRollIn() {
    return runOnce(() -> setTargetSpeed(setPercentageOutput.INTAKE));
  }

  public Command intakeRollerOut() {
    return startEnd(() -> setTargetSpeed(setPercentageOutput.EJECT),
                    () -> setTargetSpeed(setPercentageOutput.IDLE))
                    ;
  }

}

