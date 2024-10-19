package frc.robot.Subsystems;


import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;


import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class Test extends SubsystemBase {
    private final TalonFXConfiguration config; 
    private final TalonFX Test5;
    private final TalonFX Test4;

    public Test() {
       Test5 = new TalonFX(5);
       Test4 = new TalonFX(4);
       config = new TalonFXConfiguration();
       
       config.CurrentLimits.SupplyCurrentLimit = 60.0;
       config.CurrentLimits.SupplyCurrentLimitEnable = true;
       config.MotorOutput.NeutralMode = NeutralModeValue.Coast;

       Test5.getConfigurator().apply(config);
       Test4.getConfigurator().apply(config);
       
    }

    private void setTargetSpeed(int speed, int speed2) {
        Test5.set(speed);
        Test4.set(speed2);
    }

    // public String Helllooo() {
    //     String i = "hi";
        
    //     return i;
    // }
    // public int Hello() {
    //     int i = 0;


    //     return i;
    // }

    public Command motorSpeed() {
        return runOnce(() -> setTargetSpeed(1, -1));

    }
}
