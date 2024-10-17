// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands; 
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Subsystems.IntakeRollers;
import frc.robot.Subsystems.ShooterRollers;

public class RobotContainer {

  private static IntakeRollers intakeRoller = new IntakeRollers();
  private static ShooterRollers shooterRollers = new ShooterRollers();

  private CommandXboxController xbox1 = new CommandXboxController(0); 

  public RobotContainer() {
    configureBindings();
  }
 
  private void configureBindings() {
    xbox1.leftBumper().whileTrue(intakeRoller.intakeRollerIn());
    xbox1.rightBumper().whileTrue(intakeRoller.intakeRollerOut());

    xbox1.x().whileTrue(shooterRollers.shooterRollerShoot());
    xbox1.b().whileTrue(shooterRollers.shooterRollerIntake());
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
